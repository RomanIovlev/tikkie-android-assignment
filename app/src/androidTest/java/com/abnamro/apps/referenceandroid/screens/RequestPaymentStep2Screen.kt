package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KTextView

object RequestPaymentStep2Screen : Screen<RequestPaymentStep2Screen>() {
    val backButton = KImageView { withId(R.id.backButton) }
    val infoButton = KImageView { withId(R.id.infoButton) }
    val title = KTextView { withId(R.id.title) }
    val descriptionInput = KEditText { withId(R.id.descriptionInput) }
    val characterCount = KTextView { withId(R.id.characterCount) }
    val whatsappButton = KView { withId(R.id.whatsappButton) }
    val qrCodeButton = KImageView { withId(R.id.qrCodeButton) }
    val shareButton = KImageView { withId(R.id.shareButton) }
}
