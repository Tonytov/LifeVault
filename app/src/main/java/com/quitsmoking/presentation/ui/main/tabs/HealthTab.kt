package com.quitsmoking.presentation.ui.main.tabs

import android.content.Intent
import android.net.Uri
import com.quitsmoking.presentation.ui.whatif.WhatIfSimulatorActivity
import com.quitsmoking.presentation.ui.challenges.DailyChallengesActivity
import com.quitsmoking.presentation.ui.research.ResearchCitationsActivity
import com.quitsmoking.presentation.ui.disclaimers.MedicalDisclaimersActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quitsmoking.data.model.*

@Composable
fun HealthTab() {
    var selectedCategory by remember { mutableStateOf<ArticleCategory?>(null) }
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Text(
            text = "📚 Факты о здоровье",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // New Scientific Tools Section
        ScientificToolsSection(
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Categories
        Text(
            text = "Выберите тему:",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(ArticlesRepository.getAllCategories()) { category ->
                CategoryChip(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = { 
                        selectedCategory = if (selectedCategory == category) null else category 
                    }
                )
            }
        }
        
        // Articles
        if (selectedCategory != null) {
            val articles = ArticlesRepository.getArticlesByCategory(selectedCategory!!)
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "${selectedCategory!!.emoji} ${selectedCategory!!.displayName}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(articles) { article ->
                    EnhancedArticleCard(
                        article = article,
                        onClick = {
                            // Открываем научную ссылку в браузере
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.scientificLink))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        } else {
            // Welcome message when no category selected
            WelcomeCard()
        }
    }
}

@Composable
fun CategoryChip(
    category: ArticleCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                Color(0xFF4ECDC4).copy(alpha = 0.3f)
            else 
                Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.emoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) Color(0xFF4ECDC4) else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun EnhancedArticleCard(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with emoji, reading time, and credibility
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = article.imageEmoji,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    
                    Column {
                        // Difficulty level
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF9B59B6).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${article.difficulty.emoji} ${article.difficulty.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9B59B6),
                                fontSize = 10.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Credibility score
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        article.credibilityScore >= 0.9 -> Color(0xFF2ECC71).copy(alpha = 0.2f)
                                        article.credibilityScore >= 0.8 -> Color(0xFF3498DB).copy(alpha = 0.2f)
                                        article.credibilityScore >= 0.7 -> Color(0xFFF39C12).copy(alpha = 0.2f)
                                        else -> Color(0xFFE74C3C).copy(alpha = 0.2f)
                                    }
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "📊 ${(article.credibilityScore * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = when {
                                    article.credibilityScore >= 0.9 -> Color(0xFF2ECC71)
                                    article.credibilityScore >= 0.8 -> Color(0xFF3498DB)
                                    article.credibilityScore >= 0.7 -> Color(0xFFF39C12)
                                    else -> Color(0xFFE74C3C)
                                },
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF4ECDC4).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${article.readTimeMinutes} мин",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4ECDC4)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Title
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Summary
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Research citations preview
            if (article.researchCitations.isNotEmpty()) {
                Text(
                    text = "📚 Научные источники:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    items(article.researchCitations.take(3)) { citation ->
                        EvidenceLevelChip(evidenceLevel = citation.evidenceLevel)
                    }
                    
                    if (article.researchCitations.size > 3) {
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF95A5A6).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "+${article.researchCitations.size - 3}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF95A5A6),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
            
            // Key findings preview
            if (article.keyFindings.isNotEmpty()) {
                Text(
                    text = "💡 Ключевые результаты:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                
                Text(
                    text = "• ${article.keyFindings.first()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                if (article.keyFindings.size > 1) {
                    Text(
                        text = "и еще ${article.keyFindings.size - 1} важных результата...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4ECDC4),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
            
            // Read more button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔗 Изучить исследование",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4ECDC4)
                )
                
                Text(
                    text = "Открыть →",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4ECDC4),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EvidenceLevelChip(
    evidenceLevel: EvidenceLevel
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(evidenceLevel.color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = evidenceLevel.emoji + " " + evidenceLevel.displayName.split(" ")[1], // Get just the level number
            style = MaterialTheme.typography.labelSmall,
            color = evidenceLevel.color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🧠",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Изучайте науку о здоровье",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Выберите интересующую вас тему, чтобы изучить научные факты о влиянии вредных привычек на здоровье",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsItem(
                    number = "${ArticlesRepository.articles.size}",
                    label = "Статей"
                )
                
                StatsItem(
                    number = "${ArticlesRepository.getAllCategories().size}",
                    label = "Категорий"
                )
                
                StatsItem(
                    number = "100%",
                    label = "Научных"
                )
            }
        }
    }
}

@Composable
fun StatsItem(
    number: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF4ECDC4),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ScientificToolsSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(modifier = modifier) {
        Text(
            text = "🔬 Научные инструменты",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // What-If Симулятор
            ScientificToolCard(
                title = "What-If Симулятор",
                description = "Моделируйте различные сценарии жизни",
                icon = "📊",
                color = Color(0xFF3498DB),
                onClick = { 
                    val intent = Intent(context, WhatIfSimulatorActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            )
            
            // Daily Challenges
            ScientificToolCard(
                title = "Вызовы",
                description = "Ежедневные задания для здоровья",
                icon = "🎯",
                color = Color(0xFF2ECC71),
                onClick = { 
                    val intent = Intent(context, DailyChallengesActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Научные источники
            ScientificToolCard(
                title = "Исследования",
                description = "Изучите научные цитаты",
                icon = "📖",
                color = Color(0xFF9B59B6),
                onClick = { 
                    val intent = Intent(context, ResearchCitationsActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            )
            
            // Медицинские дисклеймеры
            ScientificToolCard(
                title = "Дисклеймеры",
                description = "Медицинские предупреждения",
                icon = "⚠️",
                color = Color(0xFFE67E22),
                onClick = { 
                    val intent = Intent(context, MedicalDisclaimersActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ScientificToolCard(
    title: String,
    description: String,
    icon: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = color,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Открыть →",
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}