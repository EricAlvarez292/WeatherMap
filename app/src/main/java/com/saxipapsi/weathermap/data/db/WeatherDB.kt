package com.saxipapsi.weathermap.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.saxipapsi.weathermap.data.db.entity.WeatherDbEntity

@Database(entities = [WeatherDbEntity::class], version = 1)
abstract class WeatherDB : RoomDatabase() {



    companion object {
        private const val WEATHER_DB_NAME = "weather_database"
        fun build(context: Context): WeatherDB = Room.databaseBuilder(context, WeatherDB::class.java, WEATHER_DB_NAME).build()
    }
}