package Tests.Login

import LifeVaultScreens.LoginScreen
import LifeVaultScreens.RegistrationScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import com.quitsmoking.presentation.ui.auth.LoginActivity
import org.junit.Rule
import org.junit.Test

class LoginToRegistrationNavigationTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun testNavigateToRegistration() = run {
        step("Проверяем, что находимся на экране входа") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Нажимаем на кнопку 'Нет аккаунта? Зарегистрироваться'") {
            onComposeScreen<LoginScreen>(composeTestRule) {
                registerLink.assertIsDisplayed()
                registerLink.performClick()
            }
        }

        step("Проверяем, что перешли на экран регистрации") {
            onComposeScreen<RegistrationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }
    }
}