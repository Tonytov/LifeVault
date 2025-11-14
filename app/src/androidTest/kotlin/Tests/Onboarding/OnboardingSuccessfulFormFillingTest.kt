package Tests.Onboarding

import Scenarios.OnboardingScenario
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import org.junit.Rule
import org.junit.Test

/**
 * Тест успешного заполнения формы Onboarding (Male)
 */
class OnboardingSuccessfulFormFillingTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testSuccessfulOnboardingFormFilling() = run {
        scenario(
            OnboardingScenario(
                composeTestRule = composeTestRule,
                name = "Иван Иванов",
                selectMaleGender = true,
                age = "30",
                height = "180",
                weight = "75"
            )
        )
    }
}
