package com.lifevault.presentation.ui.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifevault.data.model.*

/**
 * Bottom Sheet для быстрого добавления записи с пресетом
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddBottomSheet(
    habitType: HabitType,
    onDismiss: () -> Unit,
    onAddPreset: (String) -> Unit,
    onQuickAdd: (Int, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPresetKey by remember { mutableStateOf<String?>(null) }
    var note by remember { mutableStateOf("") }
    var customAmount by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color(0xFF1E1E2E),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = habitType.icon,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Добавить ${habitType.displayName.lowercase()}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Пресеты
            Text(
                text = "Выберите пресет",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Список пресетов в зависимости от типа привычки
            when (habitType) {
                HabitType.SMOKING -> {
                    PresetGrid(
                        presets = SmokingPreset.values().toList(),
                        selectedPresetKey = selectedPresetKey,
                        onPresetSelected = {
                            selectedPresetKey = it
                            onAddPreset(it)  // Сразу добавляем при клике
                        },
                        getKey = { it.key },
                        getEmoji = { it.emoji },
                        getDisplayName = { it.displayName },
                        getAmount = { it.amount.toString() }
                    )
                }
                HabitType.ALCOHOL -> {
                    PresetGrid(
                        presets = AlcoholPreset.values().toList(),
                        selectedPresetKey = selectedPresetKey,
                        onPresetSelected = {
                            selectedPresetKey = it
                            onAddPreset(it)  // Сразу добавляем при клике
                        },
                        getKey = { it.key },
                        getEmoji = { it.emoji },
                        getDisplayName = { it.displayName },
                        getAmount = { "${it.standardUnits} ед." }
                    )
                }
                HabitType.SUGAR -> {
                    PresetGrid(
                        presets = SugarPreset.values().toList(),
                        selectedPresetKey = selectedPresetKey,
                        onPresetSelected = {
                            selectedPresetKey = it
                            onAddPreset(it)  // Сразу добавляем при клике
                        },
                        getKey = { it.key },
                        getEmoji = { it.emoji },
                        getDisplayName = { it.displayName },
                        getAmount = { "${it.sugarGrams}г" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Поле для заметки (опционально)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = {
                    Text(
                        text = "Заметка (необязательно)",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4ECDC4),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = Color(0xFF4ECDC4)
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Кастомное количество
            OutlinedTextField(
                value = customAmount,
                onValueChange = { customAmount = it },
                label = {
                    Text(
                        text = "Или укажите количество вручную",
                        color = Color.White.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF4ECDC4),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    cursorColor = Color(0xFF4ECDC4)
                ),
                singleLine = true,
                placeholder = {
                    Text(
                        text = when (habitType) {
                            HabitType.SMOKING -> "Например: 5"
                            HabitType.ALCOHOL -> "Например: 2"
                            HabitType.SUGAR -> "Например: 25"
                        },
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Кнопка "Отмена"
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Отмена")
                }

                // Кнопка "Добавить"
                Button(
                    onClick = {
                        // Добавить с кастомным количеством или просто 1
                        val amount = customAmount.toIntOrNull() ?: 1
                        onQuickAdd(amount, note.takeIf { it.isNotBlank() })
                        // Закрытие происходит в обработчиках TodayScreen
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4ECDC4)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Добавить")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Сетка пресетов (переиспользуемый компонент)
 */
@Composable
private fun <T> PresetGrid(
    presets: List<T>,
    selectedPresetKey: String?,
    onPresetSelected: (String) -> Unit,
    getKey: (T) -> String,
    getEmoji: (T) -> String,
    getDisplayName: (T) -> String,
    getAmount: (T) -> String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { preset ->
            val key = getKey(preset)
            val isSelected = selectedPresetKey == key

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPresetSelected(key) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        Color(0xFF4ECDC4).copy(alpha = 0.3f)
                    } else {
                        Color.White.copy(alpha = 0.1f)
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getEmoji(preset),
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = getDisplayName(preset),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = getAmount(preset),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Выбрано",
                            tint = Color(0xFF4ECDC4),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}