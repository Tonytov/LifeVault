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
 * Тест валидации: кнопка неактивна при пустых полях
 */
class OnboardingButtonDisabledEmptyFieldsTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testCalculateButtonDisabledWhenFieldsEmpty() = run {
        step("Проверяем что кнопка неактивна при пустых полях") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                calculateLifeButton {
                    assertIsDisplayed()
                    assertIsNotEnabled()
                }
            }
        }
    }
}
