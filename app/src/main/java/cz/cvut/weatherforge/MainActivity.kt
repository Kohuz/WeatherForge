package cz.cvut.weatherforge

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import com.example.compose.AppTheme

import cz.cvut.weatherforge.core.presentation.Navigation

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(wrapContextWithCzechLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Navigation()
            }
        }
    }
}

private fun wrapContextWithCzechLocale(context: Context): Context {
    val czechLocale = java.util.Locale("cs", "CZ")

    val config = Configuration(context.resources.configuration)
    config.setLocale(czechLocale)

    return context.createConfigurationContext(config)
}