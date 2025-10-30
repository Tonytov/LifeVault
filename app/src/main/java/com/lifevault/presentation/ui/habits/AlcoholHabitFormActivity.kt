package com.lifevault.presentation.ui.habits

import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.lifevault.data.model.*
import com.lifevault.presentation.viewmodel.MainViewModel
import com.lifevault.ui.theme.LifeVaultTheme
import java.time.LocalDateTime

class AlcoholHabitFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val habitId = intent.getLongExtra("HABIT_ID", -1L)
        val editMode = intent.getBooleanExtra("EDIT_MODE", false)
        
        setContent {
            LifeVaultTheme {
                AlcoholHabitFormScreen(
                    habitId = if (habitId != -1L) habitId else null,
                    editMode = editMode,
                    onBackPressed = { finish() },
                    onHabitSaved = { finish() }
                )
            }
        }
    }
}

data class DrinkType(
    val name: String,
    val emoji: String,
    val averageVolume: Int // ml
)

val drinkTypes = listOf(
    DrinkType("Пиво", "🍺", 500),
    DrinkType("Вино", "🍷", 150),
    DrinkType("Водка/коньяк", "🥃", 50),
    DrinkType("Коктейли", "🍹", 200),
    DrinkType("Шампанское", "🥂", 150),
    DrinkType("Другое", "🍻", 250)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlcoholHabitFormScreen(
    habitId: Long? = null,
    editMode: Boolean = false,
    onBackPressed: () -> Unit,
    onHabitSaved: () -> Unit,
    mainViewModel: MainViewModel = viewModel(factory = ViewModelFactory(appContainer))
) {
    var selectedDrinkType by remember { mutableStateOf(drinkTypes[0]) }
    var volumePerSession by remember { mutableStateOf("") }
    var costPerSession by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableStateOf(HabitIntensity.MODERATE) }
    
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
                        text = "🍷 Алкоголь",
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


            // Drink type selection
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
                        text = "🍻 Какие напитки предпочитаете?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    drinkTypes.forEach { drinkType ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedDrinkType == drinkType,
                                    onClick = { selectedDrinkType = drinkType }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDrinkType == drinkType,
                                onClick = { selectedDrinkType = drinkType },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF4ECDC4),
                                    unselectedColor = Color.White.copy(alpha = 0.6f)
                                )
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = drinkType.emoji,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            
                            Text(
                                text = drinkType.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Volume and cost per session
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
                        text = "🥤 Объем и стоимость за раз",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    // Quick volume buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(100, 200, 300, 500).forEach { volume ->
                            OutlinedButton(
                                onClick = { volumePerSession = volume.toString() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (volumePerSession == volume.toString()) 
                                        Color(0xFF4ECDC4) else Color.White.copy(alpha = 0.7f)
                                )
                            ) {
                                Text(
                                    text = "${volume}мл",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = volumePerSession,
                            onValueChange = { volumePerSession = it },
                            label = { Text("Объем (мл)", color = Color.White.copy(alpha = 0.7f)) },
                            placeholder = { Text("${selectedDrinkType.averageVolume}", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF4ECDC4),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            ),
                            trailingIcon = {
                                Text(
                                    text = "мл",
                                    color = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        )
                        
                        OutlinedTextField(
                            value = costPerSession,
                            onValueChange = { costPerSession = it },
                            label = { Text("Стоимость", color = Color.White.copy(alpha = 0.7f)) },
                            placeholder = { Text("200", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF4ECDC4),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            ),
                            trailingIcon = {
                                Text(
                                    text = "₽",
                                    color = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        )
                    }
                    
                    // Cost calculation display
                    if (costPerSession.isNotBlank()) {
                        val times = getTimesPerMonthFromIntensity(selectedIntensity)
                        val cost = costPerSession.toDoubleOrNull() ?: 0.0
                        if (times > 0 && cost > 0) {
                            val monthlyCost = times * cost
                            val weeklyCost = monthlyCost / 4.0
                            val yearlyCost = monthlyCost * 12
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF9B59B6).copy(alpha = 0.2f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "📊 Ваши расходы на алкоголь:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "• ${String.format("%.0f", weeklyCost)}₽ в неделю",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = "• ${String.format("%.0f", monthlyCost)}₽ в месяц",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = "• ${String.format("%.0f", yearlyCost)}₽ в год",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF9B59B6),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
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
                        text = "⚡ Интенсивность употребления",
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
                                    text = getAlcoholIntensityDescription(intensity),
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
                    if (volumePerSession.isNotBlank()) {
                        val timesPerMonth = getTimesPerMonthFromIntensity(selectedIntensity)
                        val monthlyVolume = timesPerMonth * (volumePerSession.toIntOrNull() ?: 0)
                        val weeklyVolume = monthlyVolume / 4
                        val monthlyCost = timesPerMonth * (costPerSession.toDoubleOrNull() ?: 0.0)
                        val costPerUnit = if (timesPerMonth > 0) {
                            monthlyCost / timesPerMonth
                        } else 0.0
                        
                        val notes = "Тип: ${selectedDrinkType.name}, $timesPerMonth раз/месяц (${selectedIntensity.displayName}), ${volumePerSession}мл, ${costPerSession.ifBlank { "0" }}₽ за раз"
                        
                        if (editMode && habitId != null) {
                            // Update existing habit
                            val updatedHabit = Habit(
                                id = habitId,
                                type = HabitType.ALCOHOL,
                                isActive = true,
                                dailyUsage = null, // For alcohol we use weekly
                                weeklyUsage = weeklyVolume,
                                intensity = selectedIntensity,
                                costPerUnit = costPerUnit,
                                startedDate = LocalDateTime.now().minusYears(1), // Default to 1 year ago
                                quitDate = null,
                                createdAt = LocalDateTime.now(),
                                notes = notes
                            )
                            mainViewModel.updateHabit(updatedHabit)
                        } else {
                            // Create new habit
                            val habit = Habit(
                                type = HabitType.ALCOHOL,
                                isActive = true,
                                dailyUsage = null, // For alcohol we use weekly
                                weeklyUsage = weeklyVolume,
                                intensity = selectedIntensity,
                                costPerUnit = costPerUnit,
                                startedDate = LocalDateTime.now().minusYears(1), // Default to 1 year ago
                                quitDate = null,
                                createdAt = LocalDateTime.now(),
                                notes = notes
                            )
                            mainViewModel.addHabit(habit)
                        }
                        onHabitSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = volumePerSession.isNotBlank(),
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
                text = "💡 Даже умеренное употребление алкоголя влияет на здоровье. Будьте честны для точных расчетов.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun getAlcoholIntensityDescription(intensity: HabitIntensity): String {
    return when (intensity) {
        HabitIntensity.LIGHT -> "1-4 раза в месяц, малые порции"
        HabitIntensity.MODERATE -> "5-8 раз в месяц, средние порции"
        HabitIntensity.HEAVY -> "9-15 раз в месяц, большие порции"
        HabitIntensity.SEVERE -> "Более 15 раз в месяц или ежедневно"
    }
}

private fun getTimesPerMonthFromIntensity(intensity: HabitIntensity): Int {
    return when (intensity) {
        HabitIntensity.LIGHT -> 3    // средний показатель 1-4
        HabitIntensity.MODERATE -> 6 // средний показатель 5-8
        HabitIntensity.HEAVY -> 12   // средний показатель 9-15
        HabitIntensity.SEVERE -> 20  // более 15, берем 20
    }
}