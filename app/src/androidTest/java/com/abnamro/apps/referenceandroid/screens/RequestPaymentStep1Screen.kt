package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen

object RequestPaymentStep1Screen : Screen<RequestPaymentStep1Screen>() {
    val amountInput = KEditText { withId(R.id.amountInput) }
    val nextButton = KImageView { withId(R.id.nextButton) }
}
