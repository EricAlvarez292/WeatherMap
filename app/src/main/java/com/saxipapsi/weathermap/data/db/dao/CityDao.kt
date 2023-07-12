package com.saxipapsi.weathermap.data.db.dao

import androidx.room.*
import com.saxipapsi.weathermap.data.db.entity.CityEntity

@Dao
interface CityDao {

    @Query("SELECT * FROM table_city")
    suspend fun loadAll(): List<CityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CityEntity): Long

    @Query("DELETE FROM table_city")
    suspend fun clear(): Int

    @Transaction
    suspend fun deleteAndInsert(entity: CityEntity): Long {
        clear()
        return insert(entity)
    }
}