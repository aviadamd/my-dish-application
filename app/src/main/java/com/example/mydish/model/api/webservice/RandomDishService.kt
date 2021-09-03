package com.example.mydish.model.api.webservice

import retrofit2.Response

interface RandomDishService {

    /*** suspend fun to be override within the implement class */
    suspend fun getDishes(endPoint: EndPoint): Response<RandomDish.Recipes>
}