package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screens.MainScreen
import com.abnamro.apps.referenceandroid.screens.PaymentDetailsScreen
import com.abnamro.apps.referenceandroid.testdata.dinnerDetails
import org.junit.Test

class PaymentDetailsTests : TestSuite() {
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
}
