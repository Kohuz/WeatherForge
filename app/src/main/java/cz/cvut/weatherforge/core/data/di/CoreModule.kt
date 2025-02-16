package cz.cvut.weatherforge.core.data.di

import cz.cvut.weatherforge.core.data.api.RetrofitProvider
import org.koin.dsl.module

val coreModule get() = module {
    single { RetrofitProvider.provide() }
}