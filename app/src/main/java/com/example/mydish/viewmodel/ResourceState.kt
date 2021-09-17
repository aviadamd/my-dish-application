package com.example.mydish.viewmodel

import com.example.mydish.model.service.webservice.Recipes

sealed class ResourceState {
    object Empty: ResourceState()
    data class Load(var load: Boolean): ResourceState()
    data class Errors(var error: String): ResourceState()
    data class Service(var randomDishApi: Recipes?): ResourceState()
}


//
//sealed class Resource<T>(val data: T? = null, val message: String? = null) {
//    class Success<T>(data: T) : Resource<T>(data)
//    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
//    class Loading<T>(data: T? = null) : Resource<T>(data)
//}
