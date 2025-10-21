package Tests.Login

import LifeVaultScreens.LoginScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import com.quitsmoking.presentation.ui.auth.LoginActivity
import org.junit.Rule
import org.junit.Test

class LoginActivityNegativeTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun testLoginWithInvalidCredentials() = run {
        step("Проверяем, что находимся на экране входа") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим номер телефона несуществующего пользователя") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                phoneField.performTextInput("71111111111")
            }
        }

        step("Вводим пароль") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                passwordField.performTextInput("test1234")
            }
        }

        step("Нажимаем кнопку Войти") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                loginButton.performClick()
            }
        }

        step("Проверяем отображение ошибки (пользователь не найден)") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                errorMessage.assertIsDisplayed()
            }
        }
    }
}