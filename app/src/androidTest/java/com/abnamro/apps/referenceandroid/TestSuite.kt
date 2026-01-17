package com.abnamro.apps.referenceandroid

import androidx.test.ext.junit.rules.activityScenarioRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Rule

abstract class TestSuite : TestCase(
    kaspressoBuilder = com.kaspersky.kaspresso.kaspresso.Kaspresso.Builder.advanced()
) {
    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()
}
