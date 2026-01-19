package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screens.MainScreen
import com.abnamro.apps.referenceandroid.screens.PaymentDetailsScreen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep1Screen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.newPayment
import org.junit.Test

class RequestPaymentTests : TestSuite() {
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
            screenStep("Type '${newPayment.amount}'") {
                amountInput.replaceText(newPayment.amount.toString())
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
