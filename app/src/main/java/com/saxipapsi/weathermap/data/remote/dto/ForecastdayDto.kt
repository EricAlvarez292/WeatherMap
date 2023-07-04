package com.saxipapsi.weathermap.data.remote.dto

data class ForecastdayDto(
    val astroDto: AstroDto,
    val date: String,
    val date_epoch: Int,
    val dayDto: DayDto,
    val hourDto: List<HourDto>
)