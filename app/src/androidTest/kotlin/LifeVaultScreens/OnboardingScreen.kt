package LifeVaultScreens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class OnboardingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<OnboardingScreen>(
        semanticsProvider = semanticsProvider
    ) {

    val screenTitle = child<KNode> {
        hasTestTag("onboardingTitle")
    }
}