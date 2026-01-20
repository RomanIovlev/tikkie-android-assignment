package com.abnamro.apps.referenceandroid.testdata.dataproviders

import com.abnamro.apps.referenceandroid.testdata.amountInputValidationTestCases
import com.abnamro.apps.referenceandroid.testdata.descriptionInputTestCases
import com.abnamro.apps.referenceandroid.testdata.maxAmountLimitTestCases
import com.abnamro.apps.referenceandroid.testdata.unallowedAmount

object TikkieDataProviders {
    @JvmStatic
    fun amountValidationData() = amountInputValidationTestCases.map { arrayOf(it.input, it.result) }

    @JvmStatic
    fun maxAmountLimitData() = maxAmountLimitTestCases.map { arrayOf(it.input, it.result) }

    @JvmStatic
    fun unallowedAmountData() = unallowedAmount.map { arrayOf(it) }

    @JvmStatic
    fun descriptionData() = descriptionInputTestCases.map { arrayOf(it.input, it.result) }
}
