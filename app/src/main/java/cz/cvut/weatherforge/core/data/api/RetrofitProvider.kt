package cz.cvut.weatherforge.core.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitProvider {
    private val json = Json { ignoreUnknownKeys = true }

    fun provide(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://157.180.20.129:8081/")


            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            )

            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}