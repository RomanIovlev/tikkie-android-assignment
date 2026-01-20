package com.abnamro.apps.referenceandroid.testdata

import com.abnamro.apps.referenceandroid.testdata.dataclass.PaymentData

val dinnerDetails = PaymentData(
    title = "Mocked Dinner",
    status = "Paid 1x - expired",
    amount = 28.5f,
    tikkieId = 1142411970,
    executionDate = "Execution date: 16 Dec 2025, 09:58",
    paidBy = "You were paid on account: Roman",
    createdOn = "Created on: 15 December 2025"
)

val newPayment = PaymentData(
    title = "Gift",
    amount = 52f
)

val defaultDinnerPayment = PaymentData(
    title = "Dinner",
    status = "Paid 1x - expired",
    amount = 21.50f,
    tikkieId = 1142411970,
    executionDate = "Execution date: 16 Dec 2024, 09:58",
    paidBy = "You were paid on account: Alex",
    createdOn = "Created on: 15 December 2024"
)

val mockedAlicePayments = listOf(
    PaymentData(
        title = "Concert Tickets",
        status = "Paid 1x - expired",
        amount = 125.75f
    ),
        PaymentData(
        title = "Birthday Gift",
        status = "Paid 1x - expired",
        amount = 89.50f
    ), PaymentData(
        title = "Lunch",
        amount = 15.25f
    )
)