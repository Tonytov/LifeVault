package com.lifevault.presentation.ui.main.tabs

import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.lifevault.data.model.Habit
import com.lifevault.data.model.HabitType
import com.lifevault.presentation.ui.habits.HabitTrackingActivity
import com.lifevault.presentation.ui.timebank.TimeBankShopActivity
import com.lifevault.presentation.viewmodel.MainViewModel
import kotlin.math.abs
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun HabitsTab(mainViewModel: MainViewModel = viewModel(factory = ViewModelFactory(appContainer))) {
    val mainUiState by mainViewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎯 Мои привычки",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { 
                    val intent = Intent(context, HabitTrackingActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = Color(0xFF4ECDC4),
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить привычку",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        
        // Habits List
        if (mainUiState.habits.isEmpty()) {
            EmptyHabitsState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(mainUiState.habits) { habit ->
                    HabitProgressCard(
                        habit = habit,
                        onQuit = { mainViewModel.quitHabit(habit.type) },
                        onResume = { mainViewModel.resumeHabit(habit.type) }
                    )
                }
            }
        }
    }
}

@Composable
fun HealthProgressCard(habits: List<Habit>) {
    val activeHabits = habits.filter { it.isActive }
    val quitHabits = habits.filter { !it.isActive }
    
    val totalDaysGained = quitHabits.sumOf { habit ->
        habit.quitDate?.let { quitDate ->
            ChronoUnit.DAYS.between(quitDate.toLocalDate(), LocalDate.now()).toInt()
        } ?: 0
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2ECC71).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❤️",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "+$totalDaysGained дней к жизни",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2ECC71),
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "${quitHabits.size} брошенных привычек",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
            
            if (activeHabits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "⚠️ ${activeHabits.size} активных привычек",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE74C3C)
                )
            }
        }
    }
}

@Composable
fun HabitProgressCard(
    habit: Habit,
    onQuit: () -> Unit,
    onResume: () -> Unit
) {
    val context = LocalContext.current
    val daysCount = if (habit.isActive) {
        habit.startedDate?.let { startDate ->
            ChronoUnit.DAYS.between(startDate.toLocalDate(), LocalDate.now()).toInt()
        } ?: 0
    } else {
        habit.quitDate?.let { quitDate ->
            ChronoUnit.DAYS.between(quitDate.toLocalDate(), LocalDate.now()).toInt()
        } ?: 0
    }
    
    val lifeImpact = if (habit.isActive) {
        -habit.type.lifeYearsLost
    } else {
        habit.type.lifeYearsGained
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                val intent = Intent(context, HabitTrackingActivity::class.java)
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isActive) 
                Color(0xFFE74C3C).copy(alpha = 0.15f)
            else 
                Color(0xFF2ECC71).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habit.type.icon,
                        fontSize = 40.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    
                    Column {
                        Text(
                            text = habit.type.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = if (habit.isActive) "Активная привычка" else "Бросил привычку",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (habit.isActive) Color(0xFFE74C3C) else Color(0xFF2ECC71)
                        )
                    }
                }
                
                Button(
                    onClick = if (habit.isActive) onQuit else onResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (habit.isActive) Color(0xFF2ECC71) else Color(0xFFE74C3C)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (habit.isActive) "Бросить" else "Возобновить",
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            val progress = minOf(daysCount / 30f, 1f) // Прогресс до 30 дней
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (habit.isActive) Color(0xFFE74C3C) else Color(0xFF2ECC71),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$daysCount дней",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (habit.isActive) "с началом" else "без привычки",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${if (lifeImpact > 0) "+" else ""}$lifeImpact лет жизни",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (habit.isActive) Color(0xFFE74C3C) else Color(0xFF2ECC71),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "влияние на здоровье",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Usage Info
            habit.dailyUsage?.let { usage ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📊 $usage ${habit.type.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun EmptyHabitsState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌱",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            
            Text(
                text = "Начни отслеживать привычки",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Добавь свои вредные привычки и начни путь к здоровой жизни. Каждый день без вредной привычки добавляет время к твоей жизни!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}