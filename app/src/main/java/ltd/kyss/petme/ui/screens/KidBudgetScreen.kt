package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.CoinJarType
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun KidBudgetScreen(
    state: GameState,
    viewModel: GameViewModel,
    onClose: () -> Unit
) {
    val b = state.budget

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF9C4), Color(0xFFFFF176), Color(0xFFFFD54F))
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
        // Шапка окна бюджета
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "🪙 Раскладываем монетки!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
                Text(
                    text = "Период #${state.period} • Всего монет: ${b.totalStartingCoins}",
                    fontSize = 13.sp,
                    color = Color(0xFF5D4037)
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
            ) {
                Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Персонаж Финни с подсказкой детским языком
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(state.pet.species.emoji, fontSize = 40.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "${state.pet.name} подсказывает:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        state.advisorTip,
                        fontSize = 13.sp,
                        color = Color(0xFF263238),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Игровых сундучка / горшочка

        // 1. Миска питомца (Обязательное)
        PlayfulJarCard(
            title = "1. Миска и Забота 🥣",
            description = "Корм, витамины и чистота, чтобы питомец был здоровым!",
            coins = b.foodAndCareCoins,
            cardColor = Color(0xFFE8F5E9),
            accentColor = Color(0xFF2E7D32),
            onMinus = { viewModel.allocateCoins(CoinJarType.FOOD_AND_CARE, -10) },
            onPlus = { viewModel.allocateCoins(CoinJarType.FOOD_AND_CARE, 10) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Сундучок радостей (Желания)
        PlayfulJarCard(
            title = "2. Сундучок Радостей 🎁",
            description = "Мячики, вкусняшки и весёлые развлечения!",
            coins = b.funAndGamesCoins,
            cardColor = Color(0xFFFCE4EC),
            accentColor = Color(0xFFC2185B),
            onMinus = { viewModel.allocateCoins(CoinJarType.FUN_AND_GAMES, -10) },
            onPlus = { viewModel.allocateCoins(CoinJarType.FUN_AND_GAMES, 10) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Копилка на мечту (Сбережения)
        PlayfulJarCard(
            title = "3. Копилка на Мечту 🏺",
            description = "Копим на замок питомца! Цель: ${state.activeGoal?.title ?: "Домик"}",
            coins = b.piggyBankCoins,
            cardColor = Color(0xFFEDE7F6),
            accentColor = Color(0xFF512DA8),
            onMinus = { viewModel.allocateCoins(CoinJarType.PIGGY_BANK, -10) },
            onPlus = { viewModel.allocateCoins(CoinJarType.PIGGY_BANK, 10) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Индикатор остатка монет
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Разложено: ${b.totalAllocated} из ${b.totalStartingCoins} м.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = if (b.unallocated == 0) "Идеально! ✨" else "Осталось: ${b.unallocated} м.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (b.unallocated == 0) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Большая кнопка подтверждения
        Button(
            onClick = {
                viewModel.confirmBudget()
                onClose()
            },
            enabled = b.isValid && !b.isConfirmed,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = if (b.isConfirmed) "План уже утверждён ✅" else "Утвердить и начать игру! ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
}

@Composable
fun PlayfulJarCard(
    title: String,
    description: String,
    coins: Int,
    cardColor: Color,
    accentColor: Color,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cardColor,
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.3f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                description,
                fontSize = 11.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка минус 10
                Button(
                    onClick = onMinus,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("-10 🪙", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                // Количество монет в этом горшочке
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "$coins монет",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Кнопка плюс 10
                Button(
                    onClick = onPlus,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("+10 🪙", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
