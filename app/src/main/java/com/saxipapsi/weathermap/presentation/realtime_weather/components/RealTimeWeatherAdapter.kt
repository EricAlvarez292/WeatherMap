package com.saxipapsi.weathermap.presentation.realtime_weather.components

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saxipapsi.weathermap.R
import com.saxipapsi.weathermap.data.remote.dto.ForecastdayDto
import com.saxipapsi.weathermap.utility.extension.load

class RealTimeWeatherAdapter(private val forecastWeather: List<ForecastdayDto>) : RecyclerView.Adapter<RealTimeWeatherItem>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealTimeWeatherItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_forecast_item, parent, false)
        return RealTimeWeatherItem(view)
    }
    override fun onBindViewHolder(holder: RealTimeWeatherItem, position: Int) { holder.bind(forecastWeather[position]) }
    override fun getItemCount(): Int  = forecastWeather.size
}

class RealTimeWeatherItem(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val tvCondition: TextView = itemView.findViewById(R.id.tvCondition)
    private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
    fun bind(weather : ForecastdayDto){
        tvCondition.text = weather.day.condition.text
        ivIcon.load( "https:${weather.day.condition.icon}")
    }
}
