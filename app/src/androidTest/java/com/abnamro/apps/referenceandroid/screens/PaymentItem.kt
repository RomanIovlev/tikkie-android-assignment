package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

class PaymentItem(parent: Matcher<android.view.View>) : KRecyclerItem<PaymentItem>(parent) {
    val paymentTitle = KTextView { withId(R.id.title) }
    val amount = KTextView { withId(R.id.amount) }
    val status = KTextView { withId(R.id.status) }
}
