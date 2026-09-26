package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import ltd.kyss.petme.ui.viewmodel.GameViewModel

/**
 * Окно детской финансовой загадки в стиле Hello Kitty Island Adventure.
 */
@Composable
fun KidPuzzleDialog(
    friendId: Int,
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    val friend = GameCatalog.friendsList.find { it.id == friendId } ?: return
    val puzzle = state.puzzles[friendId] ?: return

    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isCorrectAnswer by remember { mutableStateOf<Boolean?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(3.dp, SanrioCardBorder),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 460.dp)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Круглый портрет друга с яркой обводкой
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

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    friend.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = SanrioTextDark
                )
                Text(
                    puzzle.title,
                    fontSize = 13.sp,
                    color = SanrioSkyBlueDark,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // История-ситуация в приятной карточке
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFF1F8E9),
                    border = BorderStroke(1.5.dp, Color(0xFFC8E6C9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "«${puzzle.storyPrompt}»",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Если задание уже выполнено
                if (puzzle.state == PuzzleState.COMPLETED) {
                    Surface(
                        color = SanrioGreenBg,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "🎉 Задание уже выполнено! Награда получена.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00796B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentGreen),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Вернуться в комнату ✓", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    return@Column
                }

                // Если решено, но награда ещё не взята
                if (puzzle.state == PuzzleState.SOLVED_UNCLAIMED) {
                    Text(
                        "✨ Ура! Всё верно!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.claimPuzzleReward(friendId)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentOrange),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Забрать +${puzzle.rewardCoins} монет 🪙", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    return@Column
                }

                // Варианты ответов для ребенка в виде красивых скверкл-кнопок
                puzzle.options.forEach { option ->
                    Button(
                        onClick = {
                            val success = viewModel.answerPuzzle(friendId, option.id)
                            isCorrectAnswer = success
                            feedbackMessage = option.feedbackText
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SanrioSkyBlueLight,
                            contentColor = SanrioSkyBlueDark
                        ),
                        border = BorderStroke(1.5.dp, SanrioCardBorder),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Text(option.emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                option.title,
                                color = SanrioTextDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Сообщение обратной связи с объяснением
                feedbackMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isCorrectAnswer == true) SanrioGreenBg else Color(0xFFFFEBEE),
                        border = BorderStroke(
                            1.dp,
                            if (isCorrectAnswer == true) SanrioAccentGreen else Color(0xFFEF9A9A)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isCorrectAnswer == true) Color(0xFF00796B) else Color(0xFFC62828),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Подумать позже", color = SanrioTextSubtitle, fontSize = 14.sp)
                }
            }
        }
    }
}
