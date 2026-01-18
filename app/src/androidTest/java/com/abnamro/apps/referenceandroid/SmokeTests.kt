package com.abnamro.apps.referenceandroid

import com.abnamro.apps.referenceandroid.screen.HelloWorldScreen
import org.junit.Test

class SmokeTests : TestSuite() {
    @Test
    fun runAppSuccessfulTest() = run("Run App Successful Test") {
        HelloWorldScreen {
            screenStep ("Verify Hello World app screen") {
                step("Title equals 'ReferenceAndroid'") {
                    pageTitle.hasTitle("ReferenceAndroid")
                }
                step("Main text is 'Hello World!'") {
                    mainText.hasText("Hello World")
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
    fun contextMenuTest() = run("Context menu Test") {
        HelloWorldScreen {
            showOverflowMenu()
            screenStep ("Verify context menu item is 'Settings'") {
                settingItem {
                    inRoot { isPlatformPopup() }
                    isDisplayed()
                    hasText("Settings")
                }
            }
        }
    }
}
