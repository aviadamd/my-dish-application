package com.example.mydish.model.api.webservice

import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Response

interface RandomDishService {

    interface RandomDishServiceResponse {
        suspend fun getDishes(endPoint: EndPoint): Response<RandomDish.Recipes>
    }

    interface RandomDishServiceResponseRx {
        fun getDishesAsRetroFitWithRx(endPoint: EndPoint): Single<RandomDish.Recipes>
    }
}