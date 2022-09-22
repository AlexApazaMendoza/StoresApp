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

    private val interactor: MainInteractor

    init {
        interactor = MainInteractor()
        //stores = MutableLiveData()
        //loadStores()
    }


    private val stores: MutableLiveData<List<StoreEntity>> by lazy { //Se ejecuta una vez
        MutableLiveData<List<StoreEntity>>().also { //Lanza la consulta inicial
            loadStores()
        }
    }

    fun getStores(): LiveData<List<StoreEntity>>{
        return stores
    }

    private fun loadStores(){
        interactor.getStores {
            stores.value = it
        }
        /*interactor.getStoresCallback(object :MainInteractor.StoresCallback{
            override fun getStoresCallback(stores: MutableList<StoreEntity>) {
                this@MainViewModel.stores.value = stores
            }
        })*/
    }

}