# 🏥 LifeVault

**Android-приложение для отслеживания времени жизни и мотивации к отказу от вредных привычек**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-24%2B-green.svg)](https://android.com)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.02-blue.svg)](https://developer.android.com/jetpack/compose)
[![Ktor](https://img.shields.io/badge/Ktor-2.3.12-orange.svg)](https://ktor.io)

---

## 📱 О проекте

LifeVault - это мотивационное приложение, которое:
- 📊 Отслеживает прогресс отказа от вредных привычек (курение, алкоголь, сладкое)
- ⏱️ Показывает сколько времени жизни вы вернули
- 💰 Рассчитывает сэкономленные деньги
- 🎯 Предлагает ежедневные челленджи
- 🏆 Награждает за достижения
- 📈 Симулирует влияние привычек на продолжительность жизни

---

## 🚀 Технологический стек

### Frontend (Android App)
- **Язык:** Kotlin 1.9.22
- **UI:** Jetpack Compose + Material 3
- **Архитектура:** MVVM + Clean Architecture
- **DI:** Service Locator Pattern
- **БД:** Room Database
- **Сеть:** Retrofit 2 + OkHttp 3
- **Асинхронность:** Coroutines + Flow

### Backend
- **Фреймворк:** Ktor 2.3.12
- **БД:** PostgreSQL 16
- **ORM:** Exposed
- **Сериализация:** Kotlinx Serialization

### Testing
- **UI Tests:** Kaspresso + Compose Support
- **Pattern:** Page Object + Scenarios
- **E2E:** Real backend integration
- **Coverage:** 20+ тестов

---

## 📁 Структура проекта

```
LifeVault/
├── app/                                    # Android приложение
│   ├── src/main/
│   │   ├── java/com/lifevault/
│   │   │   ├── core/                      # DI Container
│   │   │   ├── data/                      # Data layer
│   │   │   │   ├── database/              # Room БД
│   │   │   │   ├── model/                 # Data models
│   │   │   │   ├── network/               # API
│   │   │   │   └── repository/            # Repositories
│   │   │   └── presentation/              # UI layer
│   │   │       ├── ui/                    # Activities & Screens
│   │   │       └── viewmodel/             # ViewModels
│   │   └── res/                           # Resources
│   └── src/androidTest/                   # Tests
│       └── kotlin/
│           ├── Tests/                     # Test cases
│           │   ├── E2E/                   # E2E тесты
│           │   ├── Login/                 # Login тесты
│           │   ├── Registration/          # Registration тесты
│           │   ├── Verification/          # Verification тесты
│           │   └── DailyChallenges/       # UI тесты челленджей
│           ├── LifeVaultScreens/          # Page Objects
│           ├── Scenarios/                 # Test scenarios
│           ├── helpers/                   # Test helpers
│           └── core/                      # Test infrastructure
│
├── backend-ktor/                          # Ktor Backend
│   └── src/main/kotlin/com/lifevault/
│       ├── database/                      # Database layer
│       ├── models/                        # API models
│       └── routes/                        # API endpoints
│
└── docs/                                  # Документация
```

---

## 🛠️ Установка и запуск

### Требования
- Android Studio Hedgehog | 2023.1.1+
- JDK 17
- Android SDK 24+
- PostgreSQL 16 (для бэкенда)

### 1. Клонирование репозитория
```bash
git clone https://github.com/yourusername/LifeVault.git
cd LifeVault
```

### 2. Запуск Backend

#### Настройка PostgreSQL
```bash
# macOS (Homebrew)
brew install postgresql@16
brew services start postgresql@16

# Создание БД
createdb lifevault_db
psql lifevault_db -c "CREATE USER lifevault_user WITH PASSWORD 'lifevault_password';"
psql lifevault_db -c "GRANT ALL PRIVILEGES ON DATABASE lifevault_db TO lifevault_user;"
```

#### Запуск сервера
```bash
./gradlew :backend-ktor:run

# Backend будет доступен на http://localhost:8080
```

### 3. Запуск Android приложения

#### Через Android Studio
1. Откройте проект в Android Studio
2. Дождитесь синхронизации Gradle
3. Запустите приложение (Run 'app')

#### Через командную строку
```bash
./gradlew :app:assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Тестирование

### Структура тестов

**E2E тесты** (с реальным backend):
- `RegistrationFlowE2ETest` - полный флоу регистрации
- `LoginFlowE2ETest` - флоу авторизации

**UI тесты** (изолированные):
- Login тесты (6 тестов)
- Registration тесты (7 тестов)
- Verification тесты (3 теста)
- Daily Challenges тесты (6 тестов)

### Запуск тестов

```bash
# Все тесты
./gradlew :app:connectedAndroidTest

# Конкретная группа
./gradlew :app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.package=Tests.E2E

# Один тест
./gradlew :app:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=Tests.E2E.RegistrationFlowE2ETest
```

### Архитектура тестирования

**Page Object Pattern:**
```kotlin
object LoginScreen : ComposeScreen<LoginScreen>() {
    val phoneField = onNode(hasTestTag("phoneField"))
    val passwordField = onNode(hasTestTag("passwordField"))
    val loginButton = onNode(hasTestTag("loginButton"))
}
```

**Scenarios Pattern:**
```kotlin
class LoginScenario(phoneNumber, password) : Scenario() {
    override val steps = {
        step("Вводим номер телефона") { /* ... */ }
        step("Вводим пароль") { /* ... */ }
        step("Нажимаем кнопку входа") { /* ... */ }
    }
}
```

**Best Practices:**
- ✅ flakySafely вместо Thread.sleep
- ✅ Уникальные тестовые данные (TestUserGenerator)
- ✅ Cleanup после тестов
- ✅ Реальные E2E тесты с backend

---

## 📐 Архитектура

### MVVM + Clean Architecture

```
┌─────────────────┐
│  Presentation   │  ViewModels + Compose UI
├─────────────────┤
│    Domain       │  Use Cases (опционально)
├─────────────────┤
│     Data        │  Repositories + API + DB
└─────────────────┘
```

### Service Locator Pattern

```kotlin
class DependencyContainer(context, baseUrl) {
    val database: LifeVaultDatabase by lazy { /* ... */ }
    val authApi: AuthApi by lazy { /* ... */ }
    val authRepository: AuthRepository by lazy { /* ... */ }
}

// Использование
val viewModel = AuthViewModel(
    authRepository = appContainer.authRepository,
    lifeRepository = appContainer.lifeRepository
)
```

**Преимущества:**
- ✅ Простота (без DI фреймворков)
- ✅ Прозрачность (всё явно)
- ✅ Гибкость (легко тестировать)
- ✅ Производительность (без кодогенерации)

---

## 🎯 Основные функции

### 1. Регистрация и авторизация
- Регистрация по номеру телефона
- SMS-верификация (тестовый код: 111111)
- Безопасное хранение паролей (SHA-256)

### 2. Отслеживание привычек
- Курение
- Алкоголь
- Сладкое

### 3. Расчеты
- Время жизни вернули
- Деньги сэкономили
- Дни без привычки
- Симуляция долголетия

### 4. Ежедневные челленджи
- Фильтрация по категориям
- Фильтрация по сложности
- Прогресс трекинг
- Награды

### 5. Достижения
- Система ачивок
- Статистика
- Мотивационные сообщения

---

## 🔐 Безопасность

- ✅ SHA-256 хеширование паролей
- ✅ HTTPS для production
- ✅ Валидация на клиенте и сервере
- ✅ Безопасное хранение токенов
- ✅ Network Security Config

---

## 📊 API Endpoints

### Authentication
```http
POST /api/auth/register
POST /api/auth/verify
POST /api/auth/login
```

### Profile
```http
GET /api/profile
PUT /api/profile
```

### Testing (только для тестов)
```http
POST /test/cleanup/user
POST /test/cleanup/prefix
GET /health
```

Подробнее: [backend-ktor/README.md](backend-ktor/README.md)

---

## 📖 Документация

- [Backend README](backend-ktor/README.md)
- [Testing Architecture](TESTING_ARCHITECTURE_PROPOSAL.md)
- [Integration Guide](INTEGRATION_GUIDE.md)

---

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit изменения (`git commit -m 'Add some AmazingFeature'`)
4. Push в branch (`git push origin feature/AmazingFeature`)
5. Откройте Pull Request

---

## 📝 TODO

- [ ] Добавить UI тесты для всех экранов
- [ ] Реализовать push-уведомления
- [ ] Добавить социальные фичи
- [ ] Dark theme
- [ ] Multi-language support
- [ ] Health API интеграция

---

## 📄 License

Этот проект создан в образовательных целях.

---

## 👨‍💻 Автор

**Ваше имя**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Благодарности

- [Kaspresso](https://github.com/KasperskyLab/Kaspresso) - за отличный testing framework
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - за современный UI toolkit
- [Ktor](https://ktor.io) - за простой и мощный backend framework

---

**Made with ❤️ and Kotlin**