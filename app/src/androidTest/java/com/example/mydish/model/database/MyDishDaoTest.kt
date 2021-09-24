package com.example.mydish.model.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.filters.SmallTest
import com.example.mydish.getOrAwaitValue
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.data.Constants
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runners.MethodSorters

@SmallTest
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MyDishDaoTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    private lateinit var myDishDao: MyDishDao
    lateinit var myRoomDatabase: MyDishRoomDatabase

    @Before
    fun setup() {
        myRoomDatabase = Room
            .inMemoryDatabaseBuilder(getApplicationContext(), MyDishRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        myDishDao = myRoomDatabase.myDishDao()
    }

    @After
    fun tearDownAfterEachTest() = runBlockingTest{
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