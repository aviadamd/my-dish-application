package com.example.mydish.model.service.webservice

import com.example.mydish.model.service.webservice.EndPoint.*
import com.example.mydish.utils.data.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext

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