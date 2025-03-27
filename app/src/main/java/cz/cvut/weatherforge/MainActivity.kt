package cz.cvut.weatherforge

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.compose.AppTheme

import cz.cvut.weatherforge.core.presentation.Navigation

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(wrapContextWithCzechLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                Navigation()
            }
        }
    }
}

// Helper function to apply Czech locale to a context
private fun wrapContextWithCzechLocale(context: Context): Context {
    val czechLocale = java.util.Locale("cs", "CZ")

    val config = Configuration(context.resources.configuration)
    config.setLocale(czechLocale)

    return context.createConfigurationContext(config)
}