package cz.cvut.weatherforge.features.stations.di

import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.api.StationApiDescription
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.api.StationRetrofitDataSource
import cz.cvut.weatherforge.features.stations.presentation.list.ListScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.map.MapScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.*
import org.koin.core.module.dsl.singleOf
import org.koin.androidx.viewmodel.ext.android.viewModel

import org.koin.dsl.module

import retrofit2.Retrofit

val stationModule = module {
    single { get<Retrofit>().create(StationApiDescription::class.java) }
    factory<StationRemoteDataSource> { StationRetrofitDataSource(apiDescription = get()) }
    singleOf(::StationRepository)
    viewModel { ListScreenViewModel(get()) }
    viewModel { MapScreenViewModel(get())}


}