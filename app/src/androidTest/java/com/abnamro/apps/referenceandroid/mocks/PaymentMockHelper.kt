package com.abnamro.apps.referenceandroid.mocks

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.abnamro.apps.referenceandroid.MainActivity
import com.abnamro.apps.referenceandroid.TikkieRepository
import com.abnamro.apps.referenceandroid.TikkieRepository.addPayment
import com.abnamro.apps.referenceandroid.TikkieRepository.restorePayments
import com.abnamro.apps.referenceandroid.model.PaymentStatus
import com.abnamro.apps.referenceandroid.model.TikkiePayment
import com.abnamro.apps.referenceandroid.testdata.dataclass.PaymentData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PaymentMockHelper {
    
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    private val dateFormatLong = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    fun setupMockedPayment(paymentData: PaymentData, activityRule: ActivityScenarioRule<MainActivity>) {
        addPayment(convertPaymentDataToTikkiePayment(paymentData))
        activityRule.scenario.recreate()
    }

    fun setupMockedPayments(paymentsData: List<PaymentData>, activityRule: ActivityScenarioRule<MainActivity>) {
        restorePayments()
        paymentsData.forEach { paymentData ->
            addPayment(convertPaymentDataToTikkiePayment(paymentData))
        }
        activityRule.scenario.recreate()
    }
    
    private fun convertPaymentDataToTikkiePayment(payment: PaymentData): TikkiePayment {
        return TikkiePayment(
            id = generateMockId(),
            title = payment.title,
            amount = payment.amount.toDouble(),
            date = parseCreatedOnDate(payment.createdOn),
            status = convertTextToStatus(payment.status),
            tikkieId = extractTikkieId("Tikkie ID: ${payment.tikkieId}"),
            executionDate = parseExecutionDate(payment.executionDate),
            paidBy = extractPaidBy(payment.paidBy),
            createdAt = parseCreatedOnDate(payment.createdOn)
        )
    }
    
    private fun convertTextToStatus(statusText: String?): PaymentStatus {
        return when (statusText) {
            "Paid 1x - expired" -> PaymentStatus.PAID_EXPIRED
            "Paid" -> PaymentStatus.PAID
            else -> PaymentStatus.UNPAID
        }
    }

    private fun extractTikkieId(formattedTikkieId: String?): String? {
        return formattedTikkieId?.removePrefix("Tikkie ID: ")?.trim()
    }

    private fun parseExecutionDate(formattedExecutionDate: String?): Date? {
        return formattedExecutionDate?.let {
            val dateStr = it.removePrefix("Execution date: ").trim()
            parseDateSafely(dateStr, dateTimeFormat)
        }
    }

    private fun extractPaidBy(formattedPaidBy: String?): String? {
        return formattedPaidBy?.removePrefix("You were paid on account: ")?.trim()
    }
    
    private fun parseCreatedOnDate(formattedCreatedOn: String?): Date {
        return formattedCreatedOn?.let {
            val dateStr = it.removePrefix("Created on: ").trim()
            parseDateSafely(dateStr, dateFormatLong)
        } ?: Date()
    }
    
    private fun parseDateSafely(dateString: String, format: SimpleDateFormat): Date? {
        return try {
            format.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateMockId(): String {
        return "mock-${System.currentTimeMillis()}"
    }
}
