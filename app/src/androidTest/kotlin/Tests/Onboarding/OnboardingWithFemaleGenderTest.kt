package Tests.Onboarding

import LifeVaultScreens.OnboardingScreen
import Scenarios.OnboardingScenario
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

/**
 * Тест заполнения формы с женским полом
 */
class OnboardingWithFemaleGenderTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testOnboardingWithFemaleGender() = run {
        scenario(
            OnboardingScenario(
                composeTestRule = composeTestRule,
                name = "Мария Петрова",
                selectMaleGender = false,
                age = "25",
                height = "165",
                weight = "60"
            )
        )

        step("Проверяем что все поля заполнены") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField.assertTextContains("Мария Петрова")
                ageField.assertTextContains("25")
                heightField.assertTextContains("165")
                weightField.assertTextContains("60")
            }
        }
    }
}
