package com.example.mydish.di

import com.example.mydish.model.service.webservice.RandomDishApi
import com.example.mydish.utils.data.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

//    @Provides
//    @Singleton
//    fun provideMyDishDatabase(@ApplicationContext context: Context) =
//        Room.databaseBuilder(context, MyDishRoomDatabase::class.java, DATA_BASE_NAME).build()
//
//    @Provides
//    @Singleton
//    fun provideMyDishDao(database: MyDishRoomDatabase) = database.myDishDao()
//
//    @Provides
//    @Singleton
//    fun provideMyDishRepository(myDishDao: MyDishDao) = MyDishRepository(myDishDao)

    @Provides
    @Singleton
    suspend fun provideRandomDishApi(): RandomDishApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RandomDishApi::class.java)
    }
}