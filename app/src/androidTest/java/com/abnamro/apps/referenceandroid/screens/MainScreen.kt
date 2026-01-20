package com.abnamro.apps.referenceandroid.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.abnamro.apps.referenceandroid.R
import com.abnamro.apps.referenceandroid.screenStep
import com.abnamro.apps.referenceandroid.testdata.dataclass.PaymentData
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object MainScreen : Screen<MainScreen>() {
    val addPaymentButton = KImageView { withId(R.id.addFab) }
    val profileButton = KImageView { withId(R.id.profileButton) }
    val settingsMenuItem = KTextView { withId(R.id.profile_settings) }
    val logoutMenuItem = KTextView { withId(R.id.profile_logout) }
    val paymentRecyclerView = KRecyclerView(
        builder = { withId(R.id.paymentRecyclerView) },
        itemTypeBuilder = { itemType(::PaymentItem) }
    )

    fun TestContext<*>.verifyTikkieAppOpen() {
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

    class PaymentItem(parent: Matcher<android.view.View>) : KRecyclerItem<PaymentItem>(parent) {
        val paymentTitle = KTextView { withId(R.id.title) }
        val amount = KTextView { withId(R.id.amount) }
        val status = KTextView { withId(R.id.status) }
    }


    fun TestContext<*>.verifyPaymentCardDetails(payment: PaymentData) {
        step("Verify payment card details for '${payment.title}'") {
            val expectedAmount = "€ ${String.format("%.2f", payment.amount)}"
            val expectedTitle = payment.title
            
            paymentRecyclerView {
                val itemCount = getSize()
                var found = false

                for (i in 0 until itemCount) {
                    try {
                        childAt<PaymentItem>(i) {
                            paymentTitle.hasText(expectedTitle)
                            found = true
                            amount.hasText(expectedAmount)
                            if (payment.status != null) {
                                status.hasText(payment.status)
                            } else {
                                status.isNotDisplayed()
                            }
                        }
                        if (found) break
                    } catch (e: AssertionError) {
                        continue
                    }
                }
                
                if (!found) {
                    throw AssertionError("Payment with title '$expectedTitle' not found in the list")
                }
            }
        }
    }
}
