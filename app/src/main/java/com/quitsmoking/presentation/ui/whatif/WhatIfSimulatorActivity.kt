package com.quitsmoking.presentation.ui.whatif

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quitsmoking.data.model.*
import com.quitsmoking.presentation.viewmodel.MainViewModel
import com.quitsmoking.ui.theme.LifeVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class WhatIfSimulatorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                WhatIfSimulatorScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatIfSimulatorScreen(
    onBackPressed: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Состояния слайдеров
    var smokingDaily by remember { mutableStateOf(0f) }
    var alcoholWeekly by remember { mutableStateOf(0f) }
    var sugarDaily by remember { mutableStateOf(50f) }
    var physicalActivity by remember { mutableStateOf(50f) }
    var sleepQuality by remember { mutableStateOf(70f) }
    var stressManagement by remember { mutableStateOf(50f) }
    
    // Расчет влияния на жизнь
    val currentLifeExpectancy = uiState.userProfile?.let { profile ->
        LifeCalculator.calculateLifeExpectancy(profile, uiState.habits, 0.6f).finalLifeExpectancy
    } ?: 75.0
    
    val whatIfLifeExpectancy = calculateWhatIfLifeExpectancy(
        baseExpectancy = currentLifeExpectancy,
        smokingDaily = smokingDaily,
        alcoholWeekly = alcoholWeekly,
        sugarDaily = sugarDaily,
        physicalActivity = physicalActivity,
        sleepQuality = sleepQuality,
        stressManagement = stressManagement
    )
    
    val yearsDifference = whatIfLifeExpectancy - currentLifeExpectancy
    val daysDifference = (yearsDifference * 365.25).roundToInt()
    
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
                    .padding(top = 16.dp, bottom = 32.dp),
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
                    text = "📊 What-If Симулятор",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Результаты симуляции
            SimulationResultCard(
                currentExpectancy = currentLifeExpectancy,
                whatIfExpectancy = whatIfLifeExpectancy,
                daysDifference = daysDifference,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Слайдеры привычек
            Text(
                text = "🚬 Вредные привычки",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderCard(
                title = "Курение",
                subtitle = "сигарет в день",
                value = smokingDaily,
                onValueChange = { smokingDaily = it },
                range = 0f..40f,
                color = Color(0xFFE74C3C),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderCard(
                title = "Алкоголь",
                subtitle = "единиц в неделю",
                value = alcoholWeekly,
                onValueChange = { alcoholWeekly = it },
                range = 0f..50f,
                color = Color(0xFF8E44AD),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderCard(
                title = "Добавленный сахар",
                subtitle = "граммов в день",
                value = sugarDaily,
                onValueChange = { sugarDaily = it },
                range = 0f..150f,
                color = Color(0xFFE67E22),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Слайдеры здорового образа жизни
            Text(
                text = "💪 Здоровый образ жизни",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderCard(
                title = "Физическая активность",
                subtitle = "уровень активности",
                value = physicalActivity,
                onValueChange = { physicalActivity = it },
                range = 0f..100f,
                color = Color(0xFF2ECC71),
                isPercentage = true,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderCard(
                title = "Качество сна",
                subtitle = "7-9 часов в сутки",
                value = sleepQuality,
                onValueChange = { sleepQuality = it },
                range = 0f..100f,
                color = Color(0xFF3498DB),
                isPercentage = true,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderCard(
                title = "Управление стрессом",
                subtitle = "навыки релаксации",
                value = stressManagement,
                onValueChange = { stressManagement = it },
                range = 0f..100f,
                color = Color(0xFF1ABC9C),
                isPercentage = true,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Предустановленные сценарии
            PresetScenariosCard(
                onOptimalSelected = {
                    smokingDaily = 0f
                    alcoholWeekly = 7f
                    sugarDaily = 25f
                    physicalActivity = 90f
                    sleepQuality = 90f
                    stressManagement = 80f
                },
                onWorstSelected = {
                    smokingDaily = 40f
                    alcoholWeekly = 50f
                    sugarDaily = 150f
                    physicalActivity = 10f
                    sleepQuality = 30f
                    stressManagement = 20f
                },
                onResetSelected = {
                    smokingDaily = 0f
                    alcoholWeekly = 0f
                    sugarDaily = 50f
                    physicalActivity = 50f
                    sleepQuality = 70f
                    stressManagement = 50f
                }
            )
        }
    }
}

@Composable
fun SimulationResultCard(
    currentExpectancy: Double,
    whatIfExpectancy: Double,
    daysDifference: Int,
    modifier: Modifier = Modifier
) {
    val animatedExpectancy by animateFloatAsState(
        targetValue = whatIfExpectancy.toFloat(),
        animationSpec = tween(1000)
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎯 Результат симуляции",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Текущий прогноз",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${currentExpectancy.roundToInt()} лет",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF95A5A6),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "What-If прогноз",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${animatedExpectancy.roundToInt()} лет",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (whatIfExpectancy > currentExpectancy) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Разница в днях
            val differenceColor = when {
                daysDifference > 0 -> Color(0xFF2ECC71)
                daysDifference < 0 -> Color(0xFFE74C3C)
                else -> Color(0xFF95A5A6)
            }
            
            val differenceText = when {
                daysDifference > 0 -> "+$daysDifference дней к жизни"
                daysDifference < 0 -> "${daysDifference} дней от жизни"
                else -> "Без изменений"
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = differenceColor.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = differenceText,
                    style = MaterialTheme.typography.titleMedium,
                    color = differenceColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun SliderCard(
    title: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    color: Color,
    isPercentage: Boolean = false,
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = color.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = if (isPercentage) "${value.roundToInt()}%" else "${value.roundToInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = color.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun PresetScenariosCard(
    onOptimalSelected: () -> Unit,
    onWorstSelected: () -> Unit,
    onResetSelected: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF34495E).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "🎭 Быстрые сценарии",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOptimalSelected,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2ECC71)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("👑 Идеал", fontSize = 12.sp)
                }
                
                Button(
                    onClick = onWorstSelected,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE74C3C)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("💀 Худший", fontSize = 12.sp)
                }
                
                Button(
                    onClick = onResetSelected,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF95A5A6)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🔄 Сброс", fontSize = 12.sp)
                }
            }
        }
    }
}

// Упрощенная функция расчета для демонстрации
private fun calculateWhatIfLifeExpectancy(
    baseExpectancy: Double,
    smokingDaily: Float,
    alcoholWeekly: Float,
    sugarDaily: Float,
    physicalActivity: Float,
    sleepQuality: Float,
    stressManagement: Float
): Double {
    var adjustment = 0.0
    
    // Вредные привычки (негативное влияние)
    adjustment -= smokingDaily * 0.1 // -0.1 года за сигарету в день
    adjustment -= (alcoholWeekly - 14).coerceAtLeast(0f) * 0.05 // -0.05 года за единицу свыше 14
    adjustment -= (sugarDaily - 25) * 0.02 / 365 // Влияние избытка сахара
    
    // Здоровые привычки (позитивное влияние)
    adjustment += (physicalActivity - 50) * 0.03 // До +1.5 года за максимальную активность
    adjustment += (sleepQuality - 50) * 0.02 // До +1 год за качественный сон
    adjustment += (stressManagement - 50) * 0.015 // До +0.75 года за управление стрессом
    
    return (baseExpectancy + adjustment).coerceAtLeast(20.0) // Минимум 20 лет
}