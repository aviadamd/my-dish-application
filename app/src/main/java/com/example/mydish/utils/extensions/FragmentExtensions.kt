package com.example.mydish.utils.extensions

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.mydish.utils.data.Tags
import com.facebook.shimmer.ShimmerFrameLayout

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
fun Fragment.setPicture(image: String, imageView: ImageView, view: View?, textView: TextView?) {
    Glide.with(this)
        .load(image)
        .apply(requestOptions)
        .transition(withCrossFade())
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                @Nullable e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                Log.e(Tags.IMAGE_RESOURCE, "Error loading image", e)
                return false
            }

            override fun onResourceReady(
                resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                Log.i(Tags.IMAGE_RESOURCE, "Pass loading image")
                setPalette(view, resource, textView)
                return false
            }
        }).into(imageView)
}