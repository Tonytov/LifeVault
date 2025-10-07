package Tests

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

class LoginActivityTest : TestCase (
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
        ){
    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Test
fun test
}