package com.abnamro.apps.referenceandroid.screen

import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import com.abnamro.apps.referenceandroid.R
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation

fun KToolbar.showOverflowMenu() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
}

object HelloWorldScreen : Screen<HelloWorldScreen>() {
    val pageTitle = KToolbar { withId(R.id.toolbar) }
    val settingsToast = KTextView { withText("Settings") }

    val mainText = KTextView { withId(R.id.helloWorldText) }
    val emailButton = KImageView { withId(R.id.fab) }
}
