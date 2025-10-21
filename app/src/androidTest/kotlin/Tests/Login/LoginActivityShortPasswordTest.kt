package Tests.Login

import LifeVaultScreens.LoginScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.quitsmoking.presentation.ui.auth.LoginActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

class LoginActivityShortPasswordTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun testLoginWithShortPassword() = run {
        step("Проверяем, что находимся на экране входа"){
            onComposeScreen<LoginScreen>(composeTestRule){
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим валидный номер телефона") {
            onComposeScreen<LoginScreen>(composeTestRule){
                phoneField.performTextInput("79999999999")
            }
        }

        step("Вводим короткий пароль"){
            onComposeScreen<LoginScreen>(composeTestRule){
                passwordField.performTextInput("test11")
            }
        }

        step("Проверяем, что кнопка Войти не отображается"){
            onComposeScreen<LoginScreen>(composeTestRule){
                loginButton.assertIsNotDisplayed()
            }
        }
    }
}
