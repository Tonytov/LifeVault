package com.quitsmoking.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quitsmoking.data.repository.AuthRepository
import com.quitsmoking.data.repository.LifeRepository
import com.quitsmoking.presentation.ui.auth.LoginActivity
import com.quitsmoking.presentation.ui.main.MainActivity
import com.quitsmoking.presentation.ui.onboarding.OnboardingActivity
import com.quitsmoking.ui.theme.LifeVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    object Login : SplashDestination()
    object Onboarding : SplashDestination()
    object Main : SplashDestination()
    object Loading : SplashDestination()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val lifeRepository: LifeRepository
) : ViewModel() {
    
    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()
    
    init {
        checkUserState()
    }
    
    private fun checkUserState() {
        viewModelScope.launch {
            delay(2000) // Show splash for 2 seconds
            
            try {
                val user = authRepository.getCurrentUser().first()
                
                when {
                    user == null -> {
                        // No user exists, go to login
                        _destination.value = SplashDestination.Login
                    }
                    !user.isPhoneVerified -> {
                        // User exists but phone not verified, go to login
                        _destination.value = SplashDestination.Login
                    }
                    else -> {
                        // User exists and verified, check if profile exists
                        val profile = lifeRepository.getUserProfile().first()
                        if (profile == null) {
                            // Verified user but no profile, go to onboarding
                            _destination.value = SplashDestination.Onboarding
                        } else {
                            // Everything exists, go to main
                            _destination.value = SplashDestination.Main
                        }
                    }
                }
            } catch (e: Exception) {
                // On error, default to login
                _destination.value = SplashDestination.Login
            }
        }
    }
}

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContent {
                LifeVaultTheme {
                    SplashScreen()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to direct login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    @Composable
    fun SplashScreen(viewModel: SplashViewModel = hiltViewModel()) {
        val destination by viewModel.destination.collectAsState()
        
        LaunchedEffect(destination) {
            try {
                when (destination) {
                    is SplashDestination.Login -> {
                        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        finish()
                    }
                    is SplashDestination.Onboarding -> {
                        startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
                        finish()
                    }
                    is SplashDestination.Main -> {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                    is SplashDestination.Loading -> {
                        // Stay on splash screen
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Default fallback to login
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        }
        
        // Анимации
        val infiniteTransition = rememberInfiniteTransition()
        
        val logoScale by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutQuart),
                repeatMode = RepeatMode.Reverse
            )
        )
        
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0A0A),
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E),
                            Color(0xFF0F3460)
                        )
                    )
                )
        ) {
            // Фоновые элементы
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4ECDC4).copy(alpha = glowAlpha * 0.1f),
                                Color.Transparent
                            ),
                            radius = 800f
                        )
                    )
            )
            
            // Основной контент
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Логотип с анимацией
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .scale(logoScale)
                        .padding(bottom = 48.dp)
                ) {
                    // Светящееся кольцо вокруг иконки
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF4ECDC4).copy(alpha = glowAlpha * 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    
                    // Основная иконка хранилища
                    Card(
                        modifier = Modifier.size(140.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "🔒",
                                fontSize = 64.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                
                // Название приложения
                Text(
                    text = "LifeVault",
                    fontSize = 42.sp,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Подзаголовок с градиентом
                Text(
                    text = "Сохрани свою жизнь",
                    fontSize = 18.sp,
                    color = Color(0xFF4ECDC4),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Описание
                Text(
                    text = "Контролируй привычки • Продлевай жизнь • Достигай целей",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 64.dp)
                )
                
                // Анимированный индикатор загрузки
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = 0.0f,
                        modifier = Modifier
                            .size(48.dp)
                            .scale(1.2f),
                        color = Color(0xFF4ECDC4).copy(alpha = 0.3f),
                        strokeWidth = 3.dp
                    )
                    
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(1.2f),
                        color = Color(0xFF4ECDC4),
                        strokeWidth = 3.dp
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Загрузочный текст
                Text(
                    text = "Инициализация...",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
            
            // Версия в нижнем углу
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "v1.0.0",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}