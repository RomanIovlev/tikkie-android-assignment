package com.abnamro.apps.referenceandroid.testdata

import com.abnamro.apps.referenceandroid.testdata.dataclass.PaymentData

val dinnerDetails = PaymentData(
    title = "Dinner",
    subtitle = "Tikkie of € 21.50 p.p.",
    status = "Paid 1x - expired",
    totalSettled = "Total settled up € 21.50",
    amount = 21.5f,
    tikkieId = "Tikkie ID: 1142411970",
    executionDate = "Execution date: 16 Dec 2025, 09:58",
    paidBy = "You were paid on account: Roman",
    createdOn = "Created on: 15 December 2025"
)

val newPayment = PaymentData(
    title = "Gift",
    amount = 52f
)
