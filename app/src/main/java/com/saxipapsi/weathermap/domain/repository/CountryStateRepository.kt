package com.saxipapsi.weathermap.domain.repository

import com.saxipapsi.weathermap.data.db.entity.CityEntity
import com.saxipapsi.weathermap.data.db.entity.CountryStateEntity

interface CountryStateRepository {
    suspend fun getCountryStatesBySearch(search: String): List<CountryStateEntity>
    suspend fun getCountryStateCitiesBySearch(search: String): List<CityEntity>
    suspend fun loadDataFromJson() : List<CountryStateEntity>
}