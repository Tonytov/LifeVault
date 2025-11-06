package com.lifevault.presentation.ui.main.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * Карточка прогноза продолжительности жизни с двумя линейками (до/после)
 */
@Composable
fun LifeExpectancyCard(
    lifeExpectancyBefore: Double,
    lifeExpectancyCurrent: Double,
    yearsGained: Double,
    daysGainedThisMonth: Int,
    modifier: Modifier = Modifier
) {
    // Анимация для прогресс-баров
    val animatedBefore by animateFloatAsState(
        targetValue = (lifeExpectancyBefore.toFloat() / 85f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "before_progress"
    )

    val animatedCurrent by animateFloatAsState(
        targetValue = (lifeExpectancyCurrent.toFloat() / 85f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, delayMillis = 300, easing = EaseOutCubic),
        label = "current_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Заголовок
            Text(
                text = "⏰ Прогноз жизни",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Линейка "Раньше"
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Раньше:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${lifeExpectancyBefore.roundToInt()} лет",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF95A5A6),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Прогресс-бар "Раньше"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    // Фон
                    LinearProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        trackColor = Color.Transparent,
                    )
                    // Прогресс
                    LinearProgressIndicator(
                        progress = { animatedBefore },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color(0xFF95A5A6),
                        trackColor = Color.Transparent,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Линейка "Сейчас"
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Сейчас:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${lifeExpectancyCurrent.roundToInt()} лет",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF2ECC71),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Прогресс-бар "Сейчас"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    // Фон
                    LinearProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        trackColor = Color.Transparent,
                    )
                    // Прогресс
                    LinearProgressIndicator(
                        progress = { animatedCurrent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color(0xFF2ECC71),
                        trackColor = Color.Transparent,
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Разделитель
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.2f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Статистика изменений
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Добавлено лет
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "💚",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${String.format("%.1f", yearsGained)} года",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2ECC71),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "благодаря привычкам",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                // Вертикальный разделитель
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(0.5.dp))
                ) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                }

                // За последний месяц
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📈",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+$daysGainedThisMonth дней",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF3498DB),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "за последний месяц",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}