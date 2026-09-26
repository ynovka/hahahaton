package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.GameLocation
import ltd.kyss.petme.core.model.PuzzleState
import ltd.kyss.petme.ui.components.GameArt

/**
 * Лёгкая карта города для старых телефонов.
 *
 * Здесь нет огромного масштабируемого слоя, постоянных параллакс-анимаций и десятков
 * аппаратных graphicsLayer. Карта остаётся игровой: по улице можно прокручивать город,
 * нажимать на здания и видеть, у кого из друзей доступно задание.
 */
@Composable
fun CityMapScreen(
    state: GameState,
    onSelectLocation: (GameLocation) -> Unit,
    onOpenFriendProfile: (Int) -> Unit = {}
) {
    @Suppress("UNUSED_VARIABLE")
    val profileAction = onOpenFriendProfile
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB9E7F5))
    ) {
        CityMapHeader(state = state)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1080.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFDDF5FF), Color(0xFFA5D6A7), Color(0xFF81C784))
                        )
                    )
            ) {
                val nodeWidth = 122.dp
                val leftX = 14.dp
                val rightX = (maxWidth - nodeWidth - 14.dp).coerceAtLeast(150.dp)

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(64.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF7C8790))
                )
                repeat(9) { index ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (54 + index * 118).dp)
                            .width(5.dp)
                            .height(62.dp)
                            .background(Color(0xFFF5E8A8))
                    )
                }
                listOf(150, 320, 520, 720, 920).forEach { y ->
                    Box(
                        modifier = Modifier
                            .offset(y = y.dp)
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(Color(0xFF7C8790))
                    )
                }

                Text(
                    "☁️       ☀️       ☁️",
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 6.dp),
                    fontSize = 24.sp,
                    color = Color.White
                )
                MapDistrictLabel("Центр города", 28.dp)
                MapDistrictLabel("Улица друзей", 350.dp)

                MapPlaceNode(
                    x = leftX,
                    y = 64.dp,
                    width = nodeWidth,
                    assetName = "building_home",
                    emoji = "🏠",
                    title = "Мой дом",
                    subtitle = "Питомец ждёт",
                    badge = "ДОМОЙ",
                    badgeColor = Color(0xFF2E7D32),
                    onClick = { onSelectLocation(GameLocation.MyRoom) }
                )
                MapPlaceNode(
                    x = rightX,
                    y = 82.dp,
                    width = nodeWidth,
                    assetName = "building_bank",
                    emoji = "🏦",
                    title = "Банк",
                    subtitle = "Копилка",
                    badge = "${state.wallet.savings} м.",
                    badgeColor = Color(0xFF6A4FB3),
                    onClick = { onSelectLocation(GameLocation.Bank) }
                )
                MapPlaceNode(
                    x = leftX,
                    y = 210.dp,
                    width = nodeWidth,
                    assetName = "building_shop",
                    emoji = "🛒",
                    title = "Магазин",
                    subtitle = "Еда и вещи",
                    badge = "ОТКРЫТО",
                    badgeColor = Color(0xFFCC6B16),
                    onClick = { onSelectLocation(GameLocation.Shop) }
                )
                MapPlaceNode(
                    x = rightX,
                    y = 230.dp,
                    width = nodeWidth,
                    assetName = "building_hospital",
                    emoji = "🏥",
                    title = "Больница",
                    subtitle = "Доктор Сова",
                    badge = "ОСМОТР",
                    badgeColor = Color(0xFFC62828),
                    onClick = { onSelectLocation(GameLocation.Hospital) }
                )

                val friendY = listOf(390, 480, 590, 690, 800, 900, 990)
                GameCatalog.friendsList.forEachIndexed { index, friend ->
                    val puzzle = state.puzzles[friend.id]
                    val badge = when (puzzle?.state) {
                        PuzzleState.COMPLETED -> "ГОТОВО"
                        PuzzleState.SOLVED_UNCLAIMED -> "НАГРАДА"
                        else -> "+${friend.rewardCoins} м."
                    }
                    val color = when (puzzle?.state) {
                        PuzzleState.COMPLETED -> Color(0xFF2E7D32)
                        PuzzleState.SOLVED_UNCLAIMED -> Color(0xFFEF6C00)
                        else -> Color(0xFF1565C0)
                    }
                    MapPlaceNode(
                        x = if (index % 2 == 0) leftX else rightX,
                        y = friendY[index].dp,
                        width = nodeWidth,
                        assetName = "friend_${friend.id}_portrait",
                        emoji = friend.emoji,
                        title = friend.name,
                        subtitle = friend.houseName,
                        badge = badge,
                        badgeColor = color,
                        onClick = { onSelectLocation(GameLocation.FriendRoom(friend.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CityMapHeader(state: GameState) {
    Surface(
        color = Color(0xFFFDFDFD),
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🗺️", fontSize = 25.sp)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Город", fontSize = 17.sp, fontWeight = FontWeight.Black)
                Text("Нажми на здание · листай улицу вниз", fontSize = 10.sp, color = Color(0xFF546E7A))
            }
            Surface(color = Color(0xFFFFF3C4), shape = RoundedCornerShape(12.dp)) {
                Text(
                    "🪙 ${state.wallet.coins}   🏺 ${state.wallet.savings}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BoxScope.MapDistrictLabel(title: String, y: Dp) {
    Surface(
        modifier = Modifier.align(Alignment.TopCenter).offset(y = y),
        color = Color(0xDDFFFFFF),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            title.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF355D3A),
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun BoxScope.MapPlaceNode(
    x: Dp,
    y: Dp,
    width: Dp,
    assetName: String,
    emoji: String,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = x, y = y)
            .width(width)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(2.dp, badgeColor),
                shadowElevation = 5.dp,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    GameArt(
                        assetName = assetName,
                        fallbackEmoji = emoji,
                        modifier = Modifier.size(56.dp),
                        fallbackSize = 36.sp
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xF7FFFFFF),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(title, fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1)
                    Text(
                        subtitle,
                        fontSize = 8.sp,
                        color = Color(0xFF607D8B),
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        badge,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .background(badgeColor, RoundedCornerShape(7.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}
