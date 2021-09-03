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
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.mydish.utils.data.Tags.IMAGE_RESOURCE
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

val mRequestOptions = RequestOptions().timeout(300).centerCrop()

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
fun setPicture(fragment: Fragment, image: String, imageView: ImageView, view: View?, textView: TextView?) {
    runBlocking{
        val imageLoadingJob: Job = this.launch {
            Glide.with(fragment)
                .load(image)
                .apply(mRequestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        @Nullable e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        Log.e(IMAGE_RESOURCE, "error loading image", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        Log.i(IMAGE_RESOURCE, "pass loading image")
                        setPalette(view, resource, textView)
                        return false
                    }
                }).into(imageView)
        }

        imageLoadingJob.let { job ->
            job.invokeOnCompletion {
                Log.i(IMAGE_RESOURCE, "shimmer finished successes ${job.isCompleted}")
            }
        }
    }
}

fun setPalette(view: View?, resource: Drawable, textView: TextView?) {
    view?.let {
        Palette.from(resource.toBitmap()).generate {
                p -> it.setBackgroundColor(p?.lightVibrantSwatch?.rgb ?: 0)
        }
    }
    textView?.let { textView.setTextColor(Color.BLACK) }
}