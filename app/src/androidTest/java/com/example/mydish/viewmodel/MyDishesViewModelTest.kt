package com.example.mydish.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.mydish.TestHelpers.asserting
import com.example.mydish.getOrAwaitValue
import com.example.mydish.MyDishEntityObjects.getDishEntity
import com.example.mydish.TestHelpers.initTimberLogger
import com.example.mydish.model.database.MyDishRoomDatabase
import com.example.mydish.model.repository.MyDishRepository
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runners.MethodSorters
import timber.log.Timber

@SmallTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MyDishesViewModelTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

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
        initTimberLogger()
    }

    @After
    fun tearDownAfterEachTest() = runBlockingTest {
        Timber.i("closing and clear room data base")
        myRoomDatabase.clearAllTables()
        myRoomDatabase.close()
    }

    @Test
    fun a_testGetAllDishesList() = runBlockingTest {
        myDishViewModel.insert(getDishEntity(1))
        myDishViewModel.insert(getDishEntity(2))
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValue()
        asserting("all dishes size update").that(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun b_testGetFavoriteDishes() = runBlockingTest {
        myDishViewModel.insert(getDishEntity(1))
        myDishViewModel.insert(getDishEntity(2))
        val allDishes = myDishViewModel.favoriteDishes.getOrAwaitValue()
        asserting("favorites dishes size update").that(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun c_testFilteredListDishes() = runBlockingTest {
        val setEntity = getDishEntity(3,"dessert")
        myDishViewModel.insert(setEntity)

        val dishes = myDishViewModel.filteredListDishes(setEntity.type).getOrAwaitValue()
        asserting("all dishes list is added new object").that(dishes.size).isEqualTo(1)
        dishes.forEach {
            asserting("dish id is == 3").that(it.id).isEqualTo(3)
            asserting("dish type is == dessert").that(it.type).isEqualTo("dessert")
        }
    }

    @Test
    fun d_testInsert() = runBlockingTest {
        myDishViewModel.insert(getDishEntity(1))
        myDishViewModel.insert(getDishEntity(2))
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValue()
        asserting("all dishes size update to be greater than one").that(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun e_testUpdate() = runBlockingTest {
        val dish = getDishEntity(1)
        myDishViewModel.insert(dish)
        dish.title = "other"
        myDishViewModel.update(dish)
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValue()
        asserting("all dishes title update to other").that(allDishes[0].title).isEqualTo("other")
    }

    @Test
    fun f_testDelete() = runBlockingTest {
        val dish = getDishEntity(1)

        myDishViewModel.insert(dish)
        asserting("all dishes size update to be 1")
            .that(myDishViewModel.allDishesList.getOrAwaitValue().size)
            .isEqualTo(1)

        myDishViewModel.delete(dish)
        asserting("all dishes size update to be empty")
            .that(myDishViewModel.allDishesList.getOrAwaitValue().size)
            .isEqualTo(0)
    }
}