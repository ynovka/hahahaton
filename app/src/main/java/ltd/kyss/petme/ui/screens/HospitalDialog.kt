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
import ltd.kyss.petme.core.engine.GameEngine
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.PetHealth
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun HospitalDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
    val checkedThisPeriod = state.pet.lastCheckupPeriod == state.period
    val needsTreatment = state.pet.health == PetHealth.NEEDS_TREATMENT

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Заголовок с портретом Доктора Совы
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SanrioGreenBg,
                        border = BorderStroke(2.dp, SanrioAccentGreen),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            GameArt(
                                assetName = "worker_hospital_idle",
                                fallbackEmoji = "🦉",
                                modifier = Modifier.size(46.dp),
                                fallbackSize = 32.sp
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Лечебница 🏥",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioTextDark
                        )
                        Text(
                            "Доктор Сова на страже здоровья",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SanrioTextSubtitle
                        )
                    }
                }

                // Карточка состояния питомца
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (needsTreatment) SanrioPinkBg else SanrioGreenBg,
                    border = BorderStroke(1.5.dp, if (needsTreatment) SanrioAccentPink else SanrioAccentGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                GameArt(
                                    assetName = "pet_${state.pet.species.id}_portrait",
                                    fallbackEmoji = state.pet.species.emoji,
                                    modifier = Modifier.size(38.dp),
                                    fallbackSize = 26.sp
                                )
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${state.pet.name} · Период ${state.period}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SanrioTextDark
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(state.pet.health.emoji, fontSize = 16.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    state.pet.health.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (needsTreatment) SanrioAccentRed else Color(0xFF00796B)
                                )
                            }
                        }
                    }
                }

                // Информационная подсказка
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SanrioSkyBlueLight,
                    border = BorderStroke(1.dp, SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            if (checkedThisPeriod) "🩺 Плановый осмотр в этом периоде уже пройден! Доктор Сова довольна состоянием."
                            else "✨ Плановый осмотр бесплатный и доступен один раз за период.",
                            fontSize = 12.sp,
                            color = SanrioSkyBlueDark,
                            fontWeight = FontWeight.Medium
                        )
                        if (needsTreatment) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "💊 Питомцу нужно лечение. Стоимость: ${GameEngine.TREATMENT_COST} монет. В кошельке: ${state.wallet.coins} м.",
                                fontSize = 12.sp,
                                color = SanrioAccentRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Кнопки действий
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = viewModel::runHospitalCheckup,
                        enabled = !checkedThisPeriod,
                        colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (checkedThisPeriod) "Осмотр уже пройден ✓" else "Пройти осмотр (Бесплатно) ✨",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    if (needsTreatment) {
                        Button(
                            onClick = viewModel::treatPet,
                            enabled = state.wallet.coins >= GameEngine.TREATMENT_COST,
                            colors = ButtonDefaults.buttonColors(containerColor = SanrioAccentPink),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Оплатить лечение — ${GameEngine.TREATMENT_COST} м.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SanrioCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Закрыть", color = SanrioTextSubtitle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
