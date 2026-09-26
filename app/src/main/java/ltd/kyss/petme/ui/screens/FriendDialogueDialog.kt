package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.PuzzleState
import ltd.kyss.petme.ui.components.GameArt

@Composable
fun FriendDialogueDialog(
    friendId: Int,
    state: GameState,
    onStartPuzzle: () -> Unit,
    onDismiss: () -> Unit
) {
    val friend = GameCatalog.friendsList.find { it.id == friendId } ?: return
    val puzzle = state.puzzles[friendId] ?: return
    val lesson = GameCatalog.lessonForFriend(friendId)
    var taskDiscussed by remember(friendId) { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                GameArt(
                    assetName = "friend_${friend.id}_portrait",
                    fallbackEmoji = friend.emoji,
                    modifier = Modifier.size(38.dp),
                    fallbackSize = 26.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(friend.name)
            }
        },
        text = {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                Text(if (taskDiscussed) friend.puzzlePrompt else "«${friend.greetingText}»")
                if (taskDiscussed && lesson != null) {
                    HorizontalDivider()
                    Text("💡 Подсказка помощника: ${lesson.shortRule}")
                }
                if (puzzle.state == PuzzleState.COMPLETED) {
                    Text("✅ Задание этого периода уже выполнено. Загляни снова в следующем периоде!")
                }
            }
        },
        confirmButton = {
            when {
                puzzle.state == PuzzleState.COMPLETED -> Button(onClick = onDismiss) { Text("Закончить разговор") }
                taskDiscussed -> Button(onClick = onStartPuzzle) { Text("Начать задание") }
                else -> Button(onClick = { taskDiscussed = true }) { Text("Есть задание?") }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Закончить разговор") }
        }
    )
}
