package com.saxipapsi.weathermap.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val WEATHER_DB_TABLE_NAME = "table_weather_item"
@Entity(tableName = WEATHER_DB_TABLE_NAME)
class WeatherDbEntity {

}




data class RatingDBEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title_name") val titleName: String,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "is_rated") val isRated: Boolean,
    @ColumnInfo(name = "is_updated_to_remote") val isUpdatedToRemote: Boolean = false
)