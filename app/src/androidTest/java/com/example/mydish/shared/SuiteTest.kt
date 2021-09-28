package com.example.mydish.shared

import com.example.mydish.model.database.MyDishDaoTest
import com.example.mydish.model.repository.MyDishRepositoryTest
import com.example.mydish.viewmodel.MyDishesViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MyDishDaoTest::class,
    MyDishRepositoryTest::class,
    MyDishesViewModelTest::class
) class SuiteTest