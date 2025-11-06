package com.lifevault.presentation.ui.main.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import com.lifevault.data.model.HabitType
import com.lifevault.presentation.ui.main.components.*
import com.lifevault.presentation.viewmodel.TodayViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Главный экран "Сегодня" - ежедневный трекинг привычек
 */
@Composable
fun TodayScreen() {
    val viewModel: TodayViewModel = viewModel(factory = ViewModelFactory(appContainer))
    val uiState by viewModel.uiState.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedHabitType by remember { mutableStateOf<HabitType?>(null) }

    val scrollState = rememberScrollState()

    // Обработка событий
    LaunchedEffect(uiState.event) {
        uiState.event?.let { event ->
            // TODO: Показать Snackbar или Alert для событий
            viewModel.clearEvent()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Приветствие
            GreetingHeader()

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                // Показать загрузку
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4ECDC4))
                }
            } else {
                // Карточки привычек
                uiState.smokingSummary?.let { summary ->
                    HabitCard(
                        summary = summary,
                        onQuickAdd = {
                            selectedHabitType = HabitType.SMOKING
                            showBottomSheet = true
                        },
                        onPresetSelected = { presetKey ->
                            viewModel.addPresetEntry(HabitType.SMOKING, presetKey)
                        },
                        onViewDetails = {
                            // TODO: Открыть детальный экран привычки
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                uiState.alcoholSummary?.let { summary ->
                    HabitCard(
                        summary = summary,
                        onQuickAdd = {
                            selectedHabitType = HabitType.ALCOHOL
                            showBottomSheet = true
                        },
                        onPresetSelected = { presetKey ->
                            viewModel.addPresetEntry(HabitType.ALCOHOL, presetKey)
                        },
                        onViewDetails = {
                            // TODO: Открыть детальный экран привычки
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                uiState.sugarSummary?.let { summary ->
                    HabitCard(
                        summary = summary,
                        onQuickAdd = {
                            selectedHabitType = HabitType.SUGAR
                            showBottomSheet = true
                        },
                        onPresetSelected = { presetKey ->
                            viewModel.addPresetEntry(HabitType.SUGAR, presetKey)
                        },
                        onViewDetails = {
                            // TODO: Открыть детальный экран привычки
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Карточка прогноза жизни
                LifeExpectancyCard(
                    lifeExpectancyBefore = uiState.lifeExpectancyBefore,
                    lifeExpectancyCurrent = uiState.lifeExpectancyCurrent,
                    yearsGained = uiState.yearsGained,
                    daysGainedThisMonth = uiState.daysGainedThisMonth
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Карточка достижений
                if (uiState.recentAchievements.isNotEmpty()) {
                    RecentAchievementsCard(
                        achievements = uiState.recentAchievements,
                        onViewAll = {
                            // TODO: Открыть экран всех достижений
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Карусель научных статей
                if (uiState.featuredArticles.isNotEmpty()) {
                    FeaturedArticlesCarousel(
                        articles = uiState.featuredArticles,
                        onViewAll = {
                            // TODO: Открыть экран всех статей
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Bottom Sheet для добавления записи
        if (showBottomSheet && selectedHabitType != null) {
            QuickAddBottomSheet(
                habitType = selectedHabitType!!,
                onDismiss = {
                    showBottomSheet = false
                    selectedHabitType = null
                },
                onAddPreset = { presetKey ->
                    viewModel.addPresetEntry(selectedHabitType!!, presetKey)
                    showBottomSheet = false
                    selectedHabitType = null
                },
                onQuickAdd = { amount, note ->
                    viewModel.quickAddEntry(selectedHabitType!!, amount, note)
                    showBottomSheet = false
                    selectedHabitType = null
                }
            )
        }
    }
}

/**
 * Заголовок с приветствием и датой
 */
@Composable
fun GreetingHeader() {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("ru"))
    val dateString = currentDate.format(formatter).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }

    Column {
        Text(
            text = "👋 Привет!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = dateString,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

/**
 * Заглушка для разделов (пока не реализованы)
 */
@Composable
fun PlaceholderSection(
    title: String,
    description: String
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
                .padding(20.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🚧 В разработке",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFF39C12),
                fontSize = 12.sp
            )
        }
    }
}