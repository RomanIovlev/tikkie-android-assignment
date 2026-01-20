package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen

object RequestPaymentStep2Screen : Screen<RequestPaymentStep2Screen>() {
    val descriptionInput = KEditText { withId(R.id.descriptionInput) }
    val shareButton = KImageView { withId(R.id.shareButton) }
}
