package com.saxipapsi.weathermap.presentation.forecast_weather

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxipapsi.weathermap.common.Constant.DEFAULT_ID
import com.saxipapsi.weathermap.common.Constant.FORECAST_ID
import com.saxipapsi.weathermap.common.Resource
import com.saxipapsi.weathermap.data.remote.dto.ForecastWeatherDto
import com.saxipapsi.weathermap.domain.use_case.GetForecastWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ForecastWeatherViewModel(private val getForecastWeatherUseCase: GetForecastWeatherUseCase, savedStateHandle: SavedStateHandle) : ViewModel() {

    init {
        val id = savedStateHandle.get<String>(FORECAST_ID) ?: DEFAULT_ID
        getWeather(id)
    }

    private val _state = MutableStateFlow(ForecastWeatherState())
    val state: StateFlow<ForecastWeatherState> = _state

    private fun getWeather(id: String) {
        Log.d("eric", "ID: $id")
        getForecastWeatherUseCase(data = id).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    _state.value = ForecastWeatherState(isLoading = true)
                }
                is Resource.Error -> {
                    _state.value = ForecastWeatherState(
                        error = resource.message ?: "Unexpected error occured."
                    )
                }
                is Resource.Success -> {
                    _state.value = ForecastWeatherState(data = resource.data)
                }
            }
        }.launchIn(viewModelScope)
    }
}

data class ForecastWeatherState(
    val isLoading: Boolean = false,
    val data: ForecastWeatherDto? = null,
    val error: String = ""
)