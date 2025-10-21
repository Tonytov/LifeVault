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

class RegistrationActivityShortPhoneTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RegisterActivity>()

    @Test
    fun testRegistrationWithShortPhoneNumber() = run {
        step("Проверяем, что находимся на экране регистрации") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим короткий номер телефона") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                phoneField.performTextInput("111")
            }
        }

        step("Вводим валидный пароль") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                passwordField.performTextInput("test1111")
            }
        }

        step("Вводим подтверждение пароля") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                confirmPasswordField.performTextInput("test1111")
            }
        }

        step("Проверяем, что кнопка Зарегистрироваться не отображается") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                registerButton.assertIsNotDisplayed()
            }
        }
    }
}