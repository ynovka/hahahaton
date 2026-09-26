package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.ItemCategory
import ltd.kyss.petme.core.model.PetCareAction
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun PetCareDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    val foodCount = state.inventory.count { it.category == ItemCategory.MANDATORY_FOOD }
    val careCount = state.inventory.count { it.category == ItemCategory.MANDATORY_CARE }
    val toyCount = state.inventory.count { it.category == ItemCategory.WANT_TOY }

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
                modifier = Modifier.padding(20.dp),
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
                        color = SanrioPinkBg,
                        border = BorderStroke(2.dp, SanrioAccentPink),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            GameArt(
                                assetName = "pet_${state.pet.species.id}_portrait",
                                fallbackEmoji = state.pet.species.emoji,
                                modifier = Modifier.size(42.dp),
                                fallbackSize = 28.sp
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Забота о ${state.pet.name} 🐾",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioTextDark
                        )
                        Text(
                            "Следи за настроением и здоровьем",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SanrioTextSubtitle
                        )
                    }
                }

                // Шкалы состояния
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFFAFAFA),
                    border = BorderStroke(1.dp, SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SanrioCareMeter("Сытость", if (state.pet.isHungry) 25 else 100, "🥣", SanrioAccentOrange)
                        SanrioCareMeter("Чистота", state.pet.cleanliness, "🫧", SanrioSkyBlueDark)
                        SanrioCareMeter("Радость", state.pet.happiness, "🎾", SanrioAccentPink)
                    }
                }

                // Запасы дома
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SanrioStockBadge("🥣 Корм", foodCount, Modifier.weight(1f))
                    SanrioStockBadge("🫧 Уход", careCount, Modifier.weight(1f))
                    SanrioStockBadge("🎾 Игрушки", toyCount, Modifier.weight(1f))
                }

                // Кнопки заботы
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PetCareAction.entries.forEach { action ->
                        val completed = action in state.pet.completedCareActions
                        Button(
                            onClick = { viewModel.careForPet(action) },
                            enabled = !completed,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (completed) Color(0xFFE0E0E0) else SanrioSkyBlue
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (completed) "✅ ${action.title} (сделано)" else "${action.emoji} ${action.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (completed) SanrioTextSubtitle else Color.White
                            )
                        }
                    }
                }

                // Подсказка советника
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SanrioGoldBg,
                    border = BorderStroke(1.dp, SanrioGoldCoin.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            state.advisorTip,
                            fontSize = 11.sp,
                            color = SanrioGoldText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentGreen),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Готово ✨", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun SanrioStockBadge(title: String, count: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SanrioSkyBlueLight,
        border = BorderStroke(1.dp, SanrioCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SanrioTextDark)
            Text("×$count", fontSize = 12.sp, fontWeight = FontWeight.Black, color = SanrioSkyBlueDark)
        }
    }
}

@Composable
private fun SanrioCareMeter(title: String, value: Int, emoji: String, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SanrioTextDark)
            }
            Text("$value%", fontSize = 11.sp, fontWeight = FontWeight.Black, color = color)
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { value.coerceIn(0, 100) / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
