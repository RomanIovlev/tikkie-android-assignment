package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screen.HelloWorldScreen
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
                step("Overflow menu button is visible") {
                    overflowMenuButton.isDisplayed()
                }
            }
        }
    }
    @Test
    fun settingsPopupWorks() = run {
        step("Verify Settings popup") {
            HelloWorldScreen {
                showOverflowMenu()
                step("Settings menu item is visible") {
                    settingsItem {
                        inRoot { isPlatformPopup() }
                        isDisplayed()
                    }
                }
            }
        }
    }
}
