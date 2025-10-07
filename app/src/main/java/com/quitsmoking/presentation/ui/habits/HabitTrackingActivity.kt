package com.quitsmoking.presentation.ui.habits

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quitsmoking.data.model.*
import com.quitsmoking.presentation.viewmodel.MainViewModel
import com.quitsmoking.ui.theme.LifeVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalDateTime

@AndroidEntryPoint
class HabitTrackingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                HabitTrackingScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitTrackingScreen(
    onBackPressed: () -> Unit,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val mainUiState by mainViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(HabitTrackingTab.MY_HABITS) }
    var showAddHabitDialog by remember { mutableStateOf(false) }
    
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "📋 Отслеживание привычек",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (selectedTab == HabitTrackingTab.MY_HABITS) {
                    FloatingActionButton(
                        onClick = { showAddHabitDialog = true },
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
            }
            
            // Tab Navigation
            HabitTrackingTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content based on selected tab
            when (selectedTab) {
                HabitTrackingTab.MY_HABITS -> {
                    MyHabitsContent(
                        habits = mainUiState.habits,
                        onQuitHabit = { habit -> mainViewModel.quitHabit(habit.type) },
                        onResumeHabit = { habit -> mainViewModel.resumeHabit(habit.type) }
                    )
                }
                HabitTrackingTab.ADD_HABIT -> {
                    AddHabitContent()
                }
                HabitTrackingTab.STATISTICS -> {
                    HabitStatisticsContent(habits = mainUiState.habits)
                }
            }
        }
        
        // Add Habit Dialog
        if (showAddHabitDialog) {
            AddHabitDialog(
                onDismiss = { showAddHabitDialog = false },
                onAddHabit = { habitType, details ->
                    // TODO: Add habit with details to viewmodel
                    showAddHabitDialog = false
                }
            )
        }
    }
}

enum class HabitTrackingTab(val title: String, val emoji: String) {
    MY_HABITS("Мои привычки", "📋"),
    ADD_HABIT("Добавить", "➕"),
    STATISTICS("Статистика", "📊")
}

@Composable
fun HabitTrackingTabRow(
    selectedTab: HabitTrackingTab,
    onTabSelected: (HabitTrackingTab) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HabitTrackingTab.values().forEach { tab ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(tab) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedTab == tab) 
                        Color(0xFF4ECDC4).copy(alpha = 0.3f)
                    else 
                        Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab.emoji,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (selectedTab == tab) Color(0xFF4ECDC4) else Color.White,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MyHabitsContent(
    habits: List<Habit>,
    onQuitHabit: (Habit) -> Unit,
    onResumeHabit: (Habit) -> Unit
) {
    if (habits.isEmpty()) {
        EmptyHabitsCard()
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(habits) { habit ->
                HabitTrackingCard(
                    habit = habit,
                    onQuitHabit = { onQuitHabit(habit) },
                    onResumeHabit = { onResumeHabit(habit) }
                )
            }
        }
    }
}

@Composable
fun EmptyHabitsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🌱",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Пока нет привычек",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Добавьте свои вредные привычки, чтобы начать их отслеживать и получать мотивацию для отказа от них",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HabitTrackingCard(
    habit: Habit,
    onQuitHabit: () -> Unit,
    onResumeHabit: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(
            containerColor = if (habit.isActive) 
                Color(0xFFE74C3C).copy(alpha = 0.15f)
            else 
                Color(0xFF2ECC71).copy(alpha = 0.15f)
        )
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
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    
                    Column {
                        Text(
                            text = habit.type.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            StatusIndicator(isActive = habit.isActive)
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            IntensityChip(intensity = habit.intensity)
                        }
                    }
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
            
            // Basic Info
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                habit.dailyUsage?.let { usage ->
                    InfoChip(
                        label = "В день",
                        value = "$usage ${habit.type.unit}",
                        color = Color(0xFF3498DB)
                    )
                }
                
                habit.weeklyUsage?.let { usage ->
                    InfoChip(
                        label = "В неделю", 
                        value = "$usage единиц",
                        color = Color(0xFF9B59B6)
                    )
                }
                
                if (habit.costPerUnit > 0) {
                    InfoChip(
                        label = "Стоимость",
                        value = "${habit.costPerUnit.toInt()}₽",
                        color = Color(0xFFF39C12)
                    )
                }
            }
            
            // Expanded Details
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(bottom = 16.dp)
                    )
                    
                    // Detailed Information
                    if (habit.startedDate != null) {
                        DetailRow(
                            label = "Начало привычки",
                            value = habit.startedDate!!.toLocalDate().toString(),
                            icon = "📅"
                        )
                    }
                    
                    if (habit.quitDate != null) {
                        DetailRow(
                            label = "Дата отказа",
                            value = habit.quitDate!!.toLocalDate().toString(),
                            icon = "🎯"
                        )
                        
                        val daysSinceQuit = java.time.temporal.ChronoUnit.DAYS.between(
                            habit.quitDate!!.toLocalDate(),
                            LocalDate.now()
                        )
                        
                        DetailRow(
                            label = "Дней без привычки",
                            value = "$daysSinceQuit дней",
                            icon = "⏰"
                        )
                    }
                    
                    if (habit.triggers.isNotEmpty()) {
                        DetailRow(
                            label = "Триггеры",
                            value = habit.triggers.joinToString(", "),
                            icon = "⚡"
                        )
                    }
                    
                    habit.notes?.let { notes ->
                        if (notes.isNotBlank()) {
                            DetailRow(
                                label = "Заметки",
                                value = notes,
                                icon = "📝"
                            )
                        }
                    }
                    
                    // Health Impact
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "💊 Влияние на здоровье",
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            if (habit.isActive) {
                                Text(
                                    text = "⚠️ Сокращает жизнь на ~${habit.type.lifeYearsLost} лет",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE74C3C)
                                )
                            } else {
                                Text(
                                    text = "✅ Может добавить ~${habit.type.lifeYearsGained} лет жизни",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF2ECC71)
                                )
                            }
                        }
                    }
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (habit.isActive) {
                            Button(
                                onClick = onQuitHabit,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2ECC71)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Бросить")
                            }
                        } else {
                            Button(
                                onClick = onResumeHabit,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE74C3C)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Возобновить")
                            }
                        }
                        
                        OutlinedButton(
                            onClick = { 
                                when (habit.type) {
                                    HabitType.SMOKING -> {
                                        val intent = Intent(context, SmokingHabitFormActivity::class.java)
                                        intent.putExtra("HABIT_ID", habit.id)
                                        intent.putExtra("EDIT_MODE", true)
                                        context.startActivity(intent)
                                    }
                                    HabitType.ALCOHOL -> {
                                        val intent = Intent(context, AlcoholHabitFormActivity::class.java)
                                        intent.putExtra("HABIT_ID", habit.id)
                                        intent.putExtra("EDIT_MODE", true)
                                        context.startActivity(intent)
                                    }
                                    HabitType.SUGAR -> {
                                        val intent = Intent(context, SugarHabitFormActivity::class.java)
                                        intent.putExtra("HABIT_ID", habit.id)
                                        intent.putExtra("EDIT_MODE", true)
                                        context.startActivity(intent)
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF4ECDC4)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Изменить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(isActive: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                Color(0xFFE74C3C).copy(alpha = 0.2f)
            else 
                Color(0xFF2ECC71).copy(alpha = 0.2f)
        )
    ) {
        Text(
            text = if (isActive) "😔 Активна" else "✅ Бросил",
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) Color(0xFFE74C3C) else Color(0xFF2ECC71),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun IntensityChip(intensity: HabitIntensity) {
    val color = when (intensity) {
        HabitIntensity.LIGHT -> Color(0xFF2ECC71)
        HabitIntensity.MODERATE -> Color(0xFFF39C12)
        HabitIntensity.HEAVY -> Color(0xFFE67E22)
        HabitIntensity.SEVERE -> Color(0xFFE74C3C)
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f)
        )
    ) {
        Text(
            text = intensity.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun InfoChip(
    label: String,
    value: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    icon: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.weight(0.4f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun AddHabitContent() {
    var selectedHabitType by remember { mutableStateOf<HabitType?>(null) }
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Выберите вредную привычку:",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        
        // Habit Type Cards
        HabitType.values().forEach { habitType ->
            HabitSelectionCard(
                habitType = habitType,
                onClick = {
                    when (habitType) {
                        HabitType.SMOKING -> {
                            val intent = Intent(context, SmokingHabitFormActivity::class.java)
                            context.startActivity(intent)
                        }
                        HabitType.ALCOHOL -> {
                            val intent = Intent(context, AlcoholHabitFormActivity::class.java)
                            context.startActivity(intent)
                        }
                        HabitType.SUGAR -> {
                            val intent = Intent(context, SugarHabitFormActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HabitSelectionCard(
    habitType: HabitType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habitType.icon,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 20.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habitType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = when (habitType) {
                        HabitType.SMOKING -> "Сигареты, вейп, кальян"
                        HabitType.ALCOHOL -> "Пиво, вино, крепкие напитки"
                        HabitType.SUGAR -> "Сладости, газировки, десерты"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Открыть форму",
                tint = Color(0xFF4ECDC4),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HabitStatisticsContent(habits: List<Habit>) {
    // Statistics content
    Text(
        text = "Статистика привычек будет здесь",
        color = Color.White,
        modifier = Modifier.padding(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onAddHabit: (HabitType, Map<String, Any>) -> Unit
) {
    var selectedHabitType by remember { mutableStateOf<HabitType?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Добавить привычку")
        },
        text = {
            Column {
                Text("Выберите тип привычки:")
                Spacer(modifier = Modifier.height(16.dp))
                
                HabitType.values().forEach { habitType ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedHabitType = habitType },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedHabitType == habitType)
                                Color(0xFF4ECDC4).copy(alpha = 0.3f)
                            else
                                Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = habitType.icon,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = habitType.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedHabitType?.let { habitType ->
                        onAddHabit(habitType, emptyMap())
                    }
                },
                enabled = selectedHabitType != null
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}