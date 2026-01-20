package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screens.PaymentsScreen
import com.abnamro.apps.referenceandroid.screens.AddPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.dataproviders.TikkieDataProviders.descriptionData
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class DescriptionInputValidationTest(
    private val input: String,
    private val result: String
) : TestSuite() {

    companion object {
        @JvmStatic
        @Parameters(name = "description: {0}, expectedCount: {1}")
        fun data() = descriptionData()
    }

    @Test
    fun test() = run("Request Payment Step2 Description Input test - description '$input'") {
        PaymentsScreen { navigateToPaymentDescription() }
        AddPaymentStep2Screen {
            screenStep("Verify description input is displayed") { descriptionInput.isDisplayed() }
            screenStep("Verify character count is not displayed initially") { characterCount.isNotDisplayed() }
            if (input.isNotEmpty()) {
                screenStep("Enter '$input'") { descriptionInput.typeText(input) }
                screenStep("Verify character count shows '$result'") {
                    characterCount.hasText(result)
                    characterCount.isDisplayed()
                }
                screenStep("Clear input") { descriptionInput.clearText() }
            }
            screenStep("Verify character count is hidden") { characterCount.isNotDisplayed() }
        }
    }
}
