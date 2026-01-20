package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.TikkieRepository.restorePayments
import com.abnamro.apps.referenceandroid.mocks.PaymentMockHelper.setupMockedPayment
import com.abnamro.apps.referenceandroid.mocks.PaymentMockHelper.setupMockedPayments
import com.abnamro.apps.referenceandroid.screens.MainScreen
import com.abnamro.apps.referenceandroid.screens.PaymentDetailsScreen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep1Screen
import com.abnamro.apps.referenceandroid.screens.RequestPaymentStep2Screen
import com.abnamro.apps.referenceandroid.testdata.defaultDinnerPayment
import com.abnamro.apps.referenceandroid.testdata.dinnerDetails
import com.abnamro.apps.referenceandroid.testdata.mockedAlicePayments
import com.abnamro.apps.referenceandroid.testdata.newPayment
import org.junit.Assert.assertEquals
import org.junit.Test

class TikkieTests : TestSuite() {
    @Test
    fun dinnerPaymentDetailsTest() = run("Dinner Payment Details test") {
        step("Setup mocked payment '${dinnerDetails.title}'") {
            setupMockedPayment(dinnerDetails, activityRule)
            step("$dinnerDetails") {}
        }
        
        MainScreen {
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
            verifyTikkieAppOpen()
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

    @Test
    fun mainScreenDisplaysAllPaymentsTest() = run("Main Screen Displays All Payments test") {
        step("Setup 3 mocked payments") {
            setupMockedPayments(mockedAlicePayments, activityRule)
        }

        MainScreen {
            verifyTikkieAppOpen()
            screenStep("Verify payment list contains ${mockedAlicePayments.size} items") {
                paymentRecyclerView {
                    assertEquals(mockedAlicePayments.size, getSize())
                }
            }
            screenStep("Verify all payment cards") {
                mockedAlicePayments.forEach { payment ->
                    verifyPaymentCardDetails(payment)
                }
            }
        }
    }

    @Test
    fun mainScreenPaymentCardClickTest() = run("Main Screen Payment Card Click test") {
        step("Reset repository to default state") {
            restorePayments()
        }

        MainScreen {
            verifyTikkieAppOpen()
            selectPaymentByTitle("Coffee")
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity is displayed") {
                title.isDisplayed()
            }
            screenStep("Verify payment title matches 'Coffee'") {
                title.hasText("Coffee")
            }
            screenStep("Click back button") {
                backButton.click()
            }
        }
        MainScreen {
            screenStep("Verify MainActivity is displayed") {
                addPaymentButton.isDisplayed()
            }
            selectPaymentByTitle("Help friends")
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity is displayed") {
                title.isDisplayed()
            }
            screenStep("Verify payment title matches 'Help friends'") {
                title.hasText("Help friends")
            }
        }
    }

    @Test
    fun mainScreenFabButtonTest() = run("Main Screen FAB Button test") {
        MainScreen {
            verifyTikkieAppOpen()
            screenStep("Click FAB button") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Verify RequestPaymentStep1Activity is displayed") {
                amountInput.isDisplayed()
                nextButton.isDisplayed()
            }
        }
    }

    @Test
    fun mainScreenProfileMenuTest() = run("Main Screen Profile Menu test") {
        MainScreen {
            verifyTikkieAppOpen()
            screenStep("Click profile button") {
                profileButton.click()
            }
            screenStep("Verify popup menu is displayed") {
                settingsMenuItem.isDisplayed()
                logoutMenuItem.isDisplayed()
            }
            screenStep("Click Settings option") {
                settingsMenuItem.click()
            }
            screenStep("Click profile button again") {
                profileButton.click()
            }
            screenStep("Click Logout option") {
                logoutMenuItem.click()
            }
        }
    }

    @Test
    fun paymentDetailsDisplayAllFieldsTest() = run("Payment Details Display All Fields test") {
        step("Setup mocked payment with all fields") {
            setupMockedPayment(defaultDinnerPayment, activityRule)
        }

        MainScreen {
            selectPaymentByTitle(defaultDinnerPayment.title)
        }
        PaymentDetailsScreen {
            screenStep("Verify all fields are displayed") {
                title.isDisplayed()
                subtitle.isDisplayed()
                statusLabel.isDisplayed()
                totalSettled.isDisplayed()
                collapsibleContent.isDisplayed()
                paymentName.isDisplayed()
                amount.isDisplayed()
                tikkieId.isDisplayed()
                executionDate.isDisplayed()
                paidBy.isDisplayed()
                createdOn.isDisplayed()
            }
            screenStep("Verify payment details match expected values") {
                verifyAllTexts(defaultDinnerPayment)
            }
        }
    }

    @Test
    fun paymentDetailsMinimalDataTest() = run("Payment Details Minimal Data test") {
        step("Reset repository to default state") {
            restorePayments()
        }

        MainScreen {
            selectPaymentByTitle("Help friends")
        }
        PaymentDetailsScreen {
            screenStep("Verify basic fields are displayed") {
                title.isDisplayed()
                subtitle.isDisplayed()
                statusLabel.isDisplayed()
            }
            screenStep("Verify optional fields are not displayed") {
                tikkieId.isNotDisplayed()
                executionDate.isNotDisplayed()
                paidBy.isNotDisplayed()
                createdOn.isNotDisplayed()
            }
        }
    }

    @Test
    fun paymentDetailsUnpaidStatusTest() = run("Payment Details Unpaid Status test") {
        step("Reset repository to default state") {
            restorePayments()
        }

        MainScreen {
            selectPaymentByTitle("Coffee")
        }
        PaymentDetailsScreen {
            screenStep("Verify title is displayed") {
                title.isDisplayed()
                title.hasText("Coffee")
            }
            screenStep("Verify status button is not displayed") {
                statusLabel.isNotDisplayed()
            }
            screenStep("Verify other fields are displayed correctly") {
                subtitle.isDisplayed()
                totalSettled.isDisplayed()
            }
        }
    }

    @Test
    fun paymentDetailsBackButtonTest() = run("Payment Details Back Button test") {
        step("Reset repository to default state") {
            restorePayments()
        }

        MainScreen {
            selectPaymentByTitle("Coffee")
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity is displayed") {
                title.isDisplayed()
            }
            screenStep("Click back button") {
                backButton.click()
            }
        }
        MainScreen {
            verifyTikkieAppOpen()
            screenStep("Verify payment list is still displayed") {
                paymentRecyclerView {
                    assertEquals(3, getSize())
                }
            }
        }
    }

    @Test
    fun paymentDetailsDeleteButtonTest() = run("Payment Details Delete Button test") {
        step("Reset repository to default state") {
            restorePayments()
        }

        MainScreen {
            selectPaymentByTitle("Coffee")
        }
        PaymentDetailsScreen {
            screenStep("Verify delete button is displayed") {
                deleteButton.isDisplayed()
            }
            screenStep("Click delete button") {
                deleteButton.click()
            }
        }
        MainScreen {
            verifyTikkieAppOpen()
        }
    }

    @Test
    fun paymentDetailsCollapsibleSectionTest() = run("Payment Details Collapsible Section test") {
        step("Reset repository to default state") {
            restorePayments()
        }

        MainScreen {
            selectPaymentByTitle("Dinner")
        }
        PaymentDetailsScreen {
            screenStep("Verify collapsible content is visible (expanded)") {
                collapsibleContent.isDisplayed()
            }
            screenStep("Click on amount row to collapse") {
                amountRow.click()
            }
            screenStep("Verify collapsible content is hidden (collapsed)") {
                collapsibleContent.isNotDisplayed()
            }
            screenStep("Click expand button to expand again") {
                expandButton.click()
            }
            screenStep("Verify collapsible content is visible again") {
                collapsibleContent.isDisplayed()
            }
        }
    }

    @Test
    fun requestPaymentStep1AmountInputValidationTest() = run("Request Payment Step1 Amount Input Validation test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Verify amount input has default value") {
                amountInput.hasText(".00")
            }
            screenStep("Enter '50'") {
                amountInput.replaceText("50")
            }
            screenStep("Verify input shows '50'") {
                amountInput.hasText("50")
            }
            screenStep("Enter '.'") {
                amountInput.replaceText("50.")
            }
            screenStep("Verify input shows '50.'") {
                amountInput.hasText("50.")
            }
            screenStep("Enter '5'") {
                amountInput.replaceText("50.5")
            }
            screenStep("Verify input shows '50.5'") {
                amountInput.hasText("50.5")
            }
            screenStep("Enter '0'") {
                amountInput.replaceText("50.50")
            }
            screenStep("Verify input shows '50.50'") {
                amountInput.hasText("50.50")
            }
            screenStep("Clear input") {
                amountInput.replaceText("")
            }
            screenStep("Verify input resets to '.00'") {
                amountInput.hasText(".00")
            }
        }
    }

    @Test
    fun requestPaymentStep1MaxAmountLimitTest() = run("Request Payment Step1 Max Amount Limit test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter '1000'") {
                amountInput.replaceText("1000")
            }
            screenStep("Verify input is capped at '999'") {
                amountInput.hasText("999")
            }
            screenStep("Enter '999.99'") {
                amountInput.replaceText("999.99")
            }
            screenStep("Verify input shows '999.99'") {
                amountInput.hasText("999.99")
            }
            screenStep("Enter '1000.00'") {
                amountInput.replaceText("1000.00")
            }
            screenStep("Verify input is capped at '999'") {
                amountInput.hasText("999")
            }
        }
    }

    @Test
    fun requestPaymentStep1NextButtonValidationTest() = run("Request Payment Step1 Next Button Validation test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Verify next button is displayed") {
                nextButton.isDisplayed()
            }
            screenStep("Click next button with default '.00' value") {
                nextButton.click()
            }
            screenStep("Verify still on Step1 (amount is 0)") {
                amountInput.isDisplayed()
            }
            screenStep("Enter '0'") {
                amountInput.replaceText("0")
            }
            screenStep("Click next button with '0'") {
                nextButton.click()
            }
            screenStep("Verify still on Step1 (amount is 0)") {
                amountInput.isDisplayed()
            }
            screenStep("Enter '50'") {
                amountInput.replaceText("50")
            }
            screenStep("Click next button") {
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Verify RequestPaymentStep2Activity is displayed") {
                descriptionInput.isDisplayed()
            }
        }
    }

    @Test
    fun requestPaymentStep1BackButtonTest() = run("Request Payment Step1 Back Button test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Verify RequestPaymentStep1Activity is displayed") {
                amountInput.isDisplayed()
            }
            screenStep("Click back button") {
                backButton.click()
            }
        }
        MainScreen {
            verifyTikkieAppOpen()
        }
    }

    @Test
    fun requestPaymentStep1PayerChooseAmountToggleTest() = run("Request Payment Step1 Payer Choose Amount Toggle test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Verify toggle is displayed") {
                payerChooseAmountToggle.isDisplayed()
            }
            screenStep("Verify toggle is unchecked by default") {
                payerChooseAmountToggle.isNotChecked()
            }
            screenStep("Click toggle") {
                payerChooseAmountToggle.click()
            }
            screenStep("Verify toggle is checked") {
                payerChooseAmountToggle.isChecked()
            }
            screenStep("Click toggle again") {
                payerChooseAmountToggle.click()
            }
            screenStep("Verify toggle is unchecked") {
                payerChooseAmountToggle.isNotChecked()
            }
        }
    }

    // ========== RequestPaymentStep2Activity Tests ==========

    @Test
    fun requestPaymentStep2DescriptionInputTest() = run("Request Payment Step2 Description Input test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount and proceed") {
                amountInput.replaceText("50")
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Verify description input is displayed") {
                descriptionInput.isDisplayed()
            }
            screenStep("Verify character count is not displayed initially") {
                characterCount.isNotDisplayed()
            }
            screenStep("Enter 'Test payment'") {
                descriptionInput.typeText("Test payment")
            }
            screenStep("Verify character count shows '12'") {
                characterCount.hasText("12")
                characterCount.isDisplayed()
            }
            screenStep("Clear input") {
                descriptionInput.clearText()
            }
            screenStep("Verify character count is hidden") {
                characterCount.isNotDisplayed()
            }
        }
    }

    @Test
    fun requestPaymentStep2EmptyDescriptionValidationTest() = run("Request Payment Step2 Empty Description Validation test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount and proceed") {
                amountInput.replaceText("50")
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Verify description input is empty") {
                descriptionInput.hasText("")
            }
            screenStep("Click share button without description") {
                shareButton.click()
            }
            screenStep("Verify error message is displayed") {
                descriptionInput.hasText("Please enter a description")
            }
            screenStep("Verify still on Step2") {
                descriptionInput.isDisplayed()
            }
            screenStep("Enter description") {
                descriptionInput.typeText("Test payment")
            }
            screenStep("Click share button") {
                shareButton.click()
            }
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity is displayed") {
                title.isDisplayed()
            }
        }
    }

    @Test
    fun requestPaymentStep2ShareButtonTest() = run("Request Payment Step2 Share Button test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount and proceed") {
                amountInput.replaceText("75.50")
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Enter description") {
                descriptionInput.typeText("New test payment")
            }
            screenStep("Click Share button") {
                shareButton.click()
            }
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity is displayed") {
                title.isDisplayed()
            }
            screenStep("Verify payment details match") {
                title.hasText("New test payment")
                amount.hasText("€ 75.50")
            }
        }
    }

    @Test
    fun requestPaymentStep2BackButtonTest() = run("Request Payment Step2 Back Button test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount and proceed") {
                amountInput.replaceText("50")
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Enter some text") {
                descriptionInput.typeText("Test description")
            }
            screenStep("Click back button") {
                backButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Verify RequestPaymentStep1Activity is displayed") {
                amountInput.isDisplayed()
            }
            screenStep("Verify amount input still contains previous amount") {
                amountInput.hasText("50")
            }
        }
    }

    @Test
    fun requestPaymentStep2WhatsAppButtonTest() = run("Request Payment Step2 WhatsApp Button test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount and proceed") {
                amountInput.replaceText("100")
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Enter description") {
                descriptionInput.typeText("WhatsApp test payment")
            }
            screenStep("Click WhatsApp button") {
                whatsappButton.click()
            }
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity is displayed") {
                title.isDisplayed()
            }
            screenStep("Verify payment is created with correct description") {
                title.hasText("WhatsApp test payment")
            }
        }
    }

    @Test
    fun requestPaymentStep2QrCodeButtonTest() = run("Request Payment Step2 QR Code Button test") {
        MainScreen {
            screenStep("Navigate to payment creation") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount and proceed") {
                amountInput.replaceText("100")
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Enter description") {
                descriptionInput.typeText("QR Code test payment")
            }
            screenStep("Click QR Code button") {
                qrCodeButton.click()
            }
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity is displayed") {
                title.isDisplayed()
            }
            screenStep("Verify payment is created with correct description") {
                title.hasText("QR Code test payment")
            }
        }
    }

    @Test
    fun completePaymentCreationFlowTest() = run("Complete Payment Creation Flow test") {
        MainScreen {
            verifyTikkieAppOpen()
            screenStep("Click FAB button") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount: 100.00") {
                amountInput.replaceText("100.00")
            }
            screenStep("Toggle 'Payer choose amount' ON") {
                payerChooseAmountToggle.click()
                payerChooseAmountToggle.isChecked()
            }
            screenStep("Click next button") {
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Enter description: 'Complete flow test'") {
                descriptionInput.typeText("Complete flow test")
            }
            screenStep("Click Share button") {
                shareButton.click()
            }
        }
        PaymentDetailsScreen {
            screenStep("Verify PaymentDetailsActivity displays correct payment") {
                title.isDisplayed()
                title.hasText("Complete flow test")
                amount.hasText("€ 100.00")
            }
        }
    }

    @Test
    fun paymentCreationFlowBackNavigationTest() = run("Payment Creation Flow Back Navigation test") {
        MainScreen {
            screenStep("Start payment creation flow") {
                addPaymentButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Enter amount in Step 1") {
                amountInput.replaceText("50")
            }
            screenStep("Navigate to Step 2") {
                nextButton.click()
            }
        }
        RequestPaymentStep2Screen {
            screenStep("Click back button") {
                backButton.click()
            }
        }
        RequestPaymentStep1Screen {
            screenStep("Verify Step 1 is displayed with amount preserved") {
                amountInput.isDisplayed()
                amountInput.hasText("50")
            }
            screenStep("Click back button") {
                backButton.click()
            }
        }
        MainScreen {
            verifyTikkieAppOpen()
        }
    }
}
