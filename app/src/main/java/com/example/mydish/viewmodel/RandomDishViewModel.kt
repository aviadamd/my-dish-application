package com.example.mydish.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mydish.api.webservice.RandomDish
import com.example.mydish.api.webservice.RandomDishApiService
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
 */
class RandomDishViewModel : ViewModel() {

    /*** Retro fit RandomDishService val , will be used in end point url */
     private val randomRecipeApiService = RandomDishApiService()

    /**
     * A disposable/one time container that can hold onto multiple other Disposables and
     * offers time complexity for add(Disposable), remove(Disposable) and delete(Disposable)
     * operations. -> RxJava
     */
    private val compositeDisposable = CompositeDisposable()

    /** Create MutableLiveData with no value assigned to it **/
    /** Call randomDishResponse from this class and from the RandomDishFragment **/
    /** Call randomDishError from this class and from the RandomDishFragment **/
    val randomDishLiveDataHolder = Triple(
        MutableLiveData<Boolean>(),
        MutableLiveData<RandomDish.Recipes>(),
        MutableLiveData<Boolean>()
    )

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
    fun getRandomDishFromRecipeAPI(endPoint: RandomDishApiService.EndPoint) {
        /*** define the value of the load random dish */
        randomDishLiveDataHolder.first.value = true

        /**
         * Disposable להיפטר
         * Adds a Disposable time to this container or disposes it if the container has been disposed.
         * /*** Retro fit RandomDishService val , will be used in end point url */
         */
        compositeDisposable.add(randomRecipeApiService.getDish(endPoint)
            /*** asynchronously subscribes SingleObserver to this Single on the specified Scheduler. */
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<RandomDish.Recipes>() {
                override fun onSuccess(value: RandomDish.Recipes) {
                    /*** update the values with response in the success method. */
                    randomDishLiveDataHolder.first.value = true
                    randomDishLiveDataHolder.second.value = value
                    randomDishLiveDataHolder.third.value = true
                }

                override fun onError(e: Throwable) {
                    /*** update the values in the response in the error methods . */
                    randomDishLiveDataHolder.first.value = false
                    randomDishLiveDataHolder.third.value = true
                    printServiceErrors(e)
                }
            })
        )
    }

    private fun printServiceErrors(e: Throwable) {
        e.message.toString().let {
            when(it) {
                BLOCKED -> Log.e(BLOCK_TAG, "BLOCKED out from services calls free retries")
                NOT_FOUND -> Log.e(BLOCK_TAG, "NOT_FOUND service resource")
                FORBIDDEN -> Log.e(BLOCK_TAG, "FORBIDDEN call request to service")
                UNAUTHORIZED -> Log.e(BLOCK_TAG, "UNAUTHORIZED to call service")
                else -> Log.e(BLOCK_TAG, "Unknown error" + e.stackTrace)
            }
        }
    }

    companion object {
        const val BLOCK_TAG: String = "BLOCK"
        const val BLOCKED: String = "402"
        const val NOT_FOUND: String = "404"
        const val FORBIDDEN: String = "403"
        const val UNAUTHORIZED: String = "401"
    }
}