package com.saxipapsi.weathermap.data.remote

import com.saxipapsi.weathermap.common.Constant
import com.saxipapsi.weathermap.data.remote.dto.ForecastWeatherDto
import com.saxipapsi.weathermap.data.remote.dto.RealtimeWeatherDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherApi {
    /*
    * Query parameter based on which data is sent back. It could be following:
    * Latitude and Longitude (Decimal degree) e.g: q=48.8567,2.3508
    * city name e.g.: q=Paris
    * US zip e.g.: q=10001
    * UK postcode e.g: q=SW1
    * Canada postal code e.g: q=G2J
    * metar: e.g: q=metar:EGLL
    * iata:<3 digit airport code> e.g: q=iata:DXB
    * auto:ip IP lookup e.g: q=auto:ip
    * IP address (IPv4 and IPv6 supported) e.g: q=100.0.0.1
    * */
    @GET("/current.json")
    suspend fun getRealtimeWeather(
        @Header("X-RapidAPI-Host") host: String = Constant.HOST,
        @Header("X-RapidAPI-Key") token: String = Constant.TOKEN,
        @Query("q") data: String
    ): RealtimeWeatherDto

    @GET("/forecast.json")
    suspend fun getForecastWeather(
        @Header("X-RapidAPI-Host") host: String = Constant.HOST,
        @Header("X-RapidAPI-Key") token: String = Constant.TOKEN,
        @Query("q") data: String,
        @Query("day") day: Int
    ): ForecastWeatherDto
}