package com.abnamro.apps.referenceandroid.screens

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KTextView

object AddPaymentStep2Screen : Screen<AddPaymentStep2Screen>() {
    val descriptionInput = KEditText { withId(R.id.descriptionInput) }
    val characterCount = KTextView { withId(R.id.characterCount) }
    val shareButton = KImageView { withId(R.id.shareButton) }
}
