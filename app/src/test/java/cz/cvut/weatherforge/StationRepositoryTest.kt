package cz.cvut.weatherforge

import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.db.StationLocalDataSource
import cz.cvut.weatherforge.features.stations.data.model.*
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class StationRepositoryTest {

    private lateinit var repository: StationRepository
    private lateinit var remoteDataSource: StationRemoteDataSource
    private lateinit var localDataSource: StationLocalDataSource

    // Test stations
    private val testStation1 = Station(
        stationId = "1",
        code = "ST1",
        startDate = LocalDateTime.parse("2020-01-01T00:00:00"),
        endDate = LocalDateTime.parse("3999-12-31T23:59:00"),
        location = "Test Location 1",
        longitude = 14.0,
        latitude = 50.0,
        elevation = 200.0,
        isFavorite = false
    )

    private val testStation2 = Station(
        stationId = "2",
        code = "ST2",
        startDate = LocalDateTime.parse("2020-01-01T00:00:00"),
        endDate = LocalDateTime.parse("3999-12-31T23:59:00"),
        location = "Test Location 2",
        longitude = 14.1,
        latitude = 50.1,
        elevation = 210.0,
        isFavorite = true
    )

    // Test elements
    private val testElement1 = ElementCodelistItem(
        abbreviation = "T",
        name = "Temperature",
        unit = "°C"
    )

    private val testElement2 = ElementCodelistItem(
        abbreviation = "F",
        name = "Wind speed",
        unit = "m/s"
    )

    private val testStationElement1 = StationElement(
        stationId = "1",
        beginDate = LocalDateTime.parse("2020-01-01T00:00:00"),
        endDate = LocalDateTime.parse("3999-12-31T23:59:00"),
        elementAbbreviation = "T",
        elementName = "Temperature",
        unitDescription = "°C",
        height = 2.0,
        schedule = "hourly"
    )

    @Before
    fun setUp() {

        // Mock static Log class
        mockkStatic(android.util.Log::class)
        every { android.util.Log.v(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0


        remoteDataSource = mock(StationRemoteDataSource::class.java)
        localDataSource = mock(StationLocalDataSource::class.java)
        repository = StationRepository(remoteDataSource, localDataSource)
    }


    @Test
    fun `getElementsCodelist returns local data when available`() = runTest {
        // Arrange
        val localElements = listOf(testElement1, testElement2)
        `when`(localDataSource.getElements()).thenReturn(localElements)

        // Act
        val result = repository.getElementsCodelist()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(localElements, result.elements)
        verify(localDataSource).getElements()
        verifyNoInteractions(remoteDataSource)
    }

    @Test
    fun `getElementsCodelist fetches from remote when local is empty`() = runTest {
        // Arrange
        val remoteElements = listOf(
            ElementCodelistItem(abbreviation = "T", name = "T", unit = "°C"),
            ElementCodelistItem(abbreviation = "F", name = "F", unit = "m/s")
        )
        val expectedTranslatedElements = listOf(
            ElementCodelistItem(abbreviation = "T", name = "Průměrná teplota", unit = "°C"),
            ElementCodelistItem(abbreviation = "F", name = "Průměrná rychlost větru", unit = "m/s")
        )
        `when`(localDataSource.getElements()).thenReturn(emptyList())
        `when`(remoteDataSource.getElementsCodelist()).thenReturn(remoteElements)

        // Act
        val result = repository.getElementsCodelist()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedTranslatedElements, result.elements)
        verify(localDataSource).getElements()
        verify(remoteDataSource).getElementsCodelist()
        verify(localDataSource).insertCodelist(expectedTranslatedElements)
    }

    @Test
    fun `getStations returns failure on error`() = runTest {
        // Arrange
        `when`(localDataSource.getStations()).thenThrow(RuntimeException("DB error"))
        `when`(remoteDataSource.getStations()).thenThrow(RuntimeException("Network error"))

        // Act
        val result = repository.getStations()

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.stations.isEmpty())
    }

    @Test
    fun `getStation returns success with station data`() = runTest {
        // Arrange
        `when`(remoteDataSource.getStation(testStation1.stationId)).thenReturn(testStation1)

        // Act
        val result = repository.getStation(testStation1.stationId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testStation1, result.station)
        verify(remoteDataSource).getStation(testStation1.stationId)
    }

    @Test
    fun `getStation returns failure on error`() = runTest {
        // Arrange
        `when`(remoteDataSource.getStation(testStation1.stationId)).thenThrow(RuntimeException("Error"))

        // Act
        val result = repository.getStation(testStation1.stationId)

        // Assert
        assertFalse(result.isSuccess)
        assertNull(result.station)
    }


    @Test
    fun `getClosestStation returns success with closest station`() = runTest {
        // Arrange
        val lat = 50.0
        val long = 14.0
        `when`(remoteDataSource.getClosest(lat, long, 1)).thenReturn(listOf(testStation1))

        // Act
        val result = repository.getClosestStation(lat, long)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(testStation1, result.station)
        verify(remoteDataSource).getClosest(lat, long, 1)
    }

    @Test
    fun `getClosestStation returns failure on error`() = runTest {
        // Arrange
        val lat = 50.0
        val long = 14.0
        `when`(remoteDataSource.getClosest(lat, long, 1)).thenThrow(RuntimeException("Error"))

        // Act
        val result = repository.getClosestStation(lat, long)

        // Assert
        assertFalse(result.isSuccess)
        assertNull(result.station)
    }

    @Test
    fun `getNearbyStations returns success with nearby stations`() = runTest {
        // Arrange
        val lat = 50.0
        val long = 14.0
        val expectedStations = listOf(testStation1, testStation2)
        `when`(remoteDataSource.getClosest(lat, long, 4)).thenReturn(expectedStations)

        // Act
        val result = repository.getNearbyStations(lat, long)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(expectedStations, result.stations)
        verify(remoteDataSource).getClosest(lat, long, 4)
    }

    @Test
    fun `getNearbyStations returns failure on error`() = runTest {
        // Arrange
        val lat = 50.0
        val long = 14.0
        `when`(remoteDataSource.getClosest(lat, long, 4)).thenThrow(RuntimeException("Error"))

        // Act
        val result = repository.getNearbyStations(lat, long)

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.stations.isEmpty())
    }


    @Test
    fun `getElementsCodelist returns failure on error`() = runTest {
        // Arrange
        `when`(localDataSource.getElements()).thenThrow(RuntimeException("DB error"))
        `when`(remoteDataSource.getElementsCodelist()).thenThrow(RuntimeException("Network error"))

        // Act
        val result = repository.getElementsCodelist()

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.elements.isEmpty())
    }


    @Test
    fun `makeFavorite calls local data source`() = runTest {
        // Act
        repository.makeFavorite(testStation1.stationId)

        // Assert
        verify(localDataSource).makeFavorite(testStation1.stationId)
    }

    @Test
    fun `removeFavorite calls local data source`() = runTest {
        // Act
        repository.removeFavorite(testStation1.stationId)

        // Assert
        verify(localDataSource).removeFavorite(testStation1.stationId)
    }

    @Test
    fun `translateElementName translates known elements`() {
        assertEquals("Průměrná teplota", repository.translateElementName("T"))
        assertEquals("Minimální teplota", repository.translateElementName("TMI"))
        assertEquals("Maximální teplota", repository.translateElementName("TMA"))
        assertEquals("Průměrná rychlost větru", repository.translateElementName("F"))
        assertEquals("Nejvyšší rychlost větru", repository.translateElementName("Fmax"))
        assertEquals("Množství srážek", repository.translateElementName("SRA"))
        assertEquals("Nový sníh", repository.translateElementName("SNO"))
        assertEquals("Výška sněhu", repository.translateElementName("SCE"))
    }

    @Test
    fun `translateElementName returns original for unknown elements`() {
        assertEquals("UNKNOWN", repository.translateElementName("UNKNOWN"))
    }

    @Test
    fun `getStations returns local stations when available`() = runTest {
        // Arrange - Use existing testStation1 and testStation2
        val localStations = listOf(testStation1, testStation2)
        `when`(localDataSource.getStations()).thenReturn(localStations)

        // Act
        val result = repository.getStations()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(localStations, result.stations)
        verify(localDataSource).getStations()
        verifyNoInteractions(remoteDataSource)
        verify(localDataSource, never()).insertStations(anyList())
    }

    @Test
    fun `getStations fetches from remote and saves when local is empty`() = runTest {
        // Arrange - Use existing testStation1 and testStation2
        val remoteStations = listOf(testStation1, testStation2)
        `when`(localDataSource.getStations()).thenReturn(emptyList())
        `when`(remoteDataSource.getStations()).thenReturn(remoteStations)

        // Act
        val result = repository.getStations()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(remoteStations, result.stations)
        verify(localDataSource).getStations()
        verify(remoteDataSource).getStations()
        verify(localDataSource).insertStations(remoteStations)
    }


    @Test
    fun `getStations handles empty local and remote failure`() = runTest {
        // Arrange
        `when`(localDataSource.getStations()).thenReturn(emptyList())
        `when`(remoteDataSource.getStations()).thenThrow(RuntimeException("Network error"))

        // Act
        val result = repository.getStations()

        // Assert
        assertFalse(result.isSuccess)
        assertTrue(result.stations.isEmpty())
        verify(localDataSource).getStations()
        verify(remoteDataSource).getStations()
        verify(localDataSource, never()).insertStations(anyList())
    }

}