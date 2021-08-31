package com.example.mydish.utils.data

object Constants {

    const val DISH_TYPE : String = "DishType"
    const val DISH_CATEGORY : String = "DishCategory"
    const val DISH_COOKING_TIME = "DishCookingTime"
    const val DISH_IMAGE_SOURCE_LOCAL : String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE : String = "Online"
    const val EXTRA_DISH_DETAILS : String = "DishDetails"

    const val DURATION : Long = 300

    const val ALL_ITEMS : String = "All"
    const val FILTER_SELECTION : String = "FilterSelection"

    //Notifications constants
    const val NOTIFICATION_ID = "MyDish_notification_id"
    const val NOTIFICATION_NAME = "MyDish"
    const val NOTIFICATION_CHANNEL = "MyDish_channel_01"


    //Api constants
    const val BASE_URL : String = "https://api.spoonacular.com/"
    const val API_ENDPOINT : String = "recipes/random"

    //Api strings key params
    const val API_KEY : String = "apiKey"
    const val LIMIT_LICENSE : String = "limitLicense"
    const val TAGS : String = "tags"
    const val NUMBER : String = "number"
    //Api strings value params
    const val API_KEY_VALUE : String = "8d1a63a8ea1e4017a09ab9e8970d3d35"
    const val LIMIT_LICENSE_VALUE : Boolean = true

    //can be diets, meal types, cuisines, or intolerances
    const val TAGS_DESSERT_VALUE : String = "dessert"
    const val NUMBER_DESSERT_VALUE : Int = 0

    const val TAGS_MEAL_VALUE : String = "meal types"
    const val NUMBER_MEAL_VALUE : Int = 0

    const val TAGS_CUISINES_VALUE : String = "cuisines"
    const val NUMBER_CUISINES_VALUE : Int = 0
}