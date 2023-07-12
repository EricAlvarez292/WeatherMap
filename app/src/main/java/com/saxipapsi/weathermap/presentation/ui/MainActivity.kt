package com.saxipapsi.weathermap.presentation.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.saxipapsi.weathermap.R
import com.saxipapsi.weathermap.common.Constant.DEFAULT_ID
import com.saxipapsi.weathermap.common.Constant.FORECAST_ID
import com.saxipapsi.weathermap.common.Constant.REALTIME_ID
import com.saxipapsi.weathermap.data.remote.dto.ForecastdayDto
import com.saxipapsi.weathermap.databinding.ActivityMainWeatherBinding
import com.saxipapsi.weathermap.presentation.forecast_weather.ForecastWeatherViewModel
import com.saxipapsi.weathermap.presentation.geo.GeoLocationViewModel
import com.saxipapsi.weathermap.presentation.realtime_weather.RealtimeWeatherViewModel
import com.saxipapsi.weathermap.presentation.realtime_weather.components.RealTimeWeatherAdapter
import com.saxipapsi.weathermap.utility.extension.load
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf
import java.util.*


class MainActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainWeatherBinding

    private val savedStateHandle by lazy { SavedStateHandle() }
    private val realtimeWeatherViewModel by stateViewModel<RealtimeWeatherViewModel> { parametersOf(savedStateHandle) }
    private val forecastWeatherViewModel by stateViewModel<ForecastWeatherViewModel> { parametersOf(savedStateHandle) }
    private val geoLocationViewModel: GeoLocationViewModel by inject()

    private val forecastWeather: MutableList<ForecastdayDto> = mutableListOf()
    private val forecastAdapter: RealTimeWeatherAdapter by lazy { RealTimeWeatherAdapter(forecastWeather) }

    private val _scrollState = MutableStateFlow(false)
    private val scrollState: StateFlow<Boolean> = _scrollState

    private val permissionId = 2
    private var menu: Menu? = null
    private var scrollRange = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        savedStateHandle[REALTIME_ID] = savedInstanceState?.getChar(REALTIME_ID) ?: DEFAULT_ID
        savedStateHandle[FORECAST_ID] = savedInstanceState?.getChar(FORECAST_ID) ?: DEFAULT_ID
        binding.forecastLayout.rvForecast.adapter = forecastAdapter
        binding.appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (scrollRange == -1) {
                scrollRange = appBarLayout?.totalScrollRange ?: -1
            }
            _scrollState.value = scrollRange + verticalOffset == 0
        }
    }

    private suspend fun onScrollViewUpdateListener(){
        scrollState.collectLatest{ isShow ->
            if (isShow) {
                hideOption(R.id.action_settings)
                hideOption(R.id.action_add)
                binding.toolbarTitle.text = binding.tvLocation.text
            } else {
                showOption(R.id.action_settings)
                showOption(R.id.action_add)
                binding.toolbarTitle.text = ""
            }
        }
    }

    private fun hideOption(id: Int) {
        val item: MenuItem? = menu?.findItem(id)
        item?.isVisible = false
    }

    private fun showOption(id: Int) {
        val item: MenuItem? = menu?.findItem(id)
        item?.isVisible = true
    }

    override fun onResume() {
        super.onResume()
        getLocation()
        initObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu!!
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun initObservers() {
        lifecycleScope.launch { observeRealTimeWeather() }
        lifecycleScope.launch { observeForecastWeather() }
        lifecycleScope.launch { observeGeoLocation() }
        lifecycleScope.launch { onScrollViewUpdateListener() }
    }

    private fun getLocationWeather(coordinates: String) {
        Log.d("eric", "Coordinates : $coordinates")
        savedStateHandle[REALTIME_ID] = coordinates
        savedStateHandle[FORECAST_ID] = coordinates
        lifecycleScope.launch { realtimeWeatherViewModel.getWeather(coordinates) }
        lifecycleScope.launch { forecastWeatherViewModel.getWeather(coordinates) }
    }

    private suspend fun observeRealTimeWeather() {
        realtimeWeatherViewModel.state.collectLatest { result ->
            binding.realtimeLoading.loading.visibility = if (result.isLoading) VISIBLE else GONE
            binding.realtimeLoading.tvError.visibility = if (result.error.isNotEmpty()) VISIBLE else GONE
            binding.realtimeLoading.tvError.text = result.error
            result.data?.let { data ->
                Log.d("weather", "Realtime Weather Data : ${Gson().toJson(data)}")
                binding.tvLocation.text = data.location.name
                binding.tvDegreeCelsius.text = data.current.temp_c.toString()
                val realFeel = "RealFeel: ${data.current.feelslike_c}"
                binding.tvRealFeel.text = realFeel
                binding.tvCondition.text = data.current.condition.text
                binding.ivIcon.load("https:${data.current.condition.icon}")
            }
        }
    }

    private suspend fun observeForecastWeather() {
        forecastWeatherViewModel.state.collect { result ->
            binding.forecastLayout.forecastLoading.loading.visibility = if (result.isLoading) VISIBLE else GONE
            binding.forecastLayout.forecastLoading.tvError.visibility = if (result.error.isNotEmpty()) VISIBLE else GONE
            binding.forecastLayout.forecastLoading.tvError.text = result.error
            result.data?.let { data ->
                Log.d("weather", "Forecast Weather Data : ${data.forecast.forecastday.size}")
                forecastWeather.clear()
                forecastWeather.addAll(data.forecast.forecastday)
                forecastAdapter.notifyItemRangeChanged(0, forecastWeather.size)
            }
        }
    }

    private suspend fun observeGeoLocation() {
        geoLocationViewModel.state.collectLatest { result ->
            Log.d("eric", "Geo isLoading : ${result.isLoading}")
            Log.d("eric", "Geo error : ${result.error}")
            result.data?.let { data -> Log.d("eric", "Geo Data Size : ${data.size}") }
        }
    }

    private val mFusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this@MainActivity)
    }

    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val _location: Location? = task.result
                    _location?.let {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> = geocoder.getFromLocation(_location.latitude, _location.longitude, 1) as List<Address>
                        Log.d("eric", "Location : ${Gson().toJson(list[0])}")
                        getLocationWeather("${it.latitude},${it.longitude}")
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    private fun requestPermissions() { ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), permissionId) }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}