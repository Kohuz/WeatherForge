package cz.cvut.weatherforge

import android.util.Log
import cz.cvut.weatherforge.features.measurements.data.MeasurementRepository
import cz.cvut.weatherforge.features.measurements.data.api.MeasurementRemoteDataSource
import cz.cvut.weatherforge.features.measurements.data.model.*
import cz.cvut.weatherforge.features.record.data.model.ValueStats
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.verify
import retrofit2.HttpException
import retrofit2.Response

class MeasurementRepositoryTest {

    private lateinit var repository: MeasurementRepository
    private lateinit var remoteDataSource: MeasurementRemoteDataSource

    private val testDailyMeasurement = MeasurementDaily(
        stationId = "ST001",
        element = "T",
        date = LocalDate.parse("2023-01-01"),
        value = 15.5,
        vtype = "AVG",
        flag = null,
        quality = 1.0,
        schedule = "hourly"
    )

    private val testMonthlyMeasurement = MeasurementMonthly(
        stationId = "ST001",
        element = "T",
        year = 2023,
        month = 1,
        timeFunction = "AVG",
        mdFunction = "AVG",
        value = 10.5,
        flagRepeat = null,
        flagInterrupted = null
    )

    private val testYearlyMeasurement = MeasurementYearly(
        stationId = "ST001",
        element = "T",
        year = 2023,
        timeFunction = "AVG",
        mdFunction = "AVG",
        value = 12.5,
        flagRepeat = null,
        flagInterrupted = null
    )

    private val testValueStat = ValueStats(
        element = "T",
        highest = 25.0,
        lowest = -5.0,
        average = 10.0
    )

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0

        remoteDataSource = mockk(relaxed = true)
        repository = MeasurementRepository(remoteDataSource)
    }

    @Test
    fun `getDailyMeasurements filters correctly for all element types`() = runTest {
        val testTMeasurements = listOf(
            testDailyMeasurement.copy(element = "T", vtype = "AVG"),
            testDailyMeasurement.copy(element = "T", vtype = "MAX")  // Should be filtered out
        )

        val testFMeasurements = listOf(
            testDailyMeasurement.copy(element = "F", vtype = "AVG"),
            testDailyMeasurement.copy(element = "F", vtype = "MIN")  // Should be filtered out
        )

        val testSRAMeasurements = listOf(
            testDailyMeasurement.copy(element = "SRA", vtype = "SUM"),
            testDailyMeasurement.copy(element = "SRA", vtype = "TOTAL")
        )

        coEvery { remoteDataSource.getMeasurementsDaily("ST001", "2023-01-01", "2023-01-02", "T") } returns testTMeasurements
        coEvery { remoteDataSource.getMeasurementsDaily("ST001", "2023-01-01", "2023-01-02", "F") } returns testFMeasurements
        coEvery { remoteDataSource.getMeasurementsDaily("ST001", "2023-01-01", "2023-01-02", "SRA") } returns testSRAMeasurements

        // Test T element filtering
        val resultT = repository.getDailyMeasurements("ST001", "2023-01-01", "2023-01-02", "T")
        assertEquals(1, resultT.measurements.size)
        assertEquals("AVG", resultT.measurements[0].vtype)
        assertEquals("T", resultT.measurements[0].element)

        // Test F element filtering
        val resultF = repository.getDailyMeasurements("ST001", "2023-01-01", "2023-01-02", "F")
        assertEquals(1, resultF.measurements.size)
        assertEquals("AVG", resultF.measurements[0].vtype)
        assertEquals("F", resultF.measurements[0].element)

        // Test non-T/F element (no filtering)
        val resultSRA = repository.getDailyMeasurements("ST001", "2023-01-01", "2023-01-02", "SRA")
        assertEquals(2, resultSRA.measurements.size)
        assertTrue(resultSRA.measurements.all { it.element == "SRA" })
    }

    @Test
    fun `getMonthlyMeasurements filters correctly for all element types`() = runTest {
        val testMeasurements = listOf(
            // T - should keep only AVG/AVG
            testMonthlyMeasurement.copy(element = "T", timeFunction = "AVG", mdFunction = "AVG"),
            testMonthlyMeasurement.copy(element = "T", timeFunction = "MAX", mdFunction = "AVG"), // filtered
            testMonthlyMeasurement.copy(element = "T", timeFunction = "AVG", mdFunction = "MAX"), // filtered

            // TMA - should keep only MAX
            testMonthlyMeasurement.copy(element = "TMA", mdFunction = "MAX"),
            testMonthlyMeasurement.copy(element = "TMA", mdFunction = "AVG"), // filtered

            // TMI - should keep only MIN
            testMonthlyMeasurement.copy(element = "TMI", mdFunction = "MIN"),
            testMonthlyMeasurement.copy(element = "TMI", mdFunction = "MAX"), // filtered

            // F - should keep only AVG/AVG
            testMonthlyMeasurement.copy(element = "F", timeFunction = "AVG", mdFunction = "AVG"),
            testMonthlyMeasurement.copy(element = "F", timeFunction = "MAX", mdFunction = "AVG"), // filtered

            // FMAX - should keep only MAX
            testMonthlyMeasurement.copy(element = "FMAX", mdFunction = "MAX"),
            testMonthlyMeasurement.copy(element = "FMAX", mdFunction = "AVG"), // filtered

            // SCE - should keep only GE(1)
            testMonthlyMeasurement.copy(element = "SCE", mdFunction = "GE(1)"),
            testMonthlyMeasurement.copy(element = "SCE", mdFunction = "LT(1)"), // filtered

            // SNO - should keep only GE(1)
            testMonthlyMeasurement.copy(element = "SNO", mdFunction = "GE(1)"),
            testMonthlyMeasurement.copy(element = "SNO", mdFunction = "LT(1)"), // filtered

            // OTHER - should keep all
            testMonthlyMeasurement.copy(element = "OTHER", mdFunction = "ANY_VALUE"),
            testMonthlyMeasurement.copy(element = "OTHER", mdFunction = "OTHER_VALUE")
        )

        coEvery { remoteDataSource.getMeasurementsMonthly(any(), any(), any(), any()) } answers {
            val element = arg<String>(3)
            testMeasurements.filter { it.element == element }
        }

        // Test T element filtering
        val resultT = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "T")
        assertEquals(1, resultT.measurements.size)
        assertEquals("AVG", resultT.measurements[0].timeFunction)
        assertEquals("AVG", resultT.measurements[0].mdFunction)

        // Test TMA element filtering
        val resultTMA = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "TMA")
        assertEquals(1, resultTMA.measurements.size)
        assertEquals("MAX", resultTMA.measurements[0].mdFunction)

        // Test TMI element filtering
        val resultTMI = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "TMI")
        assertEquals(1, resultTMI.measurements.size)
        assertEquals("MIN", resultTMI.measurements[0].mdFunction)

        // Test F element filtering
        val resultF = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "F")
        assertEquals(1, resultF.measurements.size)
        assertEquals("AVG", resultF.measurements[0].timeFunction)
        assertEquals("AVG", resultF.measurements[0].mdFunction)

        // Test FMAX element filtering
        val resultFMAX = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "FMAX")
        assertEquals(1, resultFMAX.measurements.size)
        assertEquals("MAX", resultFMAX.measurements[0].mdFunction)

        // Test SCE element filtering
        val resultSCE = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "SCE")
        assertEquals(1, resultSCE.measurements.size)
        assertEquals("GE(1)", resultSCE.measurements[0].mdFunction)

        // Test SNO element filtering
        val resultSNO = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "SNO")
        assertEquals(1, resultSNO.measurements.size)
        assertEquals("GE(1)", resultSNO.measurements[0].mdFunction)

        // Test OTHER element (no filtering)
        val resultOther = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "OTHER")
        assertEquals(2, resultOther.measurements.size)
    }
    @Test
    fun `getYearlyMeasurements filters correctly for all element types`() = runTest {
        val testMeasurements = listOf(
            // T - should keep only AVG/AVG
            testYearlyMeasurement.copy(element = "T", timeFunction = "AVG", mdFunction = "AVG"),
            testYearlyMeasurement.copy(element = "T", timeFunction = "MAX", mdFunction = "AVG"), // filtered
            testYearlyMeasurement.copy(element = "T", timeFunction = "AVG", mdFunction = "MAX"), // filtered

            // TMA - should keep only MAX
            testYearlyMeasurement.copy(element = "TMA", mdFunction = "MAX"),
            testYearlyMeasurement.copy(element = "TMA", mdFunction = "AVG"), // filtered

            // TMI - should keep only MIN
            testYearlyMeasurement.copy(element = "TMI", mdFunction = "MIN"),
            testYearlyMeasurement.copy(element = "TMI", mdFunction = "MAX"), // filtered

            // F - should keep only AVG/AVG
            testYearlyMeasurement.copy(element = "F", timeFunction = "AVG", mdFunction = "AVG"),
            testYearlyMeasurement.copy(element = "F", timeFunction = "MAX", mdFunction = "AVG"), // filtered

            // FMAX - should keep only MAX
            testYearlyMeasurement.copy(element = "FMAX", mdFunction = "MAX"),
            testYearlyMeasurement.copy(element = "FMAX", mdFunction = "AVG"), // filtered

            // SCE - should keep only GE(1)
            testYearlyMeasurement.copy(element = "SCE", mdFunction = "GE(1)"),
            testYearlyMeasurement.copy(element = "SCE", mdFunction = "LT(1)"), // filtered

            // SNO - should keep only GE(1)
            testYearlyMeasurement.copy(element = "SNO", mdFunction = "GE(1)"),
            testYearlyMeasurement.copy(element = "SNO", mdFunction = "LT(1)"), // filtered

            // OTHER - should keep all
            testYearlyMeasurement.copy(element = "OTHER", mdFunction = "ANY_VALUE"),
            testYearlyMeasurement.copy(element = "OTHER", mdFunction = "OTHER_VALUE")
        )

        coEvery { remoteDataSource.getMeasurementsYearly(any(), any(), any(), any()) } answers {
            val element = arg<String>(3)
            testMeasurements.filter { it.element == element }
        }

        // Test T element filtering
        val resultT = repository.getYearlyMeasurements("ST001", "2023", "2024", "T")
        assertEquals(1, resultT.measurements.size)
        assertEquals("AVG", resultT.measurements[0].timeFunction)
        assertEquals("AVG", resultT.measurements[0].mdFunction)

        // Test TMA element filtering
        val resultTMA = repository.getYearlyMeasurements("ST001", "2023", "2024", "TMA")
        assertEquals(1, resultTMA.measurements.size)
        assertEquals("MAX", resultTMA.measurements[0].mdFunction)

        // Test TMI element filtering
        val resultTMI = repository.getYearlyMeasurements("ST001", "2023", "2024", "TMI")
        assertEquals(1, resultTMI.measurements.size)
        assertEquals("MIN", resultTMI.measurements[0].mdFunction)

        // Test F element filtering
        val resultF = repository.getYearlyMeasurements("ST001", "2023", "2024", "F")
        assertEquals(1, resultF.measurements.size)
        assertEquals("AVG", resultF.measurements[0].timeFunction)
        assertEquals("AVG", resultF.measurements[0].mdFunction)

        // Test FMAX element filtering
        val resultFMAX = repository.getYearlyMeasurements("ST001", "2023", "2024", "FMAX")
        assertEquals(1, resultFMAX.measurements.size)
        assertEquals("MAX", resultFMAX.measurements[0].mdFunction)

        // Test SCE element filtering
        val resultSCE = repository.getYearlyMeasurements("ST001", "2023", "2024", "SCE")
        assertEquals(1, resultSCE.measurements.size)
        assertEquals("GE(1)", resultSCE.measurements[0].mdFunction)

        // Test SNO element filtering
        val resultSNO = repository.getYearlyMeasurements("ST001", "2023", "2024", "SNO")
        assertEquals(1, resultSNO.measurements.size)
        assertEquals("GE(1)", resultSNO.measurements[0].mdFunction)

        // Test OTHER element (no filtering)
        val resultOther = repository.getYearlyMeasurements("ST001", "2023", "2024", "OTHER")
        assertEquals(2, resultOther.measurements.size)
    }

    @Test
    fun `getStatsDayLongTerm returns success with data`() = runTest {
        // Arrange
        val testStats = listOf(testValueStat)
        coEvery { remoteDataSource.getStatsDayLongTerm(any(), any()) } returns testStats

        // Act
        val result = repository.getStatsDayLongTerm("ST001", "2023-01-01")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.valueStats.size)
        assertEquals("T", result.valueStats[0].element)
    }

    @Test
    fun `getMeasurementsDayAndMonth filters AVG for T and F elements`() = runTest {
        // Arrange
        val testMeasurements = listOf(
            testDailyMeasurement.copy(vtype = "AVG"),
            testDailyMeasurement.copy(vtype = "MAX")
        )
        coEvery { remoteDataSource.getMeasurementsDayAndMonth(any(), any(), any()) } returns testMeasurements

        // Act
        val resultT = repository.getMeasurementsDayAndMonth("ST001", "2023-01-01", "T")
        val resultF = repository.getMeasurementsDayAndMonth("ST001", "2023-01-01", "F")
        val resultOther = repository.getMeasurementsDayAndMonth("ST001", "2023-01-01", "SRA")

        // Assert
        assertEquals(1, resultT.measurements.size)
        assertEquals("AVG", resultT.measurements[0].vtype)

        assertEquals(1, resultF.measurements.size)
        assertEquals("AVG", resultF.measurements[0].vtype)

        assertEquals(2, resultOther.measurements.size)
    }

    @Test
    fun `getMeasurementsMonth returns all measurements`() = runTest {
        // Arrange
        val testMeasurements = listOf(testMonthlyMeasurement)
        coEvery { remoteDataSource.getMeasurementsMonth(any(), any(), any()) } returns testMeasurements

        // Act
        val result = repository.getMeasurementsMonth("ST001", "2023-01", "T")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1, result.measurements.size)
        assertEquals(2023, result.measurements[0].year)
    }

    @Test
    fun `getStatsDay filters AVG for T and F elements`() = runTest {
        // Arrange
        val testMeasurements = listOf(
            testDailyMeasurement.copy(element = "T", vtype = "AVG"),
            testDailyMeasurement.copy(element = "T", vtype = "MAX"),
            testDailyMeasurement.copy(element = "SRA", vtype = "SUM")
        )
        coEvery { remoteDataSource.getStatsDay(any(), any()) } returns testMeasurements

        // Act
        val result = repository.getStatsDay("ST001", "2023-01-01")

        // Assert
        assertEquals(2, result.measurements.size) // 1 T AVG + 1 SRA SUM
        assertTrue(result.measurements.all { it.element != "T" || it.vtype == "AVG" })
    }

    @Test
    fun `getMeasurementsTop filters correctly based on element type`() = runTest {
        // Arrange
        val testTMeasurements = listOf(
            testDailyMeasurement.copy(element = "T", vtype = "AVG"),
            testDailyMeasurement.copy(element = "T", vtype = "MAX")
        )
        val testSRAMeasurements = listOf(
            testDailyMeasurement.copy(element = "SRA", vtype = "SUM")
        )

        coEvery { remoteDataSource.getMeasurementsTop("ST001", "2023-01-01", "T") } returns testTMeasurements
        coEvery { remoteDataSource.getMeasurementsTop("ST001", "2023-01-01", "SRA") } returns testSRAMeasurements

        // Act - T element (should filter to AVG only)
        val resultT = repository.getMeasurementsTop("ST001", "2023-01-01", "T")

        // Act - SRA element (no filtering)
        val resultSRA = repository.getMeasurementsTop("ST001", "2023-01-01", "SRA")

        // Assert T results
        assertEquals(1, resultT.measurements.size)
        assertEquals("AVG", resultT.measurements[0].vtype)

        // Assert SRA results
        assertEquals(1, resultSRA.measurements.size)
        assertEquals("SUM", resultSRA.measurements[0].vtype)
    }

    @Test
    fun `getDailyMeasurements handles null values correctly`() = runTest {
        // Arrange
        val testMeasurements = listOf(
            testDailyMeasurement.copy(value = null),
            testDailyMeasurement.copy(value = 15.5)
        )
        coEvery { remoteDataSource.getMeasurementsDaily(any(), any(), any(), any()) } returns testMeasurements

        // Act
        val result = repository.getDailyMeasurements("ST001", "2023-01-01", "2023-01-02", "T")

        // Assert
        assertEquals(2, result.measurements.size)
        assertNull(result.measurements[0].value)
        assertEquals(15.5, result.measurements[1].value)
    }

    @Test
    fun `getMonthlyMeasurements handles null values correctly`() = runTest {
        // Arrange
        val testMeasurements = listOf(
            testMonthlyMeasurement.copy(value = null),
            testMonthlyMeasurement.copy(value = 10.5)
        )
        coEvery { remoteDataSource.getMeasurementsMonthly(any(), any(), any(), any()) } returns testMeasurements

        // Act
        val result = repository.getMonthlyMeasurements("ST001", "2023-01", "2023-02", "T")

        // Assert
        assertEquals(2, result.measurements.size)
        assertNull(result.measurements[0].value)
        assertEquals(10.5, result.measurements[1].value)
    }

    @Test
    fun `getDailyMeasurements returns failure on network error`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getMeasurementsDaily(any(), any(), any(), any()) }  throws
        HttpException(Response.error<Any>(500, "Server error".toResponseBody()))

        // Act
        val result = repository.getDailyMeasurements("ST001", "2023-01-01", "2023-01-02", "T")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.measurements.isEmpty())
    }


    @Test
    fun `getMonthlyMeasurements returns failure on on server error`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getMeasurementsMonthly(any(), any(), any(), any()) } throws
                HttpException(Response.error<Any>(500, "Server error".toResponseBody()))

        // Act
        val result = repository.getMonthlyMeasurements("ST001", "invalid-date", "2023-02", "T")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.measurements.isEmpty())
    }

    @Test
    fun `getYearlyMeasurements returns failure on server error`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getMeasurementsYearly(any(), any(), any(), any()) } throws
                HttpException(Response.error<Any>(500, "Server error".toResponseBody()))

        // Act
        val result = repository.getYearlyMeasurements("ST001", "invalid-date", "2023-02", "T")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.measurements.isEmpty())
    }

    @Test
    fun `getStatsDayLongTerm returns failure on server error`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getStatsDayLongTerm(any(), any()) } throws
                HttpException(Response.error<Any>(500, "Server error".toResponseBody()))

        // Act
        val result = repository.getStatsDayLongTerm("ST001", "2023-01-01")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.valueStats.isEmpty())
    }

    @Test
    fun `getMeasurementsDayAndMonth returns failure on null stationId`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getMeasurementsDayAndMonth(null.toString(), any(), any()) } throws NullPointerException()

        // Act
        val result = repository.getMeasurementsDayAndMonth(null.toString(), "2023-01-01", "T")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.measurements.isEmpty())
    }


    @Test
    fun `getStatsDay returns failure on server error`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getStatsDay(any(), any()) } throws
        HttpException(Response.error<Any>(500, "Server error".toResponseBody()))

        // Act
        val result = repository.getStatsDay("ST001", "2023-01-01")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.measurements.isEmpty())
    }





    @Test
    fun `getMeasurementsMonth returns failure on invalid element`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getMeasurementsMonth(any(), any(), "INVALID") } throws IllegalArgumentException("Invalid element")

        // Act
        val result = repository.getMeasurementsMonth("ST001", "2023-01", "INVALID")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.measurements.isEmpty())
    }


    @Test
    fun `getMeasurementsTop returns failure on null date`() = runTest {
        // Arrange
        coEvery { remoteDataSource.getMeasurementsTop(any(), null, any()) } throws IllegalArgumentException("Date cannot be null")

        // Act
        val result = repository.getMeasurementsTop("ST001", null, "T")

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.measurements.isEmpty())
    }

}