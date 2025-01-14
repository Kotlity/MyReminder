package com.kotlity

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.kotlity.utils.TestApplication

class InstrumentationTestRunner: AndroidJUnitRunner() {

    override fun newApplication(classLoader: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(classLoader, TestApplication::class.java.name, context)
    }
}