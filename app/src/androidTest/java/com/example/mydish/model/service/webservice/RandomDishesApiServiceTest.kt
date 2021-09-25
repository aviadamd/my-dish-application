package com.example.mydish.model.service.webservice

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.runners.MethodSorters

@SmallTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RandomDishesApiServiceTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

}