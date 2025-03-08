package cz.cvut.weatherforge.features.stations.di

import com.kozubek.livesport.features.sportEntries.data.StationLocalDataSource
import com.kozubek.livesport.features.sportEntries.data.db.StationRoomDataSource
import cz.cvut.weatherforge.core.data.db.LocalDatabase
import cz.cvut.weatherforge.features.stations.data.StationRepository
import cz.cvut.weatherforge.features.stations.data.api.StationApiDescription
import cz.cvut.weatherforge.features.stations.data.api.StationRemoteDataSource
import cz.cvut.weatherforge.features.stations.data.api.StationRetrofitDataSource
import cz.cvut.weatherforge.features.stations.presentation.detail.DetailScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.GraphContentViewModel
import cz.cvut.weatherforge.features.stations.presentation.detail.tabs.HistoryContentViewModel
import cz.cvut.weatherforge.features.stations.presentation.list.ListScreenViewModel
import cz.cvut.weatherforge.features.stations.presentation.map.MapScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.*
import org.koin.dsl.module

import retrofit2.Retrofit

val stationModule = module {
    single { get<Retrofit>().create(StationApiDescription::class.java) }
    factory<StationRemoteDataSource> { StationRetrofitDataSource(apiDescription = get()) }
    factory<StationLocalDataSource> { StationRoomDataSource(stationDao = get(), elementCodelistDao = get()) }

    single { get<LocalDatabase>().stationDao() }
    single { get<LocalDatabase>().elementCodelistDao() }

    singleOf(::StationRepository)
    viewModel { ListScreenViewModel(get()) }
    viewModel { MapScreenViewModel(get())}
    viewModel { DetailScreenViewModel(get(), get(), get())}
    viewModel { GraphContentViewModel(get())}
    viewModel { HistoryContentViewModel(get())}



}