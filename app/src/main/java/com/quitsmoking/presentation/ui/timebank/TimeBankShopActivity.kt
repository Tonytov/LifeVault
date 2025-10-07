package com.quitsmoking.presentation.ui.timebank

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.quitsmoking.data.model.*
import com.quitsmoking.ui.theme.LifeVaultTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeBankShopActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeVaultTheme {
                TimeBankShopScreen(
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeBankShopScreen(
    onBackPressed: () -> Unit
) {
    // Состояние пользователя
    val userBalance = remember { mutableStateOf(1247L) }
    var selectedCategory by remember { mutableStateOf<RewardCategory?>(null) }
    var showPurchaseDialog by remember { mutableStateOf<TimeBankReward?>(null) }
    
    // История покупок пользователя  
    val purchaseHistory = remember {
        listOf(
            TimeBankTransaction(
                id = 1,
                userId = 1,
                type = TransactionType.SPENT_REWARD,
                amount = -30,
                source = "Золотая медаль",
                description = "Покупка виртуальной золотой медали",
                balanceBefore = 1277,
                balanceAfter = 1247,
                createdAt = java.time.LocalDateTime.now().minusDays(2)
            ),
            TimeBankTransaction(
                id = 2,
                userId = 1,
                type = TransactionType.EARNED_QUIT_HABIT,
                amount = 180,
                source = "Отказ от курения",
                description = "7 дней без курения - недельный бонус",
                balanceBefore = 1097,
                balanceAfter = 1277,
                createdAt = java.time.LocalDateTime.now().minusDays(7)
            )
        )
    }
    
    // Фильтрация наград по категории
    val filteredRewards = remember(selectedCategory) {
        if (selectedCategory == null) {
            TimeBankRewards.DEFAULT_REWARDS
        } else {
            TimeBankRewards.DEFAULT_REWARDS.filter { it.category == selectedCategory }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
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
                        text = "🏪 Time Bank Магазин",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Баланс пользователя
            item {
                BalanceCard(
                    balance = userBalance.value,
                    todayEarned = 12L,
                    weeklyEarned = 84L
                )
            }
            
            // Фильтры по категориям
            item {
                Text(
                    text = "🛍️ Категории наград",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            onClick = { selectedCategory = null },
                            label = { Text("Все") },
                            selected = selectedCategory == null
                        )
                    }
                    
                    items(RewardCategory.values()) { category ->
                        FilterChip(
                            onClick = { selectedCategory = if (selectedCategory == category) null else category },
                            label = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(category.emoji, modifier = Modifier.padding(end = 4.dp))
                                    Text(category.displayName)
                                }
                            },
                            selected = selectedCategory == category
                        )
                    }
                }
            }
            
            // Список наград
            items(filteredRewards.chunked(2)) { rewardPair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rewardPair.forEach { reward ->
                        RewardCard(
                            reward = reward,
                            userBalance = userBalance.value,
                            onPurchase = { 
                                showPurchaseDialog = reward
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Заполняем пустое место если нечетное количество
                    if (rewardPair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            // История транзакций
            item {
                Text(
                    text = "📜 История транзакций",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            items(purchaseHistory) { transaction ->
                TransactionCard(transaction)
            }
        }
    }
    
    // Диалог подтверждения покупки
    showPurchaseDialog?.let { reward ->
        PurchaseConfirmationDialog(
            reward = reward,
            userBalance = userBalance.value,
            onConfirm = { 
                // Покупка
                userBalance.value -= reward.cost
                showPurchaseDialog = null
            },
            onDismiss = { 
                showPurchaseDialog = null 
            }
        )
    }
}

@Composable
fun BalanceCard(
    balance: Long,
    todayEarned: Long,
    weeklyEarned: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF8E44AD).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💰",
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Ваш баланс",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Text(
                text = "$balance дней жизни",
                style = MaterialTheme.typography.headlineMedium,
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
                        text = "+$todayEarned",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF2ECC71),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "сегодня",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "+$weeklyEarned",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF3498DB),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "за неделю",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun RewardCard(
    reward: TimeBankReward,
    userBalance: Long,
    onPurchase: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canAfford = userBalance >= reward.cost
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Иконка и категория
            Text(
                text = reward.icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF95A5A6).copy(alpha = 0.2f)
                )
            ) {
                Text(
                    text = reward.category.emoji + " " + reward.category.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF95A5A6),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Название
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleSmall,
                color = if (canAfford) Color.White else Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            // Описание
            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (canAfford) Color.White.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Цена
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (canAfford) Color(0xFFF39C12).copy(alpha = 0.3f) else Color(0xFFE74C3C).copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = "${reward.cost} дней",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (canAfford) Color(0xFFF39C12) else Color(0xFFE74C3C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Кнопка покупки
            Button(
                onClick = onPurchase,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2ECC71),
                    disabledContainerColor = Color(0xFF95A5A6)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (canAfford) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Купить")
                } else {
                    Text("Не хватает ${reward.cost - userBalance}")
                }
            }
            
            // Premium индикатор
            if (reward.isPremium) {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE67E22).copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "⭐ Premium",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE67E22),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: TimeBankTransaction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка типа транзакции
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (transaction.amount > 0) 
                        Color(0xFF2ECC71).copy(alpha = 0.2f) else 
                        Color(0xFFE74C3C).copy(alpha = 0.2f)
                ),
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = transaction.type.icon,
                        fontSize = 20.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Информация о транзакции
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.source,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = "${transaction.createdAt.dayOfMonth}.${transaction.createdAt.monthValue}.${transaction.createdAt.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            
            // Сумма транзакции
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (transaction.amount > 0) "+${transaction.amount}" else "${transaction.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (transaction.amount > 0) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "дней",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun PurchaseConfirmationDialog(
    reward: TimeBankReward,
    userBalance: Long,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(text = reward.icon, fontSize = 48.sp)
        },
        title = {
            Text(
                text = "Подтвердить покупку",
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF39C12).copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Стоимость: ${reward.cost} дней",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFFF39C12),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Остается: ${userBalance - reward.cost} дней",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF95A5A6)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2ECC71)
                )
            ) {
                Text("💰 Купить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color(0xFF95A5A6))
            }
        }
    )
}