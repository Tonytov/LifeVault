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
 * Тест заполнения формы с максимальным возрастом (99 лет)
 */
class OnboardingWithMaximumAgeTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testOnboardingWithMaximumAge() = run {
        scenario(
            OnboardingScenario(
                composeTestRule = composeTestRule,
                name = "Пожилой Пользователь",
                selectMaleGender = false,
                age = "99",
                height = "160",
                weight = "65"
            )
        )

        step("Проверяем что возраст принят") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                ageField.assertTextContains("99")
            }
        }
    }
}
