package com.example.mydish.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mydish.model.service.webservice.EndPoint
import com.example.mydish.model.service.webservice.RandomDishesApiService
import com.example.mydish.model.service.webservice.Recipes
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

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

    /*** Retro fit RandomDishService val, will be used in end point url */
    private val randomRecipeApiService = RandomDishesApiService()

    /*** save random dish view model immutable observable state flow to the mutable observer */
    private var _setRandomDishState = MutableStateFlow<ResourceState>(ResourceState.Empty)
    /*** get the immutable state from the _randomDish state mutable observer */
    fun getRandomDishState(): StateFlow<ResourceState> { return _setRandomDishState }

    /*** common dispatchers for coroutine scope*/
    private val dispatchersIO = Dispatchers.IO + CoroutineExceptionHandler { job,throwable ->
        Timber.e("Error info: ${throwable.localizedMessage} Job active: ${job.isActive}")
    }

    /**
     * Using CoroutineScope(Dispatchers.IO + exceptionHandler) handle the rest calls from back thread
     * Using withContext(Dispatchers.Main) to return to the ui thread and use the live data
     */
    fun getRandomDishesRecipeAPINew(endPoint: EndPoint): Int {
        var statusCode = 0
        _setRandomDishState.value = ResourceState.Load(true)
        
        job = CoroutineScope(dispatchersIO).launch {
             val dishes = randomRecipeApiService.getDishes(endPoint)
             withContext(Dispatchers.Main) {
                if (this.isActive && dishes.isSuccessful) {
                    _setRandomDishState.value = ResourceState.Load(false)
                    _setRandomDishState.value = ResourceState.Service(dishes.body())
                    Timber.d("dish loading successes with code ${dishes.code()}")
                } else {
                    _setRandomDishState.value = ResourceState.Errors(dishes.code().toString())
                    Timber.d("dish loading fails with code ${dishes.code()} error")
                }
                 statusCode = dishes.code()
            }
        }

        job?.let { job ->
            job.invokeOnCompletion {
                val status = job.isCompleted
                Timber.i("dish job finish as ${status}, with http code {$statusCode}")
            }
        }

        return statusCode
    }

    fun refresh() {
        _setRandomDishState.value = ResourceState.Load(true)
        _setRandomDishState.value = ResourceState.Service(null)
        _setRandomDishState.value = ResourceState.Errors("")
    }

    /*** on each time that the view model life cycle in clean the job will be cancel */
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}