package com.example.stores.editModule.model

import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditInteractor {

    fun saveStore(storeEntity: StoreEntity, callback: (Long) -> Unit ){
        doAsync {
            val newId = StoreApplication.database.storeDao().addStore(storeEntity)
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(newId)
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit ){
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }
}