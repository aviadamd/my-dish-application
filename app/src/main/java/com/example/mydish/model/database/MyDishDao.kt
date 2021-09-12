package com.example.mydish.model.database

import androidx.room.*
import com.example.mydish.model.entities.MyDishEntity
import kotlinx.coroutines.flow.Flow

/**
 * Hold the data base operations @Insert,@Update,@Delete with @Dao annotations and suspend functions .
 * For the getData from data base we use coroutines with room @Query
 */
@Dao
interface MyDishDao {

    /**
     * All queries must be executed on a separate thread.
     * They cannot be executed from Main Thread or it will cause an crash.
     * Room has Kotlin coroutines support.
     * This allows your queries to be annotated with the suspend modifier and then called from a coroutine or from another suspension function.
     * A function to insert favorite dish details to the local database using Room.
     * @param myDishEntity - Here we will pass the entity class that we have created.
     */
    @Insert
    suspend fun insertMyDishDetails(myDishEntity: MyDishEntity)

    /**
     * A function to update my dish details to the local database using Room.
     * @param myDishEntity - Here we will pass the entity class that we have created
     * when use a suspend function to perform a request, the underlying thread isn't blocked
     */
    @Update
    suspend fun updateMyDishDetails(myDishEntity: MyDishEntity)

    /**
     * A function to delete my dish details to the local database using Room.
     * @param myDishEntity - Here we will pass the entity class that we have created
     * when use a suspend function to perform a request, the underlying thread isn't blocked
     */
    @Delete
    suspend fun deleteMyDishDetails(myDishEntity: MyDishEntity)

    /**
     * When data changes, you usually want to take some action, such as displaying the updated data in the UI.
     * This means you have to observe the data so when it changes, you can react.
     * To observe data changes we will use Flow from kotlinx-coroutines.
     * Use a return value of type Flow in your method description,
     * and Room generates all necessary code to update the Flow when the database is updated.
     * A Flow is an async sequence of values
     * Flow produces values one at a time (instead of all at once) that can generate values from async operations
     * like network requests, database calls, or other async code.
     * It supports coroutines throughout its API, so you can transform a flow using coroutines as well!
     */
    @Query("SELECT * FROM MY_DISH_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<MyDishEntity>>

    //SELECT * FROM MY_DISH_TABLE ORDER BY ID
    //SELECT DISTINCT * FROM MY_DISH_TABLE ORDER BY ID
    //SELECT * FROM MY_DISH_TABLE WHERE favoriteDish = 1
    //SELECT DISTINCT * FROM MY_DISH_TABLE WHERE favoriteDish = 1

    @Query("SELECT * FROM MY_DISH_TABLE WHERE favoriteDish = 1")
    fun getFavoriteDishesList(): Flow<List<MyDishEntity>>

    @Query("SELECT * FROM MY_DISH_TABLE WHERE type = :filterType")
    fun getFilteredDishesList(filterType: String): Flow<List<MyDishEntity>>
}