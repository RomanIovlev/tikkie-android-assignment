package com.abnamro.apps.referenceandroid.screens

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.abnamro.apps.referenceandroid.R
import com.abnamro.apps.referenceandroid.screenStep
import com.abnamro.apps.referenceandroid.testdata.dataclass.PaymentData
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object PaymentsScreen : Screen<PaymentsScreen>() {
    val addPaymentButton = KImageView { withId(R.id.addPaymentButton) }
    val paymentRecyclerView = KRecyclerView(
        builder = { withId(R.id.paymentRecyclerView) },
        itemTypeBuilder = { itemType(::PaymentItem) }
    )

    fun TestContext<*>.navigateToPaymentAmount() {
        screenStep("Navigate to create payment Amount screen") {
            addPaymentButton.click()
        }
    }
    fun TestContext<*>.navigateToPaymentDescription() {
        screenStep("Navigate to create payment Description screen") {
            addPaymentButton.click()
            AddPaymentStep1Screen {
                amountInput.replaceText("50")
                nextButton.click()
            }
        }
    }
    fun TestContext<*>.verifyMainScreenOpen() {
        screenStep("Tikkie app Main screen shown") {
            addPaymentButton.isDisplayed()
        }
    }

    fun TestContext<*>.selectPaymentByTitle(titleText: String) {
        step("Click on payment: $titleText") {
            onView(
                allOf(
                    withId(R.id.title),
                    withText(titleText)
                )
            ).perform(click())
        }
    }

    fun TestContext<*>.verifyPaymentCardDetails(payment: PaymentData) {
        step("Verify payment card details for '${payment.title}'") {
            val cardWithTitle = findCardByTitle(payment.title)
            verifyCardContains(R.id.amount, cardWithTitle, formatAmount(payment.amount))
            payment.status?.let { verifyCardContains(R.id.status, cardWithTitle, it) }
        }
    }
    
    private fun formatAmount(amount: Float): String {
        return "€ ${String.format("%.2f", amount)}"
    }
    
    private fun findCardByTitle(title: String) = allOf(
        withId(R.id.paymentCardItem),
        hasDescendant(allOf(withId(R.id.title), withText(title)))
    )

    private fun verifyCardContains(resourceId: Int, cardMatcher: Matcher<View>, expectedAmount: String) {
        onView(cardMatcher).check(
            matches(
                allOf(
                    isDisplayed(),
                    hasDescendant(allOf(withId(resourceId), withText(expectedAmount)))
                )
            )
        )
    }
}
