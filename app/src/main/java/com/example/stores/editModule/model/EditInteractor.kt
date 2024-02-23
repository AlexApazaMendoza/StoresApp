package com.example.stores.editModule.model

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditInteractor {

    fun saveStore(storeEntity: StoreEntity, callback: (Long) -> Unit ){
        doAsync {
            val newId = StoreApplication.database.storeDao().addStore(storeEntity)
            uiThread {
                callback(newId)
            }
        }
    }

    suspend fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit ){
        StoreApplication.database.storeDao().updateStore(storeEntity)
        withContext(Dispatchers.Main) {
            callback(storeEntity)
        }
    }
}