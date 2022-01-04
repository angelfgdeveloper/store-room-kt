package com.angelfgdeveloper.store.domain

import com.angelfgdeveloper.store.data.local.LocalStoreDataSource
import com.angelfgdeveloper.store.data.model.StoreEntity

class StoreRepositoryImpl(private val dataSource: LocalStoreDataSource) : StoreRepository {
    override suspend fun getAllStores(): MutableList<StoreEntity> = dataSource.getAllStores()
    override suspend fun getStoreById(id: Long): StoreEntity = dataSource.getStoreById(id)
    override suspend fun addStore(storeEntity: StoreEntity): Long = dataSource.addStore(storeEntity)
    override suspend fun updateStore(storeEntity: StoreEntity) = dataSource.updateStore(storeEntity)
    override suspend fun deleteStore(storeEntity: StoreEntity) = dataSource.deleteStore(storeEntity)
}