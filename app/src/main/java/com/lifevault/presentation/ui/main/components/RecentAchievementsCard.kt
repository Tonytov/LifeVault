package com.lifevault.presentation.ui.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifevault.data.model.Achievement

/**
 * Карточка последних достижений
 */
@Composable
fun RecentAchievementsCard(
    achievements: List<Achievement>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎉 Последние достижения",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (achievements.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Продолжайте отслеживать привычки, чтобы получить достижения!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                // Список достижений (максимум 3)
                achievements.take(3).forEach { achievement ->
                    AchievementItem(achievement = achievement)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Кнопка "Все достижения"
                TextButton(
                    onClick = onViewAll,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Все достижения →",
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Элемент достижения
 */
@Composable
private fun AchievementItem(achievement: Achievement) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Иконка достижения
        Text(
            text = achievement.emoji,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Описание
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
