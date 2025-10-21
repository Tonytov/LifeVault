package Tests.Registration

import LifeVaultScreens.LoginScreen
import LifeVaultScreens.RegistrationScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.quitsmoking.presentation.ui.auth.RegisterActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

class RegistrationToLoginNavigationTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RegisterActivity>()

    @Test
    fun testNavigateToLogin() = run {
        step("Проверяем, что находимся на экране регистрации") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Нажимаем на ссылку 'Уже есть аккаунт? Войти'") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                loginLink.assertIsDisplayed()
                loginLink.performClick()
            }
        }

        step("Проверяем, что перешли на экран входа") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }
    }
}