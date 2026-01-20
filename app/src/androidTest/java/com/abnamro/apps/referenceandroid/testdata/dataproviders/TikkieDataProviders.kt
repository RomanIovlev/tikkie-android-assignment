package com.abnamro.apps.referenceandroid.testdata.dataproviders

import com.abnamro.apps.referenceandroid.testdata.amountInputCases
import com.abnamro.apps.referenceandroid.testdata.descriptionInputCases

object TikkieDataProviders {
    @JvmStatic
    fun amountData() = amountInputCases.map { arrayOf(it.input, it.result) }

    @JvmStatic
    fun descriptionData() = descriptionInputCases.map { arrayOf(it.input, it.result) }
}
