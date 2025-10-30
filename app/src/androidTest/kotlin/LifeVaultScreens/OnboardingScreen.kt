package LifeVaultScreens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTestTag

class OnboardingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<OnboardingScreen>(
        semanticsProvider = semanticsProvider
    ) {

    val screenTitle = child<KNode> {
        hasTestTag("onboardingTitle")
    }
    val nameField = child<KNode> {
        hasTestTag("onboardingNameField")
    }

    val calculateLifeButton = child<KNode> {
        hasTestTag("calculateLifeButton")
    }
}