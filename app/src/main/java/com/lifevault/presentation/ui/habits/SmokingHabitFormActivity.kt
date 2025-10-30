package com.lifevault.presentation.ui.habits

import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SmokingHabitFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val habitId = intent.getLongExtra("HABIT_ID", -1L)
        val editMode = intent.getBooleanExtra("EDIT_MODE", false)
        
        setContent {
            LifeVaultTheme {
                SmokingHabitFormScreen(
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
fun SmokingHabitFormScreen(
    habitId: Long? = null,
    editMode: Boolean = false,
    onBackPressed: () -> Unit,
    onHabitSaved: () -> Unit,
    mainViewModel: MainViewModel = viewModel(factory = ViewModelFactory(appContainer))
) {
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var cigarettesPerDay by remember { mutableStateOf("") }
    var packCost by remember { mutableStateOf("") }
    var cigarettesPerPack by remember { mutableStateOf("20") }
    
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
                        text = "🚬 Курение",
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

            // Start Date
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
                        text = "📅 Когда начали курить?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            val datePickerDialog = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    startDate = LocalDate.of(year, month + 1, dayOfMonth)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePickerDialog.show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = startDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) 
                                ?: "Выберите дату",
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Cigarettes per day
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
                        text = "🚭 Сколько сигарет в день?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = cigarettesPerDay,
                        onValueChange = { cigarettesPerDay = it },
                        label = { Text("Количество сигарет", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("Например: 20", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4ECDC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // Pack cost and cigarettes per pack
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
                        text = "💰 Стоимость и количество",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = packCost,
                        onValueChange = { packCost = it },
                        label = { Text("Стоимость пачки", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("Например: 180", color = Color.White.copy(alpha = 0.5f)) },
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
                                text = "₽",
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cigarettesPerPack,
                            onValueChange = { cigarettesPerPack = it },
                            label = { Text("Сигарет в пачке", color = Color.White.copy(alpha = 0.7f)) },
                            placeholder = { Text("20", color = Color.White.copy(alpha = 0.5f)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF4ECDC4),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                        
                        // Quick buttons for common pack sizes
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { cigarettesPerPack = "20" },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (cigarettesPerPack == "20") 
                                            Color(0xFF4ECDC4) else Color.White.copy(alpha = 0.7f)
                                    )
                                ) {
                                    Text("20", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = { cigarettesPerPack = "25" },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = if (cigarettesPerPack == "25") 
                                            Color(0xFF4ECDC4) else Color.White.copy(alpha = 0.7f)
                                    )
                                ) {
                                    Text("25", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    
                    // Cost calculation display
                    if (packCost.isNotBlank() && cigarettesPerPack.isNotBlank() && cigarettesPerDay.isNotBlank()) {
                        val cost = packCost.toDoubleOrNull() ?: 0.0
                        val perPack = cigarettesPerPack.toIntOrNull() ?: 20
                        val perDay = cigarettesPerDay.toIntOrNull() ?: 0
                        if (cost > 0 && perPack > 0 && perDay > 0) {
                            val dailyCost = (cost / perPack) * perDay
                            val monthlyCost = dailyCost * 30
                            val yearlyCost = dailyCost * 365
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE74C3C).copy(alpha = 0.2f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = "📊 Ваши расходы на курение:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "• ${String.format("%.0f", dailyCost)}₽ в день",
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
                                        color = Color(0xFFE74C3C),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }


            // Save button
            Button(
                onClick = {
                    if (startDate != null && cigarettesPerDay.isNotBlank()) {
                        val packCostValue = packCost.toDoubleOrNull() ?: 0.0
                        val perPack = cigarettesPerPack.toIntOrNull() ?: 20
                        val costPerCigarette = if (packCostValue > 0 && perPack > 0) packCostValue / perPack else 0.0
                        val cigarettes = cigarettesPerDay.toIntOrNull() ?: 0
                        val calculatedIntensity = getIntensityFromCigarettes(cigarettes)
                        
                        val notes = "Пачка: ${packCostValue}₽, ${perPack} сигарет, ${String.format("%.2f", costPerCigarette)}₽ за сигарету"
                        
                        if (editMode && habitId != null) {
                            // Update existing habit
                            val updatedHabit = Habit(
                                id = habitId,
                                type = HabitType.SMOKING,
                                isActive = true,
                                dailyUsage = cigarettesPerDay.toIntOrNull(),
                                intensity = calculatedIntensity,
                                costPerUnit = costPerCigarette,
                                startedDate = LocalDateTime.of(startDate!!, java.time.LocalTime.now()),
                                quitDate = null,
                                createdAt = LocalDateTime.now(),
                                notes = notes
                            )
                            mainViewModel.updateHabit(updatedHabit)
                        } else {
                            // Create new habit
                            val habit = Habit(
                                type = HabitType.SMOKING,
                                isActive = true,
                                dailyUsage = cigarettesPerDay.toIntOrNull(),
                                intensity = calculatedIntensity,
                                costPerUnit = costPerCigarette,
                                startedDate = LocalDateTime.of(startDate!!, java.time.LocalTime.now()),
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
                enabled = startDate != null && cigarettesPerDay.isNotBlank(),
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
                text = "💡 Эти данные помогут рассчитать влияние курения на вашу продолжительность жизни и финансовые потери.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

private fun getSmokingIntensityDescription(intensity: HabitIntensity): String {
    return when (intensity) {
        HabitIntensity.LIGHT -> "1-10 сигарет в день"
        HabitIntensity.MODERATE -> "11-20 сигарет в день"
        HabitIntensity.HEAVY -> "21-40 сигарет в день"
        HabitIntensity.SEVERE -> "Более 40 сигарет в день"
    }
}

private fun getIntensityFromCigarettes(cigarettes: Int): HabitIntensity {
    return when {
        cigarettes <= 10 -> HabitIntensity.LIGHT
        cigarettes <= 20 -> HabitIntensity.MODERATE
        cigarettes <= 40 -> HabitIntensity.HEAVY
        else -> HabitIntensity.SEVERE
    }
}