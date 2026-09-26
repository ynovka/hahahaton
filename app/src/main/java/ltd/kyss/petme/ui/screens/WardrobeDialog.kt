package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun WardrobeDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
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
                        color = SanrioPurpleBg,
                        border = BorderStroke(2.dp, SanrioAccentPurple),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("👗", fontSize = 28.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Гардероб 🎀",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioTextDark
                        )
                        Text(
                            "Примерь наряды на ${state.pet.name}!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SanrioTextSubtitle
                        )
                    }
                }

                // Большой аватар питомца на примерке
                Surface(
                    color = SanrioGoldBg,
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(2.dp, SanrioGoldCoin.copy(alpha = 0.5f)),
                    modifier = Modifier.size(110.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        GameArt(
                            assetName = "pet_${state.pet.species.id}_idle",
                            fallbackEmoji = state.pet.species.emoji,
                            modifier = Modifier.size(90.dp),
                            fallbackSize = 58.sp
                        )
                    }
                }

                Text(
                    "Коллекция аксессуаров:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark,
                    modifier = Modifier.fillMaxWidth()
                )

                // Список аксессуаров
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(GameCatalog.wardrobeAccessories) { acc ->
                        val isUnlocked = state.pet.unlockedWardrobeIds.contains(acc.id)
                        val isEquipped = state.pet.equippedAccessories[acc.slot] == acc.id

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when {
                                isEquipped -> SanrioGreenBg
                                isUnlocked -> Color(0xFFF9F9F9)
                                else -> Color(0xFFF0F0F0)
                            },
                            border = BorderStroke(
                                if (isEquipped) 2.dp else 1.dp,
                                if (isEquipped) SanrioAccentGreen else SanrioCardBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(acc.emoji, fontSize = 22.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            acc.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SanrioTextDark
                                        )
                                        Text(
                                            if (isUnlocked) "В гардеробе ✨" else "В магазине (${acc.price} м.)",
                                            fontSize = 10.sp,
                                            color = if (isUnlocked) Color(0xFF00796B) else SanrioTextSubtitle,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                if (isUnlocked) {
                                    Button(
                                        onClick = { viewModel.toggleAccessory(acc.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEquipped) SanrioAccentPink else SanrioAccentGreen
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            if (isEquipped) "Снять" else "Надеть ✨",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SanrioOrangeBg,
                                        border = BorderStroke(1.dp, SanrioAccentOrange.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            "🔒 В лавке",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SanrioAccentOrange,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Кнопка закрытия
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SanrioSkyBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Готово ✨", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
