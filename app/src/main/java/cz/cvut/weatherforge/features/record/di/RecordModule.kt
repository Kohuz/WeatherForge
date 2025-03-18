package cz.cvut.weatherforge.features.record.di


import cz.cvut.weatherforge.features.home.presentation.HomeScreenViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import cz.cvut.weatherforge.features.record.data.RecordRepository
import cz.cvut.weatherforge.features.record.data.api.RecordApiDescription
import cz.cvut.weatherforge.features.record.data.api.RecordRemoteDataSource
import cz.cvut.weatherforge.features.record.data.api.RecordRetrofitDataSource
import cz.cvut.weatherforge.features.record.presentatioin.RecordsScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

import retrofit2.Retrofit


val recordModule = module {
    singleOf(::RecordRepository)
    single { get<Retrofit>().create(RecordApiDescription::class.java) }
    factory<RecordRemoteDataSource> { RecordRetrofitDataSource(apiDescription = get()) }
    viewModel { RecordsScreenViewModel(get(), get(), get()) }

}