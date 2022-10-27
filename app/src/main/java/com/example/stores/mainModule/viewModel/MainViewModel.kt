package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.launch
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

    private val showProcess: MutableLiveData<Boolean> = MutableLiveData()

    fun isShowProgressBar(): LiveData<Boolean>{
        return showProcess
    }

    /*private val stores: MutableLiveData<MutableList<StoreEntity>> by lazy { //Se ejecuta una vez
        MutableLiveData<MutableList<StoreEntity>>().also { //Lanza la consulta inicial
            loadStores()
        }
    }*/
    private val stores = interactor.stores

    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }

    /*fun loadStores(){
        showProcess.value = Constants.SHOW
        interactor.getStores {
            showProcess.value = Constants.HIDE
            stores.value = it
            storeList = it
        }
    }*/

    fun deleteStore(storeEntity: StoreEntity){
        viewModelScope.launch {
            interactor.deleteStore(storeEntity)
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        viewModelScope.launch {
            storeEntity.isFavorite = !storeEntity.isFavorite
            interactor.updateStore(storeEntity)
        }
    }

}