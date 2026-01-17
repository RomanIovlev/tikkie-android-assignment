package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screen.MainScreen
import org.junit.Test

class SmokeTests : TestSuite() {
    @Test
    fun runAppSuccessful() = run {
        step("Verify Tikkie app is running and MainScreen elements are displayed") {
            MainScreen {
                toolbar.isVisible()
                fab.isVisible()
            }
        }
    }
}
