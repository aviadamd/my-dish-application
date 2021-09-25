package com.example.mydish.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.mydish.getOrAwaitValue
import com.example.mydish.MyDishEntityObjects.entityObject1
import com.example.mydish.MyDishEntityObjects.entityObject2
import com.example.mydish.MyDishEntityObjects.setDishEntity
import com.example.mydish.model.database.MyDishRoomDatabase
import com.example.mydish.model.repository.MyDishRepository
import com.google.common.truth.Truth
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
    }

    @After
    fun tearDownAfterEachTest() = runBlockingTest {
        myRoomDatabase.close()
    }

    @Test
    fun a_testGetAllDishesList() = runBlockingTest {
        myDishViewModel.insert(entityObject1)
        myDishViewModel.insert(entityObject2)
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValue()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun b_testGetFavoriteDishes() = runBlockingTest {
        myDishViewModel.insert(entityObject1)
        myDishViewModel.insert(entityObject2)
        val allDishes = myDishViewModel.favoriteDishes.getOrAwaitValue()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun c_testFilteredListDishes() = runBlockingTest {
        val setEntity = setDishEntity(3,"dessert")
        myDishViewModel.insert(setEntity)

        val dishes = myDishViewModel.filteredListDishes(setEntity.type).getOrAwaitValue()
        Truth.assertWithMessage("all dishes list is added new object").that(dishes.size).isEqualTo(1)
        dishes.forEach {
            Truth.assertWithMessage("dish id is == 3").that(it.id).isEqualTo(3)
            Truth.assertWithMessage("dish type is == dessert").that(it.type).isEqualTo("dessert")
        }
    }

    @Test
    fun d_testInsert() = runBlockingTest {
        myDishViewModel.insert(entityObject1)
        myDishViewModel.insert(entityObject2)
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValue()
        Truth.assertThat(allDishes.size).isGreaterThan(1)
    }

    @Test
    fun e_testUpdate() = runBlockingTest {
        myDishViewModel.insert(entityObject1)
        entityObject1.title = "other"
        myDishViewModel.update(entityObject1)
        val allDishes = myDishViewModel.allDishesList.getOrAwaitValue()
        Truth.assertThat(allDishes.size).isEqualTo(1)
    }

    @Test
    fun f_testDelete() = runBlockingTest {
        myDishViewModel.insert(entityObject1)
        Timber.d("all dishes list is ${myDishViewModel.allDishesList.getOrAwaitValue().size}")
        Truth.assertThat(myDishViewModel.allDishesList.getOrAwaitValue().size).isEqualTo(1)

        myDishViewModel.delete(entityObject1)
        Timber.d("all dishes list is ${myDishViewModel.allDishesList.getOrAwaitValue().size}")
        Truth.assertThat(myDishViewModel.allDishesList.getOrAwaitValue().size).isEqualTo(0)
    }
}