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
 * Тест переключения между Male и Female
 */
class OnboardingSwitchGenderTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testSwitchGenderSelection() = run {
        step("Выбираем Male") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                genderMale.performClick()
            }
        }

        step("Переключаемся на Female") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                genderFemale.performClick()
            }
        }

        step("Проверяем что можно переключиться обратно на Male") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                genderMale.performClick()
            }
        }
    }
}