package com.example.stores

interface MainAux {
    fun hideFab(isVisible: Boolean = false)

    fun hideKeyboard()

    fun addStore(storeEntity: StoreEntity)

    fun updateStore(storeEntity: StoreEntity)
}