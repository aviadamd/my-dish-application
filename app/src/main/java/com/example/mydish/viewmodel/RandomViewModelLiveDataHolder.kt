package com.example.mydish.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.mydish.model.api.webservice.RandomDish

/** Create MutableLiveData with no value assigned to it **/
/** Call randomDishResponse from this class and from the RandomDishFragment **/
/** Call randomDishError from this class and from the RandomDishFragment **/
data class RandomViewModelLiveDataHolder(
    val loadData: MutableLiveData<Pair<Boolean,Boolean>>,
    val recipesData: MutableLiveData<RandomDish.Recipes>)


