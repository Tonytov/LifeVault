package com.lifevault.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifevault.presentation.ui.main.tabs.*
import com.lifevault.ui.theme.LifeVaultTheme

class MainTabsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                MainTabsScreen()
            }
        }
    }
}

enum class MainTab(val title: String, val emoji: String) {
    HOME("Главная", "🏠"),
    HABITS("Привычки", "🎯"),
    PROFILE("Профиль", "👤"),
    ACHIEVEMENTS("Достижения", "🏆"),
    HEALTH("Здоровье", "📚")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabsScreen() {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }
    
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.height(80.dp)
            ) {
                MainTab.values().forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Column {
                                Text(
                                    text = tab.emoji,
                                    fontSize = 24.sp
                                )
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    color = if (selectedTab == tab) 
                                        Color(0xFF4ECDC4) else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF4ECDC4),
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFF4ECDC4).copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
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
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                MainTab.HOME -> HomeTab()
                MainTab.HABITS -> HabitsTab()
                MainTab.PROFILE -> ProfileTab()
                MainTab.ACHIEVEMENTS -> AchievementsTab()
                MainTab.HEALTH -> HealthTab()
            }
        }
    }
}