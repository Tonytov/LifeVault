# 🔒 LifeVault Backend

**Современный backend для приложения отслеживания привычек и продолжительности жизни**

## 📋 Описание

LifeVault Backend - это надежная и масштабируемая серверная часть для мобильного приложения LifeVault, которое помогает пользователям отслеживать свои привычки, участвовать в челленджах и контролировать продолжительность жизни.

## ✨ Основные функции

### 🔐 Аутентификация
- SMS-верификация через Twilio
- JWT токены (Access + Refresh)
- Безопасное управление сессиями
- Защита от брутфорс атак

### 👤 Управление пользователями
- Профили пользователей с детальной информацией
- Региональные настройки продолжительности жизни
- Настройки приватности

### 🚭 Система привычек
- Отслеживание курения, алкоголя, сахара
- Расчет экономии денег
- Статистика улучшений здоровья
- История изменений

### 🎯 Система вызовов
- Дневные челленджи различной сложности
- Отслеживание прогресса в реальном времени
- Система достижений и наград
- Мотивационные уведомления

### 💳 Банк времени жизни
- Виртуальная валюта в днях жизни
- Заработок через выполнение вызовов
- Магазин улучшений
- История транзакций

### 📊 Аналитика и расчеты
- Комплексные расчеты продолжительности жизни
- Научно обоснованные коэффициенты
- Персонализированные прогнозы
- Медицинские дисклеймеры

## 🛠️ Технологический стек

- **Runtime**: Node.js 18+
- **Language**: TypeScript
- **Framework**: Express.js
- **Database**: PostgreSQL + Prisma ORM
- **Authentication**: JWT + SMS (Twilio)
- **Validation**: Joi
- **Logging**: Winston
- **Security**: Helmet, CORS, Rate Limiting
- **API Documentation**: Swagger/OpenAPI

## 🚀 Быстрый старт

### Предварительные требования

- Node.js 18+
- PostgreSQL 14+
- Twilio аккаунт для SMS

### Установка

1. **Клонирование и установка зависимостей**
```bash
cd backend
npm install
```

2. **Настройка окружения**
```bash
cp .env.example .env
# Отредактируйте .env файл с вашими настройками
```

3. **Настройка базы данных**
```bash
# Сгенерировать Prisma Client
npm run generate

# Применить миграции
npm run migrate

# (Опционально) Запустить Prisma Studio
npm run db:studio
```

4. **Запуск сервера**
```bash
# Режим разработки
npm run dev

# Продакшн
npm run build
npm start
```

### Переменные окружения

Скопируйте `.env.example` в `.env` и настройте:

```env
# 🔧 Основные настройки
NODE_ENV=development
PORT=3000

# 🗄️ База данных
DATABASE_URL="postgresql://username:password@localhost:5432/lifevault"

# 🔐 JWT
JWT_SECRET="your-super-secret-key"
JWT_EXPIRES_IN=7d

# 📱 SMS (Twilio)
TWILIO_ACCOUNT_SID="your-twilio-account-sid"
TWILIO_AUTH_TOKEN="your-twilio-auth-token"
TWILIO_PHONE_NUMBER="+1234567890"

# 🔒 Безопасность
BCRYPT_ROUNDS=12
API_RATE_LIMIT_MAX=100

# 🌍 CORS
CORS_ORIGIN="http://localhost:3000"
```

## 📚 API Документация

После запуска сервера, API документация доступна по адресу:
- **Swagger UI**: `http://localhost:3000/api-docs`
- **OpenAPI JSON**: `http://localhost:3000/api-docs.json`

### Основные endpoints

```
🔐 Authentication
POST /api/auth/send-code      - Отправка SMS кода
POST /api/auth/verify-code    - Верификация кода
POST /api/auth/refresh        - Обновление токена
POST /api/auth/logout         - Выход из системы
GET  /api/auth/me            - Информация о пользователе

👤 User Profile
GET    /api/user/profile     - Получение профиля
POST   /api/user/profile     - Создание профиля
PUT    /api/user/profile     - Обновление профиля
DELETE /api/user/profile     - Удаление профиля

🚭 Habits
GET    /api/habits          - Список привычек
POST   /api/habits          - Создание привычки
PUT    /api/habits/:id      - Обновление привычки
DELETE /api/habits/:id      - Удаление привычки

🎯 Challenges
GET    /api/challenges      - Доступные вызовы
POST   /api/challenges/start - Начать вызов
GET    /api/challenges/my   - Мои активные вызовы
POST   /api/challenges/:id/complete - Завершить вызов

💳 Time Bank
GET    /api/timebank/balance - Баланс времени
GET    /api/timebank/transactions - История транзакций
POST   /api/timebank/purchase - Покупка в магазине
```

## 📊 Мониторинг

### Health Check
```bash
curl http://localhost:3000/health
```

### Метрики системы
```bash
curl http://localhost:3000/metrics
```

## 🧪 Тестирование

```bash
# Запуск всех тестов
npm test

# Тесты в режиме watch
npm run test:watch

# Покрытие тестами
npm run test:coverage
```

## 📖 Структура проекта

```
backend/
├── src/
│   ├── controllers/         # Контроллеры API
│   ├── services/           # Бизнес-логика
│   ├── middleware/         # Express middleware
│   ├── routes/            # Определение маршрутов
│   ├── utils/             # Утилиты
│   ├── types/             # TypeScript типы
│   └── index.ts           # Точка входа
├── prisma/
│   └── schema.prisma      # Схема базы данных
├── logs/                  # Логи приложения
├── package.json
├── tsconfig.json
└── README.md
```

## 🔐 Безопасность

- **Rate Limiting**: Защита от DDoS и брутфорс атак
- **Helmet**: Настройка заголовков безопасности
- **CORS**: Контроль доступа с разных доменов
- **JWT**: Безопасная аутентификация
- **Валидация**: Проверка всех входящих данных
- **Логгирование**: Детальное логгирование безопасности

## 📈 Производительность

- **Сжатие**: Gzip сжатие ответов
- **Кеширование**: Умное кеширование данных
- **Пагинация**: Эффективная обработка больших списков
- **Оптимизация БД**: Индексы и оптимизированные запросы

## 🚀 Развертывание

### Docker

```bash
# Сборка образа
docker build -t lifevault-backend .

# Запуск контейнера
docker run -p 3000:3000 --env-file .env lifevault-backend
```

### Docker Compose

```bash
# Запуск всего стека (API + DB)
docker-compose up -d
```

### Облачное развертывание

Проект готов для развертывания на:
- **Heroku**
- **Vercel**
- **Railway**
- **DigitalOcean App Platform**
- **AWS Elastic Beanstalk**

## 🤝 Разработка

### Команды разработчика

```bash
# Установка зависимостей
npm install

# Разработка с hot reload
npm run dev

# Линтинг
npm run lint
npm run lint:fix

# Сборка
npm run build

# Миграции БД
npm run migrate
npm run db:push
npm run db:studio
```

### Добавление новых функций

1. Создайте миграцию БД (если нужно)
2. Обновите Prisma схему
3. Создайте/обновите сервисы
4. Добавьте контроллеры
5. Настройте маршруты
6. Добавьте валидацию
7. Напишите тесты
8. Обновите документацию

## 🐛 Отладка и логи

Логи доступны в директории `logs/`:
- `error.log` - Только ошибки
- `combined.log` - Все логи
- `exceptions.log` - Неперехваченные исключения

### Уровни логирования
- **error**: Критические ошибки
- **warn**: Предупреждения
- **info**: Информационные сообщения
- **debug**: Отладочная информация (только в development)

## 📞 Поддержка

- **GitHub Issues**: Багрепорты и feature requests
- **Документация**: В коде и README
- **Логи**: Детальное логгирование для диагностики

## 📄 Лицензия

MIT License - смотрите файл LICENSE для деталей.

## 🔮 Roadmap

- [ ] WebSocket поддержка для real-time уведомлений
- [ ] GraphQL API как альтернатива REST
- [ ] Микросервисная архитектура
- [ ] Машинное обучение для предсказаний
- [ ] Интеграция с IoT устройствами
- [ ] Социальные функции
- [ ] Аналитика и отчеты
- [ ] Мобильные push-уведомления

---

*Создано с ❤️ для LifeVault*