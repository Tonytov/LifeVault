package Tests.Onboarding

import LifeVaultScreens.OnboardingScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performScrollTo
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

/**
 * Тест скроллинга к полю выбора региона
 */
class OnboardingScrollToRegionFieldTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testScrollToRegionField() = run {
        step("Доскролливаем до поля выбора региона") {
            flakySafely(timeoutMs = 3000) {
                composeTestRule.onNode(
                    matcher = androidx.compose.ui.test.hasTestTag("regionField"),
                    useUnmergedTree = false
                ).performScrollTo()
            }
        }

        step("Проверяем что поле региона видно") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                regionField {
                    assertIsDisplayed()
                }
            }
        }
    }
}
