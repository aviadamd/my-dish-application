package com.example.mydish.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.mydish.MyDishEntityObjects.entityObject1
import com.example.mydish.R
import com.example.mydish.databinding.FragmentRandomDishBinding
import com.example.mydish.launchFragmentInHiltContainer
import com.example.mydish.view.fragments.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import javax.net.ssl.ExtendedSSLSession

@MediumTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
class AllDishesFragmentTest {

    @get:Rule var hiltRule = HiltAndroidRule(this)
    @get:Rule var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun launch_randomDishFragment() {
        val navController = Mockito.mock(NavController::class.java)

        launchFragmentInHiltContainer<RandomDishFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        Espresso.onView(withId(R.id.iv_favorite_dish)).perform(ViewActions.longClick())
        Thread.sleep(2000)
    }
}