package com.example.mydish.shared

import com.example.mydish.model.entities.MyDishEntity
import com.example.mydish.utils.data.Constants

object MyDishEntityObjects {

    fun getDishEntity(id: Int): MyDishEntity {
        return setDishEntity(
            image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
            title = "my dish title one",
            type = "dessert",
            category = "other",
            ingredients = "no need",
            cookingTime = "70",
            directionToCook = "with love",
            isFavoriteDish = true,
            id = id
        )
    }

    fun getDishEntity(id: Int, type: String): MyDishEntity {
        return MyDishEntity(
            image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
            imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
            title = "my dish title one",
            type = type,
            category = "other",
            ingredients = "no need",
            cooking_time = "70",
            direction_to_cook = "with love",
            favoriteDish = true,
            id = id
        )
    }

    fun getDishEntity(id: Int, favoriteDish: Boolean): MyDishEntity {
        return MyDishEntity(
            image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
            imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
            title = "my dish title one",
            type = "dessert",
            category = "other",
            ingredients = "no need",
            cooking_time = "70",
            direction_to_cook = "with love",
            favoriteDish = favoriteDish,
            id = id
        )
    }

    fun getDishEntity(id: Int, type: String, favoriteDish: Boolean): MyDishEntity {
        return MyDishEntity(
            image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
            imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
            title = "my dish title one",
            type = type,
            category = "other",
            ingredients = "no need",
            cooking_time = "70",
            direction_to_cook = "with love",
            favoriteDish = favoriteDish,
            id = id
        )
    }

    fun getDishEntity(cookingTime: String, title: String, type: String, id: Int, favoriteDish: Boolean): MyDishEntity {
        return setDishEntity(
            image = "https://spoonacular.com/recipeImages/664473-556x370.jpg",
            title = title,
            type = type,
            category = "other",
            ingredients = "no need",
            cookingTime = cookingTime,
            directionToCook = "with love",
            isFavoriteDish = favoriteDish,
            id = id
        )
    }

    @Suppress("SameParameterValue")
    private fun setDishEntity(
        image: String,
        title: String,
        type: String,
        category: String,
        id: Int,
        ingredients: String,
        cookingTime: String,
        directionToCook: String,
        isFavoriteDish: Boolean,
    ): MyDishEntity {
        return MyDishEntity(
            image = image,
            imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL,
            title = title,
            type = type,
            category = category,
            ingredients = ingredients,
            cooking_time = cookingTime,
            direction_to_cook = directionToCook,
            favoriteDish = isFavoriteDish,
            id = id
        )
    }
}