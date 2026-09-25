package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.GameLocation
import ltd.kyss.petme.core.model.PuzzleState

@Composable
fun CityMapScreen(
    state: GameState,
    onSelectLocation: (GameLocation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(16.dp)
    ) {
        // Шапка карты города
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🗺️ Карта города Финляндии",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Text(
                    text = "Выбери дом или здание, чтобы отправиться в гости!",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Важные городские здания (Свой дом, Банк, Магазин, Больница)
        Text(
            "Важные места города:",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1B5E20)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapLocationCard(
                emoji = "🏠",
                title = "Мой Дом",
                subtitle = "К питомцу",
                bgColor = Color(0xFFFFF9C4),
                modifier = Modifier.weight(1f),
                onClick = { onSelectLocation(GameLocation.MyRoom) }
            )
            MapLocationCard(
                emoji = "🏦",
                title = "Банк",
                subtitle = "Копилка",
                bgColor = Color(0xFFC8E6C9),
                modifier = Modifier.weight(1f),
                onClick = { onSelectLocation(GameLocation.Bank) }
            )
            MapLocationCard(
                emoji = "🛒",
                title = "Магазин",
                subtitle = "Корм и вещи",
                bgColor = Color(0xFFFFE0B2),
                modifier = Modifier.weight(1f),
                onClick = { onSelectLocation(GameLocation.Shop) }
            )
            MapLocationCard(
                emoji = "🏥",
                title = "Больница",
                subtitle = "Осмотр",
                bgColor = Color(0xFFB3E5FC),
                modifier = Modifier.weight(1f),
                onClick = { onSelectLocation(GameLocation.Hospital) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Дома 7 друзей
        Text(
            "Дома 7 друзей (Задания на монеты):",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color(0xFF1B5E20)
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(GameCatalog.friendsList) { friend ->
                val puzzle = state.puzzles[friend.id]
                val statusText = when (puzzle?.state) {
                    PuzzleState.COMPLETED -> "✅ Выполнено"
                    PuzzleState.SOLVED_UNCLAIMED -> "🎁 Забрать награду!"
                    else -> "⭐ +${friend.rewardCoins} монет"
                }
                val statusColor = when (puzzle?.state) {
                    PuzzleState.COMPLETED -> Color(0xFF388E3C)
                    PuzzleState.SOLVED_UNCLAIMED -> Color(0xFFF57C00)
                    else -> Color(0xFF1976D2)
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectLocation(GameLocation.FriendRoom(friend.id)) }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(friend.emoji, fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            friend.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            friend.houseName,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = statusText,
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapLocationCard(
    emoji: String,
    title: String,
    subtitle: String,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        shadowElevation = 2.dp,
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp)
            Text(
                title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                subtitle,
                fontSize = 9.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )
        }
    }
}
