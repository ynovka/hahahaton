package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import ltd.kyss.petme.core.model.FinancialLesson
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

/** Полноценный курс от Подручы: объяснение, правило и мини-проверка перед заданием друга. */
@Composable
fun AdvisorLessonsDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    val firstOpenLesson = GameCatalog.financialLessons.firstOrNull { it.id !in state.completedLessonIds }
        ?: GameCatalog.financialLessons.first()
    var selectedId by remember { mutableStateOf(firstOpenLesson.id) }
    var selectedAnswerIndex by remember(selectedId) { mutableStateOf<Int?>(null) }
    val selected = GameCatalog.financialLessons.first { it.id == selectedId }
    val selectedIndex = GameCatalog.financialLessons.indexOf(selected)
    val isUnlocked = GameCatalog.financialLessons.take(selectedIndex)
        .all { it.id in state.completedLessonIds }
    val isCompleted = selected.id in state.completedLessonIds
    val selectedAnswer = selectedAnswerIndex?.let(selected.checkOptions::getOrNull)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(2.dp, SanrioCardBorder),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 500.dp)
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PodruchyLessonHeader(
                    completedCount = state.completedLessonIds.size,
                    totalCount = GameCatalog.financialLessons.size,
                    onDismiss = onDismiss
                )

                Text(
                    "МАРШРУТ ОБУЧЕНИЯ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = SanrioTextSubtitle
                )
                GameCatalog.financialLessons.forEachIndexed { index, lesson ->
                    val unlocked = GameCatalog.financialLessons.take(index)
                        .all { it.id in state.completedLessonIds }
                    LessonRouteCard(
                        lesson = lesson,
                        number = index + 1,
                        selected = lesson.id == selectedId,
                        completed = lesson.id in state.completedLessonIds,
                        unlocked = unlocked,
                        onClick = { if (unlocked) selectedId = lesson.id }
                    )
                }

                if (isUnlocked) {
                    LessonPracticeCard(
                        lesson = selected,
                        completed = isCompleted,
                        selectedAnswerIndex = selectedAnswerIndex,
                        selectedAnswerFeedback = selectedAnswer?.feedback,
                        onSelectAnswer = { selectedAnswerIndex = it },
                        onSubmit = {
                            selectedAnswerIndex?.let { answerIndex ->
                                viewModel.answerLesson(selected.id, answerIndex)
                            }
                        }
                    )
                } else {
                    LockedLessonCard()
                }

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Вернуться в игру", color = SanrioTextSubtitle, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PodruchyLessonHeader(completedCount: Int, totalCount: Int, onDismiss: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(SanrioPurpleBg),
            contentAlignment = Alignment.Center
        ) {
            Text("🪽", fontSize = 31.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Подручы", fontSize = 21.sp, fontWeight = FontWeight.Black, color = SanrioTextDark)
            Text("Твой помощник по умным монетам", fontSize = 11.sp, color = SanrioTextSubtitle)
        }
        IconButton(onClick = onDismiss) {
            Text("✕", fontWeight = FontWeight.Black, color = SanrioTextSubtitle)
        }
    }
    Text(
        "Пройдено $completedCount из $totalCount уроков",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = SanrioAccentPurple
    )
    LinearProgressIndicator(
        progress = { completedCount.toFloat() / totalCount.coerceAtLeast(1) },
        modifier = Modifier.fillMaxWidth().height(8.dp),
        color = SanrioAccentPurple,
        trackColor = SanrioPurpleBg
    )
}

@Composable
private fun LessonRouteCard(
    lesson: FinancialLesson,
    number: Int,
    selected: Boolean,
    completed: Boolean,
    unlocked: Boolean,
    onClick: () -> Unit
) {
    val cardColor = when {
        selected -> SanrioSkyBlueLight
        completed -> SanrioGreenBg
        unlocked -> Color(0xFFFFFCF4)
        else -> Color(0xFFF3F1F5)
    }
    Surface(
        shape = RoundedCornerShape(15.dp),
        color = cardColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) SanrioSkyBlueDark else SanrioCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = unlocked, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (completed) SanrioAccentGreen else if (unlocked) SanrioGoldBg else Color(0xFFE0DCE3),
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (completed) "✓" else if (unlocked) number.toString() else "🔒", fontSize = 13.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.width(9.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${lesson.emoji} ${lesson.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (unlocked) SanrioTextDark else SanrioTextSubtitle)
                Text(
                    when {
                        completed -> "Готово — задание друга открыто"
                        unlocked -> "Можно пройти сейчас"
                        else -> "Сначала пройди предыдущий урок"
                    },
                    fontSize = 10.sp,
                    color = SanrioTextSubtitle
                )
            }
        }
    }
}

@Composable
private fun LessonPracticeCard(
    lesson: FinancialLesson,
    completed: Boolean,
    selectedAnswerIndex: Int?,
    selectedAnswerFeedback: String?,
    onSelectAnswer: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SanrioGoldBg,
        border = BorderStroke(1.5.dp, SanrioGoldCoin.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text("Урок: ${lesson.emoji} ${lesson.title}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SanrioGoldText)
            Text(lesson.explanation, fontSize = 12.sp, lineHeight = 17.sp, color = SanrioTextDark)
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White.copy(alpha = 0.82f)) {
                Text("📌 Правило Подручы: ${lesson.shortRule}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SanrioGoldText, modifier = Modifier.padding(9.dp))
            }
            HorizontalDivider(color = SanrioGoldCoin.copy(alpha = 0.4f))
            Text("Проверим себя", fontSize = 13.sp, fontWeight = FontWeight.Black, color = SanrioTextDark)
            Text(lesson.checkQuestion, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SanrioTextDark)
            lesson.checkOptions.forEachIndexed { index, option ->
                val selected = selectedAnswerIndex == index
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    color = if (selected) SanrioSkyBlueLight else Color.White,
                    border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) SanrioSkyBlueDark else SanrioCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !completed) { onSelectAnswer(index) }
                ) {
                    Text(option.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = SanrioTextDark, modifier = Modifier.padding(10.dp))
                }
            }
            if (selectedAnswerFeedback != null && !completed) {
                Text("Подручы: $selectedAnswerFeedback", fontSize = 11.sp, color = SanrioTextDark, lineHeight = 15.sp)
            }
            if (completed) {
                Surface(shape = RoundedCornerShape(12.dp), color = SanrioGreenBg, modifier = Modifier.fillMaxWidth()) {
                    Text("✅ Урок усвоен. Теперь можно помогать другу с его заданием!", textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SanrioAccentGreen, modifier = Modifier.padding(10.dp))
                }
            } else {
                Button(
                    onClick = onSubmit,
                    enabled = selectedAnswerIndex != null,
                    colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentGreen),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Проверить ответ", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LockedLessonCard() {
    Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF3F1F5), modifier = Modifier.fillMaxWidth()) {
        Text(
            "🔒 Подручы открывает уроки по порядку, чтобы новая тема опиралась на предыдущую.",
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = SanrioTextSubtitle,
            modifier = Modifier.padding(16.dp)
        )
    }
}
