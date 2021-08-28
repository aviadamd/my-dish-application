package com.example.mydish.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.example.mydish.R
import com.example.mydish.databinding.ActivityMainBinding
import com.example.mydish.api.notifications.NotificationManager
import com.example.mydish.utils.Constants
import com.example.mydish.utils.Constants.DURATION
import com.example.mydish.utils.Tags
import com.example.mydish.utils.extensions.hidingStatusBar
import java.util.concurrent.TimeUnit

/**
 * This activity hold the 3 fragments of lower bar navigation
 * R.id.navigation_all_dishes,
 * R.id.navigation_favorite_dishes,
 * R.id.navigation_random_dish
 *
 * This class hold the notification logic also
 */
class MainActivity : AppCompatActivity() {

    /** Create an global variable for view binding **/
    private lateinit var mBinding : ActivityMainBinding

    /** Make navController variable as global variable **/
    private lateinit var mNavController: NavController

    /**
     * Initialize the mBinding variable
     * Create navigation controller
     * Passing each menu ID as a set of Ids because each
     * menu should be considered as top level destinations.
     * Set up bar navigation controller
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** init the mBinding variable**/
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*** hide the upper phone status bar */
        hidingStatusBar()

        /** create navigation controller **/
        mNavController = findNavController(R.id.nav_host_fragment)

        /** create the menu app bar lower navigation **/
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_dishes,
                R.id.navigation_favorite_dishes,
                R.id.navigation_random_dish
            )
        )

        /** set up the navigation controller **/
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        mBinding.navView.setupWithNavController(mNavController)

        /*** pass the notification id as intent extra to handle the code when user is navigate in the app with notification. */
        passNotificationToNavigationComponent()
        /*** check that the activity has Constants.NOTIFICATION_ID on every 1 hour the application will send new recipe with notification */
        startWorkManager(NOTIFICATION_TIME_OUT, TimeUnit.HOURS)
    }

    /**
     * Add the navigate up code and pass the required params.
     * This will navigate the user from DishDetailsFragment to AllDishesFragment when user
     * clicks on the home back button.
     */
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    /**
     * Enqueue a work,
     * ExistingPeriodicWorkPolicy.KEEP ->
     * Means that if this work already exists, it will be kept
     * if the value is ExistingPeriodicWorkPolicy.REPLACE, then the work will be replaced
     * After click the notification element the notification will lead to random dish fragment
     */
    @Suppress("SameParameterValue")
    private fun startWorkManager(onEvery : Long, unit: TimeUnit) {
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "My Dish notification work",
            ExistingPeriodicWorkPolicy.KEEP,
            createWorkRequest(onEvery, unit)
        )
    }

    /**
     * You can use any of the work request builder that are available to use.
     * PeriodicWorkRequestBuilder to execute the code periodically.
     *
     * The minimum time can set is 15 minutes.
     * Can also set the TimeUnit as . for example SECONDS, MINUTES, or HOURS.
     */
    private fun createWorkRequest(onEvery : Long, unit: TimeUnit) =
        PeriodicWorkRequestBuilder<NotificationManager>(onEvery, unit)
            .setConstraints(createConstraints())
            .build()

    /**
     * Constraints ensure that work is delay/deferred until optimal conditions are met.
     * A specification of the requirements that need to be met before a WorkRequest can run.
     * By default, WorkRequests do not have any requirements and can run immediately.
     * By adding requirements, you can make sure that work only runs in certain situations
     * for example, when you have an un metered network and are charging.
     */
    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)
        .build()

    /***  Create a function to hide the BottomNavigationView with animation. */
    fun hideBottomNavigationView() {
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(mBinding.navView.height.toFloat()).duration = DURATION
        mBinding.navView.visibility = View.GONE
    }

    /*** Create a function to show the BottomNavigationView with Animation. */
    fun showBottomNavigationView() {
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(0f).duration = DURATION
        mBinding.navView.visibility = View.VISIBLE
    }

    /**
     * Pass the notification id as intent extra to handle the code when user is navigate in the app with notification.
     * Set the navigation to random dish page mBinding.navView.selectedItemId = R.id.navigation_random_dish
     */
    private fun passNotificationToNavigationComponent() {
        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            Log.i(Tags.NOTIFICATIONS, "$notificationId")
            mBinding.navView.selectedItemId = R.id.navigation_random_dish
        }
    }

    /*** notification time out */
    private companion object {
        const val NOTIFICATION_TIME_OUT : Long = 1
    }
}