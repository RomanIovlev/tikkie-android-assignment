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

val correctAmountValues = listOf(
    InputResult("5.00", "5.00"),     // Min value
    InputResult("999", "999"),       // Max whole value
    InputResult("999.00", "999.00"), // Max value

    InputResult("50.", "50."),
    InputResult(".5", ".5"),
    InputResult("001", "1"),
)

val incorrectAmountValues = listOf(
    InputResult("", ".00"),
    InputResult("4.99", "5.00"),
    InputResult("999.01", "999.00"),
)

val correctDescriptionValues = listOf(
    InputResult("Minim", "5"),                      // Minimum 5
    InputResult("Dinner party", "12"),
    InputResult("Birthday gift by friend","23"),   // Maximum 24

    InputResult("!@#%^&*()_+-=[]{}|;':\",.", "24"), // Special symbols

    // Localization support
    InputResult("测试测试测试", "6"),
    InputResult("тестик", "6"),
    InputResult("テストスト", "5"),
    InputResult("اختبار", "6"),

    InputResult("Payment 😀🎉💰🔥", "16"),         // Icons support

    InputResult("javascript:alert('XSS')", "23"),   // JS injection
    InputResult("' OR '1'='1", "11"),               // SQL injection
    InputResult("test; rm -rf /", "14"),            // Bash hack
)

val incorrectDescriptionValues = listOf("")
