package LifeVaultScreens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class VerificationScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<VerificationScreen>(
        semanticsProvider = semanticsProvider
    ) {

    val screenTitle = child<KNode> {
        hasText("Код подтверждения")
    }

    val verificationCodeField = child<KNode> {
        hasTestTag("verificationCodeField")
    }

    val verifyButton = child<KNode> {
        hasTestTag("verifyButton")
    }

    val errorMessage = child<KNode> {
        hasTestTag("errorMessage")
    }
}