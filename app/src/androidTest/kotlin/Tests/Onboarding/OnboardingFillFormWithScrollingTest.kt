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
 * Тест заполнения формы с прокруткой
 */
class OnboardingFillFormWithScrollingTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testFillFormWithScrolling() = run {
        step("Заполняем верхние поля") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField.performTextInput("Тест Скролла")
                genderMale.performClick()
                ageField.performTextInput("30")
            }
        }

        step("Скроллим к нижним полям и заполняем их") {
            flakySafely(timeoutMs = 3000) {
                composeTestRule.onNode(
                    matcher = androidx.compose.ui.test.hasTestTag("heightField"),
                    useUnmergedTree = false
                ).performScrollTo()
            }

            onComposeScreen<OnboardingScreen>(composeTestRule) {
                heightField.performTextInput("180")
                weightField.performTextInput("75")
            }
        }

        step("Скроллим к кнопке") {
            flakySafely(timeoutMs = 3000) {
                composeTestRule.onNode(
                    matcher = androidx.compose.ui.test.hasTestTag("calculateLifeButton"),
                    useUnmergedTree = false
                ).performScrollTo()
            }
        }

        step("Проверяем что кнопка доступна") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                calculateLifeButton {
                    assertIsDisplayed()
                }
            }
        }
    }
}
