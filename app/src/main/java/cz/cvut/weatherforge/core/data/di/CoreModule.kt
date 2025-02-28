package cz.cvut.weatherforge.core.data.di

import cz.cvut.weatherforge.core.data.api.RetrofitProvider
import cz.cvut.weatherforge.core.data.db.LocalDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule get() = module {
    single { RetrofitProvider.provide() }
    single { LocalDatabase.instance(androidContext()) }

}