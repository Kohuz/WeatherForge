package cz.cvut.weatherforge

import android.app.Application
import cz.cvut.weatherforge.core.data.di.coreModule
import cz.cvut.weatherforge.features.stations.di.stationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(stationModule, coreModule)
        }
    }
}