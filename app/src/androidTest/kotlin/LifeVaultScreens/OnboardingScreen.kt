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

    val nameField = child<KNode> {
        hasTestTag("nameField")
    }

    val genderMale = child<KNode> {
        hasTestTag("genderMALE")
    }

    val genderFemale = child<KNode> {
        hasTestTag("genderFEMALE")
    }

    val ageField = child<KNode> {
        hasTestTag("ageField")
    }

    val heightField = child<KNode> {
        hasTestTag("heightField")
    }

    val weightField = child<KNode> {
        hasTestTag("weightField")
    }

    val regionField = child<KNode> {
        hasTestTag("regionField")
    }

    val calculateLifeButton = child<KNode> {
        hasTestTag("calculateLifeButton")
    }
}