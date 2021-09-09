package com.example.mydish.model.service.webservice

import com.example.mydish.model.service.webservice.EndPoint.*
import com.example.mydish.utils.data.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/*** in the future will have more calls on different category dishes */
class RandomDishesApiService :
    RandomDishService.RandomDishServiceResponse,
    RandomDishService.RandomDishServiceResponseRx {

    private val key = Constants.API_KEY_VALUE
    private val license = Constants.LIMIT_LICENSE_VALUE

    /*** implement method to get dishes api call */
    override suspend fun getDishes(endPoint: EndPoint): Response<RandomDish.Recipes> {
        val recipe : Response<RandomDish.Recipes> =
            when(endPoint) {
                MEAL -> apiRegular.getTheDishes(key, license, MEAL.key, MEAL.value)
                CUISINES -> apiRegular.getTheDishes(key, license, CUISINES.key, CUISINES.value)
                DESSERT -> apiRegular.getTheDishes(key, license, DESSERT.key, CUISINES.value)
        }
        return recipe
    }

    override fun getDishesAsRetroFitWithRx(endPoint: EndPoint): Single<RandomDish.Recipes> {
        val recipe : Single<RandomDish.Recipes> =
            when(endPoint) {
                MEAL -> apiRx.getTheDishesRx(key, license, MEAL.key, MEAL.value)
                CUISINES -> apiRx.getTheDishesRx(key, license, CUISINES.key, CUISINES.value)
                DESSERT -> apiRx.getTheDishesRx(key, license, DESSERT.key, CUISINES.value)
        }
        return recipe
    }

    /**
     * Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to
     * define how requests are made. Create instances using {@linkplain Builder the builder} and pass
     * your interface to {create} to generate an implementation.
     * .addConverterFactory
     * A Converter.Factory converter which uses Gson for JSON.
     * Add converter factory for serialization and deserialization of objects.
     * Because Gson is so flexible in the types it supports, this converter assumes that it can handle all types.
     * .addConverterFactory(GsonConverterFactory.create())
     * Add a call adapter factory for supporting service method return types other than.
     * .build()
     * Create retro fit instance using configured values
     * .create(RandomDishApi::class.java)
     * Create an implementation of the API endpoints
     * defined by the service interface in our case it is RandomDishAPI.
     */
    private val apiRegular = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RandomDishApi::class.java)

    private val apiRx = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(RandomDishApi::class.java)
}