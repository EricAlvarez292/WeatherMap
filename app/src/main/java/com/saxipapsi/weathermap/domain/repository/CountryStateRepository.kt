package com.saxipapsi.weathermap.domain.repository

import com.saxipapsi.weathermap.data.remote.dto.CityDto
import com.saxipapsi.weathermap.data.remote.dto.CountryStateDto

interface CountryStateRepository {
    suspend fun getCountryStatesBySearch(search: String): List<CountryStateDto>
    suspend fun getCountryStateCitiesBySearch(search: String): List<CityDto>
}