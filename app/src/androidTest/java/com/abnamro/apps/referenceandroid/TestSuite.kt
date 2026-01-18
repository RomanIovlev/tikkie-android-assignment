package com.abnamro.apps.referenceandroid

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import org.junit.Rule

abstract class TestSuite : TestCase(
    kaspressoBuilder = com.kaspersky.kaspresso.kaspresso.Kaspresso.Builder.advanced()
) {
    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()
}

fun TestContext<*>.screenStep(stepName: String, action: TestContext<*>.() -> Unit) {
    step(stepName) {
        try {
            action()
        } catch (ex: Exception) {
            throw ex
        } finally {
            val screenshotName = stepName.lowercase()
                .replace(" ", "_")
                .replace(Regex("[^a-z0-9_]"), "")
            device.screenshots.take(screenshotName)
        }
    }
}
