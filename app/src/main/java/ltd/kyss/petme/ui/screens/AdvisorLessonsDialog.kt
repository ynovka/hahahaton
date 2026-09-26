package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun AdvisorLessonsDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    var selectedId by remember {
        mutableStateOf(GameCatalog.financialLessons.firstOrNull { it.id !in state.completedLessonIds }?.id
            ?: GameCatalog.financialLessons.first().id)
    }
    val selected = GameCatalog.financialLessons.first { it.id == selectedId }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("💡 Финни — финансовый помощник", style = MaterialTheme.typography.titleLarge)
                Text("Пройдено ${state.completedLessonIds.size} из ${GameCatalog.financialLessons.size} уроков")
                GameCatalog.financialLessons.forEach { lesson ->
                    val completed = lesson.id in state.completedLessonIds
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth().clickable { selectedId = lesson.id }
                    ) {
                        Text(
                            "${if (completed) "✅" else lesson.emoji} ${lesson.title}",
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
                HorizontalDivider()
                Text("${selected.emoji} ${selected.title}", style = MaterialTheme.typography.titleMedium)
                Text(selected.explanation)
                Text("Запомни: ${selected.shortRule}", style = MaterialTheme.typography.bodyLarge)
                Button(
                    onClick = { viewModel.completeLesson(selected.id) },
                    enabled = selected.id !in state.completedLessonIds,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selected.id in state.completedLessonIds) "Урок пройден" else "Я понял урок")
                }
                OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Закрыть") }
            }
        }
    }
}
