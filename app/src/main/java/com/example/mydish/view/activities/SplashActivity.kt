package com.example.mydish.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.mydish.R
import com.example.mydish.databinding.ActivitySplashBinding
import com.example.mydish.utils.extensions.hidingStatusBar
import com.example.mydish.utils.extensions.startAnActivity
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    /*** Present splash screen with the animation of the tvAppName with application name */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        hidingStatusBar()

        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        val splashAnimation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.anim_splash)
        splashBinding.tvAppName.animation = splashAnimation
        setAnimationListener(splashAnimation)
    }

    /**
     * Set animation with animation listener
     * OnAnimationEnd startActivity(Intent(this@SplashActivity, MainActivity::class.java))
     */
    private fun setAnimationListener(splashAnimation: Animation) {
        splashAnimation.let {
            it.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    Timber.d("animation start")
                    animation?.start()
                }

                override fun onAnimationEnd(animation: Animation?) {
                    Timber.d("animation end")
                    Handler(Looper.getMainLooper()).postDelayed({
                        startAnActivity(MainActivity::class.java, null)
                        finish()
                    }, 1000)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    Timber.d("animation repeat")
                }
            })
        }
    }
}