package com.example.mydish.model.application

import android.app.Application
import com.example.mydish.model.repository.MyDishRepository
import com.example.mydish.model.database.MyDishRoomDatabase
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * MyDishApplication sets in the Manifest file,  <application android:name=".application.MyDishApplication"
 * This class will init the room data base with the class MyDishRoomDataBase.getDataBase()
 */
@HiltAndroidApp
class MyDishApplication : Application() {

    /**
     * Using by lazy so the database and the repository are only created when they're needed
     * rather than when the application starts.
     * The "lazy" keyword used for creating a new instance that uses the specified initialization function
     * and the default thread-safety mode [LazyThreadSafetyMode.SYNCHRONIZED].
     * If the initialization of a value throws an exception, it will attempt to reinitialize the value at next access.
     *
     * Note that the returned instance uses itself to synchronize on.
     * Do not synchronize from external code on the returned instance as it may cause accidental deadlock.
     * Also this behavior can be changed in the future.
     */
    private val myDishRoomDatabase by lazy { MyDishRoomDatabase.getDatabase((this@MyDishApplication)) }

    /**
     * Get the repository variable instance , this is called from MyDishApplication
     * MyDishApplication as val repository by lazy { MyDishRepository(database.myDishDao()) }
     * And each Fragment or Activity will delegate the instance from the ViewModel classes that need
     * the data for presentation IN THE VIEWS off the application
     **/
    val myDishRepository by lazy { MyDishRepository(myDishRoomDatabase.myDishDao()) }

    override fun onCreate() {
        super.onCreate()

        val formatStrategy: FormatStrategy = PrettyFormatStrategy
            .newBuilder()
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

}