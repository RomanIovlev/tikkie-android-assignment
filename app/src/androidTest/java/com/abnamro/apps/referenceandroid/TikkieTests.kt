package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screens.MainScreen
import com.abnamro.apps.referenceandroid.screens.PaymentDetailsScreen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep1Screen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.dinnerDetails
import com.abnamro.apps.referenceandroid.testdata.newPayment
import org.junit.Test

class TikkieTests : TestSuite() {
    @Test
    fun testDinnerPaymentDetails() = run("Test Dinner Payment Details") {
        MainScreen {
            screenStep("Tikkie app main screen open") {
                addPaymentButton.isDisplayed()
            }
            selectPaymentByTitle(dinnerDetails.title)
        }
        PaymentDetailsScreen {
            screenStep("Check all texts on Dinner details") {
                verifyAllTexts(dinnerDetails)
            }
        }
    }
    @Test
    fun testRequestPaymentFlow() = run("Test Request Payment Flow") {
        MainScreen {
            screenStep("Tikkie app main screen open") {
                addPaymentButton.isDisplayed()
            }
            screenStep("Click + sign button") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            val newPaymentAsString = newPayment.amount.toString().replace("\\.?0+$".toRegex(), "")
            screenStep("Type '$newPaymentAsString'") {
                amountInput.replaceText(newPaymentAsString)
            }
            screenStep("Click Next button") {
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Type '${newPayment.title}'") {
                descriptionInput.typeText(newPayment.title)
            }
            step("Click Share button") {
                shareButton.click()
            }
        }
        PaymentDetailsScreen {
            screenStep("Check that entered values shown on result screen") {
                verifyAllTexts(newPayment)
            }
        }
    }
}
