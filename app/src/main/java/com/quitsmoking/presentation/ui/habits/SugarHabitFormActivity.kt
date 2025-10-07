package com.quitsmoking.presentation.ui.habits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import java.time.LocalDateTime

@AndroidEntryPoint
class SugarHabitFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val habitId = intent.getLongExtra("HABIT_ID", -1L)
        val editMode = intent.getBooleanExtra("EDIT_MODE", false)
        
        setContent {
            LifeVaultTheme {
                SugarHabitFormScreen(
                    habitId = if (habitId != -1L) habitId else null,
                    editMode = editMode,
                    onBackPressed = { finish() },
                    onHabitSaved = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugarHabitFormScreen(
    habitId: Long? = null,
    editMode: Boolean = false,
    onBackPressed: () -> Unit,
    onHabitSaved: () -> Unit,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    var sugarSpoons by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableStateOf(HabitIntensity.MODERATE) }
    var additionalInfo by remember { mutableStateOf("") }
    
    val context = LocalContext.current

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
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
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
                
                Column {
                    Text(
                        text = "🍬 Сахар",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (editMode) "Редактирование привычки" else "Добавление вредной привычки",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3498DB).copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ Что включать в подсчет сахара?",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    listOf(
                        "• Сахар в чае/кофе",
                        "• Сладости: конфеты, печенье, торты",
                        "• Газированные напитки",
                        "• Мед, варенье, джемы", 
                        "• Соки (даже натуральные)",
                        "• Десерты и мороженое"
                    ).forEach { item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // Sugar spoons per day
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "🥄 Сколько ложек сахара в день?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Quick spoon buttons
                    Text(
                        text = "Быстрый выбор:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(2, 4, 6, 8, 12).forEach { spoons ->
                            OutlinedButton(
                                onClick = { sugarSpoons = spoons.toString() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (sugarSpoons == spoons.toString()) 
                                        Color(0xFF4ECDC4) else Color.White.copy(alpha = 0.7f)
                                )
                            ) {
                                Text(
                                    text = "$spoons",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    OutlinedTextField(
                        value = sugarSpoons,
                        onValueChange = { sugarSpoons = it },
                        label = { Text("Ложек в день", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("Например: 6", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4ECDC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        ),
                        trailingIcon = {
                            Text(
                                text = "🥄",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )
                    
                    // Sugar equivalents
                    if (sugarSpoons.isNotBlank()) {
                        val spoons = sugarSpoons.toIntOrNull() ?: 0
                        if (spoons > 0) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.05f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "📊 Это примерно:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "• ${spoons * 5}г сахара в день",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "• ${(spoons * 5 * 365) / 1000}кг сахара в год",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "• ${spoons * 20} ккал от сахара в день",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Additional info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📝 Дополнительная информация",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = additionalInfo,
                        onValueChange = { additionalInfo = it },
                        label = { Text("Основные источники сахара", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("Например: кофе с сахаром, сладости после обеда", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4ECDC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // Intensity selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "⚡ Уровень потребления сахара",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    HabitIntensity.values().forEach { intensity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedIntensity == intensity,
                                onClick = { selectedIntensity = intensity },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF4ECDC4),
                                    unselectedColor = Color.White.copy(alpha = 0.6f)
                                )
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Column {
                                Text(
                                    text = intensity.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = getSugarIntensityDescription(intensity),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Save button
            Button(
                onClick = {
                    if (sugarSpoons.isNotBlank()) {
                        if (editMode && habitId != null) {
                            // Update existing habit
                            val updatedHabit = Habit(
                                id = habitId,
                                type = HabitType.SUGAR,
                                isActive = true,
                                dailyUsage = sugarSpoons.toIntOrNull(),
                                intensity = selectedIntensity,
                                costPerUnit = 0.0, // Can be calculated later
                                startedDate = LocalDateTime.now().minusYears(2), // Default assumption
                                quitDate = null,
                                createdAt = LocalDateTime.now(),
                                notes = if (additionalInfo.isNotBlank()) additionalInfo else null
                            )
                            mainViewModel.updateHabit(updatedHabit)
                        } else {
                            // Create new habit
                            val habit = Habit(
                                type = HabitType.SUGAR,
                                isActive = true,
                                dailyUsage = sugarSpoons.toIntOrNull(),
                                intensity = selectedIntensity,
                                costPerUnit = 0.0, // Can be calculated later
                                startedDate = LocalDateTime.now().minusYears(2), // Default assumption
                                quitDate = null,
                                createdAt = LocalDateTime.now(),
                                notes = if (additionalInfo.isNotBlank()) additionalInfo else null
                            )
                            mainViewModel.addHabit(habit)
                        }
                        onHabitSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = sugarSpoons.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4ECDC4),
                    disabledContainerColor = Color.White.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (editMode) "Обновить привычку" else "Сохранить привычку",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Help text
            Text(
                text = "💡 ВОЗ рекомендует не более 6 ложек сахара в день. Учитывайте скрытый сахар в продуктах!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun getSugarIntensityDescription(intensity: HabitIntensity): String {
    return when (intensity) {
        HabitIntensity.LIGHT -> "1-4 ложки в день (до нормы ВОЗ)"
        HabitIntensity.MODERATE -> "5-8 ложек в день (немного выше нормы)"
        HabitIntensity.HEAVY -> "9-15 ложек в день (значительно выше нормы)"
        HabitIntensity.SEVERE -> "Более 15 ложек в день (критически высоко)"
    }
}