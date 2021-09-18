package com.example.mydish.model.database

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.mydish.getOrAwaitValue
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.model.repository.MyDishRepository
import com.example.mydish.utils.data.Constants
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * @SmallTest unit TODO learn
 * @MediumTest TODO learn
 * @LargeTest TODO learn
 */
@SmallTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MyDishDaoTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    private lateinit var myDishRepository: MyDishRepository
    private lateinit var myRoomDatabase: MyDishRoomDatabase

    @Before
    fun setup() {
        myRoomDatabase = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), MyDishRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        myDishRepository = MyDishRepository(myRoomDatabase.myDishDao())
    }

    @Test
    fun a_insertDishItemToDataBase_verifyExistsInAllDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        assertThat(allDishes).contains(entityObject1)
    }

    @Test
    fun b_insertDishItemToDataBase_verifyExistsInFavoriteDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val favoriteDishes = myDishRepository.favoriteDishes.asLiveData().getOrAwaitValue()
        assertThat(favoriteDishes).contains(entityObject1)
    }

    @Test
    fun c_deleteDishItemFromDataBase_verifyDishNotExistsInAllDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        myDishRepository.deleteMyDishData(entityObject1)
        assertThat(allDishes).isNotEqualTo(entityObject1)
    }

    @Test
    fun d_deleteDishItemFromDataBase_verifyDishNotExistsInFavoriteDishes() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)

        val allDishes = myDishRepository.favoriteDishes.asLiveData().getOrAwaitValue()
        myDishRepository.deleteMyDishData(entityObject1)
        assertThat(allDishes).isNotEqualTo(entityObject1)
    }

    @Test
    fun e_insertDishItemsFromDataBase_verifySumDishesIsCorrect() = runBlockingTest {
        myDishRepository.insertMyDishData(entityObject1)
        myDishRepository.insertMyDishData(entityObject2)
        val allDishes = myDishRepository.allDishesList.asLiveData().getOrAwaitValue()
        assertThat(allDishes.size).isGreaterThan(1)
    }

    @After
    fun tearDownAfterEachTest() = runBlockingTest{
        myDishRepository.deleteMyDishData(entityObject1)
        myRoomDatabase.close()
    }

    private val entityObject1 = MyDishEntity(
        image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
        imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
        title = "my dish title one",
        type = "dessert one",
        category = "other",
        ingredients = "no need",
        cooking_time = "70",
        direction_to_cook = "with love",
        favoriteDish = true,
        id = 1
    )

    private val entityObject2 = MyDishEntity(
        image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
        imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
        title = "my dish title one",
        type = "dessert one",
        category = "other",
        ingredients = "no need",
        cooking_time = "70",
        direction_to_cook = "with love",
        favoriteDish = true,
        id = 2
    )
}