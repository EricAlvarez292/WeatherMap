package com.saxipapsi.weathermap.data.remote

import com.google.gson.GsonBuilder
import com.saxipapsi.weathermap.common.Constant
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { getOkHttpClient() }
    single { getRetrofit(get()) }
    single { getWeatherApi(get()) }
    single { RetrofitClient(get()).initialize() }
}


private fun getOkHttpClient(): OkHttpClient = OkHttpClient()
    .newBuilder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .build()

private fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
    return Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

private fun getWeatherApi(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

class RetrofitClient(private val retrofit: Retrofit) {
    private lateinit var weatherApi: WeatherApi
    fun initialize() {
        weatherApi = retrofit.create(WeatherApi::class.java)
    }
}