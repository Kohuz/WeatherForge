package cz.cvut.weatherforge.core.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitProvider {
    private val json = Json { ignoreUnknownKeys = true }

    fun provide(): Retrofit {
        return Retrofit.Builder()
//                .baseUrl("http://167.71.32.243:8081/")
            .baseUrl("http://138.2.165.231:8081/")

            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )

            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}