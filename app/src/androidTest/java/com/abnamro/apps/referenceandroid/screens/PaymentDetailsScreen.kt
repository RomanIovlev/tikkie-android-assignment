package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import com.abnamro.apps.referenceandroid.screenStep
import com.abnamro.apps.referenceandroid.testdata.dataclass.PaymentData
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.common.views.KView

object PaymentDetailsScreen : Screen<PaymentDetailsScreen>() {
    val title = KTextView { withId(R.id.title) }
    val subtitle = KTextView { withId(R.id.subtitle) }
    val statusLabel = KTextView { withId(R.id.statusButton) }
    val totalSettled = KTextView { withId(R.id.totalSettled) }
    val paymentName = KTextView { withId(R.id.paymentName) }
    val amount = KTextView { withId(R.id.amount) }
    val amountRow = KView { withId(R.id.amountRow) }

    val tikkieId = KTextView { withId(R.id.tikkieId) }
    val executionDate = KTextView { withId(R.id.executionDate) }
    val paidBy = KTextView { withId(R.id.paidBy) }
    val createdOn = KTextView { withId(R.id.createdOn) }

    fun TestContext<*>.verifyAllTexts(
        payment: PaymentData
    ) {
        screenStep("Verify Payment details") {
            title.hasText(payment.title)
            paymentName.hasText(payment.title)

            val stringAmount = String.format("%.2f", payment.amount)
            subtitle.hasText("Tikkie of € $stringAmount p.p.")
            amount.hasText("€ $stringAmount")
            totalSettled.hasText("Total settled up € $stringAmount")

            if (payment.status != null) {
                statusLabel.hasText(payment.status)
            }
            if (payment.tikkieId != null) {
                tikkieId.hasText(payment.tikkieId)
            }
            if (payment.executionDate != null) {
                executionDate.hasText(payment.executionDate)
            }
            if (payment.paidBy != null) {
                paidBy.hasText(payment.paidBy)
            }
            if (payment.createdOn != null) {
                createdOn.hasText(payment.createdOn)
            }
        }
    }
}
