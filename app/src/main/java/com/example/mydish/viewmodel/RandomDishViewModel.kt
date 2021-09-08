package com.example.mydish.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.api.webservice.RandomDishesApiService
import com.example.mydish.utils.data.Tags.DISH_INFO
import kotlinx.coroutines.*

/**
 *
 * The ViewModel's role is to provide data to the UI and survive configuration changes.
 * A ViewModel acts as a communication center between the Repository and the UI.
 * You can also use a ViewModel to share data between fragments.
 * The ViewModel is part of the lifecycle library.
 *
 * The UI no longer needs to worry about the origin of the data.
 * ViewModel instances survive Activity/Fragment recreation.
 *
 * Classes as activities/fragment in use that call to MyDishViewModel
 * This class connect with the service area and not with the Repository
 * RandomDishFragment
 *
 * This class is the observable of the observer
 */
class RandomDishViewModel : ViewModel() {

    /** will be init with the coroutine scope and will be return to null on ViewModel clear life cycle **/
    private var job: Job? = null

    /*** Retro fit RandomDishService val , will be used in end point url */
    private val randomRecipeApiService = RandomDishesApiService()

    /*** coroutine scope with exception handler for error coroutine scope */
    private val coroutineErrorScope = CoroutineExceptionHandler{ job, throwable ->
        Log.e(DISH_INFO,"Error: ${throwable.localizedMessage} Job: ${job.isActive}")
    }

    /*** coroutine scope with exception handler for coroutine scope */
    private val coroutineScope = CoroutineScope(Dispatchers.IO + coroutineErrorScope)

    /*** the getter for the populated random dish life data observer */
    fun getRandomViewModelLiveDataObserver(): RandomViewModelLiveDataHolder {
        return setRandomViewModelLiveDataObserver
    }

    private var setRandomViewModelLiveDataObserver = RandomViewModelLiveDataHolder(
        MutableLiveData(Pair(first = false, second = false)),
        MutableLiveData<RandomDish.Recipes>()
    )

    /**
     * Using CoroutineScope(Dispatchers.IO + exceptionHandler) handle the rest calls from back thread
     * Using withContext(Dispatchers.Main) to return to the ui thread and use the live data
     */
    fun getRandomDishesFromRecipeAPI(endPoint: EndPoint) {
        val observer = setRandomViewModelLiveDataObserver
        /*** define the value of the load random dish */

        /*** delegate the object MyDishViewModel with repository data base MyDishDao */
        observer.loadData.value = Pair(first = false, second = false)

        /*** add the focus to the back thread dish api CoroutineScope(Dispatchers.IO + exceptionHandler) */
        job = coroutineScope.launch {
            val dishes = randomRecipeApiService.getDishes(endPoint)
            /*** add the focus to the main thread dish api withContext(Dispatchers.Main) */
            withContext(Dispatchers.Main) {
                if (this.isActive && dishes.isSuccessful) {
                    observer.loadData.value = Pair(first = false, second = true)
                    observer.recipesData.value = dishes.body()
                    Log.i(DISH_INFO, "dish loading successes with code ${dishes.code()}")
                } else {
                    observer.loadData.value = Pair(first = true, second = false)
                    Log.i(DISH_INFO, "dish loading fails with code ${dishes.code()} error")
                }
            }
        }

        job?.let { job ->
            job.invokeOnCompletion {
                Log.i(DISH_INFO,"dish loading job finish as ${job.isCompleted}")
            }
        }
    }

    /*** on each time that the view model life cycle in clean the job will be cancel */
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    /**
     * val loadData: MutableLiveData<Pair<Boolean,Boolean>> hold the network call state
     * val recipesData: MutableLiveData<RandomDish.Recipes>) hold the network data
     */
    data class RandomViewModelLiveDataHolder(
        val loadData: MutableLiveData<Pair<Boolean,Boolean>>,
        val recipesData: MutableLiveData<RandomDish.Recipes>)
}