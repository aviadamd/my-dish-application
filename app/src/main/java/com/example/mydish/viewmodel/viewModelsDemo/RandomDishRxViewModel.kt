package com.example.mydish.viewmodel.viewModelsDemo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.api.webservicedemo.RandomDishesApiServiceRxJava
import com.example.mydish.viewmodel.RandomViewModelLiveDataHolder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishRxViewModel: ViewModel() {

    /*** Retro fit RandomDishService val , will be used in end point url */
    private val randomRecipeApiServiceRx = RandomDishesApiServiceRxJava()

    /**
     * A disposable/one time container that can hold onto multiple other Disposables and
     * offers time complexity for add(Disposable), remove(Disposable) and delete(Disposable)
     * operations. -> RxJava
     */
    private val compositeDisposable = CompositeDisposable()

    private var randomViewModelLiveDataObserver = RandomViewModelLiveDataHolder(
        MutableLiveData(Pair(first = false, second = false)),
        MutableLiveData<RandomDish.Recipes>()
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
    fun getRandomDishesFromRecipeAPIRx(endPoint: EndPoint) {
        /*** define the value of the load random dish */
        val observer = randomViewModelLiveDataObserver
        /*** define the value of the load random dish */
        observer.loadData.value = Pair(first = false, second = true)
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
                    observer.loadData.value = Pair(first = true, second = false)
                    randomViewModelLiveDataObserver.recipesData.value = dishResponce
                }

                override fun onError(e: Throwable) {
                    /*** update the values in the response in the error methods . */
                    observer.loadData.value = Pair(first = false, second = true)
                    e.printStackTrace()
                }
            })
        )
    }
}