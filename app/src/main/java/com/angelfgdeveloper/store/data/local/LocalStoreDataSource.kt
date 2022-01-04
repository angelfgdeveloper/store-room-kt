package com.angelfgdeveloper.store.data.local

import com.angelfgdeveloper.store.data.model.StoreEntity

class LocalStoreDataSource(private val storeDao: StoreDao) {
    suspend fun getAllStores(): MutableList<StoreEntity> = storeDao.getAllStores()
    suspend fun addStore(storeEntity: StoreEntity): Long = storeDao.addStore(storeEntity)
    suspend fun updateStore(storeEntity: StoreEntity) = storeDao.updateStore(storeEntity)
    suspend fun deleteStore(storeEntity: StoreEntity) = storeDao.deleteStore(storeEntity)
}