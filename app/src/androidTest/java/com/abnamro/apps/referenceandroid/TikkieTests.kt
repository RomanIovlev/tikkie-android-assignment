package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.mocks.PaymentMockHelper.setupMockedPayment
import com.abnamro.apps.referenceandroid.screens.MainScreen
import com.abnamro.apps.referenceandroid.screens.PaymentDetailsScreen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep1Screen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.dinnerDetails
import com.abnamro.apps.referenceandroid.testdata.newPayment
import org.junit.Test

class TikkieTests : TestSuite() {
    @Test
    fun dinnerPaymentDetailsTest() = run("Dinner Payment Details test") {
        step("Setup mocked payment '${dinnerDetails.title}'") {
            setupMockedPayment(dinnerDetails, activityRule)
            step("${dinnerDetails}") {}
        }
        
        MainScreen {
            screenStep("Tikkie app main screen open") {
                addPaymentButton.isDisplayed()
            }
            selectPaymentByTitle(dinnerDetails.title)
        }
        PaymentDetailsScreen {
            screenStep("Check '${dinnerDetails.title}' Payment details") {
                verifyAllTexts(dinnerDetails)
            }
        }
    }
    @Test
    fun createNewTikkiePaymentTest() = run("Create New Tikkie Payment test") {
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
            screenStep("Check newly created Payment details") {
                verifyAllTexts(newPayment)
            }
        }
    }
}
