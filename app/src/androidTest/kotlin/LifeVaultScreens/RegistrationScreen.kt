package LifeVaultScreens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTestTag

class RegistrationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<RegistrationScreen>(
        semanticsProvider = semanticsProvider
    ) {

    val screenTitle = child<KNode> {
        hasText("Регистрация")
    }

    val welcomeText = child<KNode> {
        hasText("Создать аккаунт")
    }

    val registerButton = child<KNode> {
        hasTestTag("registerButton")
    }

    val loginLink = child<KNode> {
        hasText("Уже есть аккаунт? Войти")
    }
}