package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun WardrobeDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("👗 Гардероб питомца", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Примерь обновки на ${state.pet.name}!", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(14.dp))

                // Отображение питомца
                Surface(
                    color = Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(100.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(state.pet.species.emoji, fontSize = 54.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(GameCatalog.wardrobeAccessories) { acc ->
                        val isUnlocked = state.pet.unlockedWardrobeIds.contains(acc.id)
                        val isEquipped = state.pet.equippedAccessories[acc.slot] == acc.id

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isEquipped) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                            border = if (isEquipped) BorderStroke(1.5.dp, Color(0xFF4CAF50)) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(acc.emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(acc.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            if (isUnlocked) "В гардеробе" else "Купи в магазине (${acc.price} м.)",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                if (isUnlocked) {
                                    Button(
                                        onClick = { viewModel.toggleAccessory(acc.id) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isEquipped) Color(0xFFE57373) else Color(0xFF81C784)
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(if (isEquipped) "Снять" else "Надеть", fontSize = 11.sp)
                                    }
                                } else {
                                    Text("🔒", fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Готово ✨")
                }
            }
        }
    }
}
