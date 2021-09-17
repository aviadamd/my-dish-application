package com.example.mydish.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mydish.model.service.webservice.EndPoint
import com.example.mydish.model.service.webservice.RandomDishesApiService
import com.example.mydish.model.service.webservice.Recipes
import com.example.mydish.utils.data.Tags.DISH_INFO
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

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

    /**
     * A disposable/one time container that can hold onto multiple other Disposables and
     * offers time complexity for add(Disposable), remove(Disposable) and delete(Disposable) operations.
     */
    private val compositeDisposable = CompositeDisposable()

    /*** Retro fit RandomDishService val , will be used in end point url */
    private val randomRecipeApiService = RandomDishesApiService()

    /*** save random dish view model immutable observable state flow to the mutable observer */
    private val _rxRandomDishState = MutableLiveData<RandomDishState>(RandomDishState.Empty)
    /*** get the immutable state from the _randomDish state mutable observer */
    fun getRxRandomDishState(): LiveData<RandomDishState> { return _rxRandomDishState }

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
        _rxRandomDishState.value = RandomDishState.Load(true)
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
                    _rxRandomDishState.value = RandomDishState.Load(false)
                    _rxRandomDishState.value = RandomDishState.Service(dishResponce)
                    Log.i(DISH_INFO, "dish response $dishResponce")
                }

                override fun onError(e: Throwable) {
                    _rxRandomDishState.value = RandomDishState.Errors(e.message.toString())
                    Log.i(DISH_INFO, "dish error response ${e.message}")
                }
            })
        )
    }

    /*** on each time that the view model life cycle in clean the job will be cancel */
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun refresh() {
        _rxRandomDishState.value = RandomDishState.Load(true)
        _rxRandomDishState.value = RandomDishState.Service(null)
        _rxRandomDishState.value = RandomDishState.Errors("")
    }

    sealed class RandomDishState {
        object Empty: RandomDishState()
        data class Load(var load: Boolean): RandomDishState()
        data class Errors(var error: String): RandomDishState()
        data class Service(var randomDishApi: Recipes?): RandomDishState()
    }

}