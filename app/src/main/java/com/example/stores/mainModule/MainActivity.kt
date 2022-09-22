package com.example.stores.mainModule

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stores.databinding.ActivityMainBinding
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.utils.MainAux
import com.example.stores.common.entities.StoreEntity
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.adapters.StoreAdapter
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), OnClickListener,MainAux {

    private lateinit var mBinding:ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    //MVVM
    private lateinit var mViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpViewModel()
        setupRecyclerView()

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }
    }

    private fun setUpViewModel() {
        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mViewModel.getStores().observe(this) { stores ->
            mAdapter.setStores(stores)
        }

        mEditStoreViewModel = ViewModelProvider(this)[EditStoreViewModel::class.java]
        mEditStoreViewModel.getShowFav().observe(this){
            if(it) mBinding.fab.show() else mBinding.fab.hide()
        }
        mEditStoreViewModel.getStoreSelected().observe(this){
            mAdapter.add(it)
        }
    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        mEditStoreViewModel.setShowFav(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)

        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager    // gestor que trae android para controlar los fragmentos
        val fragmentTransaction = fragmentManager.beginTransaction()   // es quien va a decidir como se va a ejecutar

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(),this)
        mGridLayout = GridLayoutManager(this,resources.getInteger(R.integer.main_columns))

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    /***
     * OnClickListener Interface
     */
    override fun onClick(storeEntity: StoreEntity) {
        launchEditFragment(storeEntity)
    }

    override fun onFavouriteStore(storeEntity: StoreEntity) {
        mViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, i ->
                when(i){
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWebsite(storeEntity.website)
                    else -> {}
                }
            }
            .show()
    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                mViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel,null)
            .show()
    }

    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWebsite(website:String){
        if(website.isEmpty()){
            Toast.makeText(this, R.string.main_error_no_website,Toast.LENGTH_SHORT).show()
        } else{
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        }
    }
    private fun startIntent(intent: Intent){
        if(intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        } else{
            Toast.makeText(this, R.string.main_error_no_resolve,Toast.LENGTH_SHORT).show()
        }
    }

    override fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(contentView?.windowToken,0)
    }

}