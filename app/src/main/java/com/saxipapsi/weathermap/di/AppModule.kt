package com.saxipapsi.weathermap.di

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.saxipapsi.weathermap.data.remote.networkModule
import com.saxipapsi.weathermap.data.repository.WeatherRepositoryImpl
import com.saxipapsi.weathermap.domain.repository.WeatherRepository
import com.saxipapsi.weathermap.domain.use_case.GetForecastWeatherUseCase
import com.saxipapsi.weathermap.domain.use_case.GetRealtimeWeatherUseCase
import com.saxipapsi.weathermap.presentation.forecast_weather.ForecastWeatherViewModel
import com.saxipapsi.weathermap.presentation.realtime_weather.RealtimeWeatherViewModel
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
                networkModule,
                repositoryModule,
                useCasesModule,
                viewModelModule
            )
        }
    }
}

val repositoryModule = module {
    single { WeatherRepositoryImpl(get()) as WeatherRepository }
}

val useCasesModule = module {
    single { GetRealtimeWeatherUseCase(get()) }
    single { GetForecastWeatherUseCase(get()) }
}

val viewModelModule = module {
    viewModel { (handle: SavedStateHandle) -> RealtimeWeatherViewModel(get(), handle) }
    viewModel { (handle: SavedStateHandle) -> ForecastWeatherViewModel(get(), handle) }
}




