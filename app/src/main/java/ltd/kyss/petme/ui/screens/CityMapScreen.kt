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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
 * Карта Города Финляндия в стиле Hello Kitty Island Adventure / Sanrio 3D.
 * Удобный, понятный для ребёнка интерфейс с крупными целями нажатия:
 * 1. Вертикальный режим (9:16) — упрощённая, доступная для пальцев ребёнка навигация.
 * 2. Панорамный режим (16:9) — живописная изометрическая арт-карта всего поселения.
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
        // Шапка карты с переключателем режима
        SanrioCityMapHeader(
            state = state,
            isPanorama = isPanorama,
            onTogglePanorama = { userPanoramaMode = !userPanoramaMode }
        )

        if (!isPanorama) {
            // =========================================================
            // 1. УПРОЩЁННАЯ ВЕРТИКАЛЬНАЯ КАРТА ДЛЯ ДЕТЕЙ (9:16)
            // =========================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Мягкий фон города (пастельная изометрическая арт-карта)
                GameArt(
                    assetName = "map_city_town_vertical",
                    fallbackEmoji = "🗺️",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Полупрозрачный рассеивающий градиент для идеальной читаемости текста
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.20f),
                                    Color.White.copy(alpha = 0.45f),
                                    Color.White.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    // Секция 1: Главные здания города (крупная сетка 2x2 для пальцев ребёнка)
                    SanrioSectionTitle("⭐ Главные места города")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KidFacilityCard(
                            modifier = Modifier.weight(1f),
                            title = "Мой дом",
                            subtitle = "Питомец ждёт",
                            assetName = "building_home",
                            emoji = "🏠",
                            badgeText = "ДОМОЙ",
                            badgeColor = SanrioAccentGreen,
                            onClick = { onSelectLocation(GameLocation.MyRoom) }
                        )

                        KidFacilityCard(
                            modifier = Modifier.weight(1f),
                            title = "Банк",
                            subtitle = "Сейф и копилка",
                            assetName = "building_bank",
                            emoji = "🏦",
                            badgeText = "${state.wallet.savings} м.",
                            badgeColor = SanrioAccentPurple,
                            onClick = { onSelectLocation(GameLocation.Bank) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        KidFacilityCard(
                            modifier = Modifier.weight(1f),
                            title = "Магазин",
                            subtitle = "Еда и наряды",
                            assetName = "building_shop",
                            emoji = "🛒",
                            badgeText = "ЛАВКА",
                            badgeColor = SanrioAccentPink,
                            onClick = { onSelectLocation(GameLocation.Shop) }
                        )

                        KidFacilityCard(
                            modifier = Modifier.weight(1f),
                            title = "Больница",
                            subtitle = "Доктор Сова",
                            assetName = "building_hospital",
                            emoji = "🏥",
                            badgeText = "ОСМОТР",
                            badgeColor = SanrioSkyBlueDark,
                            onClick = { onSelectLocation(GameLocation.Hospital) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Секция 2: Домики верных друзей
                    SanrioSectionTitle("🏡 Домики верных друзей")

                    GameCatalog.friendsList.forEach { friend ->
                        val puzzle = state.puzzles[friend.id]
                        val (badgeText, badgeColor) = when (puzzle?.state) {
                            PuzzleState.COMPLETED -> Pair("ГОТОВО ✓", SanrioAccentGreen)
                            PuzzleState.SOLVED_UNCLAIMED -> Pair("НАГРАДА 🪙", SanrioAccentOrange)
                            else -> Pair("+${friend.rewardCoins} м.", SanrioSkyBlueDark)
                        }

                        KidFriendRowCard(
                            friend = friend,
                            badgeText = badgeText,
                            badgeColor = badgeColor,
                            onAvatarClick = { onOpenFriendProfile(friend.id) },
                            onClick = { onSelectLocation(GameLocation.FriendRoom(friend.id)) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // =========================================================
            // 2. ПАНОРАМНАЯ 16:9 КАРТА С ПИНАМИ
            // =========================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(760.dp)
                ) {
                    // Огромный фон панорамы города
                    GameArt(
                        assetName = "map_city_town_horizontal",
                        fallbackEmoji = "🗺️",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Районные таблички
                    SanrioDistrictPill("⭐ Центральная площадь", maxWidth * 0.35f, 16.dp)
                    SanrioDistrictPill("🏡 Улица друзей", maxWidth * 0.38f, 220.dp)

                    // 4 Главных здания
                    SanrioPanoramaPin(
                        x = maxWidth * 0.05f, y = 60.dp,
                        title = "Мой дом", badge = "ДОМОЙ", badgeColor = SanrioAccentGreen,
                        assetName = "building_home", emoji = "🏠",
                        onClick = { onSelectLocation(GameLocation.MyRoom) }
                    )

                    SanrioPanoramaPin(
                        x = maxWidth * 0.28f, y = 60.dp,
                        title = "Банк", badge = "${state.wallet.savings} м.", badgeColor = SanrioAccentPurple,
                        assetName = "building_bank", emoji = "🏦",
                        onClick = { onSelectLocation(GameLocation.Bank) }
                    )

                    SanrioPanoramaPin(
                        x = maxWidth * 0.54f, y = 60.dp,
                        title = "Магазин", badge = "ЛАВКА", badgeColor = SanrioAccentPink,
                        assetName = "building_shop", emoji = "🛒",
                        onClick = { onSelectLocation(GameLocation.Shop) }
                    )

                    SanrioPanoramaPin(
                        x = maxWidth * 0.78f, y = 60.dp,
                        title = "Больница", badge = "ОСМОТР", badgeColor = SanrioSkyBlueDark,
                        assetName = "building_hospital", emoji = "🏥",
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

                            SanrioPanoramaPin(
                                x = posX, y = posY,
                                title = friend.name, badge = badgeText, badgeColor = badgeColor,
                                assetName = "friend_${friend.id}_portrait", emoji = friend.emoji,
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
 * Крупная карточка здания для вертикального детского интерфейса (Sanrio Squircle).
 */
@Composable
private fun KidFacilityCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    assetName: String,
    emoji: String,
    badgeText: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, badgeColor.copy(alpha = 0.45f)),
        shadowElevation = 5.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF0F8FF)),
                contentAlignment = Alignment.Center
            ) {
                GameArt(
                    assetName = assetName,
                    fallbackEmoji = emoji,
                    modifier = Modifier.size(54.dp),
                    fallbackSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SanrioTextDark,
                maxLines = 1
            )
            Text(
                subtitle,
                fontSize = 9.5.sp,
                color = SanrioTextSubtitle,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    badgeText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Удобная строка-карточка друга для детских пальцев.
 */
@Composable
private fun KidFriendRowCard(
    friend: ltd.kyss.petme.core.model.FriendCharacter,
    badgeText: String,
    badgeColor: Color,
    onAvatarClick: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, SanrioCardBorder),
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Крупный круглый аватар друга (при нажатии открывает карточку Hello Kitty)
            Surface(
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(2.5.dp, badgeColor),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .size(54.dp)
                    .clickable(onClick = onAvatarClick)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    GameArt(
                        assetName = "friend_${friend.id}_portrait",
                        fallbackEmoji = friend.emoji,
                        modifier = Modifier.size(46.dp),
                        fallbackSize = 32.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Имя друга и название домика
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        friend.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SanrioTextDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        friend.emoji,
                        fontSize = 13.sp
                    )
                }
                Text(
                    friend.houseName,
                    fontSize = 11.sp,
                    color = SanrioTextSubtitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Бейдж награды / готовности
            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/**
 * Заголовок секции карты.
 */
@Composable
private fun SanrioSectionTitle(title: String) {
    Surface(
        color = Color.White.copy(alpha = 0.90f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, SanrioCardBorder),
        shadowElevation = 2.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SanrioTextDark,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
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
        color = Color.White.copy(alpha = 0.98f),
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
                    "Выбирай, к кому пойти в гости!",
                    fontSize = 9.5.sp,
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
 * Табличка района на панораме.
 */
@Composable
private fun BoxScope.SanrioDistrictPill(title: String, x: Dp, y: Dp) {
    Surface(
        modifier = Modifier.offset(x = x, y = y),
        color = Color.White.copy(alpha = 0.95f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, SanrioCardBorder),
        shadowElevation = 3.dp
    ) {
        Text(
            title,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = SanrioTextDark
        )
    }
}

/**
 * Панорамная булавка на карте (16:9).
 */
@Composable
private fun BoxScope.SanrioPanoramaPin(
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
        Surface(
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(2.5.dp, badgeColor),
            shadowElevation = 5.dp,
            modifier = Modifier
                .size(52.dp)
                .then(
                    if (onAvatarClick != null) Modifier.clickable(onClick = onAvatarClick)
                    else Modifier
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                GameArt(
                    assetName = assetName,
                    fallbackEmoji = emoji,
                    modifier = Modifier.size(44.dp),
                    fallbackSize = 28.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

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
                    fontSize = 9.5.sp,
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
