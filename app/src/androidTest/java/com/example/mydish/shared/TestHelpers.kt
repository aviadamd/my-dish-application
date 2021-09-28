package com.example.mydish.shared

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.StandardSubjectBuilder
import com.google.common.truth.Subject
import com.google.common.truth.Truth
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import timber.log.Timber

object TestHelpers {

    val getResources: Context = ApplicationProvider.getApplicationContext()

    fun initTimberLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy
            .newBuilder()
            .showThreadInfo(true)
            .methodCount(1)
            .methodOffset(5)
            .tag("")
            .build()

        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                Logger.log(priority, "-$tag", message, t)
            }
        })
    }

    fun asserting(message: String): StandardSubjectBuilder {
        Timber.i("verify that $message")
        return Truth.assertWithMessage(message)
    }

    fun <T>asserting(message: String, value: T): Subject {
        Timber.i("verify that ${message +" "+ value.toString()}")
        return Truth.assertWithMessage(message).that(value)
    }
}