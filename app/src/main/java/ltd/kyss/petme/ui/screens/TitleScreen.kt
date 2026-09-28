package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*

/** Первая сцена приложения: точка входа в продолжение или новую историю. */
@Composable
fun TitleScreen(
    state: GameState,
    onContinue: () -> Unit,
    onNewStory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE3F2FD), Color(0xFFFCE4EC), Color(0xFFFFF8E1))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.9f),
                border = BorderStroke(2.dp, SanrioCardBorder),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GameArt("hero_idle", fallbackEmoji = "🧒", modifier = Modifier.size(66.dp), fallbackSize = 45.sp)
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .background(SanrioGoldBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            GameArt(
                                assetName = "pet_${state.pet.species.id}_portrait",
                                fallbackEmoji = state.pet.species.emoji,
                                modifier = Modifier.size(64.dp),
                                fallbackSize = 42.sp
                            )
                        }
                    }
                    Text("PET ME!", fontSize = 36.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = SanrioAccentPurple)
                    Text(
                        "Город заботы и умных монет",
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SanrioTextDark
                    )
                    Text(
                        "Составляй план, учись у Подручы и помогай друзьям принимать хорошие решения.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = SanrioTextSubtitle
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SanrioPurpleBg,
                border = BorderStroke(1.dp, SanrioAccentPurple.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🪽", fontSize = 25.sp)
                    Spacer(Modifier.width(9.dp))
                    Text(
                        if (state.isGameStarted) "Подручы бережно сохранил твой прогресс."
                        else "Привет! Я Подручы, твой помощник по финансовой грамотности.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SanrioTextDark
                    )
                }
            }

            Button(
                onClick = if (state.isGameStarted) onContinue else onNewStory,
                colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentGreen),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text(
                    if (state.isGameStarted) "Продолжить приключение" else "Выбрать питомца",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
            if (state.isGameStarted) {
                OutlinedButton(
                    onClick = onNewStory,
                    border = BorderStroke(1.5.dp, SanrioAccentPurple),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Новая история", color = SanrioAccentPurple, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
