package cz.cvut.weatherforge.features.stations.di

import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.api.StationApiDescription
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.api.StationRetrofitDataSource
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

val stationModule get() =  module {
    single { get<Retrofit>().create(StationApiDescription::class.java) }
    factory<StationRemoteDataSource> { StationRetrofitDataSource(apiDescription = get()) }

    factory {
        StationRepository(
            stationRemoteDataSource = get(),
        )
    }
    viewModelOf(::ListScreenViewModel)
    viewModelOf(::DetailScreenViewModel)
}