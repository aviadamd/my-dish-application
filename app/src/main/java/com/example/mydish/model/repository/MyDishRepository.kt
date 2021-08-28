package com.example.mydish.model.repository

import androidx.annotation.WorkerThread
import com.example.mydish.model.database.MyDishDao
import com.example.mydish.model.entities.MyDishEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

/**
 * A Repository manages queries and allows you to use multiple backend.
 *
 * The DAO is passed into the repository constructor as opposed to the whole database.
 * This is because it only needs access to the DAO, since the DAO contains all the read/write methods for the database.
 * There's no need to expose the entire database to the repository.
 *
 * @param myDishDao - Pass the MyDishDao as the parameter.
 * this class calls only from MyDishDao and wrap the accesses data with coroutines Flow/ for handle the flow data
 *
 * Class init in MyDishApplication(database.myDishDao()) for init the MyDishDao to data base
 * Class called in MyDishViewModel
 */
class MyDishRepository(private val myDishDao: MyDishDao) {

    /**
     * Room executes all queries on separate thread
     * Observed Flow (coroutines) will notify the observer when the has changed.
     * This will return from data base the dishes list
     */
    val allDishesList : Flow<List<MyDishEntity>> = myDishDao.getAllDishesList()

    /**
     * Room executes all queries on separate thread
     * Observed Flow (coroutines) will notify the observer when the has changed.
     * This will return from data base the favorites dishes list
     */
    val favoriteDishes : Flow<List<MyDishEntity>> = myDishDao.getFavoriteDishesList()

    /**
     * Room executes all queries on separate thread
     * Observed Flow (coroutines) will notify the observer when the has changed.
     * This will return from data base favorites dishes list
     */
    fun filteredListDishes(value : String) : Flow<List<MyDishEntity>> = myDishDao.getFilteredDishesList(value)

    /**
     * By default Room runs suspend queries off the main thread, therefore, we don't need to
     * implement anything else to ensure we're not doing long running database work off the main thread.
     * @WorkerThread make shore that using the back thread not the ui thread
     */
    @WorkerThread
    suspend fun insertMyDishData(myDishEntity: MyDishEntity) {
        myDishDao.insertMyDishDetails(myDishEntity)
    }

    /** @WorkerThread make shore that using the back thread not the ui thread **/
    @WorkerThread
    suspend fun updateMyDishData(myDishEntity: MyDishEntity) {
        myDishDao.updateMyDishDetails(myDishEntity)
    }

    /** @WorkerThread make shore that using the back thread not the ui thread **/
    @WorkerThread
    suspend fun deleteMyDishData(myDishEntity: MyDishEntity) {
        myDishDao.deleteMyDishDetails(myDishEntity)
    }

}