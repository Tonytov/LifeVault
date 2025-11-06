package com.lifevault.presentation.ui.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifevault.data.model.*
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * Карточка привычки с возможностью разворачивания
 */
@Composable
fun HabitCard(
    summary: TodayHabitSummary?,
    onQuickAdd: () -> Unit,
    onPresetSelected: (String) -> Unit,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    if (summary == null) {
        // Заглушка, если данных нет
        PlaceholderHabitCard(modifier = modifier)
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = Color(summary.statusColor.colorHex).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок карточки
            HabitCardHeader(
                summary = summary,
                isExpanded = isExpanded,
                onExpandToggle = { isExpanded = !isExpanded }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Статус (визуализация)
            HabitStatusVisualization(summary = summary)

            Spacer(modifier = Modifier.height(8.dp))

            // Текст статуса
            Text(
                text = summary.getStatusText(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(summary.statusColor.colorHex)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Быстрые кнопки (пресеты)
            HabitQuickActions(
                habitType = summary.habitType,
                onQuickAdd = onQuickAdd,
                onPresetSelected = onPresetSelected,
                onViewDetails = onViewDetails
            )

            // Развернутая часть (таймлайн)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Таймлайн событий сегодня
                    HabitTimeline(entries = summary.entries)
                }
            }
        }
    }
}

/**
 * Заголовок карточки привычки
 */
@Composable
private fun HabitCardHeader(
    summary: TodayHabitSummary,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Название привычки
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = summary.habitType.icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = summary.habitType.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // Кнопка разворачивания
        IconButton(onClick = onExpandToggle) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                tint = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Визуализация статуса привычки
 */
@Composable
private fun HabitStatusVisualization(summary: TodayHabitSummary) {
    when (summary.habitType) {
        HabitType.SMOKING -> {
            // Кружки для сигарет (макс 10) + текст
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(summary.totalAmount.coerceAtMost(10)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(summary.statusColor.colorHex))
                    )
                }
                if (summary.totalAmount > 10) {
                    Text(
                        text = "...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
                Text(
                    text = "${summary.totalAmount} ${if (summary.totalAmount == 1) "сигарета" else "сигарет"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        HabitType.SUGAR -> {
            // Прогресс-бар для сахара
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${summary.totalAmount}г / ${summary.limit}г",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "${summary.percentage.roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(summary.statusColor.colorHex),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { summary.getProgressPercentage() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(summary.statusColor.colorHex),
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
            }
        }
        HabitType.ALCOHOL -> {
            // Для алкоголя - просто текст с эмодзи
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = summary.statusColor.emoji,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (summary.totalAmount > 0) {
                    Text(
                        text = "${summary.totalAmount} единиц",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(summary.statusColor.colorHex),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Быстрые действия (кнопки пресетов)
 */
@Composable
private fun HabitQuickActions(
    habitType: HabitType,
    onQuickAdd: () -> Unit,
    onPresetSelected: (String) -> Unit,
    onViewDetails: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Кнопка быстрого добавления
        Button(
            onClick = onQuickAdd,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4ECDC4)
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Отметить", fontSize = 14.sp)
        }

        // Пресеты (зависят от типа привычки)
        when (habitType) {
            HabitType.SMOKING -> {
                SmokingPreset.values().take(3).forEach { preset ->
                    OutlinedButton(
                        onClick = { onPresetSelected(preset.key) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = preset.emoji, fontSize = 16.sp)
                    }
                }
            }
            HabitType.SUGAR -> {
                SugarPreset.values().take(3).forEach { preset ->
                    OutlinedButton(
                        onClick = { onPresetSelected(preset.key) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = preset.emoji, fontSize = 16.sp)
                    }
                }
            }
            HabitType.ALCOHOL -> {
                AlcoholPreset.values().take(3).forEach { preset ->
                    OutlinedButton(
                        onClick = { onPresetSelected(preset.key) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = preset.emoji, fontSize = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Кнопка "Подробнее"
        TextButton(
            onClick = onViewDetails,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Подробнее →",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Таймлайн событий за сегодня
 */
@Composable
private fun HabitTimeline(entries: List<HabitEntry>) {
    if (entries.isEmpty()) {
        Text(
            text = "Сегодня событий не было",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f)
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "⏱️ События сегодня:",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        entries.take(5).forEach { entry ->
            TimelineItem(entry = entry)
        }

        if (entries.size > 5) {
            Text(
                text = "... и еще ${entries.size - 5}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Элемент таймлайна
 */
@Composable
private fun TimelineItem(entry: HabitEntry) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Время
            Text(
                text = entry.timestamp.format(timeFormatter),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.width(50.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Иконка
            Text(
                text = entry.habitType.icon,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Детали
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${entry.amount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    entry.preset?.let { presetKey ->
                        Spacer(modifier = Modifier.width(8.dp))
                        val preset = when (entry.habitType) {
                            HabitType.SMOKING -> SmokingPreset.fromKey(presetKey)
                            HabitType.ALCOHOL -> AlcoholPreset.fromKey(presetKey)
                            HabitType.SUGAR -> SugarPreset.fromKey(presetKey)
                        }
                        preset?.let {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = when (it) {
                                        is SmokingPreset -> "${it.emoji} ${it.displayName}"
                                        is AlcoholPreset -> "${it.emoji} ${it.displayName}"
                                        is SugarPreset -> "${it.emoji} ${it.displayName}"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                entry.note?.let { note ->
                    Text(
                        text = "\"$note\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Заглушка для карточки привычки (когда нет данных)
 */
@Composable
private fun PlaceholderHabitCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Загрузка данных...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
