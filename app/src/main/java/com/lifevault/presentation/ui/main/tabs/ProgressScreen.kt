package com.lifevault.presentation.ui.main.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Экран "Прогресс" - детальная статистика, графики, достижения
 */
@Composable
fun ProgressScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Заголовок
        Text(
            text = "📊 Ваш прогресс",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Заглушка: Период
        PlaceholderSection(
            title = "Период",
            description = "[Неделя] [Месяц] [3 месяца] [Год]"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Заглушка: График
        PlaceholderSection(
            title = "📈 График привычек",
            description = "Визуализация динамики за период"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Заглушка: Детальная статистика по привычкам
        PlaceholderSection(
            title = "🚬 Курение",
            description = "Среднее: 8 сиг/день\nТренд: ↘ -40%\n[Подробнее →]"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlaceholderSection(
            title = "🍺 Алкоголь",
            description = "Среднее: 4 един/неделя\nТренд: ↘ -60%\n[Подробнее →]"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PlaceholderSection(
            title = "🍬 Сахар",
            description = "Среднее: 35г/день\nТренд: ↘ -30%\n[Подробнее →]"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Заглушка: Инсайты
        PlaceholderSection(
            title = "💡 Инсайты",
            description = "• Вы чаще курите по пятницам\n• После кофе - триггер #1\n• Лучшие дни: ПН, СР"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Заглушка: Все достижения
        PlaceholderSection(
            title = "🏆 Все достижения (12/50)",
            description = "[🔓🔓🔓🔓🔒🔒🔒...]"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Заглушка: Time Bank (интегрирован сюда)
        PlaceholderSection(
            title = "⏰ Time Bank: 42 дня",
            description = "Магазин наград и достижений\n[🛍️ Открыть магазин →]"
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}