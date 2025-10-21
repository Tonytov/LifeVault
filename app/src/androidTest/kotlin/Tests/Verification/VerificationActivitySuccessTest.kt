package Tests.Verification

import LifeVaultScreens.VerificationScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.quitsmoking.presentation.ui.auth.VerificationActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

class VerificationActivitySuccessTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<VerificationActivity>()

    @Test
    fun testVerificationWithCorrectCode() = run {
        step("Проверяем, что находимся на экране подтверждения") {
            onComposeScreen<VerificationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим правильный код подтверждения") {
            onComposeScreen<VerificationScreen>(composeTestRule) {
                verificationCodeField.performTextInput("111111")
            }
        }

        step("Нажимаем кнопку Подтвердить") {
            onComposeScreen<VerificationScreen>(composeTestRule) {
                verifyButton.assertIsDisplayed()
                verifyButton.performClick()
            }
        }
    }
}