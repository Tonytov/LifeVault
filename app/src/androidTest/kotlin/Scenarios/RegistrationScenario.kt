package Scenarios

import LifeVaultScreens.RegistrationScreen
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen


class RegistrationScenario(
    private val composeTestRule: ComposeTestRule,
    private val phoneNumber: String,
    private val password: String

) : Scenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Проверяем что находимся на экране регистрации") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }
        step("Вводим номер телефона: $phoneNumber") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                phoneField.performTextInput(phoneNumber)
            }
        }
        step("Вводим пароль") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                passwordField.performTextInput(password)
            }
        }
        step("Подтверждаем пароль") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                confirmPasswordField.performTextInput(password)
            }
        }
        step("Нажимаем кнопку 'Зарегистрироваться'") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                registerButton.assertIsDisplayed()
                registerButton.performClick()
            }
        }
    }
}
