package com.abnamro.apps.referenceandroid.screens

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.abnamro.apps.referenceandroid.R
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.screen.Screen
import org.hamcrest.Matchers.allOf

object MainScreen : Screen<MainScreen>() {
    val addPaymentButton = KImageView { withId(R.id.addFab) }

    fun TestContext<*>.selectPaymentByTitle(titleText: String) {
        step("Click on payment: $titleText") {
            onView(
                allOf(
                    withId(R.id.title),
                    withText(titleText)
                )
            ).perform(click())
        }
    }
}
