package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.model.PaymentStatus
import com.abnamro.apps.referenceandroid.model.TikkiePayment
import java.util.Calendar
import java.util.Date

object TikkieRepository {
    private val payments = mutableListOf<TikkiePayment>()
    
    init {
        // Sample data based on screenshots
        val calendar = Calendar.getInstance()
        
        // Dinner payment
        calendar.set(2025, Calendar.DECEMBER, 15)
        payments.add(
            TikkiePayment(
                id = "1",
                title = "Dinner",
                amount = 21.50,
                date = calendar.time,
                status = PaymentStatus.PAID_EXPIRED,
                tikkieId = "1142411970",
                executionDate = Calendar.getInstance().apply { 
                    set(2025, Calendar.DECEMBER, 16, 9, 58) 
                }.time,
                paidBy = "Roman",
                createdAt = calendar.time
            )
        )
        
        // Help friends payment
        calendar.set(2025, Calendar.NOVEMBER, 13)
        payments.add(
            TikkiePayment(
                id = "2",
                title = "Help friends",
                amount = 350.00,
                date = calendar.time,
                status = PaymentStatus.PAID_EXPIRED
            )
        )
        
        // Coffee payment
        payments.add(
            TikkiePayment(
                id = "3",
                title = "Coffee",
                amount = 9.80,
                date = Date(),
                status = PaymentStatus.UNPAID
            )
        )
    }
    
    fun getAllPayments(): List<TikkiePayment> = payments.toList()
    
    fun getPaymentById(id: String): TikkiePayment? = payments.find { it.id == id }
    
    fun addPayment(payment: TikkiePayment) {
        payments.add(0, payment) // Add to beginning
    }
}
