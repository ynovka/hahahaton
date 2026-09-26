package ltd.kyss.petme.ui.screens

import android.content.res.Configuration
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
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
 * Полноценная карта поселения в стиле Hello Kitty Island Adventure / Sanrio 3D.
 * Настоящая изометрическая арт-карта локации (вертикальная 9:16 и панорамная 16:9)
 * с компактными интерактивными значками-булавками зданий и персонажей поверх фона.
 */
@Composable
fun CityMapScreen(
    state: GameState,
    onSelectLocation: (GameLocation) -> Unit,
    onOpenFriendProfile: (Int) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isSystemLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var userPanoramaMode by remember { mutableStateOf(false) }
    val isPanorama = isSystemLandscape || userPanoramaMode
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SanrioSkyBlueLight)
    ) {
        // Шапка карты с переключателем Вертикальная / Панорама
        SanrioCityMapHeader(
            state = state,
            isPanorama = isPanorama,
            onTogglePanorama = { userPanoramaMode = !userPanoramaMode }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            val mapHeight = if (isPanorama) 760.dp else 1350.dp

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(mapHeight)
            ) {
                val leftX = if (isPanorama) (maxWidth * 0.06f) else 16.dp
                val rightX = if (isPanorama) (maxWidth * 0.74f) else (maxWidth - 125.dp).coerceAtLeast(160.dp)
                val centerX = (maxWidth - 110.dp) / 2f

                // 1. Огромный спрайт-фон всей локации города (изометрическая арт-карта)
                GameArt(
                    assetName = if (isPanorama) "map_city_town_horizontal" else "map_city_town_vertical",
                    fallbackEmoji = "🗺️",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Районные ориентиры-таблички
                SanrioDistrictLabel("⭐ Центральная площадь Финляндии", 14.dp)
                SanrioDistrictLabel("🏡 Улица верных друзей", if (isPanorama) 220.dp else 340.dp)

                // 3. ЗДАНИЯ ГОРОДА
                if (!isPanorama) {
                    // Вертикальная раскладка
                    SanrioMapPin(
                        x = leftX,
                        y = 65.dp,
                        assetName = "building_home",
                        emoji = "🏠",
                        title = "Мой дом",
                        badge = "ДОМОЙ",
                        badgeColor = SanrioAccentGreen,
                        onClick = { onSelectLocation(GameLocation.MyRoom) }
                    )

                    SanrioMapPin(
                        x = rightX,
                        y = 75.dp,
                        assetName = "building_bank",
                        emoji = "🏦",
                        title = "Банк",
                        badge = "${state.wallet.savings} м.",
                        badgeColor = SanrioAccentPurple,
                        onClick = { onSelectLocation(GameLocation.Bank) }
                    )

                    SanrioMapPin(
                        x = leftX + 10.dp,
                        y = 195.dp,
                        assetName = "building_shop",
                        emoji = "🛒",
                        title = "Магазин",
                        badge = "ЛАВКА",
                        badgeColor = SanrioAccentPink,
                        onClick = { onSelectLocation(GameLocation.Shop) }
                    )

                    SanrioMapPin(
                        x = rightX - 10.dp,
                        y = 205.dp,
                        assetName = "building_hospital",
                        emoji = "🏥",
                        title = "Больница",
                        badge = "ОСМОТР",
                        badgeColor = SanrioSkyBlueDark,
                        onClick = { onSelectLocation(GameLocation.Hospital) }
                    )

                    // Друзья вертикально (шахматный порядок вдоль дороги к реке)
                    val friendPositionsVertical = listOf(
                        Pair(leftX, 400.dp),
                        Pair(rightX, 490.dp),
                        Pair(leftX + 12.dp, 600.dp),
                        Pair(rightX - 12.dp, 710.dp),
                        Pair(leftX + 4.dp, 820.dp),
                        Pair(rightX - 4.dp, 930.dp),
                        Pair(centerX, 1050.dp)
                    )

                    GameCatalog.friendsList.forEachIndexed { index, friend ->
                        if (index < friendPositionsVertical.size) {
                            val (posX, posY) = friendPositionsVertical[index]
                            val puzzle = state.puzzles[friend.id]
                            val (badgeText, badgeColor) = when (puzzle?.state) {
                                PuzzleState.COMPLETED -> Pair("✓", SanrioAccentGreen)
                                PuzzleState.SOLVED_UNCLAIMED -> Pair("🪙", SanrioAccentOrange)
                                else -> Pair("+${friend.rewardCoins}", SanrioSkyBlueDark)
                            }

                            SanrioMapPin(
                                x = posX,
                                y = posY,
                                assetName = "friend_${friend.id}_portrait",
                                emoji = friend.emoji,
                                title = friend.name,
                                badge = badgeText,
                                badgeColor = badgeColor,
                                onAvatarClick = { onOpenFriendProfile(friend.id) },
                                onClick = { onSelectLocation(GameLocation.FriendRoom(friend.id)) }
                            )
                        }
                    }
                } else {
                    // Панорамная раскладка 16:9
                    SanrioMapPin(
                        x = maxWidth * 0.05f,
                        y = 60.dp,
                        assetName = "building_home",
                        emoji = "🏠",
                        title = "Мой дом",
                        badge = "ДОМОЙ",
                        badgeColor = SanrioAccentGreen,
                        onClick = { onSelectLocation(GameLocation.MyRoom) }
                    )

                    SanrioMapPin(
                        x = maxWidth * 0.28f,
                        y = 60.dp,
                        assetName = "building_bank",
                        emoji = "🏦",
                        title = "Банк",
                        badge = "${state.wallet.savings} м.",
                        badgeColor = SanrioAccentPurple,
                        onClick = { onSelectLocation(GameLocation.Bank) }
                    )

                    SanrioMapPin(
                        x = maxWidth * 0.54f,
                        y = 60.dp,
                        assetName = "building_shop",
                        emoji = "🛒",
                        title = "Магазин",
                        badge = "ЛАВКА",
                        badgeColor = SanrioAccentPink,
                        onClick = { onSelectLocation(GameLocation.Shop) }
                    )

                    SanrioMapPin(
                        x = maxWidth * 0.78f,
                        y = 60.dp,
                        assetName = "building_hospital",
                        emoji = "🏥",
                        title = "Больница",
                        badge = "ОСМОТР",
                        badgeColor = SanrioSkyBlueDark,
                        onClick = { onSelectLocation(GameLocation.Hospital) }
                    )

                    // Друзья в 2 панорамных ряда
                    val friendPositionsHorizontal = listOf(
                        Pair(maxWidth * 0.04f, 290.dp),
                        Pair(maxWidth * 0.28f, 290.dp),
                        Pair(maxWidth * 0.52f, 290.dp),
                        Pair(maxWidth * 0.76f, 290.dp),
                        Pair(maxWidth * 0.16f, 470.dp),
                        Pair(maxWidth * 0.40f, 470.dp),
                        Pair(maxWidth * 0.65f, 470.dp)
                    )

                    GameCatalog.friendsList.forEachIndexed { index, friend ->
                        if (index < friendPositionsHorizontal.size) {
                            val (posX, posY) = friendPositionsHorizontal[index]
                            val puzzle = state.puzzles[friend.id]
                            val (badgeText, badgeColor) = when (puzzle?.state) {
                                PuzzleState.COMPLETED -> Pair("✓", SanrioAccentGreen)
                                PuzzleState.SOLVED_UNCLAIMED -> Pair("🪙", SanrioAccentOrange)
                                else -> Pair("+${friend.rewardCoins}", SanrioSkyBlueDark)
                            }

                            SanrioMapPin(
                                x = posX,
                                y = posY,
                                assetName = "friend_${friend.id}_portrait",
                                emoji = friend.emoji,
                                title = friend.name,
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
    }
}

/**
 * Верхняя плашка карты: логотип, переключатель режима карты и баланс.
 */
@Composable
private fun SanrioCityMapHeader(
    state: GameState,
    isPanorama: Boolean,
    onTogglePanorama: () -> Unit
) {
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
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SanrioSkyBlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text("🗺️", fontSize = 20.sp)
            }
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Город Финляндия",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark
                )
                Text(
                    "Нажми на значок для перехода",
                    fontSize = 9.sp,
                    color = SanrioTextSubtitle
                )
            }

            // Переключатель режима карты (Вертикальная / Панорама)
            Surface(
                color = if (isPanorama) SanrioPurpleBg else SanrioGreenBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, if (isPanorama) SanrioAccentPurple else SanrioAccentGreen),
                modifier = Modifier.clickable(onClick = onTogglePanorama)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isPanorama) "🖥️ 16:9" else "📱 9:16",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPanorama) SanrioAccentPurple else SanrioAccentGreen
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            // Кошелёк: Монеты и сбережения
            Surface(
                color = SanrioGoldBg,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, SanrioGoldCoin)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GameArt("icon_paw_coin", fallbackEmoji = "🪙", modifier = Modifier.size(14.dp), fallbackSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${state.wallet.coins}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SanrioGoldText)
                    Spacer(modifier = Modifier.width(5.dp))
                    GameArt("jar_savings", fallbackEmoji = "🏺", modifier = Modifier.size(14.dp), fallbackSize = 11.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("${state.wallet.savings}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SanrioAccentPurple)
                }
            }
        }
    }
}

/**
 * Табличка названия района.
 */
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
 * Компактный интерактивный значок-булавка (Pin) на карте города.
 * Парящий круглый аватар с аккуратным бейджем. Не перекрывает соседние маркеры.
 */
@Composable
private fun BoxScope.SanrioMapPin(
    x: Dp,
    y: Dp,
    assetName: String,
    emoji: String,
    title: String,
    badge: String,
    badgeColor: Color,
    onAvatarClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = x, y = y)
            .clickable(onClick = onClick)
    ) {
        // Круглый парящий маркер с яркой каймой
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(2.5.dp, badgeColor),
            shadowElevation = 5.dp,
            modifier = Modifier
                .size(54.dp)
                .then(
                    if (onAvatarClick != null) Modifier.clickable(onClick = onAvatarClick)
                    else Modifier
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                GameArt(
                    assetName = assetName,
                    fallbackEmoji = emoji,
                    modifier = Modifier.size(46.dp),
                    fallbackSize = 30.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Компактный бейдж-табличка с названием и статусом
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.95f),
            border = BorderStroke(1.2.dp, badgeColor.copy(alpha = 0.4f)),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark
                )
                Spacer(modifier = Modifier.width(3.dp))
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        badge,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
