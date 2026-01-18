package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screen.HelloWorldScreen
import org.junit.Test

class FailedTests : TestSuite() {
    @Test
    fun failingTest() = run("Test that failed because of wrong text") {
        HelloWorldScreen {
            screenStep ("Verify Hello World app screen") {
                step("Title equals 'ReferenceAndroid'") {
                    pageTitle.hasTitle("ReferenceAndroid")
                }
                step("Main text is 'Hello World!'") {
                    mainText.hasText("Another World")
                }
                step("Email button is visible") {
                    emailButton.isDisplayed()
                }
                step("Overflow menu button is visible") {
                    overflowMenuButton.isDisplayed()
                }
            }
        }
    }
}
