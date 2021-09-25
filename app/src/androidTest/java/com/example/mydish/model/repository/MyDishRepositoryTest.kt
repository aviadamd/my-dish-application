package com.example.mydish.model.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.mydish.MyDishEntityObjects.entityObject1
import com.example.mydish.MyDishEntityObjects.entityObject2
import com.example.mydish.getOrAwaitValue
import com.example.mydish.model.database.MyDishRoomDatabase
import com.google.common.truth.Truth
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
        myDishRepository.deleteMyDishData(entityObject1)
        myRoomDatabase.close()
    }

    @Test
    fun a_insertDishItemToDatabase_verifyExistsInAllDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        Truth.assertThat(allDishes).contains(entityObject1)
    }

    @Test
    fun b_insertDishItemToDatabase_verifyExistsInFavoriteDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val favoriteDishes = myDishRepository.favoriteDishes.asLiveData().getOrAwaitValue()
        Truth.assertThat(favoriteDishes).contains(entityObject1)
    }

    @Test
    fun c_deleteDishItemFromDatabase_verifyDishNotExistsInAllDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        myDishRepository.deleteMyDishData(entityObject1)
        Truth.assertThat(allDishes).isNotEqualTo(entityObject1)
    }

    @Test
    fun d_deleteDishItemFromDatabase_verifyDishNotExistsInFavoriteDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val allDishes = myDishRepository.favoriteDishes.asLiveData().getOrAwaitValue()
        myDishRepository.deleteMyDishData(entityObject1)
        Truth.assertThat(allDishes).isNotEqualTo(entityObject1)
    }

    @Test
    fun e_insertDishItemsFromDatabase_verifySumDishesIsCorrect() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)
        myDishRepository.insertMyDishData(entityObject2)
        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }
}