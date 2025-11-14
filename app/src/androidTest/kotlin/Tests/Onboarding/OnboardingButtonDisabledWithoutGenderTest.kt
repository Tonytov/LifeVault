package Tests.Onboarding

import LifeVaultScreens.OnboardingScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

/**
 * Тест валидации: кнопка неактивна без выбора пола
 */
class OnboardingButtonDisabledWithoutGenderTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testCalculateButtonDisabledWithoutGender() = run {
        step("Заполняем все поля кроме пола") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField.performTextInput("Иван Иванов")
                ageField.performTextInput("30")
                heightField.performTextInput("180")
                weightField.performTextInput("75")
            }
        }

        step("Проверяем что кнопка неактивна без выбора пола") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                calculateLifeButton {
                    assertIsNotEnabled()
                }
            }
        }
    }
}