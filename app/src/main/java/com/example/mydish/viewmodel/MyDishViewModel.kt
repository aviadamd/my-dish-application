package com.example.mydish.viewmodel

import androidx.lifecycle.*
import com.example.mydish.model.repository.MyDishRepository
import com.example.mydish.model.entities.MyDishEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

/**
 * The ViewModel's role is to provide data to the UI and survive configuration changes.
 * A ViewModel acts as a communication center between the Repository and the UI.
 * You can also use a ViewModel to share data between fragments.
 * The ViewModel is part of the lifecycle library.
 *
 * The UI no longer needs to worry about the origin of the data.
 * ViewModel instances survive Activity/Fragment recreation.
 * Classes that use MyDishViewModel
 *
 * This view model is responsible for the room data in/out operations
 * AddUpdateDishActivity
 * AllDishesFragment
 * DishDetailsFragment
 * FavoriteDishFragment
 * RandomDishFragment
 *
 * allDishesList/favoriteDishes/filteredListDishes wrapped as live data
 * insert/update/delete wrapped as coroutine launched scooped
 */
/*** @param myDishRepository - The repository class */
class MyDishViewModel(private val myDishRepository : MyDishRepository) : ViewModel() {

    /*** asLiveData() merge between LiveData [Flow] && Coroutines*/

    /*** Use live data to observe the cashing data from all dishes list */
    val allDishesList : LiveData<List<MyDishEntity>> = myDishRepository.allDishesList.asLiveData()

    /*** Use live data to observe the cashing data from favorites dishes list */
    val favoriteDishes : LiveData<List<MyDishEntity>> = myDishRepository.favoriteDishes.asLiveData()

    /*** Use live data to observe the cashing data from filter favorite dishes list */
    fun filteredListDishes(value : String) : LiveData<List<MyDishEntity>> =
        myDishRepository.filteredListDishes(value).asLiveData()

    /*** Launching a new coroutine to insert the data in a non-blocking way. */
    fun insert(myDishEntity : MyDishEntity) = viewModelScope.launch {
        myDishRepository.insertMyDishData(myDishEntity)
    }

    /*** Launching a new coroutine to update the data in a non-blocking way. */
    fun update(myDishEntity: MyDishEntity) = viewModelScope.launch {
        myDishRepository.updateMyDishData(myDishEntity)
    }

    /*** Launching a new coroutine to delete the data in a non-blocking way. */
    fun delete(myDishEntity: MyDishEntity) = viewModelScope.launch {
        myDishRepository.deleteMyDishData(myDishEntity)
    }
}

/**
 * To create the ViewModel we implement a ViewModelProvider.Factory that gets as a parameter the dependencies
 * needed to create MyDishViewModel: the MyDishRepository.
 * By using viewModels and ViewModelProvider.Factory then the framework will take care of the lifecycle of the ViewModel.
 * It will survive configuration changes and even if the Activity is recreated,
 * you'll always get the right instance of the MyDishViewModel class.
 * This class called every time that need to init MyDishViewModel
 */
@Suppress("UNCHECKED_CAST")
class MyDishViewModelFactory(private val myDishRepository : MyDishRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyDishViewModel::class.java)) {
            return MyDishViewModel(myDishRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}