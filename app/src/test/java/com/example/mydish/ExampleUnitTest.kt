package com.example.mydish

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val result = appContext.packageName.equals("com.example.mydish")
        Truth.assertThat(result).isTrue()
    }
}