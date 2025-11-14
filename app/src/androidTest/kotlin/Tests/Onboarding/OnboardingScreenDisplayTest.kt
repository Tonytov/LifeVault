package Tests.Onboarding

import LifeVaultScreens.OnboardingScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Rule
import org.junit.Test

/**
 * Тест отображения экрана Onboarding
 *
 * Проверяет что все элементы экрана корректно отображаются
 */
class OnboardingScreenDisplayTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<OnboardingActivity>()

    @Test
    fun testOnboardingScreenDisplaysAllElements() = run {
        step("Проверяем что отображается заголовок экрана") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                screenTitle {
                    assertIsDisplayed()
                }
            }
        }

        step("Проверяем что отображается поле ввода имени") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                nameField {
                    assertIsDisplayed()
                }
            }
        }

        step("Проверяем что отображаются кнопки выбора пола") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                genderMale {
                    assertIsDisplayed()
                }
                genderFemale {
                    assertIsDisplayed()
                }
            }
        }

        step("Проверяем что отображается поле ввода возраста") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                ageField {
                    assertIsDisplayed()
                }
            }
        }

        step("Проверяем что отображается поле ввода роста") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                heightField {
                    assertIsDisplayed()
                }
            }
        }

        step("Проверяем что отображается поле ввода веса") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                weightField {
                    assertIsDisplayed()
                }
            }
        }

        step("Проверяем что отображается поле выбора региона") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                regionField {
                    assertIsDisplayed()
                }
            }
        }

        step("Проверяем что отображается кнопка") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                calculateLifeButton {
                    assertIsDisplayed()
                }
            }
        }
    }
}
