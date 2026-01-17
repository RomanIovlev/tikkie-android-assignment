package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screen.HelloWorldScreen
import com.abnamro.apps.referenceandroid.screen.showOverflowMenu
import org.junit.Test

class SmokeTests : TestSuite() {
    @Test
    fun runAppSuccessful() = run {
        step("Verify Hello World app screen") {
            HelloWorldScreen {
                step("Title equals 'ReferenceAndroid'") {
                    pageTitle.hasTitle("ReferenceAndroid")
                }
                step("Main text is 'Hello World!'") {
                    mainText.hasText("Hello World!")
                }
                step("Email button is visible") {
                    emailButton.isDisplayed()
                }
                // pageTitle.showOverflowMenu()
                // settingsToast.hasText("Settings")
            }
        }
    }
}
