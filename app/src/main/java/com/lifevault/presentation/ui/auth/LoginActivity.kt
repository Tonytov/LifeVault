package com.lifevault.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import com.lifevault.data.model.PhoneValidator
import com.lifevault.presentation.ui.main.MainActivity
import com.lifevault.presentation.ui.onboarding.OnboardingActivity
import com.lifevault.presentation.viewmodel.AuthViewModel
import com.lifevault.presentation.viewmodel.AuthUiState
import com.lifevault.ui.theme.LifeVaultTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: AuthViewModel = viewModel(factory = ViewModelFactory(appContainer))) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Валидация полей
    val isPhoneValid = PhoneValidator.isValidPhone(phoneNumber.text)
    val isPasswordValid = password.length >= 8
    val isFormValid = isPhoneValid && isPasswordValid
    
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
                text = "Добро пожаловать!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 48.dp)
            )
            
            // Login Form
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
                        text = "Вход",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    // Phone Number Field
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { newValue ->
                            val result = PhoneValidator.formatPhoneInput(
                                newValue.text, 
                                newValue.selection.start
                            )
                            phoneNumber = TextFieldValue(
                                text = result.formattedValue,
                                selection = TextRange(result.cursorPosition)
                            )
                        },
                        label = { Text("Номер телефона", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("+7 9__ ___-__-__", color = Color.White.copy(alpha = 0.5f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4ECDC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("authIdNumber")
                    )
                    
                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { newValue ->
                            if (newValue.length <= 8) {
                                password = newValue
                            }
                        },
                        label = { Text("Пароль", color = Color.White.copy(alpha = 0.7f)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(
                                    text = if (passwordVisible) "🙈" else "👁️",
                                    fontSize = 18.sp
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF4ECDC4),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .testTag("loginPassword")
                    )
                    
                    // Error Message
                    uiState.error?.let { error ->
                        Text(
                            text = error,
                            color = Color(0xFFFF6B6B),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                                .testTag("errorMessage")
                        )
                    }
                    
                    // Login Button (отображается только при валидных данных)
                    if (isFormValid) {
                        Button(
                            onClick = {
                                viewModel.login(phoneNumber.text, password)
                            },
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4ECDC4),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("loginButton")
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "Войти",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Register Link
                    TextButton(
                        onClick = {
                            val intent = Intent(context, RegisterActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .testTag("registerLinkButton")
                            .semantics {
                                contentDescription = "registerButton"
                            }
                    ) {
                        Text(
                            text = "Нет аккаунта? Зарегистрироваться",
                            color = Color(0xFF4ECDC4)

                        )
                    }
                    
                    // Test button to clear all users (remove in production)
                    TextButton(
                        onClick = {
                            viewModel.clearAllUsersForTesting()
                        }
                    ) {
                        Text(
                            text = "🧹 Очистить данные (для тестов)",
                            color = Color(0xFFFF6B6B),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}