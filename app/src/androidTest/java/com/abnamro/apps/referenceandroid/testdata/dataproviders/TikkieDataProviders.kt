package com.abnamro.apps.referenceandroid.testdata.dataproviders

import com.abnamro.apps.referenceandroid.testdata.correctAmountValues
import com.abnamro.apps.referenceandroid.testdata.correctDescriptionValues

object TikkieDataProviders {
    @JvmStatic
    fun amountData() = correctAmountValues.map { arrayOf(it.input, it.result) }

    @JvmStatic
    fun descriptionData() = correctDescriptionValues.map { arrayOf(it.input, it.result) }
}
