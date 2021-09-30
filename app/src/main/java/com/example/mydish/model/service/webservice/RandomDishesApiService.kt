package com.example.mydish.model.service.webservice

import com.example.mydish.di.AppModule
import com.example.mydish.model.service.webservice.EndPoint.*
import com.example.mydish.utils.data.Constants
import retrofit2.Response
import timber.log.Timber

class RandomDishesApiService {

    /*** implement method to get dishes api call */
    suspend fun getDishes(endPoint: EndPoint): Response<Recipes> {
        val appModule = AppModule()
        val key = Constants.API_KEY_VALUE
        val license = Constants.LIMIT_LICENSE_VALUE
        val recipe: Response<Recipes> = when(endPoint) {
            MEAL ->  appModule.provideRandomDishApi().getTheDishes(key, license, MEAL.key, MEAL.value)
            CUISINES -> appModule.provideRandomDishApi().getTheDishes(key, license, CUISINES.key, CUISINES.value)
            DESSERT -> appModule.provideRandomDishApi().getTheDishes(key, license, DESSERT.key, CUISINES.value)
        }
        return recipe
    }

}