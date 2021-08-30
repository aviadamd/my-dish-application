package com.example.mydish.model.api.webservice

import com.example.mydish.utils.Constants
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
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

    /**
     * Make a GET request. pass the endpoint of the URL that is defined in the Constants.
     * Values are the key of the header the value is called from RandomDishApiService
     *
     * RxJava - Single is something like an Observable,
     * but instead of emitting a series of values
     * anywhere from none at all to an infinite number
     * it always either emits one value or an error notification.
     */
    @GET(Constants.API_ENDPOINT)
    fun getTheDishesRx(
        @Query(Constants.API_KEY) apiKey : String,
        @Query(Constants.LIMIT_LICENSE) limitLicense : Boolean,
        @Query(Constants.TAGS) tags : String,
        @Query(Constants.NUMBER) number : Int,
    ): Single<RandomDish.Recipes>


    @GET(Constants.API_ENDPOINT)
    suspend fun gettingTheDishes(
        @Query(Constants.API_KEY) apiKey : String,
        @Query(Constants.LIMIT_LICENSE) limitLicense : Boolean,
        @Query(Constants.TAGS) tags : String,
        @Query(Constants.NUMBER) number : Int
    ): Flow<RandomDish.Recipes>

}