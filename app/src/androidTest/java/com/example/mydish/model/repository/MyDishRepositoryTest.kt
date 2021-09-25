package com.example.mydish.model.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.mydish.MyDishEntityObjects.getDishEntity
import com.example.mydish.getOrAwaitValue
import com.example.mydish.model.database.MyDishRoomDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runners.MethodSorters

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
    fun tearDownAfterEachTest() = runBlockingTest{
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
}