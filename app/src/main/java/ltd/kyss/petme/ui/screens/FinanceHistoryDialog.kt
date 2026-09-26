package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.MoneyTransactionType

@Composable
fun FinanceHistoryDialog(state: GameState, onDismiss: () -> Unit) {
    val income = state.transactions.filter { it.type == MoneyTransactionType.INCOME }.sumOf { it.amount }
    val expenses = -state.transactions.filter { it.type == MoneyTransactionType.EXPENSE }.sumOf { it.amount }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("👛 Мои финансы") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("В кошельке: ${state.wallet.coins} м. • В копилке: ${state.wallet.savings} м.")
                Text("Получено: $income м. • Потрачено на покупки и услуги: $expenses м.")
                state.activeGoal?.let { goal ->
                    Text("Цель: ${goal.emoji} ${goal.title} — ${state.wallet.savings}/${goal.targetCoins} м.")
                    LinearProgressIndicator(
                        progress = { state.goalProgressFraction },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                HorizontalDivider()
                Text("Последние операции", style = MaterialTheme.typography.titleMedium)
                state.transactions.takeLast(12).reversed().forEach { transaction ->
                    val isPositive = transaction.amount > 0
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${transaction.emoji} ${transaction.title} · период ${transaction.period}", modifier = Modifier.weight(1f))
                        Text(
                            "${if (isPositive) "+" else ""}${transaction.amount}",
                            color = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }
                }
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Закрыть") } }
    )
}
