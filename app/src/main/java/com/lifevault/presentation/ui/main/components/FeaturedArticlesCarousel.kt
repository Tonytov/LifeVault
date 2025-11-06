package com.lifevault.presentation.ui.main.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifevault.data.model.Article

/**
 * Карусель научных статей
 */
@Composable
fun FeaturedArticlesCarousel(
    articles: List<Article>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📚 Научные статьи",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onViewAll) {
                Text(
                    text = "Все статьи →",
                    color = Color(0xFF4ECDC4),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (articles.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "Нет доступных статей",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(20.dp)
                )
            }
        } else {
            // Горизонтальная прокрутка статей
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(articles) { article ->
                    ArticleCard(
                        article = article,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.scientificLink))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Карточка статьи
 */
@Composable
private fun ArticleCard(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Эмодзи и время чтения
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = article.imageEmoji,
                    fontSize = 32.sp
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "${article.readTimeMinutes} мин",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4ECDC4),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Заголовок
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Краткое описание
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Достоверность
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${(article.credibilityScore * 100).toInt()}% достоверность",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        article.credibilityScore >= 0.9 -> Color(0xFF2ECC71)
                        article.credibilityScore >= 0.8 -> Color(0xFF3498DB)
                        article.credibilityScore >= 0.7 -> Color(0xFFF39C12)
                        else -> Color(0xFFE74C3C)
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка "Читать"
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.3f)
                ),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Text(
                    text = "Читать →",
                    color = Color(0xFF4ECDC4),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
