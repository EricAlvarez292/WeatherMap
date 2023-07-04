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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.saxipapsi.weathermap.common.Constant.REALTIME_ID
import com.saxipapsi.weathermap.databinding.ActivityMainWeatherBinding
import com.saxipapsi.weathermap.presentation.forecast_weather.ForecastWeatherViewModel
import com.saxipapsi.weathermap.presentation.realtime_weather.RealtimeWeatherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class MainActivity() : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainWeatherBinding

    private val savedStateHandle by lazy{ SavedStateHandle() }
    private val realtimeWeatherViewModel by stateViewModel<RealtimeWeatherViewModel> { parametersOf( savedStateHandle)}
    private val forecastWeatherViewModel by stateViewModel<ForecastWeatherViewModel> { parametersOf( SavedStateHandle()) }
    private val permissionId = 2
    private var location = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLocation()
    }

    private fun requestWeather(_location : String){
        lifecycleScope.launch {
            location = _location
            Log.d("eric", "Location : $location")
            savedStateHandle[REALTIME_ID] = location
            realtimeWeatherViewModel.state.collectLatest { result ->
                binding.loading.visibility = if(result.isLoading) VISIBLE else GONE
                binding.tvError.visibility = if (result.error.isNotEmpty()) VISIBLE else GONE
                binding.tvError.text = result.error
                result.data?.let { data ->
                    Log.d("eric", "Data : ${Gson().toJson(data)}")
                    binding.tvDegreeCelsius.text = data.location.name
                }
            }
        }
    }

    private val mFusedLocationClient : FusedLocationProviderClient by lazy{
        LocationServices.getFusedLocationProviderClient(this@MainActivity)
    }

    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    location?.let {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        Log.d("eric", "Location : ${Gson().toJson(list[0])}")
                        requestWeather("${it.latitude},${it.longitude}") }
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
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun checkPermissions(): Boolean = ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    private fun requestPermissions() { ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), permissionId) }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) { if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) { getLocation() } }
    }


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
}