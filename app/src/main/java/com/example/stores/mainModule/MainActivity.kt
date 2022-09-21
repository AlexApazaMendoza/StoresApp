package com.example.stores.mainModule

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stores.databinding.ActivityMainBinding
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.*
import com.example.stores.common.utils.MainAux
import com.example.stores.common.entities.StoreEntity
import com.example.stores.editModule.EditStoreFragment
import com.example.stores.mainModule.adapters.StoreAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), OnClickListener, MainAux {

    private lateinit var mBinding:ActivityMainBinding
    private lateinit var mAdapter: StoreAdapter
    private lateinit var mGridLayout: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupRecyclerView()

        //setupOkhttp()
        //setupOkhttpInterceptor()

        /*mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etName.text.toString().trim())
            Thread{
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }*/

        mBinding.fab.setOnClickListener {
            launchEditFragment()
        }
    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditStoreFragment()
        if(args != null){
            fragment.arguments = args
        }

        val fragmentManager = supportFragmentManager    // gestor que trae android para controlar los fragmentos
        val fragmentTransaction = fragmentManager.beginTransaction()   // es quien va a decidir como se va a ejecutar

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        hideFab()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreAdapter(mutableListOf(),this)
        mGridLayout = GridLayoutManager(this,resources.getInteger(R.integer.main_columns))
        getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    private fun getStores(){
        doAsync {
            val stores = StoreApplication.database.storeDao().getAllStores()
            uiThread {
                mAdapter.setStores(stores)
            }
        }
    }

    /***
     * OnClickListener Interface
     */
    override fun onClick(storeId: Long) {
        val args = Bundle() // Clave, valor
        args.putLong(getString(R.string.arg_Id), storeId)

        launchEditFragment(args)
    }

    override fun onFavouriteStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                updateStore(storeEntity)
            }
        }
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
                doAsync {
                    StoreApplication.database.storeDao().deleteStore(storeEntity)
                    uiThread {
                        mAdapter.delete(storeEntity)
                    }
                }
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

    /***
     * MainAux Interface
     */
    override fun hideFab(isVisible: Boolean) {
        if(isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(contentView?.windowToken,0)
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)
    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }
/*private fun setupOkhttpInterceptor() {

        var interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        //Metodo 1
        //var okHttpClient = OkHttpClient().interceptors().add(interceptor)
        //Metodo 2
        var okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(interceptor)
            .build()


        var url = "https://reqres.in/api/users?page=2"

        var request: Request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    var myResponse = response.body()?.string()

                    GlobalScope.launch(Dispatchers.Main) {
                        mBinding.etName.setText(myResponse)
                    }

                }
            }

        })
    }

    private fun setupOkhttp() {
        var okHttpClient:OkHttpClient = OkHttpClient()

        var url = "https://reqres.in/api/users?page=2"

        var request:Request = Request.Builder()
            .url(url)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful){
                    var myResponse = response.body()?.string()

                    GlobalScope.launch(Dispatchers.Main) {
                        mBinding.etName.setText(myResponse)
                    }

                }
            }

        })
    }*/

}