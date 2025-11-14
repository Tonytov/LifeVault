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
 * Тест ограничения длины поля веса (максимум 3 цифры)
 */
class OnboardingWeightFieldLimitTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testWeightFieldLimitedTo3Digits() = run {
        step("Вводим 3 цифры") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                weightField.performTextInput("123")
            }
        }

        step("Пытаемся ввести еще одну цифру") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                weightField.performTextInput("4")
            }
        }

        step("Закрываем клавиатуру") {
            Espresso.closeSoftKeyboard()
        }

        step("Проверяем что введено только 3 цифры (4-я не добавилась)") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                weightField {
                    assertTextContains("123")
                }
            }
        }
    }
}