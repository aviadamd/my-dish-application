package com.example.mydish.model.api.webservicedemo

import com.example.mydish.model.api.webservice.EndPoint
import com.example.mydish.model.api.webservice.RandomDish
import com.example.mydish.model.api.webservice.RandomDishApi
import com.example.mydish.utils.data.Constants
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RandomDishesApiServiceFlow {

    /**
     * Retrofit adapts a Java interface to HTTP calls by using annotations on the declared methods to
     * define how requests are made. Create instances using {@linkplain Builder the builder} and pass
     * your interface to {create} to generate an implementation.
     *
     * .addConverterFactory
     * A Converter.Factory converter which uses Gson for JSON.
     * Add converter factory for serialization and deserialization of objects.
     * Because Gson is so flexible in the types it supports, this converter assumes that it can handle all types.
     *
     * .addConverterFactory(GsonConverterFactory.create())
     * Add a call adapter factory for supporting service method return types other than.
     *
     * .build()
     * Create retro fit instance using configured values
     *
     * .create(RandomDishApi::class.java)
     * Create an implementation of the API endpoints
     * defined by the service interface in our case it is RandomDishAPI.
     */
    private val api = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RandomDishApi::class.java)

    fun getDishFlow(endPoint: EndPoint): Flow<RandomDish.Recipes> {
        val key = Constants.API_KEY_VALUE
        val license = Constants.LIMIT_LICENSE_VALUE

        return flow {
            val recipe : Flow<RandomDish.Recipes> = when(endPoint) {
                EndPoint.MEAL -> api.getTheDishesFlow(key, license, EndPoint.MEAL.key, EndPoint.MEAL.value)
                EndPoint.CUISINES -> api.getTheDishesFlow(key, license, EndPoint.CUISINES.key, EndPoint.CUISINES.value)
                EndPoint.DESSERT -> api.getTheDishesFlow(key, license, EndPoint.DESSERT.key, EndPoint.CUISINES.value)
            }
            emit(recipe.single())
            delay(2000L)
        }
    }
}