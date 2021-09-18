package com.example.mydish.model.database

object MyDishDataBaseQueries {

    const val GET_ALL_DISHES = "SELECT * FROM MY_DISH_TABLE ORDER BY ID"
    const val GET_FAVORITES_DISHES = "SELECT * FROM MY_DISH_TABLE WHERE favoriteDish = 1"
    const val GET_FILTERED_DISHES = "SELECT * FROM MY_DISH_TABLE WHERE type = :filterType"

}