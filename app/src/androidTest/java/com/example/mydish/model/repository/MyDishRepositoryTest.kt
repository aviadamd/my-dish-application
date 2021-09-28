package com.example.mydish.model.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.mydish.shared.MyDishEntityObjects.getDishEntity
import com.example.mydish.shared.getOrAwaitValue
import com.example.mydish.model.database.MyDishRoomDatabase
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runners.MethodSorters
import timber.log.Timber
import kotlin.streams.toList

@SmallTest
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MyDishRepositoryTest {

    @get:Rule var instantTaskExecutor = InstantTaskExecutorRule()
    private lateinit var myDishRepository: MyDishRepository
    private lateinit var myRoomDatabase: MyDishRoomDatabase

    @Before
    fun setup() {
        myRoomDatabase = Room
            .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), MyDishRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        myDishRepository = MyDishRepository(myRoomDatabase.myDishDao())
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
        myDishRepository.insertMyDishData(dish)

        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        assertThat(allDishes).contains(dish)
    }

    @Test
    fun b_insertDishItemToDatabase_verifyExistsInFavoriteDishes() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishRepository.insertMyDishData(dish)

        val favoriteDishes = myDishRepository.favoriteDishes.asLiveData().getOrAwaitValue()
        assertThat(favoriteDishes).contains(dish)
    }

    @Test
    fun c_deleteDishItemFromDatabase_verifyDishNotExistsInAllDishes() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishRepository.insertMyDishData(dish)

        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        myDishRepository.deleteMyDishData(dish)
        assertThat(allDishes).isNotEqualTo(dish)
    }

    @Test
    fun d_deleteDishItemFromDatabase_verifyDishNotExistsInFavoriteDishes() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishRepository.insertMyDishData(dish)

        val allDishes = myDishRepository.favoriteDishes.asLiveData().getOrAwaitValue()
        myDishRepository.deleteMyDishData(dish)
        assertThat(allDishes).isNotEqualTo(dish)
    }

    @Test
    fun e_insertDishItemsFromDatabase_verifySumDishesIsCorrect() = runBlockingTest {
        myDishRepository.insertMyDishData(getDishEntity(1))
        myDishRepository.insertMyDishData(getDishEntity(2))
        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun f_insertDishItemsFromDatabase_verifySumDishesIsCorrect() = runBlockingTest {
        myDishRepository.insertMyDishData(getDishEntity(1,true))
        myDishRepository.insertMyDishData(getDishEntity(2,true))
        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        assertThat(allDishes).hasSize(2)
        val numberOfDishes = allDishes.stream().filter { it.favoriteDish }.count().toInt()
        assertThat(numberOfDishes).isEqualTo(2)
    }

    @Test
    fun g_insertDishItemsFromDatabase_verifyDishUpdated() = runBlockingTest {
        val dish = getDishEntity("50","my dish","dessert",1,true)
        myDishRepository.insertMyDishData(dish)
        myDishRepository.allDishesList.asLiveData().getOrAwaitValue().forEach {
            assertThat(it.id).isEqualTo(dish.id)
            assertThat(it.title).isEqualTo(dish.title)
            assertThat(it.type).isEqualTo(dish.type)
            assertThat(it.cooking_time).isEqualTo(dish.cooking_time)
            assertThat(it.favoriteDish).isEqualTo(dish.favoriteDish)
        }
    }
}