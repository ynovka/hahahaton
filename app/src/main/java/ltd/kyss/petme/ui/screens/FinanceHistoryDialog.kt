package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.MoneyTransactionType
import ltd.kyss.petme.ui.theme.*

@Composable
fun FinanceHistoryDialog(state: GameState, onDismiss: () -> Unit) {
    val income = state.transactions.filter { it.type == MoneyTransactionType.INCOME }.sumOf { it.amount }
    val expenses = -state.transactions.filter { it.type == MoneyTransactionType.EXPENSE }.sumOf { it.amount }

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
                // Заголовок
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
                            Text("👛", fontSize = 28.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Мои финансы 🪙",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioTextDark
                        )
                        Text(
                            "История доходов и расходов",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SanrioTextSubtitle
                        )
                    }
                }

                // Сводка баланса
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = SanrioGreenBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SanrioAccentGreen.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Получено", fontSize = 11.sp, color = Color(0xFF00796B), fontWeight = FontWeight.Bold)
                            Text("+$income м.", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF00796B))
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = SanrioOrangeBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SanrioAccentOrange.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Потрачено", fontSize = 11.sp, color = SanrioGoldText, fontWeight = FontWeight.Bold)
                            Text("-$expenses м.", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SanrioGoldText)
                        }
                    }
                }

                // Текущая цель
                state.activeGoal?.let { goal ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SanrioPinkBg,
                        border = BorderStroke(1.dp, SanrioAccentPink.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎯 ${goal.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SanrioAccentPink)
                                Text("${state.wallet.savings}/${goal.targetCoins} м.", fontSize = 12.sp, fontWeight = FontWeight.Black, color = SanrioAccentPink)
                            }
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { state.goalProgressFraction },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = SanrioAccentPink,
                                trackColor = Color.White
                            )
                        }
                    }
                }

                // История операций
                Text(
                    "Последние события:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    state.transactions.takeLast(10).reversed().forEach { tx ->
                        val isPositive = tx.amount > 0
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPositive) SanrioGreenBg else Color(0xFFFAFAFA),
                            border = BorderStroke(1.dp, if (isPositive) SanrioAccentGreen.copy(alpha = 0.3f) else SanrioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(tx.emoji, fontSize = 16.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Column {
                                        Text(tx.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SanrioTextDark)
                                        Text("Период ${tx.period}", fontSize = 10.sp, color = SanrioTextSubtitle)
                                    }
                                }
                                Text(
                                    "${if (isPositive) "+" else ""}${tx.amount} м.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isPositive) Color(0xFF00796B) else SanrioAccentRed
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SanrioSkyBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
