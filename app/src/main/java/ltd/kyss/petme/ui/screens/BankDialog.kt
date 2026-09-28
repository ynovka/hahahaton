package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun BankDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    var amountText by remember { mutableStateOf("10") }
    var pendingWithdrawal by remember { mutableStateOf<Int?>(null) }
    val amount = amountText.toIntOrNull()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(2.dp, SanrioCardBorder),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Заголовок с портретом Бобра-банкира
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SanrioGoldBg,
                        border = BorderStroke(2.dp, SanrioGoldCoin),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            GameArt(
                                assetName = "worker_bank_idle",
                                fallbackEmoji = "🦫",
                                modifier = Modifier.size(46.dp),
                                fallbackSize = 32.sp
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Городской Банк 🏛️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioTextDark
                        )
                        Text(
                            "Копилка на мечту с Бобром",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SanrioTextSubtitle
                        )
                    }
                }

                // Информационные плашки: Кошелёк и Копилка
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = SanrioGoldBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, SanrioGoldCoin.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("👛 В кошельке", fontSize = 11.sp, color = SanrioGoldText, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(2.dp))
                            Text("${state.wallet.coins} м.", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SanrioGoldText)
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = SanrioPinkBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, SanrioAccentPink.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏺 В копилке", fontSize = 11.sp, color = SanrioAccentPink, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(2.dp))
                            Text("${state.wallet.savings} м.", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SanrioAccentPink)
                        }
                    }
                }

                // Выбор финансовой цели
                Text(
                    "Выбери свою цель мечты:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.dreamGoals.forEach { goal ->
                        val isSelected = state.activeGoalId == goal.id
                        val currentProgress = (state.wallet.savings.toFloat() / goal.targetCoins).coerceIn(0f, 1f)

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) SanrioSkyBlueLight else Color(0xFFFAFAFA),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) SanrioSkyBlueDark else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectGoal(goal.id) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(goal.emoji, fontSize = 20.sp)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            goal.title,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            color = if (isSelected) SanrioSkyBlueDark else SanrioTextDark
                                        )
                                    }
                                    Text(
                                        "${goal.targetCoins} м.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SanrioGoldText
                                    )
                                }
                                if (isSelected) {
                                    Spacer(Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { currentProgress },
                                        modifier = Modifier.fillMaxWidth().height(6.dp),
                                        color = SanrioSkyBlue,
                                        trackColor = SanrioSkyBlueLight
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    val remaining = (goal.targetCoins - state.wallet.savings).coerceAtLeast(0)
                                    Text(
                                        if (remaining == 0) "🎉 Цель достигнута!" else "Осталось накопить: $remaining монет",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (remaining == 0) Color(0xFF2E7D32) else SanrioTextSubtitle
                                    )
                                }
                            }
                        }
                    }
                }

                // Сумма для операции
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Сумма монет:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SanrioTextDark
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(5, 10, 25, 50).forEach { quickAmount ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (amountText == quickAmount.toString()) SanrioSkyBlue else SanrioSkyBlueLight,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { amountText = quickAmount.toString() }
                            ) {
                                Text(
                                    "+$quickAmount",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (amountText == quickAmount.toString()) Color.White else SanrioSkyBlueDark,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { input ->
                            if (input.length <= 10 && input.all { it in '0'..'9' }) amountText = input
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Предупреждения
                if (amount == null || amount <= 0) {
                    Text("Введи положительное число монет.", fontSize = 11.sp, color = SanrioAccentOrange)
                } else {
                    if (amount > state.wallet.coins) {
                        Text("В кошельке не хватает ${amount - state.wallet.coins} монет.", fontSize = 11.sp, color = SanrioAccentRed)
                    }
                    if (amount > state.funEnvelope) {
                        Text("Из сундучка радостей можно перевести только ${state.funEnvelope} м. Деньги на заботу не трогаем.", fontSize = 11.sp, color = SanrioAccentRed)
                    }
                    if (amount > state.wallet.savings) {
                        Text("В копилке недостаточно монет для снятия.", fontSize = 11.sp, color = SanrioAccentRed)
                    }
                }

                // Кнопки действий
                Button(
                    enabled = state.budget.isConfirmed && amount != null && amount > 0 && amount <= state.wallet.coins && amount <= state.funEnvelope,
                    onClick = { amount?.let(viewModel::depositSavings) },
                    colors = ButtonDefaults.buttonColors(containerColor = SanrioSkyBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Перевести в копилку 🏺", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                OutlinedButton(
                    enabled = amount != null && amount > 0 && amount <= state.wallet.savings,
                    onClick = { pendingWithdrawal = amount },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, SanrioAccentOrange.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Взять на важный расход 👛", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SanrioAccentOrange)
                }

                // Совет советника
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SanrioGoldBg,
                    border = BorderStroke(1.dp, SanrioGoldCoin.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            state.advisorTip,
                            fontSize = 11.sp,
                            color = SanrioGoldText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Кнопка закрыть
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть", fontWeight = FontWeight.Bold, color = SanrioTextSubtitle, fontSize = 13.sp)
                }
            }
        }
    }

    // Подтверждение снятия
    pendingWithdrawal?.let { requested ->
        Dialog(onDismissRequest = { pendingWithdrawal = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(2.dp, SanrioCardBorder),
                shadowElevation = 10.dp,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🤔 Взять $requested монет из резерва?", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SanrioTextDark)
                    Text(
                        "Деньги вернутся в конверт «Забота». В копилке останется ${(state.wallet.savings - requested).coerceAtLeast(0)} м.",
                        fontSize = 12.sp,
                        color = SanrioTextSubtitle
                    )
                    state.activeGoal?.let { goal ->
                        val remaining = (goal.targetCoins.toLong() - state.wallet.savings + requested).coerceAtLeast(0)
                        Text(
                            "До цели «${goal.title}» останется накопить $remaining монет.",
                            fontSize = 11.sp,
                            color = SanrioAccentOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { pendingWithdrawal = null },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Отмена", fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                val confirmed = pendingWithdrawal
                                pendingWithdrawal = null
                                confirmed?.let(viewModel::withdrawSavings)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentOrange),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Подтвердить", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
