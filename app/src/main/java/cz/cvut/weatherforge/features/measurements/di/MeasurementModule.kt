package cz.cvut.weatherforge.features.measurements.di


import cz.cvut.weatherforge.features.measurements.data.MeasurementRepository
import cz.cvut.weatherforge.features.measurements.data.api.MeasurementApiDescription
import cz.cvut.weatherforge.features.measurements.data.api.MeasurementRemoteDataSource
import cz.cvut.weatherforge.features.measurements.data.api.MeasurementRetrofitDataSource
import retrofit2.Retrofit
import org.koin.core.module.dsl.*
import org.koin.dsl.module

val measurementModule = module {
    single { get<Retrofit>().create(MeasurementApiDescription::class.java) }
    factory<MeasurementRemoteDataSource> { MeasurementRetrofitDataSource(apiDescription = get()) }
    singleOf(::MeasurementRepository)
}