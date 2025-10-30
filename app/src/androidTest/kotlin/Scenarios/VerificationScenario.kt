package Scenarios

import LifeVaultScreens.VerificationScreen
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen

class VerificationScenario(
    private val composeTestRule: ComposeTestRule,
    private val code: String = "111111"
) : Scenario() {

    override val steps: TestContext<Unit>.() -> Unit = {
        step("Проверяем что находимся на экране верификации") {
            onComposeScreen<VerificationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим код подтверждения: $code") {
            onComposeScreen<VerificationScreen>(composeTestRule) {
                verificationCodeField.performTextInput(code)
            }
        }

        step("Нажимаем кнопку 'Подтвердить'") {
            onComposeScreen<VerificationScreen>(composeTestRule) {
                verifyButton.assertIsDisplayed()
                verifyButton.performClick()
            }
        }
    }
}
