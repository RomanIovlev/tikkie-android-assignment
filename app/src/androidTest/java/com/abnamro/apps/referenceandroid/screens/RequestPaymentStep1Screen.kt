package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.switch.KSwitch

object RequestPaymentStep1Screen : Screen<RequestPaymentStep1Screen>() {
    val backButton = KImageView { withId(R.id.backButton) }
    val amountInput = KEditText { withId(R.id.amountInput) }
    val payerChooseAmountToggle = KSwitch { withId(R.id.payerChooseAmountToggle) }
    val nextButton = KImageView { withId(R.id.nextButton) }

}
