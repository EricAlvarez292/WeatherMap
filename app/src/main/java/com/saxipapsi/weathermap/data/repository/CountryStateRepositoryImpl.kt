package com.saxipapsi.weathermap.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saxipapsi.weathermap.R
import com.saxipapsi.weathermap.data.db.dao.CityDao
import com.saxipapsi.weathermap.data.db.dao.CountryStateDao
import com.saxipapsi.weathermap.data.db.entity.CityEntity
import com.saxipapsi.weathermap.data.db.entity.CountryStateEntity
import com.saxipapsi.weathermap.domain.repository.CountryStateRepository
import com.saxipapsi.weathermap.utility.file.RawFileLoader
import java.lang.Exception
import java.lang.reflect.Type


class CountryStateRepositoryImpl(
    private val rawFileLoader: RawFileLoader,
    private val countryStateDao: CountryStateDao,
    private val cityDao: CityDao
) : CountryStateRepository {

    override suspend fun getCountryStatesBySearch(search: String): List<CountryStateEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getCountryStateCitiesBySearch(search: String): List<CityEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun loadDataFromJson(): List<CountryStateEntity> {
        val data = if (countryStateDao.isEmpty()) {
            try {
                val data = rawFileLoader.getRawString(R.raw.states_and_cities)
                val listType: Type = object : TypeToken<List<CountryStateEntity>>() {}.type
                val geoLocationData: List<CountryStateEntity> = Gson().fromJson(data, listType)
                geoLocationData.forEach { state ->
                    Log.d("eric", "A--->")
                    countryStateDao.insert(state)
                    state.cities.forEach { city ->
                        Log.d("eric", "B@")
                        city.state_id = state.id
                        cityDao.insert(city)
                    }
                }
            } catch (e: Exception) {
                Log.d("eric", "E@ : ${e.localizedMessage}")
            }
            countryStateDao.loadAll()
        } else {
            countryStateDao.loadAll()
        }
        return data
    }

}