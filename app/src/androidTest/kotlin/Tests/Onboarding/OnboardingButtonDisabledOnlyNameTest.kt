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
 * Тест валидации: кнопка неактивна когда заполнено только имя
 */
class OnboardingButtonDisabledOnlyNameTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testCalculateButtonDisabledWhenOnlyNameFilled() = run {
        step("Вводим только имя") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField.performTextInput("Иван Иванов")
            }
        }

        step("Проверяем что кнопка всё ещё неактивна") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                calculateLifeButton {
                    assertIsNotEnabled()
                }
            }
        }
    }
}