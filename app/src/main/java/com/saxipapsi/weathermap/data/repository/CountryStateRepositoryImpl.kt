package com.saxipapsi.weathermap.data.repository

import com.saxipapsi.weathermap.data.remote.dto.CityDto
import com.saxipapsi.weathermap.data.remote.dto.CountryStateDto
import com.saxipapsi.weathermap.domain.repository.CountryStateRepository

class CountryStateRepositoryImpl() : CountryStateRepository {

    init {

    }

    override suspend fun getCountryStatesBySearch(search: String): List<CountryStateDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getCountryStateCitiesBySearch(search: String): List<CityDto> {
        TODO("Not yet implemented")
    }
}