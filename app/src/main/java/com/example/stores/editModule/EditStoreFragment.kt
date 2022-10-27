package com.example.stores.editModule

import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditStoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditStoreFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var id: Long? = null

    private lateinit var mBinding: FragmentEditStoreBinding

    private lateinit var mViewModel: EditStoreViewModel

    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        mViewModel = ViewModelProvider(requireActivity())[EditStoreViewModel::class.java]
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(mBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
            etWebsite.text = storeEntity.website.editable()
        }
    }

    private fun String.editable(): Editable{
        return Editable.Factory.getInstance().newEditable(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentEditStoreBinding.inflate(inflater,container,false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()
        setUpTextFields()
    }

    private fun setUpViewModel() {
        mViewModel.getStoreSelected().observe(viewLifecycleOwner){
            mStoreEntity = it
            if(it != null){
                mIsEditMode = true
                setUiStore(it)
            } else {
                mIsEditMode = false
            }
            setUpActionBar()
        }

        mViewModel.getResult().observe(viewLifecycleOwner){
            mActivity?.hideKeyboard()
            when(it){
                is Long -> {
                    mStoreEntity.id = it
                    mViewModel.setStoreSelected(mStoreEntity)
                    Toast.makeText(mActivity,getString(R.string.edit_store_message_save_success),Toast.LENGTH_LONG).show()
                    mActivity?.onBackPressed()
                }
                is StoreEntity -> {
                    mViewModel.setStoreSelected(mStoreEntity)
                    Snackbar.make(mBinding.root,R.string.edit_store_message_update_success, Snackbar.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }
    }

    private fun setUpActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if (mIsEditMode) getString(R.string.edit_store_title_edit) else getString(R.string.edit_store_title_add)
        setHasOptionsMenu(true)
    }

    private fun setUpTextFields() {
        with(mBinding){
            etName.addTextChangedListener {
                validateFields(tilName)
            }
            etPhone.addTextChangedListener {
                validateFields(tilPhone)
            }
            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun loadImage(url :String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if(validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)){
                    with(mStoreEntity){
                        name = mBinding.etName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWebsite.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    if (mIsEditMode){
                        mViewModel.updateStore(mStoreEntity)
                    } else{
                        mViewModel.saveStore(mStoreEntity)
                    }
                }
                true
            }
            else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }
    private fun validateFields(vararg textsFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textsFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                isValid = false
            } else {
                textField.error = null
            }
        }

        if (!isValid) Snackbar
            .make(mBinding.root, R.string.edit_store_message_valid, Snackbar.LENGTH_SHORT)
            .show()

        return isValid
    }

    override fun onDestroyView() {
        mActivity?.hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)

        mViewModel.setShowFav(true)
        mViewModel.setResult(Any())
        setHasOptionsMenu(false)
        super.onDestroy()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditStoreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun nenstance(param1: String, param2: String) =
            EditStoreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}