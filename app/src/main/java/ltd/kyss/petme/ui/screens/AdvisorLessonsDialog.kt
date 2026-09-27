package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun AdvisorLessonsDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    var selectedId by remember {
        mutableStateOf(GameCatalog.financialLessons.firstOrNull { it.id !in state.completedLessonIds }?.id
            ?: GameCatalog.financialLessons.first().id)
    }
    val selected = GameCatalog.financialLessons.first { it.id == selectedId }

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
                            Text("💡", fontSize = 28.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Уроки Финни 🎓",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioTextDark
                        )
                        Text(
                            "Пройдено ${state.completedLessonIds.size} из ${GameCatalog.financialLessons.size} уроков",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SanrioTextSubtitle
                        )
                    }
                }

                // Список уроков
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameCatalog.financialLessons.forEach { lesson ->
                        val completed = lesson.id in state.completedLessonIds
                        val isSelected = lesson.id == selectedId

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = when {
                                isSelected -> SanrioSkyBlueLight
                                completed -> SanrioGreenBg
                                else -> Color(0xFFFAFAFA)
                            },
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) SanrioSkyBlueDark else SanrioCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedId = lesson.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (completed) "✅" else lesson.emoji, fontSize = 18.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        lesson.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) SanrioSkyBlueDark else SanrioTextDark
                                    )
                                }
                                if (completed) {
                                    Text("Пройден", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                                }
                            }
                        }
                    }
                }

                // Выбранный урок
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SanrioGoldBg,
                    border = BorderStroke(1.5.dp, SanrioGoldCoin.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "${selected.emoji} ${selected.title}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioGoldText
                        )
                        Text(
                            selected.explanation,
                            fontSize = 12.sp,
                            color = SanrioTextDark,
                            lineHeight = 16.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "📌 Запомни: ${selected.shortRule}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SanrioGoldText,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = { viewModel.completeLesson(selected.id) },
                    enabled = selected.id !in state.completedLessonIds,
                    colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (selected.id in state.completedLessonIds) "Урок уже усвоен ✓" else "Я понял этот урок! ✨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Закрыть", color = SanrioTextSubtitle, fontSize = 13.sp)
                }
            }
        }
    }
}
