package com.example.mydish.viewmodel

import com.example.mydish.model.service.webservice.Recipes

sealed class ResourceState {
    object Empty: ResourceState()
    data class Load(var load: Boolean): ResourceState()
    data class Errors(var error: String): ResourceState()
    data class Service(var randomDishApi: Recipes?): ResourceState()
}
