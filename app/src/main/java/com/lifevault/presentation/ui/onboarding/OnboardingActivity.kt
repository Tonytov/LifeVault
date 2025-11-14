package com.lifevault.presentation.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifevault.core.ViewModelFactory
import com.lifevault.core.appContainer
import com.lifevault.data.model.Gender
import com.lifevault.data.model.LifeExpectancyRegion
import com.lifevault.presentation.ui.main.MainActivity
import com.lifevault.presentation.ui.main.MainTabsActivity
import com.lifevault.presentation.viewmodel.OnboardingViewModel
import com.lifevault.ui.theme.LifeVaultTheme
import java.time.LocalDate

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                OnboardingScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: OnboardingViewModel = viewModel(factory = ViewModelFactory(appContainer))) {
    val context = LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf<LifeExpectancyRegion?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    LaunchedEffect(viewModel.isProfileCreated.collectAsState().value) {
        if (viewModel.isProfileCreated.value) {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as OnboardingActivity).finish()
        }
    }

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
        // Background illustration placeholder
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .alpha(0.1f),
            tint = Color.White
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Back button (only show if user has a profile)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        val intent = Intent(context, MainTabsActivity::class.java)
                        context.startActivity(intent)
                        (context as OnboardingActivity).finish()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to Main",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "LifeVault",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Calculate your life and make it longer",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tell us about yourself",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .testTag("onboardingTitle")
                    )
                    
                    // Name Input
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { if (it.length <= 50) fullName = it },
                        label = { Text("Ваше имя") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("nameField"),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Gender Selection
                    Text(
                        text = "Gender",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Gender.values().forEach { gender ->
                            FilterChip(
                                onClick = { selectedGender = gender },
                                label = {
                                    Text(if (gender == Gender.MALE) "Male" else "Female")
                                },
                                selected = selectedGender == gender,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("gender${gender.name}")
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Age Input
                    OutlinedTextField(
                        value = age,
                        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) age = it },
                        label = { Text("Age") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ageField")
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = height,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) height = it },
                            label = { Text("Height (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("heightField")
                        )

                        OutlinedTextField(
                            value = weight,
                            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 3) weight = it },
                            label = { Text("Weight (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("weightField")
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Region Selection
                    Text(
                        text = "Region",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    
                    var expanded by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedRegion?.displayName ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select your region") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("regionField")
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            LifeExpectancyRegion.values().forEach { region ->
                                DropdownMenuItem(
                                    text = { Text(region.displayName) },
                                    onClick = {
                                        selectedRegion = region
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            val ageInt = age.toIntOrNull()
                            val heightInt = height.toIntOrNull()
                            val weightInt = weight.toIntOrNull()

                            if (fullName.isNotBlank() && selectedGender != null && ageInt != null && heightInt != null &&
                                weightInt != null && selectedRegion != null) {
                                isLoading = true
                                val birthDate = LocalDate.now().minusYears(ageInt.toLong())
                                viewModel.createUserProfile(
                                    fullName = fullName,
                                    gender = selectedGender!!,
                                    birthDate = birthDate,
                                    height = heightInt,
                                    weight = weightInt,
                                    region = selectedRegion!!
                                )
                            }
                        },
                        enabled = !isLoading && fullName.isNotBlank() && selectedGender != null && age.isNotBlank() &&
                                height.isNotBlank() && weight.isNotBlank() && selectedRegion != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("calculateLifeButton")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "Calculate My Life",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            }
        }
    }
}