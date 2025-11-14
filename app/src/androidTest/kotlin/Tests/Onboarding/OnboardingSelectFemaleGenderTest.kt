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
 * Тест выбора женского пола
 */
class OnboardingSelectFemaleGenderTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testSelectFemaleGender() = run {
        step("Нажимаем на кнопку Female") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                genderFemale {
                    assertIsDisplayed()
                    performClick()
                }
            }
        }

        step("Проверяем что Female выбран") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                genderFemale {
                    assertIsDisplayed()
                }
            }
        }
    }
}