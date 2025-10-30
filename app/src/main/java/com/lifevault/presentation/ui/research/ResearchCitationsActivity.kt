package com.lifevault.presentation.ui.research

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifevault.data.model.*
import com.lifevault.ui.theme.LifeVaultTheme

class ResearchCitationsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                ResearchCitationsScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearchCitationsScreen(
    onBackPressed: () -> Unit
) {
    var selectedEvidenceLevel by remember { mutableStateOf<EvidenceLevel?>(null) }
    var selectedCategory by remember { mutableStateOf<ResearchCategory?>(null) }
    var expandedCitation by remember { mutableStateOf<String?>(null) }
    
    // Фильтрация исследований
    val filteredCitations = remember(selectedEvidenceLevel, selectedCategory) {
        ScientificCitations.ALL_CITATIONS.filter { citation ->
            val levelMatch = selectedEvidenceLevel == null || citation.evidenceLevel == selectedEvidenceLevel
            val categoryMatch = selectedCategory == null || citation.category == selectedCategory
            levelMatch && categoryMatch
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "📚 Научные исследования",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Статистика
            item {
                ResearchStatisticsCard(
                    totalCitations = ScientificCitations.ALL_CITATIONS.size,
                    levelICitations = ScientificCitations.ALL_CITATIONS.count { it.evidenceLevel == EvidenceLevel.LEVEL_I },
                    levelIICitations = ScientificCitations.ALL_CITATIONS.count { it.evidenceLevel == EvidenceLevel.LEVEL_II },
                    peerReviewed = ScientificCitations.ALL_CITATIONS.count { it.isPeerReviewed }
                )
            }
            
            // Фильтры по уровню доказательности
            item {
                Text(
                    text = "🎯 Уровень доказательности",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            onClick = { selectedEvidenceLevel = null },
                            label = { Text("Все уровни") },
                            selected = selectedEvidenceLevel == null
                        )
                    }
                    
                    items(EvidenceLevel.values()) { level ->
                        FilterChip(
                            onClick = { 
                                selectedEvidenceLevel = if (selectedEvidenceLevel == level) null else level
                            },
                            label = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(level.emoji, modifier = Modifier.padding(end = 4.dp))
                                    Text(level.displayName)
                                }
                            },
                            selected = selectedEvidenceLevel == level,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = level.color.copy(alpha = 0.3f),
                                selectedLabelColor = level.color
                            )
                        )
                    }
                }
            }
            
            // Фильтры по категориям исследований
            item {
                Text(
                    text = "🔬 Категория исследований",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            onClick = { selectedCategory = null },
                            label = { Text("Все категории") },
                            selected = selectedCategory == null
                        )
                    }
                    
                    items(ResearchCategory.values()) { category ->
                        FilterChip(
                            onClick = { 
                                selectedCategory = if (selectedCategory == category) null else category
                            },
                            label = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(category.emoji, modifier = Modifier.padding(end = 4.dp))
                                    Text(category.displayName)
                                }
                            },
                            selected = selectedCategory == category
                        )
                    }
                }
            }
            
            // Результаты фильтрации
            item {
                if (selectedEvidenceLevel != null || selectedCategory != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF3498DB).copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Найдено исследований: ${filteredCitations.size}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3498DB),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Список исследований
            items(filteredCitations) { citation ->
                CitationCard(
                    citation = citation,
                    isExpanded = expandedCitation == citation.id,
                    onToggleExpanded = {
                        expandedCitation = if (expandedCitation == citation.id) null else citation.id
                    }
                )
            }
            
            // Пустое состояние
            if (filteredCitations.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🔍",
                                fontSize = 48.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Нет исследований",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Попробуйте изменить фильтры поиска",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResearchStatisticsCard(
    totalCitations: Int,
    levelICitations: Int,
    levelIICitations: Int,
    peerReviewed: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8E44AD).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📊 Статистика исследований",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = totalCitations.toString(),
                    label = "Всего источников",
                    emoji = "📚",
                    color = Color(0xFF3498DB)
                )
                
                StatisticItem(
                    value = levelICitations.toString(),
                    label = "Уровень I",
                    emoji = "🥇",
                    color = EvidenceLevel.LEVEL_I.color
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = levelIICitations.toString(),
                    label = "Уровень II",
                    emoji = "🥈",
                    color = EvidenceLevel.LEVEL_II.color
                )
                
                StatisticItem(
                    value = peerReviewed.toString(),
                    label = "Peer-reviewed",
                    emoji = "✅",
                    color = Color(0xFF2ECC71)
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    value: String,
    label: String,
    emoji: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CitationCard(
    citation: ScientificCitation,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpanded() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Заголовок и уровень доказательности
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = citation.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = citation.authors,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    Text(
                        text = "${citation.journal} (${citation.year})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                // Уровень доказательности
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = citation.evidenceLevel.color.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = citation.evidenceLevel.emoji,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = citation.evidenceLevel.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = citation.evidenceLevel.color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Категория и теги
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Категория
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF95A5A6).copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = citation.category.emoji + " " + citation.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF95A5A6),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                
                // Peer-reviewed индикатор
                if (citation.isPeerReviewed) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2ECC71).copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "✅ Peer-reviewed",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF2ECC71),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Основные результаты (всегда видимы)
            Text(
                text = "🎯 Основные результаты:",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = citation.keyFindings,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Развернутая информация
            if (isExpanded) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Методология
                        Text(
                            text = "🔬 Методология:",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = citation.methodology,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        // Размер выборки
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "👥 Размер выборки:",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${citation.sampleSize} участников",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF3498DB),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // DOI
                        if (citation.doi.isNotBlank()) {
                            Text(
                                text = "🔗 DOI: ${citation.doi}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Кнопка развернуть/свернуть
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = onToggleExpanded,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4ECDC4)
                    )
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isExpanded) "Свернуть" else "Подробнее")
                }
            }
        }
    }
}