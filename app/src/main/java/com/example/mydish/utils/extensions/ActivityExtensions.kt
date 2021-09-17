package com.example.mydish.utils.extensions

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mydish.R
import com.example.mydish.utils.data.Tags
import java.io.IOException

/*** hide the upper phone status bar */
fun Activity.hidingStatusBar() {
    this.let{ activity ->
        activity.window.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.insetsController?.hide(WindowInsets.Type.statusBars())
            } else {
                @Suppress("DEPRECATION")
                it.setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN)
            }
        }
    }
}

/** global variable progressDialog **/
private var mProgressDialog : Dialog? = null

/*** show the spinner load animation */
fun Activity.showSpinnerCustomProgressDialog(activity: Activity) {
    mProgressDialog = Dialog(activity)
    mProgressDialog?.let {
        it.setContentView(R.layout.dialog_custom_progress)
        it.show()
    }
}

/*** hide the spinner load animation */
fun Activity.hideSpinnerCustomProgressDialog() {
    mProgressDialog?.dismiss()
}

fun Context.startAnActivity(clazz: Class<*>, extras: Bundle?) {
    val intent = Intent(this, clazz)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    if (extras != null) intent.putExtras(extras)
    startActivity(intent)
}

/** Implement the listeners to get the bitmap. Load the dish image in the image view **/
fun Activity.setPicture(image: String, imageView: ImageView, view: View?, textView: TextView?) {
    try {
        Glide.with(this)
            .load(image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.e(Tags.IMAGE_RESOURCE, "Error loading image", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Log.i(Tags.IMAGE_RESOURCE, "Pass loading image ${model.toString()}")
                    if (view != null) {
                        setPalette(view, resource, textView)
                    }
                    return false
                }
            })
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    } catch (e: IOException) {
        Log.e(Tags.IMAGE_RESOURCE,"error loading image ${e.message}")
    } catch (e: Exception) {
        Log.e(Tags.IMAGE_RESOURCE,"error loading image ${e.message}")
    }
}