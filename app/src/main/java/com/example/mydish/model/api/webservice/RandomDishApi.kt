package com.example.mydish.model.api.webservice

import com.example.mydish.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/*** RetroFit */
interface RandomDishApi {

    /**
     * Make a GET request. pass the endpoint of the URL that is defined in the Constants.
     * Values are the key of the header the value is called from RandomDishApiService
     */
    @GET(Constants.API_ENDPOINT)
    suspend fun getTheDishes(
        @Query(Constants.API_KEY) apiKey : String,
        @Query(Constants.LIMIT_LICENSE) limitLicense : Boolean,
        @Query(Constants.TAGS) tags : String,
        @Query(Constants.NUMBER) number : Int
    ): Response<RandomDish.Recipes>
}