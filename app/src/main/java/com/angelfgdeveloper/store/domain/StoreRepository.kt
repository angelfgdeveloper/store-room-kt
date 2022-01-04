package com.angelfgdeveloper.store.domain

import com.angelfgdeveloper.store.data.model.StoreEntity

interface StoreRepository {
    suspend fun getAllStores(): MutableList<StoreEntity>
    suspend fun addStore(storeEntity: StoreEntity): Long
    suspend fun updateStore(storeEntity: StoreEntity)
    suspend fun deleteStore(storeEntity: StoreEntity)
}