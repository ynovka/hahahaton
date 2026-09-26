package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun PeriodFinishDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    var finished by remember { mutableStateOf(false) }

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
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Иконка
                Surface(
                    shape = CircleShape,
                    color = if (finished) SanrioGoldBg else SanrioPurpleBg,
                    border = BorderStroke(2.dp, if (finished) SanrioGoldCoin else SanrioAccentPurple),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (finished) "🏆" else "📅", fontSize = 28.sp)
                    }
                }

                Text(
                    if (finished) "Итоги периода #${state.period} 🎉" else "Завершить период #${state.period}?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = SanrioTextDark,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (finished) SanrioGreenBg else SanrioSkyBlueLight,
                    border = BorderStroke(1.dp, if (finished) SanrioAccentGreen.copy(alpha = 0.4f) else SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (finished) state.periodReport.orEmpty()
                        else "Игра посчитает заботу о питомце, сохранённые сбережения и выполненные задания друзей! Готов перейти на следующую неделю?",
                        fontSize = 13.sp,
                        color = SanrioTextDark,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (finished) onDismiss()
                            else {
                                viewModel.finishPeriod()
                                finished = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (finished) SanrioAccentGreen else SanrioSkyBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (finished) "Отлично, вперёд! ✨" else "Подвести итоги 📊",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (!finished) {
                        OutlinedButton(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, SanrioCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Пока рано", color = SanrioTextSubtitle, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
