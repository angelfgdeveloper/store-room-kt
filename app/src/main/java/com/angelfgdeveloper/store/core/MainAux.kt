package com.angelfgdeveloper.store.core

import com.angelfgdeveloper.store.data.model.StoreEntity

interface MainAux {
    fun hideFab(isVisible: Boolean = false)

    fun addStore(storeEntity: StoreEntity)
    fun updateStore(storeEntity: StoreEntity)
}