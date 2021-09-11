package com.example.mydish.di

import com.example.mydish.model.service.webservice.RandomDishesApiService
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: RandomDishesApiService)
}