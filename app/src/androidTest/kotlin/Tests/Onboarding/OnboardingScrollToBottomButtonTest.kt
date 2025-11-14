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
 * Тест скроллинга к кнопке Calculate My Life
 */
class OnboardingScrollToBottomButtonTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testScrollToBottomButton() = run {
        step("Проверяем что можем доскроллить до кнопки") {
            flakySafely(timeoutMs = 3000) {
                composeTestRule.onNode(
                    matcher = androidx.compose.ui.test.hasTestTag("calculateLifeButton"),
                    useUnmergedTree = false
                ).performScrollTo()
            }
        }

        step("Проверяем что кнопка теперь видна") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                calculateLifeButton {
                    assertIsDisplayed()
                }
            }
        }
    }
}
