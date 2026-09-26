package ltd.kyss.petme.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun PeriodFinishDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    var finished by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (finished) "🎉 Итоги готовы" else "Завершить период #${state.period}?")
        },
        text = {
            Text(
                if (finished) state.periodReport.orEmpty()
                else "Игра посчитает заботу о питомце, сбережения и выполненные задания. После этого начнётся следующий период."
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (finished) onDismiss()
                    else {
                        viewModel.finishPeriod()
                        finished = true
                    }
                }
            ) {
                Text(if (finished) "Продолжить" else "Подвести итоги")
            }
        },
        dismissButton = {
            if (!finished) OutlinedButton(onClick = onDismiss) { Text("Пока рано") }
        }
    )
}
