package com.lifevault.presentation.ui.main.tabs

import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifevault.data.model.Gender
import com.lifevault.data.model.LifeExpectancyRegion
import com.lifevault.presentation.ui.auth.LoginActivity
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import com.lifevault.presentation.viewmodel.AuthViewModel
import com.lifevault.presentation.viewmodel.MainViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Composable
fun ProfileTab(
    mainViewModel: MainViewModel = viewModel(factory = ViewModelFactory(appContainer)),
    authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory(appContainer))
) {
    val mainUiState by mainViewModel.uiState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "👤 Профиль",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // User Avatar and Basic Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4ECDC4).copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (mainUiState.userProfile?.gender) {
                            Gender.MALE -> "👨"
                            Gender.FEMALE -> "👩"
                            else -> "👤"
                        },
                        fontSize = 48.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // User name and phone
                authUiState.user?.fullName?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                authUiState.user?.phoneNumber?.let { phoneNumber ->
                    Text(
                        text = phoneNumber,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Normal
                    )
                }
                
                // Age calculation
                mainUiState.userProfile?.let { profile ->
                    val age = Period.between(profile.birthDate, LocalDate.now()).years
                    Text(
                        text = "$age лет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
        
        // Profile Details
        mainUiState.userProfile?.let { profile ->
            ProfileDetailsCard(
                profile = profile,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
        
        // Health Stats
        HealthStatsCard(
            healthScore = mainUiState.healthProgress,
            lifeExtensionDays = mainUiState.lifeExtensionDays,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Action Buttons
        ActionsCard(
            onEditProfile = {
                val intent = Intent(context, OnboardingActivity::class.java)
                context.startActivity(intent)
            },
            onLogout = {
                authViewModel.logout()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
        )
    }
}

@Composable
fun ProfileDetailsCard(
    profile: com.lifevault.data.model.UserProfile,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📋 Личные данные",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            ProfileDetailItem(
                label = "Пол",
                value = when (profile.gender) {
                    Gender.MALE -> "Мужской"
                    Gender.FEMALE -> "Женский"
                },
                emoji = when (profile.gender) {
                    Gender.MALE -> "♂️"
                    Gender.FEMALE -> "♀️"
                }
            )
            
            ProfileDetailItem(
                label = "Дата рождения",
                value = profile.birthDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                emoji = "🎂"
            )
            
            ProfileDetailItem(
                label = "Рост",
                value = "${profile.height} см",
                emoji = "📏"
            )
            
            ProfileDetailItem(
                label = "Вес",
                value = "${profile.weight} кг",
                emoji = "⚖️"
            )
            
            ProfileDetailItem(
                label = "Регион",
                value = when (profile.region) {
                    LifeExpectancyRegion.WESTERN_EUROPE -> "Западная Европа"
                    LifeExpectancyRegion.EASTERN_EUROPE -> "Восточная Европа"
                    LifeExpectancyRegion.NORTH_AMERICA -> "Северная Америка"
                    LifeExpectancyRegion.SOUTH_AMERICA -> "Южная Америка"
                    LifeExpectancyRegion.DEVELOPED_ASIA -> "Развитая Азия"
                    LifeExpectancyRegion.DEVELOPING_ASIA -> "Развивающаяся Азия"
                    LifeExpectancyRegion.AFRICA -> "Африка"
                    LifeExpectancyRegion.OCEANIA -> "Океания"
                },
                emoji = "🌍"
            )
            
            ProfileDetailItem(
                label = "Базовая продолжительность жизни",
                value = "${profile.baseLifeExpectancy} лет",
                emoji = "📅"
            )
        }
    }
}

@Composable
fun ProfileDetailItem(
    label: String,
    value: String,
    emoji: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun HealthStatsCard(
    healthScore: Float,
    lifeExtensionDays: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E8B57).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💪 Показатели здоровья",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HealthStatItem(
                    title = "Health Score",
                    value = "${(healthScore * 100).toInt()}%",
                    emoji = "❤️",
                    color = Color(0xFFFF6B6B)
                )
                
                HealthStatItem(
                    title = "Добавлено к жизни",
                    value = "$lifeExtensionDays дней",
                    emoji = "📈",
                    color = Color(0xFF2E8B57)
                )
            }
        }
    }
}

@Composable
fun HealthStatItem(
    title: String,
    value: String,
    emoji: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ActionsCard(
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "⚙️ Действия",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Edit Profile Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditProfile() }
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✏️",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "Редактировать профиль",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
            
            // Logout Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B6B).copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🚪",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "Выйти из аккаунта",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}