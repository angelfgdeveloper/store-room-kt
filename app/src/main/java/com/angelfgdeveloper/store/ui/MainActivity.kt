package com.angelfgdeveloper.store.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.angelfgdeveloper.store.R
import com.angelfgdeveloper.store.core.MainAux
import com.angelfgdeveloper.store.core.OnClickListener
import com.angelfgdeveloper.store.core.Resource
import com.angelfgdeveloper.store.data.local.LocalStoreDataSource
import com.angelfgdeveloper.store.data.local.StoreDatabase
import com.angelfgdeveloper.store.data.model.StoreEntity
import com.angelfgdeveloper.store.databinding.ActivityMainBinding
import com.angelfgdeveloper.store.domain.StoreRepositoryImpl
import com.angelfgdeveloper.store.presentation.StoreViewModel
import com.angelfgdeveloper.store.presentation.StoreViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

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

        mBinding.fab.setOnClickListener { launchEditFragment() }

        setupRecyclerView()
    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        if (args != null) fragment.arguments = args

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab()
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
    override fun onClick(storeId: Long) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), storeId)

        launchEditFragment(args)
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
//                    mAdapter.update(storeEntity)
                    updateStore(storeEntity)
                }
            }
        })
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { dialogInterface, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWebsite(storeEntity.website)
                }
            }
            .show()
    }

    private fun confirmDelete(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, i ->
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
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun dial(phone: String) {
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

        startIntent(callIntent)
    }

    private fun goToWebsite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_SHORT).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }

            startIntent(websiteIntent)
        }

    }

    private fun startIntent(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null)
            startActivity(intent)
        else
            Toast.makeText(this, R.string.main_error_no_resolve, Toast.LENGTH_SHORT).show()
    }

    /**
     * MainAux
     * */
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
}