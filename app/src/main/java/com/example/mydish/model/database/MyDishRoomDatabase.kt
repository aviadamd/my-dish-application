package com.example.mydish.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mydish.model.entities.MyDishEntity

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 * If need more then one Dao then this class is the place for it
 */
@Database(entities = [MyDishEntity::class], version = 1)
abstract class MyDishRoomDatabase : RoomDatabase() {

    /**
     * Get the repository variable instance , this is called from MyDishApplication
     * MyDishApplication as val repository by lazy { MyDishRepository(database.myDishDao()) }
     * And each Fragment or Activity will delegate the instance from the ViewModel classes
     **/
    abstract fun myDishDao(): MyDishDao

    companion object {
        /*** Singleton prevents multiple instances of database opening at the same time. */
        /*** Volatile keyword guarantees that value of the volatile variable will always be read from main memory and not from Thread's local cache.*/
        @Volatile
        private var INSTANCE: MyDishRoomDatabase? = null

        fun getDatabase(context: Context): MyDishRoomDatabase {
            /*** if the INSTANCE is not null, then return it, if it is, then create the database */
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    /** context.applicationContext The context for the database. This is usually the Application context. */
                    /** MyDishRoomDatabase         The abstract class which is annotated with Database and extends RoomDatabase. */
                    /** my_dish_database           The name of the database file. */
                    .databaseBuilder(context.applicationContext, MyDishRoomDatabase::class.java,"my_dish_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}