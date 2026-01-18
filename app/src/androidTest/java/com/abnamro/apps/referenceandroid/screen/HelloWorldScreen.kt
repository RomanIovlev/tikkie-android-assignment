package com.abnamro.apps.referenceandroid.screen

import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.abnamro.apps.referenceandroid.R
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.text.KTextView
import io.github.kakaocup.kakao.toolbar.KToolbar
import io.github.kakaocup.kakao.common.views.KView

object HelloWorldScreen : Screen<HelloWorldScreen>() {
    val pageTitle = KToolbar { withId(R.id.toolbar) }
    val overflowMenuButton = KView { withContentDescription("More options") }
    val settingItem = KTextView { withText("Settings") }
    val mainText = KTextView { withId(R.id.helloWorldText) }
    val emailButton = KImageView { withId(R.id.fab) }

    fun TestContext<*>.showOverflowMenu() {
        step("Open context menu") {
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
        }
    }
}
