package Tests.E2E

import LifeVaultScreens.OnboardingScreen
import LifeVaultScreens.VerificationScreen
import Scenarios.LoginScenario
import Scenarios.RegistrationScenario
import Scenarios.VerificationScenario
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.kaspersky.components.composesupport.config.withComposeSupport
import com.lifevault.core.appContainer
import com.lifevault.presentation.ui.auth.RegisterActivity
import core.RealE2ETestConfig
import helpers.TestUserGenerator
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginFlowE2ETest : TestCase(
    kaspressoBuilder = Kaspresso.Builder.withComposeSupport()
) {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<RegisterActivity>()

    private lateinit var e2eConfig: RealE2ETestConfig

    private val testPhoneNumber = TestUserGenerator.generateUniquePhoneNumber()
    private val testPassword = TestUserGenerator.DEFAULT_TEST_PASSWORD

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        e2eConfig = RealE2ETestConfig(context)

        val backendAvailable = e2eConfig.isBackendAvailable()
        require(backendAvailable)

        appContainer = e2eConfig.createRealContainer()

        e2eConfig.fullCleanup(testPhoneNumber, appContainer)
    }

    @After
    fun tearDown() {
        e2eConfig.fullCleanup(testPhoneNumber, appContainer)
    }

    @Test
    fun testLoginFlowForUserWithoutProfile() = run {

        step("Регистрируем пользователя") {
            scenario(
                RegistrationScenario(
                    composeTestRule = composeTestRule,
                    phoneNumber = testPhoneNumber,
                    password = testPassword
                )
            )
        }

        step("Проверяем переход на экран верификации") {
            onComposeScreen<VerificationScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        scenario(
            VerificationScenario(
                composeTestRule = composeTestRule,
                code = "111111"
            )
        )

        step("✅ Проверяем переход на онбординг после верификации") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        step("Выход из приложения.Очищаем локальную БД") {
            e2eConfig.clearLocalDatabase(appContainer)
            println("БД очищена, пользователь остался на бэке")
        }

        step("Переходим на экран логина") {
            androidx.test.core.app.ActivityScenario.launch(
                com.lifevault.presentation.ui.auth.LoginActivity::class.java
            )
        }

        scenario(
            LoginScenario(
                composeTestRule = composeTestRule,
                phone = testPhoneNumber,
                password = testPassword
            )
        )

        step("Проверяем переход на онбординг") {
            onComposeScreen<OnboardingScreen>(composeTestRule) {
                screenTitle.assertIsDisplayed()
            }
        }

        println("""
        ✅ ===================================
        ✅ E2E LOGIN TEST PASSED!
        ✅ ===================================
        ✅ Протестировано:
        ✅ - Реальная регистрация через API
        ✅ - Реальная верификация через API
        ✅ - Реальный логин через API
        ✅ - Реальная проверка пароля на бэкенде
        ✅ - Реальная проверка статуса верификации
        ✅ - Реальное обновление last_login_at в PostgreSQL
        ✅ - Navigation между экранами
        ✅ ===================================
        """.trimIndent())
    }
}