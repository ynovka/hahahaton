package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun BankDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    var amountText by remember { mutableStateOf("10") }
    var pendingWithdrawal by remember { mutableStateOf<Int?>(null) }
    val amount = amountText.toIntOrNull()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Банк · Копилка на мечту") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("В кошельке: ${state.wallet.coins} монет\nВ копилке: ${state.wallet.savings} монет")
                Text("Выбери цель. При смене цели монеты сохраняются.")
                state.dreamGoals.forEach { goal ->
                    OutlinedButton(
                        onClick = { viewModel.selectGoal(goal.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${if (state.activeGoalId == goal.id) "✓ " else ""}${goal.emoji} ${goal.title} — ${goal.targetCoins}")
                    }
                }
                state.activeGoal?.let { goal ->
                    Text("До цели осталось: ${(goal.targetCoins - state.wallet.savings).coerceAtLeast(0)} монет")
                }
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        if (input.length <= 10 && input.all { it in '0'..'9' }) amountText = input
                    },
                    label = { Text("Сумма монет") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (amount == null || amount <= 0) {
                    Text("Введи положительное целое число монет.")
                } else {
                    if (amount > state.wallet.coins) Text("Для пополнения не хватает ${amount - state.wallet.coins} монет в кошельке.")
                    if (amount > state.wallet.savings) Text("Для снятия в копилке недостаточно монет.")
                }
                Button(
                    enabled = amount != null && amount > 0 && amount <= state.wallet.coins,
                    onClick = { amount?.let(viewModel::depositSavings) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Перевести в копилку") }
                OutlinedButton(
                    enabled = amount != null && amount > 0 && amount <= state.wallet.savings,
                    onClick = { pendingWithdrawal = amount },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Вернуть в кошелёк…") }
                Text(state.advisorTip)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Закрыть") } }
    )

    pendingWithdrawal?.let { requested ->
        AlertDialog(
            onDismissRequest = { pendingWithdrawal = null },
            title = { Text("Забрать $requested монет?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("В копилке останется ${(state.wallet.savings - requested).coerceAtLeast(0)} монет.")
                    state.activeGoal?.let { goal ->
                        val remaining = (goal.targetCoins.toLong() - state.wallet.savings + requested).coerceAtLeast(0)
                        Text("До цели «${goal.title}» останется накопить $remaining монет.")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Clear first so repeated taps cannot confirm this withdrawal twice.
                    val confirmed = pendingWithdrawal
                    pendingWithdrawal = null
                    confirmed?.let(viewModel::withdrawSavings)
                }) { Text("Подтвердить") }
            },
            dismissButton = {
                TextButton(onClick = { pendingWithdrawal = null }) { Text("Отмена") }
            }
        )
    }
}
