package com.example.mydish.di

import com.example.mydish.model.service.webservice.RandomDishApi
import com.example.mydish.utils.data.Constants
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

//@Module
class ApiModule {

  //  @Provides
    fun provideRandomDishApi(): RandomDishApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomDishApi::class.java)
    }

    //@Provides
    fun provideRandomDishApiRx(): RandomDishApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(RandomDishApi::class.java)
    }

}