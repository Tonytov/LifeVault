package com.quitsmoking.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.quitsmoking.presentation.ui.main.MainActivity
import com.quitsmoking.presentation.ui.onboarding.OnboardingActivity
import com.quitsmoking.presentation.viewmodel.AuthViewModel
import com.quitsmoking.presentation.viewmodel.AuthUiState
import com.quitsmoking.ui.theme.LifeVaultTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class VerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
        
        setContent {
            LifeVaultTheme {
                VerificationScreen(phoneNumber = phoneNumber)
            }
        }
    }
}

@Composable
fun VerificationScreen(
    phoneNumber: String,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var verificationCode by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(300) } // 5 minutes
    var canResend by remember { mutableStateOf(false) }
    
    // Countdown timer
    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            canResend = true
        }
    }
    
    LaunchedEffect(uiState.isAuthenticated, uiState.hasProfile) {
        if (uiState.isAuthenticated) {
            val intent = if (uiState.hasProfile) {
                // Пользователь уже заполнял профиль - идем на главный экран
                Intent(context, MainActivity::class.java)
            } else {
                // Первый вход - идем на онбординг
                Intent(context, OnboardingActivity::class.java)
            }
            context.startActivity(intent)
            (context as? ComponentActivity)?.finish()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F23),
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Title
            Text(
                text = "LifeVault",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Подтверждение номера",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Verification Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📱",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Код подтверждения",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Text(
                        text = "Мы отправили SMS с кодом подтверждения на номер:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = phoneNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF4ECDC4),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Demo SMS Code Display (remove in production)
                    uiState.verificationCode?.let { code ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "DEMO: Ваш код",
                                    color = Color(0xFF4ECDC4),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = code,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    // Verification Code Field
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = { newValue ->
                            // Ограничиваем 6 символами и только цифрами
                            if (newValue.length <= 6 && newValue.all { char -> char.isDigit() }) {
                                verificationCode = newValue
                            }
                        },
                        label = { Text("Код из SMS", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("000000", color = Color.White.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4ECDC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                    
                    // Timer or Resend Button
                    if (!canResend) {
                        Text(
                            text = "Повторно отправить код можно через ${timeLeft / 60}:${String.format("%02d", timeLeft % 60)}",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    } else {
                        TextButton(
                            onClick = {
                                viewModel.resendVerificationCode(phoneNumber)
                                timeLeft = 300
                                canResend = false
                            },
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Отправить код повторно",
                                color = Color(0xFF4ECDC4)
                            )
                        }
                    }
                    
                    // Error Message
                    uiState.error?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFFFF6B6B),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    
                    // Verify Button
                    Button(
                        onClick = {
                            viewModel.verifyCode(phoneNumber, verificationCode)
                        },
                        enabled = !uiState.isLoading && verificationCode.length == 6,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4ECDC4),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Подтвердить",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Back to Register
                    TextButton(
                        onClick = {
                            val intent = Intent(context, RegisterActivity::class.java)
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        }
                    ) {
                        Text(
                            text = "Изменить номер телефона",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}