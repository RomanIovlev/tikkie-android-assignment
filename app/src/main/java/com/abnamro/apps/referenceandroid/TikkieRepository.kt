package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.model.PaymentStatus.PAID_EXPIRED
import com.abnamro.apps.referenceandroid.model.PaymentStatus.UNPAID
import com.abnamro.apps.referenceandroid.model.TikkiePayment
import java.util.Date

object TikkieRepository {

    private val dinnerCreatedDate = Date(1734220800000L)
    private val dinnerExecutionDate = Date(1734307080000L)
    private val helpFriendsDate = Date(1731456000000L)
    private val coffeeDate = Date(1735689600000L)
    
    private val defaultPayments = listOf(
        TikkiePayment(
            id = "1",
            title = "Dinner",
            amount = 21.50,
            date = dinnerCreatedDate,
            status = PAID_EXPIRED,
            tikkieId = "1142411970",
            executionDate = dinnerExecutionDate,
            paidBy = "Alex",
            createdAt = dinnerCreatedDate
        ),
        TikkiePayment(
            id = "2",
            title = "Help friends",
            amount = 350.00,
            date = helpFriendsDate,
            status = PAID_EXPIRED
        ),
        TikkiePayment(
            id = "3",
            title = "Coffee",
            amount = 9.80,
            date = coffeeDate,
            status = UNPAID
        )
    )
    
    private val payments = defaultPayments.toMutableList()
    
    fun getAllPayments(): List<TikkiePayment> = payments.toList()
    
    fun getPaymentById(id: String): TikkiePayment? = payments.find { it.id == id }
    
    fun addPayment(payment: TikkiePayment) {
        payments.add(0, payment)
    }

    fun restorePayments() {
        payments.clear()
        payments.addAll(defaultPayments)
    }
}
