package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.mocks.PaymentMockHelper.setupMockedPayments
import com.abnamro.apps.referenceandroid.screens.MainScreen
import com.abnamro.apps.referenceandroid.screens.PaymentDetailsScreen
import com.abnamro.apps.referenceandroid.screens.AddPaymentStep1Screen
import com.abnamro.apps.referenceandroid.screens.AddPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.dinnerDetails
import com.abnamro.apps.referenceandroid.testdata.mockedAlicePayments
import com.abnamro.apps.referenceandroid.testdata.newPayment
import org.junit.Assert.assertEquals
import org.junit.Test

class TikkieSmokeTests : TestSuite() {
    @Test
    fun dinnerPaymentDetailsTest() = run("Dinner Payment Details test") {
        step("Setup mocked payment '${dinnerDetails.title}'") {
            setupMockedPayments(listOf(dinnerDetails), activityRule)
            step("$dinnerDetails") {}
        }
        MainScreen { selectPaymentByTitle(dinnerDetails.title) }
        PaymentDetailsScreen {
            verifyAllTexts(dinnerDetails)
            screenStep("Click back button") {
                backButton.click()
            }
        }
        MainScreen { verifyMainScreenOpen() }
    }

    @Test
    fun createNewTikkiePaymentTest() = run("Create New Tikkie Payment test") {
        MainScreen {
            verifyMainScreenOpen()
            screenStep("Click + sign button") {
                addPaymentButton.click()
            }
        }
        AddPaymentStep1Screen {
            val newPaymentAsString = newPayment.amount.toString().replace("\\.?0+$".toRegex(), "")
            screenStep("Type '$newPaymentAsString'") {
                amountInput.replaceText(newPaymentAsString)
            }
            screenStep("Click Next button") { nextButton.click() }
        }
        AddPaymentStep2Screen {
            screenStep("Type '${newPayment.title}'") {
                descriptionInput.typeText(newPayment.title)
            }
            step("Click Share button") { shareButton.click() }
        }
        PaymentDetailsScreen { verifyAllTexts(newPayment) }
    }

    @Test
    fun paymentsListTest() = run("Main Screen Displays All Payments test") {
        step("Setup 3 mocked payments") {
            setupMockedPayments(mockedAlicePayments, activityRule)
        }

        MainScreen {
            verifyMainScreenOpen()
            step("Verify payment list contains ${mockedAlicePayments.size} items") {
                paymentRecyclerView {
                    assertEquals(mockedAlicePayments.size, getSize())
                }
            }
            step("Verify all payment cards") {
                mockedAlicePayments.forEach { verifyPaymentCardDetails(it) }
            }
        }
    }
}
