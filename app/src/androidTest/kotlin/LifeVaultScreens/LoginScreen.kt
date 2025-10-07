package LifeVaultScreens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTestTag

class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider
    ) {

    val screenTitle = child<KNode> {
        hasText("Вход")
    }

    val welcomeText = child<KNode> {
        hasText("Добро пожаловать!")
    }

    val phoneField = child<KNode> {
        hasTestTag("authIdNumber")
    }

    val loginButton = child<KNode> {
        hasText("Войти")
    }

    val registerLink = child<KNode> {
        hasTestTag("registerLinkButton")
    }
}