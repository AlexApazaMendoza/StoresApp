package com.example.stores

import androidx.room.*

@Dao
interface StoreDao {

    @Query("select *from StoreEntity")
    fun getAllStores() : MutableList<StoreEntity>

    @Insert
    fun addStore(storeEntity: StoreEntity): Long

    @Update
    fun updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)

    @Query("select *from StoreEntity where id = :id")
    fun getStore(id: Long) : StoreEntity

}