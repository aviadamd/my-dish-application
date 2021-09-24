package com.example.mydish

import androidx.lifecycle.MutableLiveData
import com.example.mydish.model.database.MyDishDao
import com.example.mydish.model.entities.MyDishEntity

class MyDishFakeRepository() {

    private val myDishesEntity = mutableListOf<MyDishEntity>()
    private val observableMyDishEntity = MutableLiveData<List<MyDishEntity>>(myDishesEntity)

    fun allDishesList(): List<MyDishEntity> {
        observableMyDishEntity.postValue(myDishesEntity)
        return myDishesEntity
    }

    fun favoriteDishes(): List<MyDishEntity> {
        return myDishesEntity.filter { it.favoriteDish }
    }

    fun filteredListDishes(index: Int, value : String): List<MyDishEntity> {
        if (myDishesEntity[index].favoriteDish && myDishesEntity[index].title.contains(value)) {
            return myDishesEntity
        }
        return emptyList()
    }

    fun insertMyDishData(myDishEntity: MyDishEntity) {
        myDishesEntity.add(myDishEntity)
        observableMyDishEntity.postValue(myDishesEntity)
    }

    fun updateMyDishData(index: Int, myDishEntity: MyDishEntity) {
        observableMyDishEntity.postValue(myDishesEntity)
        myDishesEntity[index] = myDishEntity
    }

    fun deleteMyDishData(myDishEntity: MyDishEntity) {
        observableMyDishEntity.postValue(myDishesEntity)
        myDishesEntity.remove(myDishEntity)
    }
}