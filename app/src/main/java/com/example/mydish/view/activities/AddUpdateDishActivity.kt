package com.example.mydish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.provider.Settings
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mydish.R
import com.example.mydish.model.application.MyDishApplication
import com.example.mydish.databinding.ActivityAddUpdatedDishBinding
import com.example.mydish.databinding.DialodCustomListBinding
import com.example.mydish.databinding.DialogCustomImageSelectionBinding
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.data.Constants
import com.example.mydish.utils.data.Constants.DISH_CATEGORY
import com.example.mydish.utils.data.Constants.DISH_COOKING_TIME
import com.example.mydish.utils.data.Constants.DISH_TYPE
import com.example.mydish.view.adapters.CustomListItemAdapter
import com.example.mydish.utils.extensions.hidingStatusBar
import com.example.mydish.utils.extensions.setPicture
import com.example.mydish.utils.extensions.toast
import com.example.mydish.viewmodel.MyDishViewModel
import com.example.mydish.viewmodel.MyDishViewModelFactory
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    /** single late init var view bind for the ActivityAddUpdatedDishBinding **/
    private lateinit var mBinding : ActivityAddUpdatedDishBinding

    /** image path for external and internal point image path**/
    private var mImagePath : String = ""

    /** custom for xml view dialog instance late init **/
    private lateinit var mCustomListDialog : Dialog

    /** create instance var from dish entity data class **/
    private var mMyDishEntity : MyDishEntity? = null

    /**
     * To create the ViewModel we used the viewModels delegate,
     * passing in an instance of our MyDishViewModelFactory.
     * This is constructed based on the repository retrieved from the MyDishApplication.
     */
    private val mMyDishViewModel : MyDishViewModel by viewModels {
        MyDishViewModelFactory((application as MyDishApplication).myDishRepository)
    }

    /**
     * On create set the activity_add_updated_dish presentation,
     * bind the activity from the mBinding class member
     * for reuses call on the others method in the AddUpdateDishActivity
     *
     * Set up the View.OnClickListener on the edit text in the page
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_updated_dish)
        mBinding = ActivityAddUpdatedDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        Timber.i("onCreate add/update dish activity launch")

        /*** hide the status bar */
        hidingStatusBar()
        /*** verify if the activity as extra carry string data from MyDishAdapter */
        getExtraDishDetails()
        /** Set up the action bar with back button **/
        setUpActionBar()
        /*** If the entity is not empty and the dish id is populated then init the image presentation with the dish data */
        presentDataDishInTheUi()
        /*** on click logic on each click event in the page */
        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnUpdateDishData.setOnClickListener(this)
    }

    /** View.OnClickListener interface definition for callback to be invoked when a view is clicked. */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_add_dish_image -> {
                customImageSelectionDialog()
                return
            }
            R.id.et_type -> {
                customItemListDialog(
                    resources.getString(R.string.title_select_dish_type),
                    resources.getStringArray(R.array.dishTypes).toList(),
                    DISH_TYPE
                )
                return
            }
            R.id.et_category -> {
                customItemListDialog(
                    resources.getString(R.string.title_select_dish_category),
                    resources.getStringArray(R.array.dishCategory).toList(),
                    DISH_CATEGORY
                )
                return
            }
            R.id.et_cooking_time -> {
                customItemListDialog(
                    resources.getString(R.string.title_select_dish_cooking_time),
                    resources.getStringArray(R.array.dishCookingTime).toList(),
                    DISH_COOKING_TIME
                )
                return
            }

            R.id.btn_update_dish_data -> {
                /** clean empty spaces from side string chars **/
                val title = mBinding.etTitle.text.toString().trim { it <= ' ' }
                val type = mBinding.etType.text.toString().trim { it <= ' ' }
                val category = mBinding.etCategory.text.toString().trim { it <= ' ' }
                val ingredients = mBinding.etIngredients.text.toString().trim { it <= ' ' }
                val cookingTimeInMinutes = mBinding.etCookingTime.text.toString().trim { it <= ' ' }
                val cookingDirection = mBinding.etDirectionToCook.text.toString().trim { it <= ' ' }

                when {

                    /** toast messages on empty texts **/
                    isEmpty(mImagePath) -> toast(this@AddUpdateDishActivity, resources.getString(R.string.err_msg_select_dish_image)).show()
                    isEmpty(title) -> toast(this@AddUpdateDishActivity, resources.getString(R.string.err_msg_enter_dish_title)).show()
                    isEmpty(type) -> toast(this@AddUpdateDishActivity, resources.getString(R.string.err_msg_select_dish_type)).show()
                    isEmpty(category) -> toast(this@AddUpdateDishActivity, resources.getString(R.string.err_msg_select_dish_category)).show()
                    isEmpty(ingredients) -> toast(this@AddUpdateDishActivity, resources.getString(R.string.err_msg_enter_dish_ingredients)).show()
                    isEmpty(cookingTimeInMinutes) -> toast(this@AddUpdateDishActivity, resources.getString(R.string.err_msg_select_dish_cooking_time)).show()
                    isEmpty(cookingDirection) -> toast(this@AddUpdateDishActivity, resources.getString(R.string.err_msg_enter_dish_cooking_instructions)).show()

                    else -> {
                        /*** if all edit text are full, update or insert new data to room data base */
                        enterDishDataToRoom(title, type, category, ingredients, cookingTimeInMinutes, cookingDirection)
                        /*** this will close the activity and navigate to main activity with all dishes fragment */
                        finish()
                    }
                }
            }
        }
    }

    /**
     * @param requestCode
     * The integer request code originally supplied to
     * startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode
     * The integer result code returned by the child activity through its setResult().
     * @param data
     * An Intent, which can return result data to the caller
     * (various data can be attached to Intent "extras").
     */
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var isLoadingImage = false
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                CAMERA -> {
                    data?.extras?.let {
                        mImagePath = saveImageToInternalStorage(it.get("data") as Bitmap)
                        Glide.with(this)
                            .load(mImagePath)
                            .centerCrop()
                            .into(mBinding.ivDishImage)
                        isLoadingImage = true
                    }
                }
                GALLERY -> {
                    data?.let {
                        Glide.with(this)
                            .load(it.data)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                                    Timber.e("Error loading image from gallery task", e)
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                                    resource?.let { drawable ->
                                        mImagePath = saveImageToInternalStorage(drawable.toBitmap())
                                    }
                                    return false
                                }
                            }).into(mBinding.ivDishImage)
                        isLoadingImage = true
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled","Cancelled")
        }

        // Replace the add icon with edit icon once the image is selected.
        if (isLoadingImage) {
            mBinding.ivAddDishImage.setImageDrawable(getDrawable(this, R.drawable.ic_vector_edit))
        }
    }

    /*** if all edit text are full, update or insert new data*/
    private fun enterDishDataToRoom(
        title: String, type: String, category: String,
        ingredients: String, cookingTimeInMinutes: String,
        cookingDirection: String) {

        var dishID = 0
        var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
        var favoriteDish = false

        //@PrimaryKey(autoGenerate = true) val id: Int = 0
        //if not 0 it means the dish have id with new id number
        mMyDishEntity?.let {
            if (it.id != 0) {
                dishID = it.id
                imageSource = it.imageSource
                favoriteDish = it.favoriteDish
            }
        }

        //Init MyDish Object data and insert to MyDishEntity room data base
        val dishEntity = MyDishEntity(
            mImagePath,
            imageSource,
            title,
            type,
            category,
            ingredients,
            cookingTimeInMinutes,
            cookingDirection,
            favoriteDish,
            dishID
        )

        //if has new entry, when the dish id is == 0
        //else create update for exist data
        if(dishID == 0) {
            /** create list instance from MyDishEntity **/
            mMyDishViewModel.insert(dishEntity)
            toast(this@AddUpdateDishActivity, "${dishEntity.title} is added to yours dishes").show()
        } else {
            mMyDishEntity?.let {
                if(it.title == title && it.type == type && it.category == category && it.cooking_time == cookingTimeInMinutes) {
                    toast(this@AddUpdateDishActivity, "${it.title} didn't have changes").show()
                } else {
                    mMyDishViewModel.update(dishEntity)
                    toast(this@AddUpdateDishActivity, "${dishEntity.title} updated successfully").show()
                }
            }
        }
    }

    /*** verify if the activity as extra carry string data that will also init the mMyDishEntity */
    private fun getExtraDishDetails() {
        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS)) {
            mMyDishEntity = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }
    }

    /*** If the entity is not empty and the dish id is populated then init the image presentation with the dish data */
    private fun presentDataDishInTheUi() {
        mMyDishEntity?.let { it ->
            if (it.id != 0) {
                mImagePath = it.image
                setPicture(mImagePath,mBinding.ivDishImage, mBinding.flSelectImage,null)
                //Set the dish presentation data to page
                listOf(
                    Pair(mBinding.etTitle, it.title),
                    Pair(mBinding.etType, it.type),
                    Pair(mBinding.etCategory, it.category),
                    Pair(mBinding.etIngredients, it.ingredients),
                    Pair(mBinding.etCookingTime, it.cooking_time),
                    Pair(mBinding.etDirectionToCook, it.direction_to_cook),
                    Pair(mBinding.btnUpdateDishData, resources.getString(R.string.label_update_dish))
                ).forEach { data -> data.first.text = data.second }
            }
        }
    }

    /**
     * Set up the action bar with back button
     * Set the page title with edit/add dish
     **/
    private fun setUpActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)

        if (mMyDishEntity != null && mMyDishEntity!!.id != 0) {
            supportActionBar?.let { it.title = resources.getString(R.string.title_edit_dish) }
        } else supportActionBar?.let { it.title = resources.getString(R.string.title_add_dish) }

        //Set the action bar items presentations on the screen
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        //Handel navigation back with the arrow and the back button from the phone
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /*** A function to launch the custom image selection dialog. */
    private fun customImageSelectionDialog() {
        val dialog = Dialog(this@AddUpdateDishActivity)
        val binding = DialogCustomImageSelectionBinding.inflate(layoutInflater)

        /** Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen **/
        dialog.setContentView(binding.root)

        //Handel camera permissions
        binding.tvCamera.setOnClickListener {
            setPermissionsDialogCamera()
            dialog.dismiss()
        }

        //Handel gallery permissions
        binding.tvGallery.setOnClickListener {
            setPermissionsDialogGallery()
            dialog.dismiss()
        }

        //Start the dialog and display it on screen.
        dialog.show()
    }

    /**
     * A function to set the selected item to the view.
     * on each click open the dialog
     * @param item - Selected Item.
     * @param selection - Identify the selection and set it to the view accordingly.
     */
    fun selectedListItem(item: String, selection: String) {
        when(selection) {
            DISH_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            DISH_COOKING_TIME -> {
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
            DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
        }
    }

    /**
     * A function used to show the alert dialog when the permissions
     * are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(R.string.error_on_permissions_off)
            .setPositiveButton("GO TO SETTINGS") { _,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", packageName, null)
                    startActivity(intent)
                } catch (e : ActivityNotFoundException) {
                    toast(this@AddUpdateDishActivity, resources.getString(R.string.settings_dialog_error)).show()
                }
            }.setNegativeButton("Cancel") { dialog,_ ->
                dialog.dismiss()
            }.show()
    }

    /**
     * permissions dialog for the
     * type of storage the need to navigate to
     * can be to phone gallery or camera
     * Dexter is an Android library that simplifies the process of requesting permissions at runtime.
     **/
    @Suppress("DEPRECATION")
    private fun setPermissionsDialogCamera() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
            //Handle the camera permission
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.areAllPermissionsGranted().let {
                    startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE), CAMERA)
                    toast(this@AddUpdateDishActivity, resources.getString(R.string.camera_permissions_on)).show()
                }
            }

            //show the alert dialog when the permissions are denied
            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }

    /**
     * permissions dialog for
     * the type of storage the need to navigate to
     * can be to phone storage
     * Dexter is an Android library that simplifies the process of requesting permissions at runtime.
     **/
    @Suppress("DEPRECATION")
    private fun setPermissionsDialogGallery() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {

                //start activity on the phone gallery
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startActivityForResult(Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI), GALLERY)
                }

                //show the toast message on denied storage permission
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    toast(this@AddUpdateDishActivity, resources.getString(R.string.storage_off)).show()
                }

                //show the alert dialog when the permissions are denied
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest, token: PermissionToken) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
    }

    /**
     * A function to save a copy of an image to internal storage for FavDishApp to use.
     * @param bitmap
     * The Mode Private here is
     * File creation mode: the default mode, where the created file can only
     * be accessed by the calling application (or all applications sharing the
     * same user ID).
     */
    private fun saveImageToInternalStorage(bitmap: Bitmap) : String {
        // Get the context wrapper instance
        val wrapper = ContextWrapper(this.applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream : OutputStream = FileOutputStream(file)
            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            // Flush the stream
            stream.flush()
            // Close stream
            stream.close()
            Timber.i("save ${file.name} to storage")

        } catch (e : IOException) {
            Timber.e("error save image to storage ${e.message}")
        }
        // Return the saved image absolute path
        return file.absolutePath
    }

    /**
     * A function to launch the custom list dialog.
     * @param title - Define the title at runtime according to the list items.
     * @param itemsList - List of items to be selected.
     * @param selection - By passing this param you can identify the list item selection.
     */
    private fun customItemListDialog(title: String, itemsList : List<String>, selection : String) {
        mCustomListDialog = Dialog(this@AddUpdateDishActivity)
        val binding : DialodCustomListBinding = DialodCustomListBinding.inflate(layoutInflater)
        // Set the screen content from a layout resource.
        // The resource will be inflated, adding all top-level views to the screen.*/
        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text = title
        // Set the LayoutManager that this RecyclerView will use.
        binding.rvList.layoutManager = LinearLayoutManager(this@AddUpdateDishActivity)
        // Adapter class is initialized and list is passed in the param.
        val adapter = CustomListItemAdapter(this@AddUpdateDishActivity, itemsList, selection)
        // Adapter instance is set to the recyclerview to inflate the items.
        binding.rvList.adapter = adapter
        //Start the dialog and display it on screen.
        mCustomListDialog.show()
    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
        private const val IMAGE_DIRECTORY = "MyDishImages"
    }
}
