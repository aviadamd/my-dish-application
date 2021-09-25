package com.example.mydish.model.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.filters.SmallTest
import com.example.mydish.MyDishEntityObjects.entityObject1
import com.example.mydish.MyDishEntityObjects.entityObject2
import com.example.mydish.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runners.MethodSorters
import javax.inject.Inject
import javax.inject.Named

@SmallTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MyDishDaoTest {

    private lateinit var myDishDao: MyDishDao
    @get:Rule var hiltRule = HiltAndroidRule(this)
    @get:Rule var instantTaskExecutor = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var myRoomDatabase: MyDishRoomDatabase

    @Before
    fun setup() {
        hiltRule.inject()
        myDishDao = myRoomDatabase.myDishDao()
    }

    @After
    fun tearDownAfterEachTest() = runBlockingTest {
        myDishDao.deleteMyDishDetails(entityObject1)
        myRoomDatabase.close()
    }

    @Test
    fun a_insertDishItemToDatabase_verifyExistsInAllDishes() = runBlockingTest {
        myDishDao.insertMyDishDetails(entityObject1)

        val allDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        assertThat(allDishes).contains(entityObject1)
    }

    @Test
    fun b_insertDishItemToDatabase_verifyExistsInFavoriteDishes() = runBlockingTest {
        myDishDao.insertMyDishDetails(entityObject1)

        val favoriteDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        assertThat(favoriteDishes).contains(entityObject1)
    }

    @Test
    fun c_deleteDishItemFromDatabase_verifyDishNotExistsInAllDishes() = runBlockingTest {
        myDishDao.insertMyDishDetails(entityObject1)

        val allDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        myDishDao.deleteMyDishDetails(entityObject1)
        assertThat(allDishes).isNotEqualTo(entityObject1)
    }

    @Test
    fun d_deleteDishItemFromDatabase_verifyDishNotExistsInFavoriteDishes() = runBlockingTest {
        myDishDao.insertMyDishDetails(entityObject1)

        val allDishes = myDishDao.getFavoriteDishesList().asLiveData().getOrAwaitValue()
        myDishDao.deleteMyDishDetails(entityObject1)
        assertThat(allDishes).isNotEqualTo(entityObject1)
    }

    @Test
    fun e_insertDishItemsFromDatabase_verifySumDishesIsCorrect() = runBlockingTest {
        myDishDao.insertMyDishDetails(entityObject1)
        myDishDao.insertMyDishDetails(entityObject2)
        val allDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        assertThat(allDishes.size).isGreaterThan(1)
    }
}