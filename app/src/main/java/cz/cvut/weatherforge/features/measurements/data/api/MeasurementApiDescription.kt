//package cz.cvut.weatherforge.features.measurements.data.api
//
//import cz.cvut.weatherforge.features.stations.data.model.ElementCodelistItem
//import cz.cvut.weatherforge.features.stations.data.model.Station
//import retrofit2.http.GET
//import retrofit2.http.Path
//import retrofit2.http.Query
//
//interface MeasurementApiDescription {
//    @GET("measurements/{stationId}")
//    suspend fun getMeasurements(
//        @Path("stationId") stationId: String,
//        @Query("dateFrom") dateFrom: String?,
//        @Query("dateTo") dateTo: String?,
//        @Query("element") element: String?,
//        @Query("resolution") resolution: String?
//    ): Unit
//
//    @GET("measurements/{stationId}/actual")
//    suspend fun getActualMeasurements(@Path("stationId") stationId: String): List<MeasurementLatest>
//
//    @GET("measurements/{stationId}/recent")
//    suspend fun getRecentMeasurements(@Path("stationId") stationId: String): Unit
//
//    @GET("measurements/{stationId}/statsDay")
//    suspend fun getStatsDay(
//        @Path("stationId") stationId: String,
//        @Query("date") date: String
//    ): List<Pair>
//
//    @GET("measurements/{stationId}/statsDayLongTerm")
//    suspend fun getStatsDayLongTerm(
//        @Path("stationId") stationId: String,
//        @Query("date") date: String
//    ): List<Pair>
//
//}