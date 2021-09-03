package com.example.mydish.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.api.webservice.RandomDishesApiService
import com.example.mydish.utils.data.Tags.DISH_INFO
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

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
        observer.loadData.value = Pair(first = false, second = false)

        /*** add the focus to the back thread dish api CoroutineScope(Dispatchers.IO + exceptionHandler) */
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val dishes = randomRecipeApiService.getDishes(endPoint)
            /*** add the focus to the main thread dish api withContext(Dispatchers.Main) */
            withContext(Dispatchers.Main) {
                if (dishes.isSuccessful) {
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

    fun getRandomViewModelLiveDataObserver(): RandomViewModelLiveDataHolder {
        return setRandomViewModelLiveDataObserver
    }

    /**
     * Create MutableLiveData with no value assigned to it
     * Call randomDishLoading,randomDishResponse,randomDishError
     * from this class and from the RandomDishFragment
     * **/
    private var _randomDishLiveData = MutableLiveData<RandomDishState>()
    val getRandomDishLiveData: MutableLiveData<RandomDishState> = _randomDishLiveData

    /** Create MutableLiveData with no value assigned to it **/
    /** Call randomDishResponse from this class and from the RandomDishFragment **/
    /** Call randomDishError from this class and from the RandomDishFragment **/
    data class RandomViewModelLiveDataHolder(
        val loadData: MutableLiveData<Pair<Boolean,Boolean>>,
        val recipesData: MutableLiveData<RandomDish.Recipes>)

    val randomViewModelLiveDataObserverChannel = Channel<RandomDishState>()

    fun getRandomDishesFromRecipeAPINewWay(endPoint: EndPoint) {
        val observer = _randomDishLiveData

        observer.value = RandomDishState.Loading
        /*** add the focus to the back thread dish api CoroutineScope(Dispatchers.IO + exceptionHandler) */
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val dishes = randomRecipeApiService.getDishes(endPoint)
            /*** add the focus to the main thread dish api withContext(Dispatchers.Main) */
            withContext(Dispatchers.Main) {
                if (dishes.isSuccessful) {
                    observer.value = dishes.body()?.let { RandomDishState.Success(it) }
                    Log.i(DISH_INFO, "dish loading successes with code ${dishes.code()}")
                } else {
                    observer.value = RandomDishState.Error
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

    sealed class RandomDishState {
        object Error: RandomDishState()
        object Loading: RandomDishState()
        data class Success(var data:RandomDish.Recipes): RandomDishState()
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}