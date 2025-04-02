package cz.cvut.weatherforge.core.utils


import com.google.android.gms.maps.model.LatLng
import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
import cz.cvut.weatherforge.features.stations.data.model.Station
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

fun calculateDistancesForNearbyStations(
    stations: List<Station>,
    userLocation: LatLng
): List<Pair<Station, Double>> {
    return stations.drop(1).map { station ->
        val distance = pythagoreanDistance(
            userLocation.latitude,
            userLocation.longitude,
            station.latitude,
            station.longitude
        ) * 111.32 // Convert to kilometers
        station to distance // Return Pair<Station, Double>
    }
}

fun getLocalizedDateString(date: LocalDate?, locale: Locale = Locale("cs", "CZ")): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(locale)

    if (date != null) {
        return date.format(formatter)
    }
    else {
        return ""
    }
}

fun pythagoreanDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dx = lat2 - lat1
    val dy = lon2 - lon1
    return sqrt(dx. pow(2) + dy.pow(2))
}

fun getUnitByElementAbbreviation(abbreviation: String, codelist: List<ElementCodelistItem>): String{
    return codelist.find { item -> item.abbreviation == abbreviation }?.unit ?: ""
}

fun elementAbbreviationToNameUnitPair(abbreviation: String, codelist: List<ElementCodelistItem>): ElementCodelistItem? {
    return codelist.find { item -> item.abbreviation == abbreviation }
}
