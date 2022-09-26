package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.mainModule.model.MainInteractor
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainViewModel: ViewModel() {

    private var storeList: MutableList<StoreEntity>

    private val interactor: MainInteractor

    init {
        interactor = MainInteractor()
        storeList = mutableListOf()
        //stores = MutableLiveData()
        //loadStores()
    }


    private val stores: MutableLiveData<MutableList<StoreEntity>> by lazy { //Se ejecuta una vez
        MutableLiveData<MutableList<StoreEntity>>().also { //Lanza la consulta inicial
            loadStores()
        }
    }

    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun loadStores(){
        interactor.getStores {
            stores.value = it
            storeList = it
        }
    }

    fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity) {
            val index = storeList.indexOf(it)
            if (index != -1) {
                storeList.removeAt(index)
                stores.value = storeList
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFavorite = !storeEntity.isFavorite
        interactor.deleteStore(storeEntity) {
            val index = storeList.indexOf(it)
            if(index != -1){
                storeList[index] = storeEntity
                stores.value = storeList
            }
        }
    }

}