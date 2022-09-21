package com.example.stores.mainModule

import com.example.stores.common.entities.StoreEntity

interface OnClickListener {
    fun onClick(storeId: Long)
    fun onFavouriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}