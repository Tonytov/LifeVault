package com.quitsmoking.presentation.ui.main.tabs

import android.content.Intent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quitsmoking.data.model.HabitType
import com.quitsmoking.presentation.ui.onboarding.OnboardingActivity
import com.quitsmoking.presentation.ui.statistics.StatisticsActivity
import com.quitsmoking.presentation.viewmodel.MainViewModel
import com.quitsmoking.presentation.viewmodel.MainUiState
import com.quitsmoking.presentation.viewmodel.SavingsData

@Composable
fun HomeTab(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Title and Statistics
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp)
            ) {
                // Back button (only show for users without profile) - positioned at start
                if (uiState.userProfile == null) {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, OnboardingActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Profile",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Title - always centered
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(
                        text = "LifeVault",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Твое хранилище жизни",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                // Statistics button - positioned at end
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable {
                            val intent = Intent(context, StatisticsActivity::class.java)
                            context.startActivity(intent)
                        }
                        .padding(12.dp)
                ) {
                    Text(
                        text = "📊",
                        fontSize = 20.sp
                    )
                }
            }

            // Life Countdown Card
            LifeCountdownCard(
                countdown = uiState.lifeCountdown,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Scientific Life Expectancy Card
            ScientificLifeExpectancyCard(
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Savings and Streaks Card - показываем всегда если есть привычки
            if (uiState.habits.isNotEmpty()) {
                SavingsAndStreaksCard(
                    savingsData = uiState.savingsData,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }


        }
    }
}

@Composable
fun LifeCountdownCard(
    countdown: com.quitsmoking.data.model.LifeCalculator.LifeCountdown,
    modifier: Modifier = Modifier
) {
    // Анимация для более живого отображения
    val animatedYears by animateFloatAsState(
        targetValue = countdown.years.toFloat(),
        animationSpec = tween(1500)
    )
    val animatedDays by animateFloatAsState(
        targetValue = countdown.days.toFloat(),
        animationSpec = tween(1500, delayMillis = 200)
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A0A).copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box {
            // Фоновый градиент
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF667eea).copy(alpha = 0.1f),
                                Color(0xFF764ba2).copy(alpha = 0.1f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Новый заголовок
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Text("🔮", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Твоя временная капсула",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Основной счетчик - годы большими цифрами
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "${animatedYears.toInt()}",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 56.sp
                    )
                    Text(
                        text = if (animatedYears.toInt() % 10 == 1 && animatedYears.toInt() % 100 != 11) "год впереди" 
                              else if (animatedYears.toInt() % 10 in 2..4 && (animatedYears.toInt() % 100 < 10 || animatedYears.toInt() % 100 >= 20)) "года впереди"
                              else "лет впереди",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Дополнительная информация в компактном виде
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ModernCountItem(
                        value = animatedDays.toInt(),
                        label = "дней",
                        icon = "📅",
                        color = Color(0xFF4ECDC4)
                    )
                    ModernCountItem(
                        value = countdown.hours.toInt(),
                        label = "часов", 
                        icon = "🕐",
                        color = Color(0xFFFFE66D)
                    )
                    ModernCountItem(
                        value = countdown.minutes.toInt(),
                        label = "минут",
                        icon = "⚡",
                        color = Color(0xFFA8E6CF)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernCountItem(
    value: Int,
    label: String,
    icon: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun CountdownItem(
    value: Long,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 28.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun LifeExtensionCard(
    daysGained: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E8B57).copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎉",
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = "+$daysGained days to life!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Great job quitting bad habits!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun HealthProgressCard(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000)
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❤️ Health Progress",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFFFF6B6B).copy(alpha = animatedProgress)
                )
            }
            
            Text(
                text = "${(animatedProgress * 100).toInt()}% Healthy",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}


@Composable
fun ScientificLifeExpectancyCard(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Получаем научные расчеты
    val profile = uiState.userProfile
    val habits = uiState.habits
    
    if (profile != null) {
        val calculation = com.quitsmoking.data.model.LifeCalculator.calculateLifeExpectancy(
            profile, habits, 0.6f
        )
        
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2C3E50).copy(alpha = 0.8f)
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
                        text = "🔬",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "Научный прогноз",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Text(
                    text = "Ожидаемая продолжительность жизни",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${calculation.finalLifeExpectancy.toInt()} лет",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF3498DB),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "95% CI: ${calculation.confidenceInterval.first.toInt()}-${calculation.confidenceInterval.second.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    
                    if (calculation.daysGainedFromQuits > 0) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "+${calculation.daysGainedFromQuits}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF2ECC71),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "дней добавлено",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                // Disclaimer
                Text(
                    text = "⚠️ Основано на эпидемиологических данных. Не заменяет медицинскую консультацию.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable 
fun TimeBankCard(
    modifier: Modifier = Modifier
) {
    // Пример данных Time Bank
    val currentBalance = 1247L // Дни в банке
    val todayEarned = 2L
    val weeklyGoal = 14L
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8E44AD).copy(alpha = 0.8f)
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
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Time Bank",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimeBankItem(
                    title = "Баланс",
                    value = "${currentBalance}",
                    subtitle = "дней жизни",
                    color = Color(0xFFE74C3C)
                )
                TimeBankItem(
                    title = "Сегодня",
                    value = "+${todayEarned}",
                    subtitle = "заработано",
                    color = Color(0xFF2ECC71)
                )
                TimeBankItem(
                    title = "Цель",
                    value = "${weeklyGoal}",
                    subtitle = "на неделю",
                    color = Color(0xFFF39C12)
                )
            }
            
            // Кнопка магазина наград
            Button(
                onClick = { /* TODO: Открыть магазин наград */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = "🎁 Магазин наград",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun TimeBankItem(
    title: String,
    value: String,
    subtitle: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
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
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun SavingsAndStreaksCard(
    savingsData: SavingsData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2ECC71).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(20.dp)
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
                    text = "💰",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = "Ваши достижения",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            
            // Total savings
            if (savingsData.totalMoneySaved > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2ECC71).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${String.format("%.0f", savingsData.totalMoneySaved)}₽",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color(0xFF2ECC71),
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "сэкономлено денег",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            // Streaks
            val streaks = listOf(
                Triple("🚬", "Без курения", savingsData.smokingStreak),
                Triple("🍷", "Без алкоголя", savingsData.alcoholStreak),  
                Triple("🍬", "Без сахара", savingsData.sugarStreak)
            ).filter { it.third > 0 }
            
            if (streaks.isNotEmpty()) {
                Text(
                    text = "🔥 Серии без вредных привычек:",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                streaks.forEach { (emoji, label, days) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = emoji,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        
                        Text(
                            text = "$days ${getDaysWord(days)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF2ECC71),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Breakdown by habit
            if (savingsData.smokingSavings > 0 || savingsData.alcoholSavings > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "💵 Экономия по привычкам:",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (savingsData.smokingSavings > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🚬 Курение:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = String.format("%.0f₽", savingsData.smokingSavings),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2ECC71),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (savingsData.alcoholSavings > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🍷 Алкоголь:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = String.format("%.0f₽", savingsData.alcoholSavings),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2ECC71),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun getDaysWord(days: Int): String {
    return when {
        days % 10 == 1 && days % 100 != 11 -> "день"
        days % 10 in 2..4 && (days % 100 < 10 || days % 100 >= 20) -> "дня"
        else -> "дней"
    }
}