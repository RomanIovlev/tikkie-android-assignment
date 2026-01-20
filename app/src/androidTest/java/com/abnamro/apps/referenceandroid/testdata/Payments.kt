package com.abnamro.apps.referenceandroid.testdata

import com.abnamro.apps.referenceandroid.testdata.dataclass.InputResult
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

val unpaidPayment = PaymentData(
    title = "Unpaid Test Payment",
    amount = 45.75f,
    status = null
)

val amountInputValidationTestCases = listOf(
    InputResult("50", "50"),
    InputResult("50.", "50."),
    InputResult("50.5", "50.5"),
    InputResult("50.50", "50.50"),
    InputResult("", ".00")
)

val maxAmountLimitTestCases = listOf(
    InputResult("1000", "999"),
    InputResult("999.99", "999.99"),
    InputResult("1000.00", "999")
)

val unallowedAmount = listOf(".00", "0", "50")

val descriptionInputTestCases = listOf(
    InputResult("Test payment", "12"),
    InputResult("A", "1"),
    InputResult("This is a longer description text", "35"),
    InputResult("", "0")
)
