package com.example.stores.common.database

import androidx.room.*
import com.example.stores.common.entities.StoreEntity

@Dao
interface StoreDao {

    @Query("select *from StoreEntity")
    fun getAllStores() : MutableList<StoreEntity>

    @Insert
    fun addStore(storeEntity: StoreEntity): Long

    @Update
    suspend fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)

    @Query("select *from StoreEntity where id = :id")
    fun getStore(id: Long) : StoreEntity

}