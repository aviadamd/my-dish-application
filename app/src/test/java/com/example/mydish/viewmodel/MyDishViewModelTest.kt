package com.example.mydish.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.mydish.MainCoroutineRule
import com.example.mydish.getOrAwaitValues
import com.example.mydish.model.database.MyDishRoomDatabase
import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.model.repository.MyDishRepository
import com.example.mydish.utils.data.Constants
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.runners.MethodSorters

@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MyDishViewModelTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var myRoomDatabase: MyDishRoomDatabase
    private lateinit var myDishRepository: MyDishRepository
    private lateinit var myDishViewModel: MyDishViewModel

    @Before
    fun setup() {
        myRoomDatabase = Room
            .inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                MyDishRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        myDishRepository = MyDishRepository(myRoomDatabase.myDishDao())
        myDishViewModel = MyDishViewModel(myDishRepository)
    }

    @Test
    fun testGetAllDishesList() {
        myDishViewModel.insert(entityObject1)
        myDishViewModel.insert(entityObject2)
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValues()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun testGetFavoriteDishes() {
        myDishViewModel.insert(entityObject1)
        myDishViewModel.insert(entityObject2)
        val allDishes = myDishViewModel.favoriteDishes.getOrAwaitValues()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun testFilteredListDishes() {
        myDishViewModel.insert(entityObject1)
        myDishViewModel.insert(entityObject2)
        val allDishes = myDishViewModel.filteredListDishes(Constants.ALL_ITEMS).getOrAwaitValues()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun testInsert() {
        myDishViewModel.insert(entityObject1)
        myDishViewModel.insert(entityObject2)
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValues()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun testUpdate() {
        myDishViewModel.insert(entityObject1)
        entityObject1.title = "other"
        myDishViewModel.update(entityObject1)
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValues()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun testDelete() {
        myDishViewModel.insert(entityObject1)
        val allDishes1 = myDishViewModel.allDishesList.getOrAwaitValues()
        Truth.assertThat(allDishes1.size).isGreaterThan(1)

        myDishViewModel.delete(entityObject1)
        val allDishes2 = myDishViewModel.allDishesList.getOrAwaitValues()
        Truth.assertThat(allDishes2.size).isAtLeast(1)
    }

    @After
    fun tearDownAfterEachTest() {
        myDishViewModel.delete(entityObject1)
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