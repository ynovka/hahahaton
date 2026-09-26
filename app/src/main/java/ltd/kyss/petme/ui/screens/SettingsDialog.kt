package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun SettingsDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("⚙️ Настройки") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingRow("🔊 Звуки", state.soundEnabled, viewModel::toggleSound)
                SettingRow("🔎 Крупный текст", state.largeFontEnabled, viewModel::toggleLargeFont)
                Text("Игра работает без интернета.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Готово") } }
    )
}

@Composable
private fun SettingRow(title: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
