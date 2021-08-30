package com.example.mydish.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.api.webservice.RandomDishesApiService
import com.example.mydish.model.api.webservicedemo.RandomDishesApiServiceRxJava
import com.example.mydish.utils.Tags.DISH_INFO
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
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

    /*** Retro fit RandomDishService val , will be used in end point url */
    private val randomRecipeApiServiceRx = RandomDishesApiServiceRxJava()

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
                    Log.i(DISH_INFO, "dish loading successes")
                } else {
                    randomViewModelLiveDataObserver.loadData.value = false
                    randomViewModelLiveDataObserver.errors.value = true
                    Log.i(DISH_INFO, "dish loading fails with code ${dishes.code()} error")
                }
            }
        }.also {
            it.isCompleted.let { value -> Log.i(DISH_INFO,"dish loading job finish as $value") }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    /**
     * A disposable/one time container that can hold onto multiple other Disposables and
     * offers time complexity for add(Disposable), remove(Disposable) and delete(Disposable)
     * operations. -> RxJava
     */
    private val compositeDisposable = CompositeDisposable()

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
    fun getRandomDishesFromRecipeAPIRx(endPoint: EndPoint) {
        /*** define the value of the load random dish */
        randomViewModelLiveDataObserver.loadData.value = true
        /**
         * Disposable להיפטר
         * Adds a Disposable time to this container or disposes it if the container has been disposed.
         * /*** Retro fit RandomDishService val , will be used in end point url */
         */
        compositeDisposable.add(randomRecipeApiServiceRx.getDishesRx(endPoint)
            /*** asynchronously subscribes SingleObserver to this Single on the specified Scheduler. */
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<RandomDish.Recipes>() {
                override fun onSuccess(dishResponce: RandomDish.Recipes) {
                    /*** update the values with response in the success method. */
                    randomViewModelLiveDataObserver.loadData.value = false
                    randomViewModelLiveDataObserver.recipesData.value = dishResponce
                    randomViewModelLiveDataObserver.errors.value = false
                }

                override fun onError(e: Throwable) {
                    /*** update the values in the response in the error methods . */
                    randomViewModelLiveDataObserver.loadData.value = false
                    randomViewModelLiveDataObserver.errors.value = true
                    e.printStackTrace()
                }
            })
        )
    }
}