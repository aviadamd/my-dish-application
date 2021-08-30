package com.example.mydish.model.api.webservice

import retrofit2.Response

interface RandomDishService {

    suspend fun getDishes(endPoint: EndPoint): Response<RandomDish.Recipes>
}