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
 * Тест валидации типа данных: поле возраста принимает только цифры
 */
class OnboardingAgeAcceptsOnlyNumbersTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testAgeFieldAcceptsOnlyNumbers() = run {
        step("Пытаемся ввести буквы в поле возраста") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                ageField.performTextInput("abc")
            }
        }

        step("Вводим корректные цифры") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                ageField.performTextInput("25")
            }
        }

        step("Закрываем клавиатуру") {
            Espresso.closeSoftKeyboard()
        }

        step("Проверяем что цифры приняты") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                ageField {
                    assertTextContains("25")
                }
            }
        }
    }
}