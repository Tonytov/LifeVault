package Scenarios

import LifeVaultScreens.LoginScreen
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen


class LoginScenario(
    private val composeTestRule: ComposeTestRule,
    private val phone: String,
    private val password: String
) : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Проверяем, что находимся на экране входа") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим номер телефона") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                phoneField.performTextInput(phone)
            }
        }

        step("Вводим пароль") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                passwordField.performTextInput(password)
            }
        }

        step("Нажимаем кнопку Войти") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                loginButton.assertIsDisplayed()
                loginButton.performClick()
            }
        }
    }
}