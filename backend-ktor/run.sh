#!/bin/bash

echo "🚀 Starting LifeVault Backend..."
echo ""

# Проверяем PostgreSQL
if ! /opt/homebrew/opt/postgresql@16/bin/psql lifevault_db -c "SELECT 1" > /dev/null 2>&1; then
    echo "❌ PostgreSQL не запущен или база недоступна!"
    echo "Запустите: brew services start postgresql@16"
    exit 1
fi

echo "✅ PostgreSQL доступен"
echo ""

# Определяем путь к проекту
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
echo "📁 Проект: $PROJECT_DIR"
echo ""

# Запускаем через Gradle (используем app/gradlew если есть)
if [ -f "$PROJECT_DIR/app/gradlew" ]; then
    echo "🔧 Используем Gradle wrapper из app модуля..."
    cd "$PROJECT_DIR"
    ./app/gradlew :backend-ktor:run
elif command -v gradle &> /dev/null; then
    echo "🔧 Используем системный Gradle..."
    cd "$PROJECT_DIR/backend-ktor"
    gradle run
else
    echo "❌ Gradle не найден!"
    echo ""
    echo "Запустите backend через Android Studio:"
    echo "1. Откройте Android Studio"
    echo "2. File → Sync Project with Gradle Files"
    echo "3. Gradle panel (справа) → backend-ktor → Tasks → application → run"
    echo ""
    echo "Или установите Gradle:"
    echo "brew install gradle"
    exit 1
fi