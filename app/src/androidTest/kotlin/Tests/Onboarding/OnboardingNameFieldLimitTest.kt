package Tests.Onboarding

import LifeVaultScreens.OnboardingScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

/**
 * Тест ограничения длины поля имени (максимум 50 символов)
 */
class OnboardingNameFieldLimitTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testNameFieldLimitedTo50Characters() = run {
        step("Вводим имя длиной 50 символов") {
            val maxName = "A".repeat(50)
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField.performTextInput(maxName)
            }
        }

        step("Пытаемся ввести еще 10 символов") {
            val extraChars = "B".repeat(10)
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField.performTextInput(extraChars)
            }
        }

        step("Закрываем клавиатуру") {
            Espresso.closeSoftKeyboard()
        }

        step("Проверяем что введено только 50 символов (дополнительные не добавились)") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField {
                    assertTextContains("A".repeat(50))
                }
            }
        }
    }
}