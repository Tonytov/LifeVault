package com.lifevault.presentation.ui.challenges

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.testTag
import com.lifevault.data.model.*
import com.lifevault.ui.theme.LifeVaultTheme

class DailyChallengesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                DailyChallengesScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengesScreen(
    onBackPressed: () -> Unit
) {
    // Состояние фильтров
    var selectedCategory by remember { mutableStateOf<ChallengeCategory?>(null) }
    var selectedDifficulty by remember { mutableStateOf<ChallengeDifficulty?>(null) }
    
    
    // Пример данных пользователя
    val userStats = remember {
        ChallengeStats(
            totalChallengesStarted = 23,
            totalChallengesCompleted = 18,
            completionRate = 78.3,
            longestStreak = 14,
            currentStreak = 7,
            totalRewardsEarned = 2840L,
            favoriteCategory = ChallengeCategory.QUIT_HABITS,
            averageDifficulty = ChallengeDifficulty.INTERMEDIATE
        )
    }
    
    // Активные вызовы пользователя
    val activeProgress = remember {
        listOf(
            UserChallengeProgress(
                userId = 1,
                challengeId = 1,
                startDate = java.time.LocalDate.now().minusDays(7),
                endDate = java.time.LocalDate.now().plusDays(23),
                status = ChallengeStatus.IN_PROGRESS,
                currentStreak = 7,
                maxStreak = 7,
                completedDays = 7,
                totalDays = 30,
                progress = 0.23
            ),
            UserChallengeProgress(
                userId = 1,
                challengeId = 4,
                startDate = java.time.LocalDate.now().minusDays(2),
                endDate = java.time.LocalDate.now().plusDays(5),
                status = ChallengeStatus.IN_PROGRESS,
                currentStreak = 2,
                maxStreak = 2,
                completedDays = 2,
                totalDays = 7,
                progress = 0.29
            )
        )
    }
    
    // Фильтрация вызовов
    val availableChallenges = remember(selectedCategory, selectedDifficulty) {
        DefaultChallenges.ALL_CHALLENGES.filter { challenge ->
            (selectedCategory == null || challenge.category == selectedCategory) &&
            (selectedDifficulty == null || challenge.difficulty == selectedDifficulty)
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
                .testTag("challengesList")
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
                        text = "🎯 Ежедневные Вызовы",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("challengesScreenTitle")
                    )
                }
            }
            
            // Статистика пользователя
            item {
                UserStatsCard(userStats)
            }
            
            // Активные вызовы
            if (activeProgress.isNotEmpty()) {
                item {
                    Text(
                        text = "🔥 Активные вызовы",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(activeProgress) { progress ->
                    val challenge = DefaultChallenges.ALL_CHALLENGES.find { it.id == progress.challengeId }
                    if (challenge != null) {
                        ActiveChallengeCard(
                            challenge = challenge,
                            progress = progress
                        )
                    }
                }
            }
            
            // Фильтры
            item {
                Text(
                    text = "📋 Доступные вызовы",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Фильтр по категориям
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("categoryFilters")
                ) {
                    item {
                        FilterChip(
                            onClick = { selectedCategory = null },
                            label = { Text("Все") },
                            selected = selectedCategory == null,
                            modifier = Modifier.testTag("filterCategory_all")
                        )
                    }

                    items(ChallengeCategory.values()) { category ->
                        FilterChip(
                            onClick = { selectedCategory = if (selectedCategory == category) null else category },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(category.icon, modifier = Modifier.padding(end = 4.dp))
                                    Text(category.displayName)
                                }
                            },
                            selected = selectedCategory == category,
                            modifier = Modifier.testTag("filterCategory_${category.name}")
                        )
                    }
                }
                
                // Фильтр по сложности
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("difficultyFilters")
                ) {
                    item {
                        FilterChip(
                            onClick = { selectedDifficulty = null },
                            label = { Text("Любая сложность") },
                            selected = selectedDifficulty == null,
                            modifier = Modifier.testTag("filterDifficulty_all")
                        )
                    }

                    items(ChallengeDifficulty.values()) { difficulty ->
                        FilterChip(
                            onClick = { selectedDifficulty = if (selectedDifficulty == difficulty) null else difficulty },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(difficulty.emoji, modifier = Modifier.padding(end = 4.dp))
                                    Text(difficulty.displayName)
                                }
                            },
                            selected = selectedDifficulty == difficulty,
                            modifier = Modifier.testTag("filterDifficulty_${difficulty.name}")
                        )
                    }
                }
            }
            
            // Список доступных вызовов
            items(availableChallenges) { challenge ->
                IndependentChallengeItem(challenge = challenge)
            }
            
            // Дневной вызов
            item {
                DailyChallengeOfTheDayCard(
                    challenge = ChallengeSystem.getDailyChallengeOfTheDay()
                )
            }
        }
    }
}

@Composable
fun UserStatsCard(
    stats: ChallengeStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("userStatsCard"),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "📊 Ваша статистика",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Серия",
                    value = "${stats.currentStreak}",
                    subtitle = "дней подряд",
                    color = Color(0xFFE74C3C),
                    modifier = Modifier.testTag("statItem_streak")
                )

                StatItem(
                    title = "Успешность",
                    value = "${stats.completionRate.toInt()}%",
                    subtitle = "${stats.totalChallengesCompleted}/${stats.totalChallengesStarted}",
                    color = Color(0xFF2ECC71),
                    modifier = Modifier.testTag("statItem_success")
                )

                StatItem(
                    title = "Рекорд",
                    value = "${stats.longestStreak}",
                    subtitle = "максимальная серия",
                    color = Color(0xFF3498DB),
                    modifier = Modifier.testTag("statItem_record")
                )

                StatItem(
                    title = "Заработано",
                    value = "${stats.totalRewardsEarned}",
                    subtitle = "дней жизни",
                    color = Color(0xFFF39C12),
                    modifier = Modifier.testTag("statItem_earned")
                )
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ActiveChallengeCard(
    challenge: DailyChallenge,
    progress: UserChallengeProgress,
    modifier: Modifier = Modifier
) {
    val progressAnimation by animateFloatAsState(
        targetValue = progress.progress.toFloat(),
        animationSpec = tween(1000)
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("activeChallengeCard_${challenge.id}"),
        colors = CardDefaults.cardColors(
            containerColor = challenge.category.color.let { Color(android.graphics.Color.parseColor(it)) }.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = challenge.category.icon,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = challenge.duration.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Стрик индикатор
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE74C3C).copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("🔥", fontSize = 16.sp)
                        Text(
                            text = "${progress.currentStreak}",
                            color = Color(0xFFE74C3C),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Прогресс бар
            Text(
                text = "Прогресс: ${progress.completedDays}/${progress.totalDays} дней",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LinearProgressIndicator(
                progress = progressAnimation,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFF2ECC71),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Мотивационное сообщение
            Text(
                text = ChallengeMotivation.getStreakMessage(progress.currentStreak),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: DailyChallenge,
    onStartChallenge: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("challengeItem_${challenge.id}"),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = challenge.category.icon,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = challenge.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    // Сложность
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF9B59B6).copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(challenge.difficulty.emoji, fontSize = 14.sp)
                            Text(
                                text = challenge.difficulty.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9B59B6),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Награда
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF39C12).copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "+${challenge.rewardTimeBank} дней",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFF39C12),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Продолжительность: ${challenge.duration.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                
                Button(
                    onClick = onStartChallenge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2ECC71)
                    ),
                    modifier = Modifier.testTag("startChallengeButton_${challenge.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Начать")
                }
            }
        }
    }
}

@Composable
fun DailyChallengeOfTheDayCard(
    challenge: DailyChallenge,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("challengeOfTheDayCard"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE67E22).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⭐",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Вызов дня",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFE67E22),
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Button(
                onClick = { /* Feature: Accept daily challenge - Coming soon */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE67E22)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🎯 Принять вызов дня",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun IndependentChallengeItem(
    challenge: DailyChallenge,
    modifier: Modifier = Modifier
) {
    // Полностью независимое состояние для этого конкретного элемента
    var isActive by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }
    
    if (isActive && startTime > 0L) {
        // Показываем активный вызов с прогресс-баром
        IndependentActiveCard(
            challenge = challenge,
            startTime = startTime,
            onComplete = {
                isActive = false
                startTime = 0L
            },
            modifier = modifier
        )
    } else {
        // Показываем обычную карточку с кнопкой "Начать"
        ChallengeCard(
            challenge = challenge,
            onStartChallenge = { 
                startTime = System.currentTimeMillis()
                isActive = true
            },
            modifier = modifier
        )
    }
}

@Composable
fun IndependentActiveCard(
    challenge: DailyChallenge,
    startTime: Long,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Определяем длительность вызова в миллисекундах
    val challengeDuration = when (challenge.duration) {
        ChallengeDuration.ONE_DAY -> 24 * 60 * 60 * 1000L
        ChallengeDuration.THREE_DAYS -> 3 * 24 * 60 * 60 * 1000L
        ChallengeDuration.ONE_WEEK -> 7 * 24 * 60 * 60 * 1000L
        ChallengeDuration.TWO_WEEKS -> 14 * 24 * 60 * 60 * 1000L
        ChallengeDuration.ONE_MONTH -> 30L * 24 * 60 * 60 * 1000L
        ChallengeDuration.THREE_MONTHS -> 90L * 24 * 60 * 60 * 1000L
        ChallengeDuration.SIX_MONTHS -> 180L * 24 * 60 * 60 * 1000L
        ChallengeDuration.ONE_YEAR -> 365L * 24 * 60 * 60 * 1000L
    }
    
    // Локальное состояние прогресса
    var currentProgress by remember { mutableStateOf(0f) }
    var timeRemaining by remember { mutableStateOf(challengeDuration) }
    
    // Таймер для этого конкретного вызова
    LaunchedEffect(startTime) {
        if (startTime <= 0L) return@LaunchedEffect
        
        currentProgress = 0f
        timeRemaining = challengeDuration
        
        while (currentProgress < 1f) {
            val currentTime = System.currentTimeMillis()
            val elapsed = currentTime - startTime
            val newProgress = (elapsed.toFloat() / challengeDuration).coerceAtMost(1f)
            val newTimeRemaining = (challengeDuration - elapsed).coerceAtLeast(0L)
            
            currentProgress = newProgress
            timeRemaining = newTimeRemaining
            
            if (newProgress >= 1f) {
                kotlinx.coroutines.delay(500)
                onComplete()
                break
            }
            
            kotlinx.coroutines.delay(1000)
        }
    }
    
    // Анимация прогресса
    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("challengeItem_${challenge.id}"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2ECC71).copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = challenge.category.icon,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "В процессе выполнения...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2ECC71),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                // Пульсирующий индикатор активности
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2ECC71))
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Прогресс-бар с анимацией
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Прогресс:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2ECC71),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Красивый прогресс-бар
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF2ECC71),
                                        Color(0xFF27AE60)
                                    )
                                )
                            )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Время до завершения
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Осталось: ${formatTimeRemaining(timeRemaining)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                
                // Кнопка завершить досрочно
                OutlinedButton(
                    onClick = onComplete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2ECC71)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Завершить",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

private fun formatTimeRemaining(timeInMillis: Long): String {
    val seconds = timeInMillis / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        days > 0 -> {
            val remainingHours = hours % 24
            if (remainingHours > 0) {
                "${days}д ${remainingHours}ч"
            } else {
                "${days}д"
            }
        }
        hours > 0 -> {
            val remainingMinutes = minutes % 60
            if (remainingMinutes > 0) {
                "${hours}ч ${remainingMinutes}м"
            } else {
                "${hours}ч"
            }
        }
        minutes > 0 -> {
            val remainingSeconds = seconds % 60
            "${minutes}м ${remainingSeconds}с"
        }
        else -> "${seconds}с"
    }
}