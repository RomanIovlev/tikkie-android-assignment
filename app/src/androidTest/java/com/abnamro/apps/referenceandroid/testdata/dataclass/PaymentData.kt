package com.abnamro.apps.referenceandroid.testdata.dataclass

data class PaymentData (
    val title: String,
    val subtitle: String? = null,
    val status: String? = null,
    val totalSettled: String? = null,
    val amount: Float,
    val tikkieId: String? = null,
    val executionDate: String? = null,
    val paidBy: String? = null,
    val createdOn: String? = null
)
