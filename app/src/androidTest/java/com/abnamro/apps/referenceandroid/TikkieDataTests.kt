package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screens.MainScreen
import com.abnamro.apps.referenceandroid.screens.AddPaymentStep1Screen
import com.abnamro.apps.referenceandroid.screens.AddPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.dataproviders.TikkieDataProviders
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class TikkieDataTests : TestSuite() {

    @Test
    @Parameters(source = TikkieDataProviders::class, method = "amountValidationData")
    fun amountInputValidation(input: String, result: String) = run("Request Payment Step1 Amount Input Validation test - input '$input'") {
        MainScreen { navigateToPaymentAmount() }
        AddPaymentStep1Screen {
            screenStep("Enter '$input'") { amountInput.replaceText(input) }
            screenStep("Verify input shows '$result'") { amountInput.hasText(result) }
        }
    }

    @Test
    @Parameters(source = TikkieDataProviders::class, method = "maxAmountLimitData")
    fun maxAmountLimitValidation(input: String, result: String) = run("Request Payment Step1 Max Amount Limit test - input '$input'") {
        MainScreen { navigateToPaymentAmount() }
        AddPaymentStep1Screen {
            screenStep("Enter '$input'") { amountInput.replaceText(input) }
            screenStep("Verify input shows '$result'") { amountInput.hasText(result) }
        }
    }

    @Test
    @Parameters(source = TikkieDataProviders::class, method = "unallowedAmountData")
    fun nextButtonValidation(input: String) = run("Request Payment Step1 Next Button Validation test - amount '$input'") {
        MainScreen { navigateToPaymentAmount() }
        AddPaymentStep1Screen {
            screenStep("Verify next button is displayed") { nextButton.isDisplayed() }
            if (input != ".00") {
                screenStep("Enter '$input'") { amountInput.replaceText(input) }
            }
            screenStep("Click next button") { nextButton.click() }
            screenStep("Verify still on Step1 (amount is 0)") { amountInput.isDisplayed() }
        }
    }

    @Test
    @Parameters(source = TikkieDataProviders::class, method = "descriptionData")
    fun descriptionInputValidation(input: String, result: String) = run("Request Payment Step2 Description Input test - description '$input'") {
        MainScreen { navigateToPaymentDescription() }
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
