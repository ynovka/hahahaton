package ltd.kyss.petme.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import ltd.kyss.petme.ui.theme.*

/**
 * Карта Города в стиле Hello Kitty Island Adventure / Sanrio 3D.
 * Уютная сказочная дорожка, цветущие лужайки, домики и друзья в форме скверкл-карточек.
 */
@Composable
fun CityMapScreen(
    state: GameState,
    onSelectLocation: (GameLocation) -> Unit,
    onOpenFriendProfile: (Int) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SanrioSkyBlueLight)
    ) {
        // Шапка карты (Sanrio Pill Header)
        SanrioCityMapHeader(state = state)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1150.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFE1F5FE), // Небо у въезда в город
                                Color(0xFFC8E6C9), // Зелёные холмы
                                Color(0xFFA5D6A7), // Центр лужайки
                                Color(0xFF81C784)  // Улица друзей
                            )
                        )
                    )
            ) {
                val nodeWidth = if (isLandscape) 160.dp else 140.dp
                val leftX = if (isLandscape) (maxWidth * 0.15f) else 14.dp
                val rightX = if (isLandscape) (maxWidth * 0.85f - nodeWidth) else (maxWidth - nodeWidth - 14.dp).coerceAtLeast(160.dp)

                // Пешеходная мощёная улочка города (вместо серого асфальта!)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(76.dp)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFFECB3), // Тёплый песчаник
                                    Color(0xFFFFF8E1), // Светлая брусчатка
                                    Color(0xFFFFECB3)
                                )
                            )
                        )
                        .border(BorderStroke(2.dp, Color(0xFFFFD54F).copy(alpha = 0.6f)))
                )

                // Декоративные плитки брусчатки по центру улочки
                repeat(11) { index ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (45 + index * 105).dp)
                            .width(8.dp)
                            .height(45.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFCA28))
                    )
                }

                // Боковые тропинки к зданиям (с мягким скруглением)
                listOf(165, 335, 535, 735, 935).forEach { y ->
                    Box(
                        modifier = Modifier
                            .offset(y = y.dp)
                            .fillMaxWidth()
                            .height(34.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFFECB3), Color(0xFFFFE082))
                                )
                            )
                    )
                }

                // Цветущие кусты и ромашки на полянках
                listOf(
                    Pair(30.dp, 10.dp), Pair(180.dp, 30.dp), Pair(310.dp, 15.dp),
                    Pair(120.dp, 470.dp), Pair(260.dp, 650.dp), Pair(40.dp, 840.dp),
                    Pair(290.dp, 1020.dp)
                ).forEach { (xOffset, yOffset) ->
                    Text(
                        "🌸",
                        fontSize = 16.sp,
                        modifier = Modifier.offset(x = xOffset, y = yOffset)
                    )
                }

                // Таблички районов
                SanrioDistrictLabel("⭐ Центр города", 24.dp)
                SanrioDistrictLabel("🏡 Улица верных друзей", 355.dp)

                // 1. Мой дом
                SanrioMapNode(
                    x = leftX,
                    y = 65.dp,
                    width = nodeWidth,
                    assetName = "building_home",
                    emoji = "🏠",
                    title = "Мой дом",
                    subtitle = "Питомец ждёт",
                    badge = "ДОМОЙ",
                    badgeColor = SanrioAccentGreen,
                    onClick = { onSelectLocation(GameLocation.MyRoom) }
                )

                // 2. Городской Банк
                SanrioMapNode(
                    x = rightX,
                    y = 80.dp,
                    width = nodeWidth,
                    assetName = "building_bank",
                    emoji = "🏦",
                    title = "Банк",
                    subtitle = "Копилка и сейф",
                    badge = "${state.wallet.savings} м.",
                    badgeColor = SanrioAccentPurple,
                    onClick = { onSelectLocation(GameLocation.Bank) }
                )

                // 3. Универсальный магазин
                SanrioMapNode(
                    x = leftX,
                    y = 220.dp,
                    width = nodeWidth,
                    assetName = "building_shop",
                    emoji = "🛒",
                    title = "Магазин",
                    subtitle = "Еда и наряды",
                    badge = "ЛАВКА",
                    badgeColor = SanrioAccentPink,
                    onClick = { onSelectLocation(GameLocation.Shop) }
                )

                // 4. Больница Доктора Совы
                SanrioMapNode(
                    x = rightX,
                    y = 240.dp,
                    width = nodeWidth,
                    assetName = "building_hospital",
                    emoji = "🏥",
                    title = "Больница",
                    subtitle = "Доктор Сова",
                    badge = "ОСМОТР",
                    badgeColor = SanrioSkyBlueDark,
                    onClick = { onSelectLocation(GameLocation.Hospital) }
                )

                // Домики 7 друзей города
                val friendY = listOf(400, 500, 610, 715, 820, 925, 1025)
                GameCatalog.friendsList.forEachIndexed { index, friend ->
                    val puzzle = state.puzzles[friend.id]
                    val (badgeText, badgeColor) = when (puzzle?.state) {
                        PuzzleState.COMPLETED -> Pair("ГОТОВО ✓", SanrioAccentGreen)
                        PuzzleState.SOLVED_UNCLAIMED -> Pair("НАГРАДА 🪙", SanrioAccentOrange)
                        else -> Pair("+${friend.rewardCoins} м.", SanrioSkyBlueDark)
                    }

                    SanrioMapNode(
                        x = if (index % 2 == 0) leftX else rightX,
                        y = friendY[index].dp,
                        width = nodeWidth,
                        assetName = "friend_${friend.id}_portrait",
                        emoji = friend.emoji,
                        title = friend.name,
                        subtitle = friend.houseName,
                        badge = badgeText,
                        badgeColor = badgeColor,
                        onAvatarClick = { onOpenFriendProfile(friend.id) },
                        onClick = { onSelectLocation(GameLocation.FriendRoom(friend.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SanrioCityMapHeader(state: GameState) {
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        border = BorderStroke(2.dp, SanrioCardBorder),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SanrioSkyBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text("🗺️", fontSize = 24.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Город Финляндия",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark
                )
                Text(
                    "Нажми на друга или здание для визита",
                    fontSize = 10.sp,
                    color = SanrioTextSubtitle
                )
            }

            // Монеты и сбережения
            Surface(
                color = SanrioGoldBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, SanrioGoldCoin)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameArt("icon_paw_coin", fallbackEmoji = "🪙", modifier = Modifier.size(15.dp), fallbackSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${state.wallet.coins}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SanrioGoldText)
                    Spacer(modifier = Modifier.width(6.dp))
                    GameArt("jar_savings", fallbackEmoji = "🏺", modifier = Modifier.size(15.dp), fallbackSize = 12.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${state.wallet.savings}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SanrioAccentPurple)
                }
            }
        }
    }
}

@Composable
private fun BoxScope.SanrioDistrictLabel(title: String, y: Dp) {
    Surface(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .offset(y = y),
        color = Color.White.copy(alpha = 0.95f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, SanrioCardBorder),
        shadowElevation = 3.dp
    ) {
        Text(
            title,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SanrioTextDark
        )
    }
}

/**
 * Игровой узел на карте города в форме скверкл-карточки Sanrio.
 */
@Composable
private fun BoxScope.SanrioMapNode(
    x: Dp,
    y: Dp,
    width: Dp,
    assetName: String,
    emoji: String,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    onAvatarClick: (() -> Unit)? = null,
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
            // Круглый аватар с яркой рамкой и мягкой тенью
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(3.dp, badgeColor),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .size(72.dp)
                    .then(
                        if (onAvatarClick != null) Modifier.clickable(onClick = onAvatarClick)
                        else Modifier
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    GameArt(
                        assetName = assetName,
                        fallbackEmoji = emoji,
                        modifier = Modifier.size(62.dp),
                        fallbackSize = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Скверкл-карточка с описанием и кнопкой
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.97f),
                border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.4f)),
                shadowElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SanrioTextDark,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        subtitle,
                        fontSize = 9.sp,
                        color = SanrioTextSubtitle,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Surface(
                        color = badgeColor,
                        shape = RoundedCornerShape(10.dp),
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
