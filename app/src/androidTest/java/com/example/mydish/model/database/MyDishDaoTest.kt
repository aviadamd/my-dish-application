package com.example.mydish.model.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.filters.SmallTest
import com.example.mydish.shared.TestHelpers.asserting
import com.example.mydish.shared.MyDishEntityObjects.getDishEntity
import com.example.mydish.shared.TestHelpers.initTimberLogger
import com.example.mydish.shared.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runners.MethodSorters
import timber.log.Timber
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
        initTimberLogger()
    }

    @After
    fun tearDownAfterEachTest() = runBlockingTest {
        Timber.i("closing and clear room data base")
        myRoomDatabase.clearAllTables()
        myRoomDatabase.close()
    }

    @Test
    fun a_insertDishItemToDatabase_verifyExistsInAllDishes() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishDao.insertMyDishDetails(dish)

        val allDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        asserting("all dishes dish dao contains", allDishes[0]).isEqualTo(dish)
    }

    @Test
    fun b_insertDishItemToDatabase_verifyExistsInFavoriteDishes() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishDao.insertMyDishDetails(dish)

        val favoriteDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        asserting("favorite dishes dish dao contains", favoriteDishes[0]).isEqualTo(dish)
    }

    @Test
    fun c_deleteDishItemFromDatabase_verifyDishNotExistsInAllDishes() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishDao.insertMyDishDetails(dish)

        myDishDao.deleteMyDishDetails(dish)
        val allDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        assertThat(allDishes).isEmpty()
    }

    @Test
    fun d_deleteDishItemFromDatabase_verifyDishNotExistsInFavoriteDishes() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishDao.insertMyDishDetails(dish)

        myDishDao.deleteMyDishDetails(dish)
        val allDishes = myDishDao.getFavoriteDishesList().asLiveData().getOrAwaitValue()
        assertThat(allDishes).isEmpty()

        val dish1 = getDishEntity(2,false)
        myDishDao.insertMyDishDetails(dish1)
        val allDishes1 = myDishDao.getFavoriteDishesList().asLiveData().getOrAwaitValue()
        assertThat(allDishes1).isEmpty()
    }

    @Test
    fun e_insertDishItemsFromDatabase_verifySumDishesIsCorrect() = runBlockingTest {
        myDishDao.insertMyDishDetails(getDishEntity(1,true))
        myDishDao.insertMyDishDetails(getDishEntity(2,true))
        val allDishes = myDishDao.getAllDishesList().asLiveData().getOrAwaitValue()
        assertThat(allDishes.size).isGreaterThan(1)

        myDishDao.insertMyDishDetails(getDishEntity(3,true))
        myDishDao.insertMyDishDetails(getDishEntity(4,false))
        val allDishes1 = myDishDao.getFavoriteDishesList().asLiveData().getOrAwaitValue()
        assertThat(allDishes1.size).isEqualTo(3)
    }
}