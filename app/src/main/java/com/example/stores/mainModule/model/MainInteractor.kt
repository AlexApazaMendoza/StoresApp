package com.example.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainInteractor {

    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit){
        doAsync {
            val storesList = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                val json = Gson().toJson(storesList)
                Log.i("Gson", json)
                callback(storesList)
            }
        }
    }

    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val url = Constants.STORES_URL + Constants.GET_ALL_PATH

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {
            //val status = it.getInt(Constants.STATUS_PROPERTY)

            val status = it.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)  //de manera opcional extrae un elemento

            if(status == Constants.SUCCESS){
                /*val jsonObject = Gson().fromJson(
                    it.getJSONArray(Constants.STORES_PROPERTY).get(0).toString(),
                    StoreEntity::class.java
                )*/
                val jsonList = it.optJSONArray(Constants.STORES_PROPERTY)?.toString()
                if(jsonList != null){
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){}.type
                    val storeList = Gson().fromJson<MutableList<StoreEntity>>(jsonList, mutableListType)
                    callback(storeList)
                    return@JsonObjectRequest
                }
            }
            callback(mutableListOf())
        },{
            it.printStackTrace()
            callback(mutableListOf())
        })

        StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)
    }
    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit ){
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }

    suspend fun updateStore(storeEntity: StoreEntity){
        delay(1000)
        StoreApplication.database.storeDao().updateStore(storeEntity)
    }

}