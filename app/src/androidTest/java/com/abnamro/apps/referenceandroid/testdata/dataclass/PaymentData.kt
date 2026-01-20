package com.abnamro.apps.referenceandroid.testdata.dataclass

data class PaymentData (
    val title: String,
    val amount: Float,
    val status: String? = null,
    val tikkieId: Int? = null,
    val executionDate: String? = null,
    val paidBy: String? = null,
    val createdOn: String? = null
)
