package com.abnamro.apps.referenceandroid.repository

import com.abnamro.apps.referenceandroid.model.TikkiePayment

/**
 * Repository interface for payment data operations.
 * This abstraction allows for easy testing and swapping implementations.
 */
interface PaymentRepository {
    fun getAllPayments(): List<TikkiePayment>
    fun getPaymentById(id: String): TikkiePayment?
    fun addPayment(payment: TikkiePayment)
}
