package com.example.mydish.model.service.webservice

import com.example.mydish.utils.data.Constants

enum class EndPoint(val key: String, val value: Int) {
    DESSERT(Constants.TAGS_DESSERT_VALUE, Constants.NUMBER_DESSERT_VALUE),
    MEAL(Constants.TAGS_MEAL_VALUE, Constants.NUMBER_MEAL_VALUE),
    CUISINES(Constants.TAGS_CUISINES_VALUE, Constants.NUMBER_CUISINES_VALUE)
}

enum class EndPoints(val pair: Pair<String,Int>) {
    DESSERT(Pair(Constants.TAGS_DESSERT_VALUE, Constants.NUMBER_DESSERT_VALUE)),
    MEAL(Pair(Constants.TAGS_MEAL_VALUE, Constants.NUMBER_MEAL_VALUE)),
    CUISINES(Pair(Constants.TAGS_CUISINES_VALUE, Constants.NUMBER_CUISINES_VALUE))
}