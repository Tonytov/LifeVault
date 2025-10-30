package Tests.DailyChallenges

import LifeVaultScreens.DailyChallengesScreen
import Scenarios.DailyChallengesScenario
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.presentation.ui.challenges.DailyChallengesActivity
import org.junit.Rule
import org.junit.Test

/**
 * Тест для проверки базового отображения экрана Ежедневных Вызовов
 */
class DailyChallengesScreenDisplayTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DailyChallengesActivity>()

    @Test
    fun testDailyChallengesScreenDisplay() = run {
        step("Проверяем отображение основных элементов экрана") {
            scenario(
                DailyChallengesScenario(composeTestRule)
            )
        }
    }
}