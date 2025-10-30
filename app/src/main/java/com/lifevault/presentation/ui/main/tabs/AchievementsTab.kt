package com.lifevault.presentation.ui.main.tabs

import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.lifevault.data.model.Achievement
import com.lifevault.data.model.AchievementCategory
import com.lifevault.data.model.AchievementsRepository
import com.lifevault.presentation.viewmodel.MainViewModel

@Composable
fun AchievementsTab(viewModel: MainViewModel = viewModel(factory = ViewModelFactory(appContainer))) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Получаем достижения с учетом текущего прогресса
    val achievements = remember(uiState.habits, uiState.healthProgress, uiState.lifeExtensionDays) {
        AchievementsRepository.checkAchievements(
            habits = uiState.habits,
            healthScore = uiState.healthProgress,
            lifeExtensionDays = uiState.lifeExtensionDays
        )
    }
    
    val unlockedAchievements = achievements.filter { it.isUnlocked }
    val lockedAchievements = achievements.filter { !it.isUnlocked }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Text(
            text = "🏆 Достижения",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Progress Summary
        AchievementsProgressCard(
            unlockedCount = unlockedAchievements.size,
            totalCount = achievements.size,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Разблокированные достижения
            if (unlockedAchievements.isNotEmpty()) {
                item {
                    Text(
                        text = "✨ Разблокированные (${unlockedAchievements.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2E8B57),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(unlockedAchievements) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        isUnlocked = true
                    )
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
            
            // Заблокированные достижения
            if (lockedAchievements.isNotEmpty()) {
                item {
                    Text(
                        text = "🔒 Заблокированные (${lockedAchievements.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                items(lockedAchievements) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        isUnlocked = false
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementsProgressCard(
    unlockedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "Прогресс достижений",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "$unlockedCount из $totalCount",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF4ECDC4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF4ECDC4))
                )
            }
            
            Text(
                text = "${(progress * 100).toInt()}% завершено",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) 
                Color(0xFF2E8B57).copy(alpha = 0.2f)
            else 
                Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement Emoji/Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isUnlocked) 
                            Color(0xFF4ECDC4).copy(alpha = 0.3f)
                        else 
                            Color.White.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = achievement.emoji,
                    fontSize = 32.sp,
                    color = if (!isUnlocked) Color.White.copy(alpha = 0.4f) else Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Achievement Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                    
                    if (isUnlocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓",
                            color = Color(0xFF2E8B57),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUnlocked) 
                        Color.White.copy(alpha = 0.8f) 
                    else 
                        Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                // Category Badge
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isUnlocked) 
                                Color(0xFF4ECDC4).copy(alpha = 0.3f)
                            else 
                                Color.White.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = achievement.category.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isUnlocked) 
                            Color(0xFF4ECDC4) 
                        else 
                            Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}