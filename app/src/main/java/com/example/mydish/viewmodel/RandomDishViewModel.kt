package com.example.mydish.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mydish.model.service.webservice.EndPoint
import com.example.mydish.model.service.webservice.RandomDishesApiService
import com.example.mydish.model.service.webservice.Recipes
import com.example.mydish.utils.data.Tags.DISH_INFO
import com.example.mydish.utils.extensions.SharedPreferenceHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
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

    /*** holding the state of loading dish processes */
    val randomDishLoading = MutableLiveData<Boolean>()
    /*** holding the date of dish recipes data */
    val randomDishResponse = MutableLiveData<Recipes>()
    /*** holding the state of error dish processes */
    val randomDishLoadingError = MutableLiveData<Boolean>()

    /**
     * A disposable/one time container that can hold onto multiple other Disposables and
     * offers time complexity for add(Disposable), remove(Disposable) and delete(Disposable)
     * operations. -> RxJava
     */
    private val compositeDisposable = CompositeDisposable()

    /*** shared preference savings data */
   // private var prefs = SharedPreferenceHelper()

    /*** save random dish view model state flow for observer */
    private val _randomDishState = MutableStateFlow<RandomDishState>(RandomDishState.Empty)
    fun getRandomDishState(): StateFlow<RandomDishState> { return _randomDishState }

    /*** common dispatchers for coroutine scope*/
    private val dispatchersIO = Dispatchers.IO + CoroutineExceptionHandler { job,throwable ->
        Log.e(DISH_INFO,"Error info: ${throwable.localizedMessage} Job active: ${job.isActive}")
    }

    /*** method to call service with coroutine or rx java options */
    fun getRandomRecipeApiCall(with: With, endPoint: EndPoint) {
        when(with) {
            With.RX -> getRandomDishesFromRecipeAPIRx(endPoint)
            With.COROUTINE -> getRandomDishesRecipeAPINew(endPoint)
        }
    }

    /**
     * Using CoroutineScope(Dispatchers.IO + exceptionHandler) handle the rest calls from back thread
     * Using withContext(Dispatchers.Main) to return to the ui thread and use the live data
     */
    private fun getRandomDishesRecipeAPINew(endPoint: EndPoint) {
        /*** add the focus to the back thread dish api CoroutineScope(Dispatchers.IO + exceptionHandler) */
        job = CoroutineScope(dispatchersIO).launch {
            val dishes = randomRecipeApiService.getDishes(endPoint)
            /*** add the focus to the main thread dish api withContext(Dispatchers.Main) */
            withContext(Dispatchers.Main) {
                if (this.isActive && dishes.isSuccessful) {
                    _randomDishState.value = RandomDishState.Load(true)
                    _randomDishState.value = RandomDishState.Service(dishes.body())
                    Log.i(DISH_INFO, "dish loading successes with code ${dishes.code()}")
                } else {
                    _randomDishState.value = RandomDishState.Errors(dishes.code().toString())
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

    /**
    * .subscribeOn(Schedulers.newThread())
    * Static factory methods for returning standard Scheduler instances.
    * The initial and runtime values of the various scheduler types can be overridden via the
    * {RxJavaPlugins.setInit(scheduler name)SchedulerHandler()} and
    * {RxJavaPlugins.set(scheduler name)SchedulerHandler()} respectively.
    *
    * .subscribeOn(Schedulers.newThread())
    * Signals the success item or the terminal signals of the current Single on the specified Scheduler,
    * asynchronously.
    * A Scheduler which executes actions on the Android main thread.
    *
    * .observeOn(AndroidSchedulers.mainThread())
    * Subscribes a given SingleObserver (subclass) to this Single and returns the given
    * SingleObserver as is.
    */
    private fun getRandomDishesFromRecipeAPIRx(endPoint: EndPoint) {
        /**
         * Disposable == get reed of...
         * Adds a Disposable time to this container or disposes it if the container has been disposed.
         * /*** Retro fit RandomDishService val , will be used in end point url */
         */
        compositeDisposable.add(randomRecipeApiService.getDishesAsRetroFitWithRx(endPoint)
            /*** asynchronously subscribes SingleObserver to this Single on the specified Scheduler. */
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Recipes>() {
                override fun onSuccess(dishResponce: Recipes) {
                    randomDishLoading.value = false
                    randomDishResponse.value = dishResponce
                    Log.i(DISH_INFO, "dish response $dishResponce")
                }

                override fun onError(e: Throwable) {
                    randomDishLoadingError.value = true
                    Log.i(DISH_INFO, "dish error response ${e.message}")
                    e.printStackTrace()
                }
            })
        )
    }

    /*** on each time that the view model life cycle in clean the job will be cancel */
    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        compositeDisposable.clear()
    }

    fun refresh(with: With) {
        when(with) {
            With.RX -> {
                randomDishLoading.value = false
                randomDishLoadingError.value = false
                randomDishResponse.value = null
            }
            With.COROUTINE -> {
                _randomDishState.value = RandomDishState.Load(true)
                _randomDishState.value = RandomDishState.Service(null)
                _randomDishState.value = RandomDishState.Errors("")
            }
        }
    }

    sealed class RandomDishState {
        object Empty: RandomDishState()
        data class Load(var load: Boolean): RandomDishState()
        data class Errors(var error: String): RandomDishState()
        data class Service(var randomDishApi: Recipes?): RandomDishState()
    }

    enum class With {
        RX, COROUTINE
    }
}