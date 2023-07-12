package com.saxipapsi.weathermap.data.db.dao

import androidx.room.*
import com.saxipapsi.weathermap.data.db.entity.CountryStateEntity
import com.saxipapsi.weathermap.data.db.entity.CountryStateWithCitiesEntity

@Dao
interface CountryStateDao {

    @Query("SELECT * FROM table_country_state")
    suspend fun loadAll(): List<CountryStateEntity>

    @Query("SELECT * FROM table_country_state")
    suspend fun loadAllWithCities(): List<CountryStateWithCitiesEntity>

    @Query("SELECT * FROM table_country_state WHERE id=:id ")
    fun getStateWithCitiesById(id: Int): CountryStateWithCitiesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CountryStateEntity): Long

    @Query("DELETE FROM table_country_state")
    suspend fun clear(): Int

    @Query("SELECT count(1) where not exists (SELECT * from table_country_state)")
    suspend fun getOneAsEmpty() : Int

    suspend fun isEmpty() = getOneAsEmpty() == 1

    @Transaction
    suspend fun deleteAndInsert(entity: CountryStateEntity): Long {
        clear()
        return insert(entity)
    }


}