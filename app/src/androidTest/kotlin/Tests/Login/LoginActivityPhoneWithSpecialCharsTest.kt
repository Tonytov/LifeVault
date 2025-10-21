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

class LoginActivityPhoneWithSpecialCharsTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
){
    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun testLoginWithSpecialCharactersInPhone() = run {
        step("Проверяем, что находимся на экране входа"){
            onComposeScreen<LoginScreen>(composeTestRule){
                screenTitle.assertIsDisplayed()
            }
        }
        step("Вводим спецсимволы вместо номера телефона") {
            onComposeScreen<LoginScreen>(composeTestRule){
                phoneField.performTextInput("@@@@@@@@@")
            }
        }
        step("Вводим корректный пароль"){
            onComposeScreen<LoginScreen>(composeTestRule){
                passwordField.performTextInput("test1111")
            }
        }
        step("Проверяем, что кнопка Войти не отображается"){
            onComposeScreen<LoginScreen>(composeTestRule){
                loginButton.assertIsNotDisplayed()
            }
        }
    }
}