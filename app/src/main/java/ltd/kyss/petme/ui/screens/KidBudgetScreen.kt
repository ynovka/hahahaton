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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.CoinJarType
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

/**
 * Экран распределения детского бюджета в стиле Sanrio / Hello Kitty Island Adventure.
 */
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
            .background(SanrioSkyBlueLight),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp)
                .padding(14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Шапка окна бюджета
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                border = BorderStroke(2.dp, SanrioCardBorder),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🪙 Раскладываем монетки!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SanrioTextDark
                        )
                        Text(
                            text = "Период #${state.period} • Доход: ${b.totalStartingCoins} монет",
                            fontSize = 12.sp,
                            color = SanrioTextSubtitle
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SanrioSkyBlueLight)
                    ) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SanrioSkyBlueDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Карточка-подсказка от питомца
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color(0xFFFFFDE7),
                border = BorderStroke(1.5.dp, Color(0xFFFFD54F)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.5.dp, SanrioGoldCoin, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        GameArt(
                            assetName = "pet_${state.pet.species.id}_portrait",
                            fallbackEmoji = state.pet.species.emoji,
                            modifier = Modifier.size(38.dp),
                            fallbackSize = 26.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "${state.pet.name} подсказывает:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SanrioGoldText
                        )
                        Text(
                            state.advisorTip,
                            fontSize = 11.sp,
                            color = SanrioTextDark,
                            lineHeight = 15.sp,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Миска питомца (Обязательное - 50%)
            SanrioJarCard(
                title = "1. Миска и Забота (50%) 🥣",
                description = "Корм, витамины и чистота для здоровья питомца",
                coins = b.foodAndCareCoins,
                cardColor = SanrioGreenBg,
                accentColor = SanrioAccentGreen,
                assetName = "jar_needs",
                onMinus = { viewModel.allocateCoins(CoinJarType.FOOD_AND_CARE, -10) },
                onPlus = { viewModel.allocateCoins(CoinJarType.FOOD_AND_CARE, 10) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Сундучок радостей (Желания - 30%)
            SanrioJarCard(
                title = "2. Сундучок Радостей (30%) 🎁",
                description = "Игрушки, наряды и весёлые развлечения",
                coins = b.funAndGamesCoins,
                cardColor = SanrioPinkBg,
                accentColor = SanrioAccentPink,
                assetName = "jar_fun",
                onMinus = { viewModel.allocateCoins(CoinJarType.FUN_AND_GAMES, -10) },
                onPlus = { viewModel.allocateCoins(CoinJarType.FUN_AND_GAMES, 10) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Копилка на мечту (Сбережения - 20%)
            SanrioJarCard(
                title = "3. Копилка на Мечту (20%) 🏺",
                description = "Копим на цель: ${state.activeGoal?.title ?: "Домик мечты"}",
                coins = b.piggyBankCoins,
                cardColor = SanrioPurpleBg,
                accentColor = SanrioAccentPurple,
                assetName = "jar_savings",
                onMinus = { viewModel.allocateCoins(CoinJarType.PIGGY_BANK, -10) },
                onPlus = { viewModel.allocateCoins(CoinJarType.PIGGY_BANK, 10) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Индикатор остатка монет
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.5.dp, SanrioCardBorder),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Разложено: ${b.totalAllocated} из ${b.totalStartingCoins} м.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SanrioTextDark
                    )
                    Surface(
                        color = if (b.unallocated == 0) SanrioGreenBg else SanrioOrangeBg,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (b.unallocated == 0) "Идеально! ✨" else "Осталось: ${b.unallocated} м.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (b.unallocated == 0) SanrioAccentGreen else SanrioGoldText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Большая кнопка подтверждения (Sanrio Pill Button)
            Button(
                onClick = {
                    viewModel.confirmBudget()
                    onClose()
                },
                enabled = b.isValid && !b.isConfirmed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SanrioAccentGreen,
                    disabledContainerColor = Color(0xFFCFD8DC)
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (b.isConfirmed) "План уже утверждён ✅" else "Утвердить и начать игру! ✨",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SanrioJarCard(
    title: String,
    description: String,
    coins: Int,
    cardColor: Color,
    accentColor: Color,
    assetName: String = "",
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = cardColor,
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.4f)),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (assetName.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        GameArt(
                            assetName = assetName,
                            fallbackEmoji = "🏺",
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = accentColor)
                    Text(description, fontSize = 10.sp, color = SanrioTextSubtitle, maxLines = 1)
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "$coins м.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onMinus,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("− 10", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = onPlus,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("+ 10", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
