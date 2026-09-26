package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.ItemCategory
import ltd.kyss.petme.core.model.PetCareAction
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun PetCareDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    val foodCount = state.inventory.count { it.category == ItemCategory.MANDATORY_FOOD }
    val careCount = state.inventory.count { it.category == ItemCategory.MANDATORY_CARE }
    val toyCount = state.inventory.count { it.category == ItemCategory.WANT_TOY }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🐾 Забота о ${state.pet.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CareMeter("Сытость", if (state.pet.isHungry) 25 else 100, "🥣")
                CareMeter("Чистота", state.pet.cleanliness, "🫧")
                CareMeter("Радость", state.pet.happiness, "🎾")
                HorizontalDivider()
                Text("Запасы дома: корм ×$foodCount, уход ×$careCount, игрушки ×$toyCount")
                PetCareAction.entries.forEach { action ->
                    val completed = action in state.pet.completedCareActions
                    Button(
                        onClick = { viewModel.careForPet(action) },
                        enabled = !completed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (completed) "✅ ${action.title}" else "${action.emoji} ${action.title}")
                    }
                }
                Text(state.advisorTip, style = MaterialTheme.typography.bodySmall)
                Text(
                    "Если чего-то нет, закрой окно, выйди на карту и купи предмет в Лавке Енотика.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = { Button(onClick = onDismiss) { Text("Готово") } }
    )
}

@Composable
private fun CareMeter(title: String, value: Int, emoji: String) {
    Column {
        Text("$emoji $title: $value%")
        LinearProgressIndicator(
            progress = { value.coerceIn(0, 100) / 100f },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
