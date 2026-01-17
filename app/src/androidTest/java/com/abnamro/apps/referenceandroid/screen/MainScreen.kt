package com.abnamro.apps.referenceandroid.screen

import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.toolbar.KToolbar
import io.github.kakaocup.kakao.image.KImageView

object MainScreen : Screen<MainScreen>() {
    val toolbar = KToolbar { withId(R.id.toolbar) }
    val fab = KImageView { withId(R.id.fab) }
}
