package com.saxipapsi.weathermap.data.remote.dto

data class CountryStateDto(
    val cities: List<CityDto>,
    val country_id: Int,
    val id: Int,
    val latitude: String,
    val longitude: String,
    val name: String,
    val state_code: String
)