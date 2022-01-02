package com.angelfgdeveloper.store.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.angelfgdeveloper.store.core.Resource
import com.angelfgdeveloper.store.data.model.StoreEntity
import com.angelfgdeveloper.store.domain.StoreRepository
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

class StoreViewModel(private val repo: StoreRepository) : ViewModel() {

    fun getAllStores() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            emit(Resource.Success(repo.getAllStores()))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun addStore(storeEntity: StoreEntity) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            emit(Resource.Success(repo.addStore(storeEntity)))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun updateStore(storeEntity: StoreEntity) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            emit(Resource.Success(repo.updateStore(storeEntity)))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

    fun deleteStore(storeEntity: StoreEntity) = liveData(Dispatchers.IO) {
        emit(Resource.Loading())

        try {
            emit(Resource.Success(repo.deleteStore(storeEntity)))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }

}

class StoreViewModelFactory(private val repo: StoreRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(StoreRepository::class.java).newInstance(repo)
    }
}
