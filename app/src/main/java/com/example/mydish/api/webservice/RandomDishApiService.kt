package com.example.mydish.api.webservice

import com.example.mydish.api.webservice.RandomDishApiService.EndPoint.*
import com.example.mydish.utils.Constants
import com.example.mydish.utils.Constants.NUMBER_CUISINES_VALUE
import com.example.mydish.utils.Constants.NUMBER_MEAL_VALUE
import com.example.mydish.utils.Constants.NUMBER_VEGETARIAN_VALUE
import com.example.mydish.utils.Constants.TAGS_CUISINES_VALUE
import com.example.mydish.utils.Constants.TAGS_MEAL_VALUE
import com.example.mydish.utils.Constants.TAGS_VEGETARIAN_VALUE
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/*** in the future will have more calls on different category dishes */
class RandomDishApiService {

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
    private val api = Retrofit.Builder()
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
    fun getDish(endPoint: EndPoint): Single<RandomDish.Recipes> {
        val key = Constants.API_KEY_VALUE
        val license = Constants.LIMIT_LICENSE_VALUE

        val recipe : Single<RandomDish.Recipes> = when(endPoint) {
            MEAT -> api.getDishes(key, license, TAGS_MEAL_VALUE, NUMBER_MEAL_VALUE)
            RANDOM -> api.getDishes(key, license, TAGS_VEGETARIAN_VALUE, NUMBER_VEGETARIAN_VALUE)
            CUISINES -> api.getDishes(key, license, TAGS_CUISINES_VALUE, NUMBER_CUISINES_VALUE)
            VEGETARIAN_DESSERT -> api.getDishes(key, license, TAGS_VEGETARIAN_VALUE, NUMBER_VEGETARIAN_VALUE)
        }

        return recipe
    }

    enum class EndPoint {
        VEGETARIAN_DESSERT,
        MEAT,
        CUISINES,
        RANDOM
    }
}