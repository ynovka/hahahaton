package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.ItemCategory
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

/**
 * Диалог магазина покупок в стиле Sanrio / Hello Kitty Island Adventure.
 */
@Composable
fun ShopDialog(
    state: GameState,
    viewModel: GameViewModel,
    onDismiss: () -> Unit
) {
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
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок и баланс
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SanrioPinkBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛒", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Лавка покупок",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SanrioTextDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GameArt("icon_paw_coin", fallbackEmoji = "🪙", modifier = Modifier.size(14.dp), fallbackSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "В кошельке: ${state.wallet.coins}",
                                    fontSize = 12.sp,
                                    color = SanrioGoldText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .background(SanrioSkyBlueLight, CircleShape)
                    ) {
                        Text("✕", fontWeight = FontWeight.Bold, color = SanrioSkyBlueDark)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(state.shopItems) { item ->
                        val isCarePurchase = item.category == ItemCategory.MANDATORY_FOOD ||
                            item.category == ItemCategory.MANDATORY_CARE
                        val envelopeName = if (isCarePurchase) "Забота" else "Радости"
                        val envelopeCoins = if (isCarePurchase) state.careEnvelope else state.funEnvelope
                        val canAfford = state.budget.isConfirmed &&
                            state.wallet.canAfford(item.price) && envelopeCoins >= item.price

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = SanrioSkyBlueLight,
                            border = BorderStroke(1.5.dp, SanrioCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
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
                                    val assetName = if (item.id.startsWith("item_")) item.id else "item_${item.id}"
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White)
                                            .border(1.5.dp, SanrioCardBorder, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        GameArt(
                                            assetName = assetName,
                                            fallbackEmoji = item.emoji,
                                            modifier = Modifier.size(38.dp),
                                            fallbackSize = 28.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            item.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = SanrioTextDark
                                        )
                                        Text(
                                            item.description,
                                            fontSize = 10.sp,
                                            color = SanrioTextSubtitle,
                                            maxLines = 2
                                        )
                                        Text(
                                            "Конверт «$envelopeName»: $envelopeCoins м.",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (envelopeCoins >= item.price) SanrioAccentGreen else SanrioAccentRed
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { viewModel.buyShopItem(item.id) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SanrioAccentOrange,
                                        disabledContainerColor = Color(0xFFCFD8DC)
                                    ),
                                    shape = RoundedCornerShape(50),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        GameArt("icon_paw_coin", fallbackEmoji = "🪙", modifier = Modifier.size(13.dp), fallbackSize = 10.sp)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            "${item.price}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.5.dp, SanrioCardBorder),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Закрыть лавку", color = SanrioTextSubtitle, fontSize = 14.sp)
                }
            }
        }
    }
}
