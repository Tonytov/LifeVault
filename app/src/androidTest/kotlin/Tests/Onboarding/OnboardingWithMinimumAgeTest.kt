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
 * Тест заполнения формы с минимальным возрастом (18 лет)
 */
class OnboardingWithMinimumAgeTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testOnboardingWithMinimumAge() = run {
        scenario(
            OnboardingScenario(
                composeTestRule = composeTestRule,
                name = "Молодой Пользователь",
                selectMaleGender = true,
                age = "18",
                height = "175",
                weight = "70"
            )
        )

        step("Проверяем что возраст принят") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                ageField.assertTextContains("18")
            }
        }
    }
}
