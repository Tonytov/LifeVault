package Scenarios

import LifeVaultScreens.OnboardingScreen
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen


class OnboardingScenario(
    private val composeTestRule: ComposeTestRule,
    private val name: String,
    private val selectMaleGender: Boolean = true,  // true = Male, false = Female
    private val age: String,
    private val height: String,
    private val weight: String
) : Scenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("Проверяем что находимся на экране Onboarding") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Вводим имя: $name") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField.performTextInput(name)
            }
        }

        step("Выбираем пол: ${if (selectMaleGender) "Male" else "Female"}") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                if (selectMaleGender) {
                    genderMale.performClick()
                } else {
                    genderFemale.performClick()
                }
            }
        }

        step("Вводим возраст: $age") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                ageField.performTextInput(age)
            }
        }

        step("Вводим рост: $height см") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                heightField.performTextInput(height)
            }
        }

        step("Вводим вес: $weight кг") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                weightField.performTextInput(weight)
            }
        }
    }
}
