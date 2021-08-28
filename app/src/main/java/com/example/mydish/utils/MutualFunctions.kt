package com.example.mydish.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mydish.utils.Tags.IMAGE_RESOURCE
import com.facebook.shimmer.ShimmerFrameLayout
import java.io.IOException
import java.util.*

/*** Writing short cut for the toast message */
fun toast(context: Context, print: String) : Toast {
    return Toast.makeText(context, print, Toast.LENGTH_SHORT)
}

fun setShimmer(shimmer: List<ShimmerFrameLayout>, viewToBeVisible: List<View>, delay : Long) {
    Handler(Looper.getMainLooper()).postDelayed({
            shimmer.let { items ->
                items.forEach {
                    it.visibility = View.VISIBLE
                    it.startShimmer()
                    it.hideShimmer()
                    it.visibility = View.GONE
                }
            }
            viewToBeVisible.let { view -> view.forEach { it.visibility = View.VISIBLE } }
        }, delay
    )
}

/*** adjustment */
fun replaceFirstCharToLocalRoot(string: String): String {
    return string.replaceFirstChar { e ->
        if (e.isLowerCase()) {
            e.titlecase(Locale.ROOT)
        } else string
    }
}

/** Implement the listeners to get the bitmap. Load the dish image in the image view **/
fun setPicture(fragment: Fragment,image: String, imageView: ImageView, view: View?, textView: TextView?) {
    try {
        fragment.let {
            Glide.with(it).load(image).centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(@Nullable e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.e(IMAGE_RESOURCE, "Error loading image", e)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Log.i(IMAGE_RESOURCE, "Pass loading image")
                        setPalette(view, resource, textView)
                        return false
                    }
                }).into(imageView)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

/** Implement the listeners to get the bitmap. Load the dish image in the image view **/
fun setPicture(activity: Activity, image: String, imageView: ImageView, view: View?, textView: TextView?) {
    try {
        Glide.with(activity)
            .load(image)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(@Nullable e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.e(IMAGE_RESOURCE, "Error loading image", e)
                    return false
                }

                override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Log.i(IMAGE_RESOURCE, "Pass loading image")
                    setPalette(view, resource, textView)
                    return false
                }
            }).into(imageView)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun setPalette(view: View?, resource: Drawable, textView: TextView?) {
    view?.let {
        Palette.from(resource.toBitmap()).generate { p -> it.setBackgroundColor(p?.lightMutedSwatch?.rgb ?: 0) }
    }
    textView?.let { textView.setTextColor(Color.BLACK) }
}