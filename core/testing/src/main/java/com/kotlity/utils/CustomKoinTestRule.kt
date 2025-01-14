package com.kotlity.utils

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.getKoinApplicationOrNull
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class CustomKoinTestRule(private val modules: List<Module>): TestWatcher() {

    override fun starting(description: Description?) {
        if (getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
                androidLogger()
                modules(modules)
            }
        } else loadKoinModules(modules)
    }

    override fun finished(description: Description?) {
        unloadKoinModules(modules)
    }
}