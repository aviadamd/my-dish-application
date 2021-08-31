package com.example.mydish.utils

import android.annotation.SuppressLint
import android.content.Context
import com.example.mydish.R

fun readFromSharedPref(context: Context, key: String?, defaultValue: String?): String {
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    return sharedPref.getString(key, defaultValue)!!
}

fun writeToSharedPref(context: Context, key: String?, value: String?) {
    val sharedPref = context.getSharedPreferences(
        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
    )
    val editor = sharedPref.edit()
    editor.putString(key, value)
    editor.apply()
}

@SuppressLint("ApplySharedPref")
fun clearSharedPref(context: Context) {
    val sharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file_key),
        Context.MODE_PRIVATE
    )
    val editor = sharedPreferences.edit()
    editor.clear().commit()
}