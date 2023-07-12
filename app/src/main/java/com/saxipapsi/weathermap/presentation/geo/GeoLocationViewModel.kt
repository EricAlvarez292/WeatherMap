package com.saxipapsi.weathermap.presentation.geo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saxipapsi.weathermap.common.Resource
import com.saxipapsi.weathermap.data.db.entity.CountryStateEntity
import com.saxipapsi.weathermap.domain.use_case.geo.LoadGeoLocationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GeoLocationViewModel(private val loadGeoLocationUseCase: LoadGeoLocationUseCase) : ViewModel() {

    init {  loadGeoLocation() }

    private val _state = MutableStateFlow(LoadGeoLocationState())
    val state: StateFlow<LoadGeoLocationState> = _state

    private fun loadGeoLocation() = CoroutineScope(IO).launch {
        loadGeoLocationUseCase().onEach { resource ->
            when (resource) {
                is Resource.Loading -> { _state.value = LoadGeoLocationState(isLoading = true) }
                is Resource.Error -> { _state.value = LoadGeoLocationState(error = resource.message ?: "Unexpected error occured.") }
                is Resource.Success -> { _state.value = LoadGeoLocationState(data = resource.data) }
            }
        }.launchIn(viewModelScope)
    }
}

data class LoadGeoLocationState(
    val isLoading: Boolean = false,
    val data: List<CountryStateEntity>? = null,
    val error: String = ""
)
