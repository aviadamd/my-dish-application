package com.example.mydish.model.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Annotated class that describes a database table when working with Room.
 * This class hold structure's data base elements
 */
@kotlinx.parcelize.Parcelize
@Entity(tableName = "my_dish_table")
data class  MyDishEntity(
    @ColumnInfo val image : String,
    /** Local or Online **/
    @ColumnInfo(name = "image_source") val imageSource: String,
    @ColumnInfo val title: String,
    @ColumnInfo val type: String,
    @ColumnInfo val category: String,
    @ColumnInfo val ingredients: String,
    /**
     * Specifies the name of the column in the table if you want it to be different
     * from the name of the member variable.
     */
    @ColumnInfo(name = "cookingTime") val cooking_time: String,
    @ColumnInfo(name = "instructions") val direction_to_cook: String,
    @ColumnInfo(name = "favoriteDish") var favoriteDish: Boolean = false,
    /** Get id of each item **/
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable
