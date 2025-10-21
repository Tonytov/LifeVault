package Tests.Registration

import LifeVaultScreens.RegistrationScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.quitsmoking.presentation.ui.auth.RegisterActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test


class RegistrationActivityPasswordMismatchTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
){
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RegisterActivity>()

    @Test
    fun testRegistrationWithMismatchedPasswords() = run {
        step("Проверяем, что находимся на экране регистрации") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим валидный номер телефона") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                phoneField.performTextInput("79999999999")
            }
        }

        step("Вводим валидный пароль") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                passwordField.performTextInput("test1111")
            }
        }

        step("Вводим ДРУГОЙ пароль в подтверждение") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                confirmPasswordField.performTextInput("test2222")
            }
        }

        step("Проверяем, что кнопка Зарегистрироваться не отображается") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                registerButton.assertIsNotDisplayed()
            }
        }
    }
}