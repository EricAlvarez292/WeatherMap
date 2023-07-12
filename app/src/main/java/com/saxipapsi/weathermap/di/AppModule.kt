package com.saxipapsi.weathermap.di

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.saxipapsi.weathermap.data.db.MapGeoDB
import com.saxipapsi.weathermap.data.remote.networkModule
import com.saxipapsi.weathermap.data.repository.CountryStateRepositoryImpl
import com.saxipapsi.weathermap.data.repository.WeatherRepositoryImpl
import com.saxipapsi.weathermap.domain.repository.CountryStateRepository
import com.saxipapsi.weathermap.domain.repository.WeatherRepository
import com.saxipapsi.weathermap.domain.use_case.geo.LoadGeoLocationUseCase
import com.saxipapsi.weathermap.domain.use_case.weather.GetForecastWeatherUseCase
import com.saxipapsi.weathermap.domain.use_case.weather.GetRealtimeWeatherUseCase
import com.saxipapsi.weathermap.presentation.forecast_weather.ForecastWeatherViewModel
import com.saxipapsi.weathermap.presentation.geo.GeoLocationViewModel
import com.saxipapsi.weathermap.presentation.realtime_weather.RealtimeWeatherViewModel
import com.saxipapsi.weathermap.utility.file.RawFileLoader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

object AppModule {

    fun initKoin(app: Application) {
        startKoin() {
            androidLogger(Level.ERROR)
            androidContext(app)
            modules(
                otherModule(app),
                daoModules(app),
                networkModule(app),
                repositoryModule,
                useCasesModule,
                viewModelModule,
            )
        }
    }
}

val repositoryModule = module {
    single { WeatherRepositoryImpl(get()) as WeatherRepository }
    single { CountryStateRepositoryImpl(get(), get(), get()) as CountryStateRepository }
//    single { CountryStateRepositoryImpl(get()) as CountryStateRepository }
}

val useCasesModule = module {
    single { LoadGeoLocationUseCase(get()) }
    single { GetRealtimeWeatherUseCase(get()) }
    single { GetForecastWeatherUseCase(get()) }
}

fun otherModule(app: Application) = module {
    single { RawFileLoader(app.applicationContext) }
}

val viewModelModule = module {
    viewModel { GeoLocationViewModel(get()) }
    viewModel { (handle: SavedStateHandle) -> RealtimeWeatherViewModel(get(), handle) }
    viewModel { (handle: SavedStateHandle) -> ForecastWeatherViewModel(get(), handle) }
}

fun daoModules(app: Application) = module {
    single { MapGeoDB.build(app.applicationContext) }
    single { get<MapGeoDB>().countryStateDao() }
    single { get<MapGeoDB>().cityDao() }
}




