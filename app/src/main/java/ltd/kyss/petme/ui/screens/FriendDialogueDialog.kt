package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.PuzzleState
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*

/**
 * Диалог общения с другом в стиле Sanrio / Hello Kitty Island Adventure.
 */
@Composable
fun FriendDialogueDialog(
    friendId: Int,
    state: GameState,
    onStartPuzzle: () -> Unit,
    onOpenLessons: () -> Unit,
    onDismiss: () -> Unit
) {
    val friend = GameCatalog.friendsList.find { it.id == friendId } ?: return
    val puzzle = state.puzzles[friendId] ?: return
    val lesson = GameCatalog.lessonForFriend(friendId)
    val lessonCompleted = lesson?.id in state.completedLessonIds
    var taskDiscussed by remember(friendId) { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(3.dp, SanrioCardBorder),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 440.dp)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Аватар друга с цветной обводкой
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SanrioSkyBlueLight)
                        .border(3.dp, SanrioSkyBlueDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    GameArt(
                        assetName = "friend_${friend.id}_portrait",
                        fallbackEmoji = friend.emoji,
                        modifier = Modifier.size(68.dp),
                        fallbackSize = 44.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    friend.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark
                )
                Text(
                    friend.species,
                    fontSize = 12.sp,
                    color = SanrioTextSubtitle
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Облачко диалога персонажа
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF0F8FF),
                    border = BorderStroke(1.5.dp, SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "«${friend.greetingText}»",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = SanrioTextDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                // После согласия ребёнок видит, какой будет следующий шаг.
                if (taskDiscussed) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFFDE7),
                        border = BorderStroke(1.5.dp, Color(0xFFFFD54F)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                when {
                                    lessonCompleted -> "Подручы напоминает: ${lesson?.shortRule}"
                                    lesson != null -> "Сначала пройди у Подручы урок «${lesson?.title}», затем это задание откроется."
                                    else -> "Финни сейчас подскажет, как подумать над решением. Ответ ты выберешь сам!"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }

                // Если уже решено
                if (puzzle.state == PuzzleState.COMPLETED) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SanrioGreenBg,
                        border = BorderStroke(1.5.dp, SanrioAccentGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "✅ Задание этого периода выполнено! Ты получил награду.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Кнопки действий (Sanrio Pill Buttons)
                when {
                    puzzle.state == PuzzleState.COMPLETED -> {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentGreen),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Закончить разговор ✓", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    taskDiscussed && lesson != null && !lessonCompleted -> {
                        Button(
                            onClick = onOpenLessons,
                            colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentPurple),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Пойти к Подручы 🪽", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.5.dp, SanrioCardBorder),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("Подумать позже", color = SanrioTextSubtitle, fontSize = 14.sp)
                        }
                    }
                    taskDiscussed -> {
                        Button(
                            onClick = onStartPuzzle,
                            colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentOrange),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Позвать Финни ✨", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.5.dp, SanrioCardBorder),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("Подумать позже", color = SanrioTextSubtitle, fontSize = 14.sp)
                        }
                    }
                    else -> {
                        Button(
                            onClick = { taskDiscussed = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SanrioSkyBlueDark),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Помогу! ❓", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.5.dp, SanrioCardBorder),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Text("Закончить разговор", color = SanrioTextSubtitle, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
