package cz.cvut.weatherforge.features.record.data

import android.util.Log
import cz.cvut.weatherforge.features.record.data.api.RecordRemoteDataSource
import cz.cvut.weatherforge.features.record.data.model.RecordStats
import cz.cvut.weatherforge.features.record.data.model.StationRecord
import cz.cvut.weatherforge.features.record.data.model.StatsResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RecordRepositoryTest {

    private lateinit var repository: RecordRepository
    private lateinit var remoteDataSource: RecordRemoteDataSource

    // Test data
    private val testStationRecord = StationRecord(
        stationId = "ST001",
        element = "T",
        recordType = "HIGHEST",
        value = 35.0,
        recordDate = LocalDate.parse("2023-07-15")
    )

    private val testRecordStats = listOf(
        RecordStats(
            highest = testStationRecord.copy(element = "T", recordType = "HIGHEST"),
            lowest = testStationRecord.copy(element = "T", recordType = "LOWEST", value = -10.0),
            average = 12.5,
            element = "T"
        ),
        RecordStats(
            highest = testStationRecord.copy(element = "F", recordType = "HIGHEST", value = 25.0),
            lowest = testStationRecord.copy(element = "F", recordType = "LOWEST", value = 0.0),
            average = 5.0,
            element = "F"
        )
    )

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0

        remoteDataSource = mockk()
        repository = RecordRepository(remoteDataSource)
    }

    @Test
    fun `getAllTimeStationRecords returns success with data`() = runTest {
        // Arrange
        val stationId = "ST001"
        coEvery { remoteDataSource.getAllTimeStationRecords(stationId) } returns testRecordStats

        // Act
        val result = repository.getAllTimeStationRecords(stationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testRecordStats, result.stats)
        assertEquals(2, result.stats.size)
        assertEquals("T", result.stats[0].element)
        assertEquals(35.0, result.stats[0].highest?.value)
        verify(exactly = 0) { Log.e(any(), any()) }
    }

    @Test
    fun `getAllTimeStationRecords returns failure on network error`() = runTest {
        // Arrange
        val stationId = "ST001"
        coEvery { remoteDataSource.getAllTimeStationRecords(stationId) } throws IOException("Network error")

        // Act
        val result = repository.getAllTimeStationRecords(stationId)

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.stats.isEmpty())
        verify { Log.e("RecordRepository", "Error fetching records: Network error") }
    }

    @Test
    fun `getAllTimeRecords returns success with data`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getAllTimeRecords() } returns testRecordStats

        // Act
        val result = repository.getAllTimeRecords()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testRecordStats, result.stats)
        assertEquals("F", result.stats[1].element)
        assertEquals(0.0, result.stats[1].lowest?.value)
    }

    @Test
    fun `getAllTimeRecords handles null values in records`() = runTest {
        // Arrange
        val statsWithNulls = listOf(
            RecordStats(
                highest = null,
                lowest = testStationRecord.copy(recordType = "LOWEST", value = -10.0),
                average = null,
                element = "T"
            ),
            RecordStats(
                highest = testStationRecord.copy(recordType = "HIGHEST", value = 25.0),
                lowest = null,
                average = 5.0,
                element = "F"
            )
        )
        coEvery { remoteDataSource.getAllTimeRecords() } returns statsWithNulls

        // Act
        val result = repository.getAllTimeRecords()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(2, result.stats.size)
        assertNull(result.stats[0].highest)
        assertNull(result.stats[0].average)
        assertNull(result.stats[1].lowest)
    }

    @Test
    fun `getAllTimeRecords returns failure on server error`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getAllTimeRecords() } throws RuntimeException("Server error")

        // Act
        val result = repository.getAllTimeRecords()

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.stats.isEmpty())
        verify { Log.e("RecordRepository", "Error fetching records: Server error") }
    }

    @Test
    fun `getAllTimeRecords returns empty list as success`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getAllTimeRecords() } returns emptyList()

        // Act
        val result = repository.getAllTimeRecords()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.stats.isEmpty())
    }
}