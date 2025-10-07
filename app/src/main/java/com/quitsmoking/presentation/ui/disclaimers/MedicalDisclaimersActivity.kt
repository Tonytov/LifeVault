package com.quitsmoking.presentation.ui.disclaimers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.quitsmoking.data.model.*
import com.quitsmoking.ui.theme.LifeVaultTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicalDisclaimersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                MedicalDisclaimersScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalDisclaimersScreen(
    onBackPressed: () -> Unit
) {
    var expandedDisclaimer by remember { mutableStateOf<DisclaimerType?>(null) }
    
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
                        text = "⚕️ Медицинские уведомления",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Главное предупреждение
            item {
                ImportantWarningCard()
            }
            
            // Контактная информация для экстренных случаев
            item {
                EmergencyContactCard()
            }
            
            // Список медицинских уведомлений
            items(MedicalDisclaimerData.ALL_DISCLAIMERS) { disclaimer ->
                DisclaimerCard(
                    disclaimer = disclaimer,
                    isExpanded = expandedDisclaimer == disclaimer.type,
                    onToggleExpanded = {
                        expandedDisclaimer = if (expandedDisclaimer == disclaimer.type) null else disclaimer.type
                    }
                )
            }
            
            // Информация о разработчиках и версии
            item {
                AppInfoCard()
            }
            
            // Дополнительные ресурсы
            item {
                AdditionalResourcesCard()
            }
        }
    }
}

@Composable
fun ImportantWarningCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE74C3C).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "ВАЖНОЕ ПРЕДУПРЕЖДЕНИЕ",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFE74C3C),
                    fontWeight = FontWeight.ExtraBold
                )
            }
            
            Text(
                text = "Это приложение предоставляет только общую информацию и НЕ ЯВЛЯЕТСЯ медицинской консультацией. Все расчеты основаны на статистических данных и не учитывают индивидуальные особенности вашего здоровья.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF).copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "ОБЯЗАТЕЛЬНО проконсультируйтесь с квалифицированным врачом перед принятием любых решений, касающихся вашего здоровья и образа жизни.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun EmergencyContactCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2ECC71).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "📞",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Экстренная медицинская помощь",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            EmergencyContactItem(
                service = "Скорая помощь",
                number = "103",
                description = "При угрозе жизни и здоровью"
            )
            
            EmergencyContactItem(
                service = "Телефон доверия",
                number = "8-800-2000-122",
                description = "Психологическая помощь 24/7"
            )
            
            EmergencyContactItem(
                service = "Наркологическая помощь",
                number = "8-800-200-0-200",
                description = "Консультации по зависимостям"
            )
        }
    }
}

@Composable
fun EmergencyContactItem(
    service: String,
    number: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = service,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2ECC71).copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFF2ECC71),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun DisclaimerCard(
    disclaimer: MedicalDisclaimer,
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
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = disclaimer.emoji,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = disclaimer.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = disclaimer.severity.color.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = disclaimer.severity.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = disclaimer.severity.color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Краткое описание (всегда видимо)
            Text(
                text = disclaimer.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f)
            )
            
            // Развернутая информация
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.05f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "📝 Подробности:",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = disclaimer.detailedDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        if (disclaimer.recommendations.isNotEmpty()) {
                            Text(
                                text = "💡 Рекомендации:",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            disclaimer.recommendations.forEach { recommendation ->
                                Text(
                                    text = "• $recommendation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                        
                        if (disclaimer.legalText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF95A5A6).copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = "⚖️ Правовая информация:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF95A5A6),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                                Text(
                                    text = disclaimer.legalText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppInfoCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF34495E).copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "ℹ️ О приложении",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            InfoItem(
                label = "Название",
                value = "LifeVault - Твое хранилище жизни"
            )
            
            InfoItem(
                label = "Версия",
                value = "1.0.0 (Beta)"
            )
            
            InfoItem(
                label = "Разработчик",
                value = "QuitSmoking Team"
            )
            
            InfoItem(
                label = "Дата обновления",
                value = "Ноябрь 2024"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Приложение использует только статистические данные из научных исследований и не предоставляет персонализированных медицинских консультаций.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdditionalResourcesCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9B59B6).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📖 Полезные ресурсы",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            ResourceItem(
                title = "Минздрав России",
                description = "Официальная информация о здоровье",
                url = "minzdrav.gov.ru"
            )
            
            ResourceItem(
                title = "ВОЗ (WHO)",
                description = "Всемирная организация здравоохранения",
                url = "who.int"
            )
            
            ResourceItem(
                title = "PubMed",
                description = "База медицинских исследований",
                url = "pubmed.ncbi.nlm.nih.gov"
            )
            
            ResourceItem(
                title = "Портал о здоровье",
                description = "Проверенная медицинская информация",
                url = "takzdorovo.ru"
            )
        }
    }
}

@Composable
fun ResourceItem(
    title: String,
    description: String,
    url: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
        )
        Text(
            text = "🔗 $url",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF9B59B6),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}