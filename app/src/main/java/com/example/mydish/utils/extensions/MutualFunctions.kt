package com.example.mydish.utils.extensions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.mydish.utils.data.Tags.IMAGE_RESOURCE
import java.io.IOException
import java.util.*

/*** Writing short cut for the toast message */
fun toast(context: Context, print: String) : Toast {
    return Toast.makeText(context, print, Toast.LENGTH_SHORT)
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
fun setPicture(fragment: Fragment, image: String, imageView: ImageView, platte: Boolean, textView: TextView?) {
    try {
        Glide.with(fragment)
            .load(image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.e(IMAGE_RESOURCE, "error loading image", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Log.i(IMAGE_RESOURCE, "Pass loading image ${model.toString()}")
                    if (platte) {
                        setPalette(fragment.view, resource, textView)
                    }
                    return false
                }
            })
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    } catch (e : IOException) {
        Log.e(IMAGE_RESOURCE,"error loading image ${e.message}")
    }
}

fun setPalette(view: View?, resource: Drawable, textView: TextView?) {
    if (view != null) {
        Palette.from(resource.toBitmap()).generate { p ->
            view.setBackgroundColor(p?.lightVibrantSwatch?.rgb ?: 0)
        }
    }
    textView?.setTextColor(Color.BLACK)
}