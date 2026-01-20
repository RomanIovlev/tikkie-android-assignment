package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screens.PaymentsScreen
import com.abnamro.apps.referenceandroid.screens.AddPaymentStep1Screen
import com.abnamro.apps.referenceandroid.screens.AddPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.dataproviders.TikkieDataProviders.amountData
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class AmountInputValidationTest(
    private val input: String,
    private val result: String
) : TestSuite() {

    companion object {
        @JvmStatic
        @Parameters(name = "input: {0}, expected: {1}")
        fun data() = amountData()
    }

    @Test
    fun test() = run("Request Payment Step1 Amount Input Validation test - input '$input'") {
        PaymentsScreen { navigateToPaymentAmount() }
        AddPaymentStep1Screen {
            screenStep("Enter '$input'") { amountInput.replaceText(input) }
            screenStep("Verify input shows '$result'") { amountInput.hasText(result) }
            step("Click Next button") { nextButton.click() }
        }
        AddPaymentStep2Screen {
            screenStep("Verify Description input screen opened") {
                descriptionInput.isDisplayed()
            }
        }
    }
}
