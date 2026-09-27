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
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun SettingsDialog(state: GameState, viewModel: GameViewModel, onDismiss: () -> Unit) {
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
                // Заголовок
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SanrioSkyBlueLight,
                        border = BorderStroke(2.dp, SanrioSkyBlueDark),
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⚙️", fontSize = 28.sp)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Настройки 🛠️",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SanrioTextDark
                        )
                        Text(
                            "Управление игрой и звуком",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SanrioTextSubtitle
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SanrioSettingRow("🔊 Звуковые эффекты", state.soundEnabled, viewModel::toggleSound)
                    SanrioSettingRow("🔎 Крупный шрифт", state.largeFontEnabled, viewModel::toggleLargeFont)
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SanrioGreenBg,
                    border = BorderStroke(1.dp, SanrioAccentGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌿", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Игра работает автономно без интернета.",
                            fontSize = 11.sp,
                            color = Color(0xFF00796B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

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

@Composable
private fun SanrioSettingRow(title: String, checked: Boolean, onToggle: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFAFAFA),
        border = BorderStroke(1.dp, SanrioCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SanrioTextDark)
            Switch(
                checked = checked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SanrioSkyBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCFD8DC)
                )
            )
        }
    }
}
