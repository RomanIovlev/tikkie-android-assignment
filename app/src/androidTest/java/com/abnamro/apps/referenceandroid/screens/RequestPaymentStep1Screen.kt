package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.switch.KSwitch
import io.github.kakaocup.kakao.text.KTextView

object RequestPaymentStep1Screen : Screen<RequestPaymentStep1Screen>() {
    val backButton = KImageView { withId(R.id.backButton) }
    val infoButton = KImageView { withId(R.id.infoButton) }
    val title = KTextView { withId(R.id.title) }
    val amountInput = KEditText { withId(R.id.amountInput) }
    val maxAmountHint = KTextView { withId(R.id.maxAmountHint) }
    val payerChooseAmountToggle = KSwitch { withId(R.id.payerChooseAmountToggle) }
    val nextButton = KImageView { withId(R.id.nextButton) }
}
