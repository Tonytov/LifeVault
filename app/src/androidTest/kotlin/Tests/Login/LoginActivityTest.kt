package Tests.Login

import Scenarios.LoginScenario
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.auth.LoginActivity
import org.junit.Rule
import org.junit.Test

class LoginActivityTest : TestCase (
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
        ){
    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun testLogin() = run {
        step("Выполняем авторизацию с валидными данными") {
            scenario(
                LoginScenario(
                    composeTestRule = composeTestRule,
                    phone = "79999999999",
                    password = "test1111"
                )
            )
        }
    }
}