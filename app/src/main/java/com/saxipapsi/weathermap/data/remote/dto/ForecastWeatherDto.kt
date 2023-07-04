package com.saxipapsi.weathermap.data.remote.dto

data class ForecastWeatherDto(
    val current: CurrentDto,
    val forecastDto: ForecastDto,
    val location: LocationDto
)