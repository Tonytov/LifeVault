package com.lifevault.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import com.lifevault.data.model.HabitType
import com.lifevault.presentation.ui.statistics.StatisticsActivity
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import com.lifevault.presentation.viewmodel.MainViewModel
import com.lifevault.presentation.viewmodel.MainUiState
import com.lifevault.ui.theme.LifeVaultTheme
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Перенаправляем на новый экран с вкладками
        val intent = Intent(this, MainTabsActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel(factory = ViewModelFactory(appContainer))) {
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Back, Title and Statistics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    IconButton(
                        onClick = {
                            val intent = Intent(context, OnboardingActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Profile",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Text(
                        text = "LifeVault",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Statistics button
                    Box(
                        modifier = Modifier
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

                // Life Extension Info
                if (uiState.lifeExtensionDays > 0) {
                    LifeExtensionCard(
                        daysGained = uiState.lifeExtensionDays,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // Health Progress
                HealthProgressCard(
                    progress = uiState.healthProgress,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Quick Habits
                QuickHabitsCard(
                    habits = uiState.habits,
                    onQuitHabit = viewModel::quitHabit,
                    onResumeHabit = viewModel::resumeHabit
                )
            }
        }
    }
}

@Composable
fun LifeCountdownCard(
    countdown: com.lifevault.data.model.LifeCalculator.LifeCountdown,
    modifier: Modifier = Modifier
) {
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
                text = "⏳ Time Remaining",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CountdownItem(
                    value = countdown.years,
                    label = "Years",
                    color = Color(0xFFFF6B6B)
                )
                CountdownItem(
                    value = countdown.days,
                    label = "Days",
                    color = Color(0xFF4ECDC4)
                )
                CountdownItem(
                    value = countdown.hours,
                    label = "Hours",
                    color = Color(0xFFFFE66D)
                )
                CountdownItem(
                    value = countdown.minutes,
                    label = "Min",
                    color = Color(0xFFA8E6CF)
                )
            }
        }
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
fun QuickHabitsCard(
    habits: List<com.lifevault.data.model.Habit>,
    onQuitHabit: (HabitType) -> Unit,
    onResumeHabit: (HabitType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🎯 Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            HabitType.values().take(4).forEach { habitType ->
                val habit = habits.find { it.type == habitType }
                val isQuit = habit?.isActive == false
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = habitType.icon,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = habitType.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    
                    Button(
                        onClick = {
                            if (isQuit) {
                                onResumeHabit(habitType)
                            } else {
                                onQuitHabit(habitType)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isQuit) Color(0xFF2E8B57) else Color(0xFFFF6B6B)
                        ),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (isQuit) "Quit ✓" else "Quit",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LifeVaultTheme {
        MainScreen()
    }
}