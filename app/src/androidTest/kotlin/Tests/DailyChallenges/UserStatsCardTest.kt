package Tests.DailyChallenges

import LifeVaultScreens.DailyChallengesScreen
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import com.lifevault.presentation.ui.challenges.DailyChallengesActivity
import org.junit.Rule
import org.junit.Test

/**
 * Тест для проверки отображения карточки статистики пользователя
 */
class UserStatsCardTest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DailyChallengesActivity>()

    @Test
    fun testUserStatsCard() = run {
        step("Проверяем отображение карточки статистики пользователя") {
            flakySafely(timeoutMs = 5000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    userStatsCard.assertExists()
                }
            }
        }

        step("Проверяем наличие элементов статистики") {
            flakySafely(timeoutMs = 5000) {
                onComposeScreen<DailyChallengesScreen>(composeTestRule) {
                    statItemStreak.assertExists()
                    statItemSuccess.assertExists()
                    statItemRecord.assertExists()
                    statItemEarned.assertExists()
                }
            }
        }
    }
}
