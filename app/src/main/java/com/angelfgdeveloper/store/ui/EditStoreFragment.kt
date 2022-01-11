package com.angelfgdeveloper.store.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.angelfgdeveloper.store.R
import com.angelfgdeveloper.store.core.Resource
import com.angelfgdeveloper.store.data.local.LocalStoreDataSource
import com.angelfgdeveloper.store.data.local.StoreDatabase
import com.angelfgdeveloper.store.data.model.StoreEntity
import com.angelfgdeveloper.store.databinding.FragmentEditStoreBinding
import com.angelfgdeveloper.store.domain.StoreRepositoryImpl
import com.angelfgdeveloper.store.presentation.StoreViewModel
import com.angelfgdeveloper.store.presentation.StoreViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private lateinit var mViewModel: StoreViewModel

    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val repo = StoreRepositoryImpl(
            LocalStoreDataSource(
                StoreDatabase.getDatabase(requireContext()).storeDao()
            )
        )
        mViewModel =
            ViewModelProvider(this, StoreViewModelFactory(repo))[StoreViewModel::class.java]
        mBinding = FragmentEditStoreBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if (id != null && id != 0L) {
            mIsEditMode = true
            getStore(id)
        } else {
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")
        }

        setupActionBar()
        setupTextFields()
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title =
            if (mIsEditMode) getString(R.string.edit_store_title_edit) else getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)
    }

    private fun setupTextFields() {
        with(mBinding) {
            etName.addTextChangedListener { validateFields(tilName) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.ivPhoto)
    }

    private fun getStore(id: Long) {
        mViewModel.getStoreById(id).observe(viewLifecycleOwner, { result ->
            when (result) {
                is Resource.Failure -> {
                    Log.d("EditStoreFragment", "Error")
                }
                is Resource.Loading -> {
                    Log.d("EditStoreFragment", "Cargando")
                }
                is Resource.Success -> {
                    mStoreEntity = result.data
                    if (mStoreEntity != null) setUiStore(mStoreEntity!!)

                }
            }
        })
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding) {
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard()
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if (mStoreEntity != null && validateFields(
                        mBinding.tilPhotoUrl,
                        mBinding.tilPhone,
                        mBinding.tilName
                    )
                ) {

                    with(mStoreEntity!!) {
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    if (mIsEditMode) {
                        mViewModel.updateStore(mStoreEntity!!).observe(this, { result ->
                            when (result) {
                                is Resource.Failure -> {
                                    hideKeyboard()
                                    Log.d("EditStoreFragment", "Error")
                                }
                                is Resource.Loading -> {
                                    Log.d("EditStoreFragment", "Cargando")
                                }
                                is Resource.Success -> {
                                    mActivity?.updateStore(mStoreEntity!!)
                                    hideKeyboard()

                                    Snackbar.make(
                                        mBinding.root,
                                        R.string.edit_store_message_update_success,
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                    mActivity?.onBackPressed()
                                }
                            }
                        })
                    } else {
                        mViewModel.addStore(mStoreEntity!!).observe(this, { result ->
                            when (result) {
                                is Resource.Failure -> {
                                    hideKeyboard()
                                    Log.d("EditStoreFragment", "Error")
                                }
                                is Resource.Loading -> {
                                    Log.d("EditStoreFragment", "Cargando")
                                }
                                is Resource.Success -> {
                                    mStoreEntity!!.id = result.data
                                    mActivity?.addStore(mStoreEntity!!)
                                    hideKeyboard()

                                    Toast.makeText(
                                        mActivity,
                                        R.string.edit_store_message_save_success,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    mActivity?.onBackPressed()
                                }
                            }
                        })
                    }
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean {
        var isValid = true

        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.helper_required)
//                textField.editText?.requestFocus()
                isValid = false
            } else textField.error = null
        }

        if (!isValid) Snackbar.make(
            mBinding.root,
            R.string.edit_store_message_valid,
            Snackbar.LENGTH_SHORT
        ).show()

        return isValid
    }

    private fun validateFields(): Boolean {
        var isValid = true

        if (mBinding.etPhotoUrl.text.toString().trim().isEmpty()) {
            mBinding.tilPhotoUrl.error = getString(R.string.helper_required)
            mBinding.etPhotoUrl.requestFocus()
            isValid = false
        }

        if (mBinding.etPhone.text.toString().trim().isEmpty()) {
            mBinding.tilPhone.error = getString(R.string.helper_required)
            mBinding.etPhone.requestFocus()
            isValid = false
        }

        if (mBinding.etName.text.toString().trim().isEmpty()) {
            mBinding.tilName.error = getString(R.string.helper_required)
            mBinding.etName.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun hideKeyboard() {
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (view != null) {
            imm?.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}