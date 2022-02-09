package com.example.mydish.utils.extensions

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import timber.log.Timber
import java.io.IOException

/*** Set shimmer animation */
fun Fragment.setShimmer(shimmer: ShimmerFrameLayout, viewToBeVisible: View, delay : Long) {
    setShimmer(listOf(shimmer), listOf(viewToBeVisible), delay)
}

/*** Set shimmer animation */
fun Fragment.setShimmer(shimmer: List<ShimmerFrameLayout>, viewToBeVisible: List<View>, delay : Long) {
    this.let {
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
        }, delay)
    }
}

/*** present the drawable */
fun Fragment.setImageDrawable(imageView: ImageView, drawableId: Int) {
    imageView.setImageDrawable(ContextCompat.getDrawable(this.requireActivity(), drawableId))
}

/** Implement the listeners to get the bitmap. Load the dish image in the image view **/
fun Fragment.setPicture(
    image: String, imageView: ImageView, view: View?, textView: TextView?): Boolean {
    var imageLoading: Boolean
    try {
        Glide.with(this)
            .load(image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Timber.e("Error loading image", e)
                    imageLoading = false
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Timber.i( "Pass loading image ${model.toString()}")
                    if (view != null) {
                        setPalette(view, resource, textView)
                    }
                    imageLoading = true
                    return false
                }
            })
            .centerCrop()
            .transition(withCrossFade())
            .into(imageView)
        imageLoading = true
    } catch (e: Exception) {
        when(e) {
            is IOException -> Timber.e("io exception loading image ${e.message}")
            is NullPointerException -> Timber.e("null exception loading image ${e.message}")
            else -> Timber.e("exception loading image ${e.message}")
        }
        imageLoading = false
    }
    return imageLoading
}