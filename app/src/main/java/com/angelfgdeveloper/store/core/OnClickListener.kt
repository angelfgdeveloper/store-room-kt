package com.angelfgdeveloper.store.core

import com.angelfgdeveloper.store.data.model.StoreEntity

interface OnClickListener {
    fun onClick(storeId: Long)
    fun onFavoriteStore(storeEntity: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}