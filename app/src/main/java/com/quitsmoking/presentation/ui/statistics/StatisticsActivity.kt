package com.quitsmoking.presentation.ui.statistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import androidx.hilt.navigation.compose.hiltViewModel
import com.quitsmoking.data.model.HabitType
import com.quitsmoking.data.model.Habit
import com.quitsmoking.presentation.viewmodel.StatisticsViewModel
import com.quitsmoking.presentation.viewmodel.StatisticsUiState
import com.quitsmoking.ui.theme.LifeVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class StatisticsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                StatisticsScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "📊 Life Statistics",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F0F23),
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4ECDC4))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Life Overview Card
                    LifeOverviewCard(
                        totalLifeExtension = uiState.totalLifeExtensionDays,
                        healthScore = uiState.healthScore,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Habits Progress Chart
                    HabitsProgressCard(
                        habits = uiState.habits,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Health Timeline
                    HealthTimelineCard(
                        quitHabits = uiState.quitHabits,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Money Savings Chart  
                    MoneySavingsCard(
                        habits = uiState.habits,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Daily Progress Chart
                    DailyProgressCard(
                        daysWithoutBadHabits = uiState.daysWithoutBadHabits,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LifeOverviewCard(
    totalLifeExtension: Int,
    healthScore: Float,
    modifier: Modifier = Modifier
) {
    val animatedHealthScore by animateFloatAsState(
        targetValue = healthScore,
        animationSpec = tween(durationMillis = 1500)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Обзор здоровья",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Life Extension with gradient background
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF2E8B57).copy(alpha = 0.3f),
                                    Color(0xFF228B22).copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📈",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+$totalLifeExtension",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF32CD32)
                        )
                        Text(
                            text = "дней добавлено",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Health Score with circular progress
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        healthScore = animatedHealthScore,
                        modifier = Modifier.size(120.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🎯",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "${(animatedHealthScore * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            
            Text(
                text = "Health Score",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun CircularProgressIndicator(
    healthScore: Float,
    modifier: Modifier = Modifier
) {
    val sweepAngle = 360f * healthScore
    
    Canvas(modifier = modifier) {
        val strokeWidth = 12.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        val topLeft = Offset(center.x - radius, center.y - radius)
        
        // Background circle
        drawArc(
            color = Color.White.copy(alpha = 0.2f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Progress arc with gradient effect
        if (sweepAngle > 0) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF4ECDC4),
                        Color(0xFF44A08D),
                        Color(0xFF2E8B57)
                    ),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun HabitsProgressCard(
    habits: List<Habit>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4ECDC4).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📊",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Прогресс привычек",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            HabitType.values().forEach { habitType ->
                val habit = habits.find { it.type == habitType }
                val isQuit = habit?.isActive == false
                val progress = if (isQuit) 1f else 0f

                HabitProgressItem(
                    habitType = habitType,
                    progress = progress,
                    isQuit = isQuit,
                    quitDate = habit?.quitDate,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun HabitProgressItem(
    habitType: HabitType,
    progress: Float,
    isQuit: Boolean,
    quitDate: LocalDateTime?,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, delayMillis = habitType.ordinal * 150)
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isQuit) 
                Color(0xFF2E8B57).copy(alpha = 0.15f) 
            else 
                Color(0xFFFF6B6B).copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isQuit) 
                                    Color(0xFF2E8B57).copy(alpha = 0.3f)
                                else 
                                    Color(0xFFFF6B6B).copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = habitType.icon,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = habitType.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (isQuit) "Завершено" else "Активна",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                if (isQuit && quitDate != null) {
                    val daysSinceQuit = ChronoUnit.DAYS.between(quitDate.toLocalDate(), LocalDateTime.now().toLocalDate())
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2E8B57).copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${daysSinceQuit} дн.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF32CD32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Enhanced progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = if (isQuit) listOf(
                                    Color(0xFF32CD32),
                                    Color(0xFF2E8B57)
                                ) else listOf(
                                    Color(0xFFFF6B6B),
                                    Color(0xFFDC143C)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun HealthTimelineCard(
    quitHabits: List<Habit>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "⏰",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Health Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (quitHabits.isEmpty()) {
                Text(
                    text = "Start quitting habits to see your progress timeline!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                quitHabits.sortedByDescending { it.quitDate }.forEach { habit ->
                    TimelineItem(habit = habit)
                }
            }
        }
    }
}

@Composable
fun TimelineItem(habit: Habit) {
    val daysSinceQuit = habit.quitDate?.let { 
        ChronoUnit.DAYS.between(it.toLocalDate(), LocalDateTime.now().toLocalDate())
    } ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2E8B57))
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Quit ${habit.type.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = habit.quitDate?.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        
        Text(
            text = "${daysSinceQuit}d ago",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF2E8B57)
        )
    }
}

@Composable
fun DailyProgressCard(
    daysWithoutBadHabits: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E8B57).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔥",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "$daysWithoutBadHabits",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E8B57),
                fontSize = 36.sp
            )
            Text(
                text = "Days of Healthy Living",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MoneySavingsCard(
    habits: List<Habit>,
    modifier: Modifier = Modifier
) {
    val quitHabits = habits.filter { !it.isActive && it.quitDate != null }
    val totalSavings = quitHabits.sumOf { habit ->
        val daysSinceQuit = habit.quitDate?.let {
            ChronoUnit.DAYS.between(it.toLocalDate(), LocalDateTime.now().toLocalDate())
        } ?: 0
        
        when (habit.type) {
            HabitType.SMOKING -> {
                val costPerDay = (habit.costPerUnit * (habit.dailyUsage ?: 0))
                daysSinceQuit * costPerDay
            }
            HabitType.ALCOHOL -> {
                val costPerDay = habit.costPerUnit * ((habit.weeklyUsage ?: 0) / 7.0)
                daysSinceQuit * costPerDay
            }
            else -> 0.0
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9B59B6).copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9B59B6).copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💰",
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Экономия денег",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Total savings display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF9B59B6).copy(alpha = 0.3f),
                                Color(0xFF8E44AD).copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "₽${String.format("%.0f", totalSavings)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBB86FC),
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Всего сэкономлено",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Individual habit savings
            quitHabits.forEach { habit ->
                MoneySavingItem(habit = habit)
            }

            if (quitHabits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Начните бросать вредные привычки\nчтобы увидеть экономию!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MoneySavingItem(habit: Habit) {
    val daysSinceQuit = habit.quitDate?.let {
        ChronoUnit.DAYS.between(it.toLocalDate(), LocalDateTime.now().toLocalDate())
    } ?: 0

    val savings = when (habit.type) {
        HabitType.SMOKING -> {
            val costPerDay = (habit.costPerUnit * (habit.dailyUsage ?: 0))
            daysSinceQuit * costPerDay
        }
        HabitType.ALCOHOL -> {
            val costPerDay = habit.costPerUnit * ((habit.weeklyUsage ?: 0) / 7.0)
            daysSinceQuit * costPerDay
        }
        else -> 0.0
    }

    if (savings > 0) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.type.icon,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = habit.type.displayName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "₽${String.format("%.0f", savings)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFBB86FC),
                fontWeight = FontWeight.Bold
            )
        }
    }
}