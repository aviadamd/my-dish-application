package com.example.mydish.model.service.webservice

import com.example.mydish.di.ApiModule
import com.example.mydish.model.service.webservice.EndPoint.*
import com.example.mydish.utils.data.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.Response

//@InstallIn
class RandomDishesApiService {

    private val api = ApiModule()

    private val key = Constants.API_KEY_VALUE
    private val license = Constants.LIMIT_LICENSE_VALUE

    /*** implement method to get dishes api call */
    suspend fun getDishes(endPoint: EndPoint): Response<Recipes> {
        val recipe : Response<Recipes> =
            when(endPoint) {
                MEAL ->  api.provideRandomDishApi().getTheDishes(key, license, MEAL.key, MEAL.value)
                CUISINES -> api.provideRandomDishApi().getTheDishes(key, license, CUISINES.key, CUISINES.value)
                DESSERT -> api.provideRandomDishApi().getTheDishes(key, license, DESSERT.key, CUISINES.value)
        }
        return recipe
    }

    fun getDishesAsRetroFitWithRx(endPoint: EndPoint): Single<Recipes> {
        val recipe : Single<Recipes> =
            when(endPoint) {
                MEAL -> api.provideRandomDishApiRx().getTheDishesRx(key, license, MEAL.key, MEAL.value)
                CUISINES -> api.provideRandomDishApiRx().getTheDishesRx(key, license, CUISINES.key, CUISINES.value)
                DESSERT -> api.provideRandomDishApiRx().getTheDishesRx(key, license, DESSERT.key, CUISINES.value)
        }
        return recipe
    }
}