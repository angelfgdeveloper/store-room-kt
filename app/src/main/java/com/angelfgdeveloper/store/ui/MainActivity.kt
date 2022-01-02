package com.angelfgdeveloper.store.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.angelfgdeveloper.store.core.OnClickListener
import com.angelfgdeveloper.store.core.Resource
import com.angelfgdeveloper.store.data.local.LocalStoreDataSource
import com.angelfgdeveloper.store.data.local.StoreDatabase
import com.angelfgdeveloper.store.data.model.StoreEntity
import com.angelfgdeveloper.store.databinding.ActivityMainBinding
import com.angelfgdeveloper.store.domain.StoreRepositoryImpl
import com.angelfgdeveloper.store.presentation.StoreViewModel
import com.angelfgdeveloper.store.presentation.StoreViewModelFactory

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    private lateinit var viewModel: StoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo =
            StoreRepositoryImpl(LocalStoreDataSource(StoreDatabase.getDatabase(this).storeDao()))
        viewModel = ViewModelProvider(this, StoreViewModelFactory(repo))[StoreViewModel::class.java]
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etName.text.toString().trim())

            viewModel.addStore(store).observe(this, { result ->
                when (result) {
                    is Resource.Failure -> {
                        Log.d("mainActivity", "Error")
                    }
                    is Resource.Loading -> {
                        Log.d("mainActivity", "Cargando")
                    }
                    is Resource.Success -> {
                        mAdapter.add(store)
                    }
                }
            })

        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(), this)
        mGridLayout = GridLayoutManager(this, 2)
        getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores() {
        viewModel.getAllStores().observe(this, { result ->
            when (result) {
                is Resource.Failure -> {
                    Log.d("mainActivity", "Error")
                }
                is Resource.Loading -> {
                    Log.d("mainActivity", "Cargando")
                }
                is Resource.Success -> {
                    mAdapter.setStores(result.data)
                }
            }
        })
    }

    /**
     * OnClickListener
     * */
    override fun onClick(storeEntity: StoreEntity) {

    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        viewModel.updateStore(storeEntity).observe(this, { result ->
            when (result) {
                is Resource.Failure -> {
                    Log.d("mainActivity", "Error")
                }
                is Resource.Loading -> {
                    Log.d("mainActivity", "Cargando")
                }
                is Resource.Success -> {
                    mAdapter.update(storeEntity)
                }
            }
        })
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        viewModel.deleteStore(storeEntity).observe(this, { result ->
            when (result) {
                is Resource.Failure -> {
                    Log.d("mainActivity", "Error")
                }
                is Resource.Loading -> {
                    Log.d("mainActivity", "Cargando")
                }
                is Resource.Success -> {
                    mAdapter.delete(storeEntity)
                }
            }
        })
    }
}