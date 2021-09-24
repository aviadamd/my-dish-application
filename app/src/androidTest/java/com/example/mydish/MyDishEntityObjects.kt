package com.example.mydish

import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.data.Constants

object MyDishEntityObjects {

    val entityObject1 = MyDishEntity(
        image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
        imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
        title = "my dish title one",
        type = "dessert one",
        category = "other",
        ingredients = "no need",
        cooking_time = "70",
        direction_to_cook = "with love",
        favoriteDish = true,
        id = 1
    )

    val entityObject2 = MyDishEntity(
        image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
        imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
        title = "my dish title one",
        type = "dessert one",
        category = "other",
        ingredients = "no need",
        cooking_time = "70",
        direction_to_cook = "with love",
        favoriteDish = true,
        id = 2
    )
}