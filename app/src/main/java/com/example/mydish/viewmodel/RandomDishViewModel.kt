package com.example.mydish.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.mydish.model.service.webservice.EndPoint
import com.example.mydish.model.service.webservice.RandomDishesApiService
import com.example.mydish.model.service.webservice.Recipes
import com.example.mydish.utils.data.Tags.DISH_INFO
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
class RandomDishViewModel(application: Application) : AndroidViewModel(application) {

    /** will be init with the coroutine scope and will be return to null on ViewModel clear life cycle **/
    private var job: Job? = null

    /*** Retro fit RandomDishService val , will be used in end point url */
    private val randomRecipeApiService = RandomDishesApiService()

    /*** save random dish view model immutable observable state flow to the mutable observer */
    private val _randomDishState = MutableStateFlow<RandomDishState>(RandomDishState.Empty)
    /*** get the immutable state from the _randomDish state mutable observer */
    fun getRandomDishState(): StateFlow<RandomDishState> { return _randomDishState }

    /*** common dispatchers for coroutine scope*/
    private val dispatchersIO = Dispatchers.IO + CoroutineExceptionHandler { job,throwable ->
        Log.e(DISH_INFO,"Error info: ${throwable.localizedMessage} Job active: ${job.isActive}")
    }

    /**
     * Using CoroutineScope(Dispatchers.IO + exceptionHandler) handle the rest calls from back thread
     * Using withContext(Dispatchers.Main) to return to the ui thread and use the live data
     */
    fun getRandomDishesRecipeAPINew(endPoint: EndPoint) {
        _randomDishState.value = RandomDishState.Load(true)
        var statusCode = 0
        /*** add the focus to the back thread dish api CoroutineScope(Dispatchers.IO + exceptionHandler) */
        job = CoroutineScope(dispatchersIO).launch {
            val dishes = randomRecipeApiService.getDishes(endPoint)
            /*** add the focus to the main thread dish api withContext(Dispatchers.Main) */
            withContext(Dispatchers.Main) {
                if (this.isActive && dishes.isSuccessful) {
                    _randomDishState.value = RandomDishState.Load(false)
                    _randomDishState.value = RandomDishState.Service(dishes.body())
                    Log.i(DISH_INFO, "dish loading successes with code ${dishes.code()}")
                } else {
                    _randomDishState.value = RandomDishState.Errors(dishes.code().toString())
                    Log.i(DISH_INFO, "dish loading fails with code ${dishes.code()} error")
                }
                statusCode = dishes.code()
            }
        }

        job?.let { job ->
            job.invokeOnCompletion {
                Log.i(DISH_INFO,"dish job finish as ${job.isCompleted}, with http code {$statusCode}")
            }
        }
    }

    /*** on each time that the view model life cycle in clean the job will be cancel */
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun refresh() {
        _randomDishState.value = RandomDishState.Load(true)
        _randomDishState.value = RandomDishState.Service(null)
        _randomDishState.value = RandomDishState.Errors("")
    }

    sealed class RandomDishState {
        object Empty: RandomDishState()
        data class Load(var load: Boolean): RandomDishState()
        data class Errors(var error: String): RandomDishState()
        data class Service(var randomDishApi: Recipes?): RandomDishState()
    }
}