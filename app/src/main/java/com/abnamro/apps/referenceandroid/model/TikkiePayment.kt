package com.abnamro.apps.referenceandroid.model

import java.util.Date

data class TikkiePayment(
    val id: String,
    val title: String,
    val amount: Double,
    val currency: String = "€",
    val date: Date,
    val status: PaymentStatus,
    val iconRes: Int? = null,
    val tikkieId: String? = null,
    val executionDate: Date? = null,
    val paidBy: String? = null,
    val createdAt: Date? = null
)

enum class PaymentStatus {
    PAID_EXPIRED,
    UNPAID,
    PAID
}
