package com.example.mydish.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.mydish.api.webservice.RandomDish

/** Create MutableLiveData with no value assigned to it **/
/** Call randomDishResponse from this class and from the RandomDishFragment **/
/** Call randomDishError from this class and from the RandomDishFragment **/
data class RandomViewModelLiveDataHolder(
    val loadData: MutableLiveData<Boolean>,
    val recipesData: MutableLiveData<RandomDish.Recipes>,
    val errors: MutableLiveData<Boolean>)


