package com.example.mydish.model.api.webservice

import com.example.mydish.utils.Constants

enum class EndPoint(val key: String, val value: Int) {
    DESSERT(Constants.TAGS_DESSERT_VALUE, Constants.NUMBER_DESSERT_VALUE),
    MEAL(Constants.TAGS_MEAL_VALUE, Constants.NUMBER_MEAL_VALUE),
    CUISINES(Constants.TAGS_CUISINES_VALUE, Constants.NUMBER_CUISINES_VALUE),
}