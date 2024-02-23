package com.example.stores.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stores.common.entities.StoreEntity
import com.example.stores.editModule.model.EditInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditStoreViewModel :ViewModel() {

    private val storeSelected = MutableLiveData<StoreEntity>()
    private val showFav = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()

    private val interactor: EditInteractor

    init {
        interactor = EditInteractor()
    }

    fun setStoreSelected(storeEntity: StoreEntity){
        storeSelected.value = storeEntity
    }
    fun getStoreSelected(): LiveData<StoreEntity>{
        return storeSelected
    }
    fun setShowFav(isVisible:Boolean){
        showFav.value = isVisible
    }
    fun getShowFav(): LiveData<Boolean>{
        return showFav
    }
    fun setResult(value:Any){
        result.value = value
    }
    fun getResult(): LiveData<Any>{
        return result
    }

    fun saveStore(storeEntity: StoreEntity){
        interactor.saveStore(storeEntity) {
            result.value = it
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        viewModelScope.launch(Dispatchers.IO) {
            interactor.updateStore(storeEntity) {
                result.value = it
            }
        }
    }

}