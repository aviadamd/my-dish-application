package com.example.mydish.model.api.webservicedemo

import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.api.webservice.RandomDishApi
import com.example.mydish.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishesApiServiceRxJava {

    /**
     * Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to
     * define how requests are made. Create instances using {@linkplain Builder the builder} and pass
     * your interface to {create} to generate an implementation.
     *
     * .addConverterFactory
     * A Converter.Factory converter which uses Gson for JSON.
     * Add converter factory for serialization and deserialization of objects.
     * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
     * all types.
     *
     * .addConverterFactory(GsonConverterFactory.create())
     * Add a call adapter factory for supporting service method return types other than.
     * A CallAdapter.
     * Factory call adapter which uses RxJava 3 for creating observables.
     * Adding this class to Retrofit allows you to return an Observable, Flowable, Single, Completable
     * or Maybe from service methods.
     *
     * .build()
     * Create retro fit instance using configured values
     *
     * .create(RandomDishApi::class.java)
     * Create an implementation of the API endpoints
     * defined by the service interface in our case it is RandomDishAPI.
     *
     */
    private val api1 = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomDishApi::class.java)

    /**
     *  Constants.API_KEY_VALUE - the api key for identification
     *  Constants.LIMIT_LICENSE_VALUE = true - free user
     *  Constants.TAGS_VALUE : String = "vegetarian, dessert"
     *  Constants.NUMBER_VALUE : Int = 0
     */
    fun getDishesRx(endPoint: EndPoint): Single<RandomDish.Recipes> {
        val key = Constants.API_KEY_VALUE
        val license = Constants.LIMIT_LICENSE_VALUE

        val recipe : Single<RandomDish.Recipes> = when(endPoint) {
            EndPoint.MEAL -> api1.getTheDishesRx(key, license, EndPoint.MEAL.key, EndPoint.MEAL.value)
            EndPoint.CUISINES -> api1.getTheDishesRx(key, license, EndPoint.CUISINES.key, EndPoint.CUISINES.value)
            EndPoint.DESSERT -> api1.getTheDishesRx(key, license, EndPoint.DESSERT.key, EndPoint.CUISINES.value)
        }

        return recipe
    }
}