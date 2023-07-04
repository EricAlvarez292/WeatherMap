package com.saxipapsi.weathermap.presentation.realtime_weather

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxipapsi.weathermap.common.Constant.DEFAULT_ID
import com.saxipapsi.weathermap.common.Constant.REALTIME_ID
import com.saxipapsi.weathermap.common.Resource
import com.saxipapsi.weathermap.data.remote.dto.RealtimeWeatherDto
import com.saxipapsi.weathermap.domain.use_case.GetRealtimeWeatherUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RealtimeWeatherViewModel(private val getRealtimeWeatherUseCase: GetRealtimeWeatherUseCase, private val savedStateHandle: SavedStateHandle) : ViewModel() {

    init {
        val id = savedStateHandle[REALTIME_ID] ?: DEFAULT_ID
        getWeather(id)
    }

    private val _state = MutableStateFlow(RealtimeWeatherState())
    val state: StateFlow<RealtimeWeatherState> = _state

    private fun getWeather(id: String) {
        Log.d("eric", "ID: $id")
        getRealtimeWeatherUseCase(data = id).onEach { resource ->
            when (resource) {
                is Resource.Loading -> _state.value = RealtimeWeatherState(isLoading = true)
                is Resource.Error -> _state.value = RealtimeWeatherState(error = resource.message ?: "Unexpected error occured.")
                is Resource.Success -> _state.value = RealtimeWeatherState(data = resource.data)
            }
        }.launchIn(viewModelScope)
    }
}

data class RealtimeWeatherState(
    val isLoading: Boolean = false,
    val data: RealtimeWeatherDto? = null,
    val error: String = ""
)