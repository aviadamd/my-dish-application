package com.example.mydish.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.api.webservice.RandomDishesApiService
import com.example.mydish.utils.Tags.DISH_INFO
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
 */
class RandomDishViewModel : ViewModel() {

    /** will be init with the coroutine scope and will be return to null on ViewModel clear life cycle **/
    private var job: Job? = null

    /*** Retro fit RandomDishService val , will be used in end point url */
     private val randomRecipeApiService = RandomDishesApiService()

    /*** exception handler for coroutine scope */
    private val exceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        Log.e(DISH_INFO,"Exception:  ${throwable.localizedMessage}")
    }

    /**
     * Create MutableLiveData with no value assigned to it
     * Call randomDishLoading,randomDishResponse,randomDishError
     * from this class and from the RandomDishFragment
     * **/
    var randomViewModelLiveDataObserver = RandomViewModelLiveDataHolder(
        MutableLiveData<Boolean>(),
        MutableLiveData<RandomDish.Recipes>(),
        MutableLiveData<Boolean>())

    /**
     * Using CoroutineScope(Dispatchers.IO + exceptionHandler) handle the rest calls from back thread
     * Using withContext(Dispatchers.Main) to return to the ui thread and use the live data
     */
    fun getRandomDishesFromRecipeAPI(endPoint: EndPoint) {
        /*** define the value of the load random dish */
        randomViewModelLiveDataObserver.loadData.value = true

        /*** add the focus to the back thread dish api CoroutineScope(Dispatchers.IO + exceptionHandler) */
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val dishes = randomRecipeApiService.getDishes(endPoint)
            /*** add the focus to the main thread dish api withContext(Dispatchers.Main) */
            withContext(Dispatchers.Main) {
                if (dishes.isSuccessful) {
                    randomViewModelLiveDataObserver.loadData.value = false
                    randomViewModelLiveDataObserver.recipesData.value = dishes.body()
                    randomViewModelLiveDataObserver.errors.value = false
                    Log.i(DISH_INFO,"dish loading successes")
                } else {
                    randomViewModelLiveDataObserver.loadData.value = false
                    randomViewModelLiveDataObserver.errors.value = true
                    Log.i(DISH_INFO,"dish loading fails")
                }
            }
        }
        job?.isCompleted.let {
            Log.i(DISH_INFO,"dish loading job finish")
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}