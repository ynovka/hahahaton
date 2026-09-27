package ltd.kyss.petme.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Интерактивная карта Города Финляндия в стиле Hello Kitty Island Adventure.
 * Полностью соответствует эскизу автора:
 * - Сверху слева: Аватар игрока + текущее время суток ☀️
 * - Сверху справа: Монеты 🪙 + Копилка 🏺 + переключатель режима 📱/🖥️
 * - Поверх домов: Круглые интерактивные бейджи-аватарки жителей и зданий с мягкой пульсацией
 * - Снизу: Удобный детский хотбар (Домой, Банк, Лавка, Клиника, Наряды, Бюджет, Опции)
 * - Поддерживает как вертикальный (9:16), так и горизонтальный (16:9) режим.
 */
@Composable
fun CityMapScreen(
    state: GameState,
    onSelectLocation: (GameLocation) -> Unit,
    onOpenFriendProfile: (Int) -> Unit = {},
    onOpenFriendDialogue: (Int) -> Unit = {},
    onOpenWardrobe: () -> Unit = {},
    onOpenBudget: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isSystemLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    var userPanoramaMode by remember { mutableStateOf(false) }
    val isPanorama = isSystemLandscape || userPanoramaMode

    // Текущее время для детского интерфейса (например 10:30 ☀️)
    val currentTimeStr = remember {
        try {
            val now = LocalTime.now()
            val hour = now.hour
            val sunMoon = if (hour in 7..19) "☀️" else "🌙"
            "${now.format(DateTimeFormatter.ofPattern("HH:mm"))} $sunMoon"
        } catch (e: Exception) {
            "12:00 ☀️"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SanrioSkyBlueLight)
    ) {
        if (!isPanorama) {
            // =========================================================
            // 1. ВЕРТИКАЛЬНАЯ АРТ-КАРТА (9:16) С КРУГЛЫМИ ПИНАМИ НАД ДОМАМИ
            // =========================================================
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val screenW = maxWidth
                val screenH = maxHeight

                // Фоновая живописная изометрическая карта города
                GameArt(
                    assetName = "map_city_town_vertical",
                    fallbackEmoji = "🗺️",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // -----------------------------------------------------
                // Интерактивные кружки НАД зданиями (строго по стрелкам автора)
                // -----------------------------------------------------

                // 1. Друг 2: Белочка Рыжик (домик на дереве)
                SanrioAvatarMapPin(
                    x = screenW * 0.072f,
                    y = screenH * 0.400f,
                    title = "Рыжик",
                    emoji = "🐿️",
                    assetName = "friend_2_portrait",
                    borderColor = SanrioAccentOrange,
                    badgeText = getFriendBadge(state, 2),
                    badgeColor = getFriendBadgeColor(state, 2),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(2)) }
                )

                // 2. Друг 1: Медвежонок Потап (крыша розового коттеджа)
                SanrioAvatarMapPin(
                    x = screenW * 0.093f,
                    y = screenH * 0.261f,
                    title = "Потап",
                    emoji = "🐻",
                    assetName = "friend_1_portrait",
                    borderColor = SanrioAccentPink,
                    badgeText = getFriendBadge(state, 1),
                    badgeColor = getFriendBadgeColor(state, 1),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(1)) }
                )

                // 3. Друг 3: Енотик Тёма (крыша верхнего коттеджа)
                SanrioAvatarMapPin(
                    x = screenW * 0.682f,
                    y = screenH * 0.233f,
                    title = "Тёма",
                    emoji = "🦝",
                    assetName = "friend_3_portrait",
                    borderColor = SanrioAccentPurple,
                    badgeText = getFriendBadge(state, 3),
                    badgeColor = getFriendBadgeColor(state, 3),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(3)) }
                )

                // 4. Друг 4: Совушка София (крыша золотистого коттеджа)
                SanrioAvatarMapPin(
                    x = screenW * 0.915f,
                    y = screenH * 0.274f,
                    title = "София",
                    emoji = "🦉",
                    assetName = "friend_4_portrait",
                    borderColor = SanrioAccentYellow,
                    badgeText = getFriendBadge(state, 4),
                    badgeColor = getFriendBadgeColor(state, 4),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(4)) }
                )

                // 5. Друг 6: Зайчик Сеня (крыша сиреневого домика)
                SanrioAvatarMapPin(
                    x = screenW * 0.315f,
                    y = screenH * 0.332f,
                    title = "Сеня",
                    emoji = "🐰",
                    assetName = "friend_6_portrait",
                    borderColor = SanrioAccentGreen,
                    badgeText = getFriendBadge(state, 6),
                    badgeColor = getFriendBadgeColor(state, 6),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(6)) }
                )

                // 6. Магазин вкусностей (козырёк лавки)
                SanrioAvatarMapPin(
                    x = screenW * 0.346f,
                    y = screenH * 0.440f,
                    title = "Лавка",
                    emoji = "🛒",
                    assetName = "building_shop",
                    borderColor = SanrioAccentPink,
                    badgeText = "ЛАВКА",
                    badgeColor = SanrioAccentPink,
                    onClick = { onSelectLocation(GameLocation.Shop) }
                )

                // 7. Городской Банк (крыша здания банка)
                SanrioAvatarMapPin(
                    x = screenW * 0.739f,
                    y = screenH * 0.412f,
                    title = "Банк",
                    emoji = "🏦",
                    assetName = "building_bank",
                    borderColor = SanrioAccentYellow,
                    badgeText = "${state.wallet.savings} м.",
                    badgeColor = SanrioAccentPurple,
                    onClick = { onSelectLocation(GameLocation.Bank) }
                )

                // 8. Больница / Клиника Заботы (над крышей клиники)
                SanrioAvatarMapPin(
                    x = screenW * 0.870f,
                    y = screenH * 0.520f,
                    title = "Клиника",
                    emoji = "🏥",
                    assetName = "building_hospital",
                    borderColor = SanrioSkyBlueDark,
                    badgeText = "ОСМОТР",
                    badgeColor = SanrioSkyBlueDark,
                    onClick = { onSelectLocation(GameLocation.Hospital) }
                )

                // 9. Друг 5: Лисичка Алиса (крыша домика-норы)
                SanrioAvatarMapPin(
                    x = screenW * 0.718f,
                    y = screenH * 0.656f,
                    title = "Алиса",
                    emoji = "🦊",
                    assetName = "friend_5_portrait",
                    borderColor = SanrioAccentOrange,
                    badgeText = getFriendBadge(state, 5),
                    badgeColor = getFriendBadgeColor(state, 5),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(5)) }
                )

                // 10. Друг 7: Щенок Барбос (над сиреневым коттеджем внизу справа)
                SanrioAvatarMapPin(
                    x = screenW * 0.852f,
                    y = screenH * 0.738f,
                    title = "Барбос",
                    emoji = "🐶",
                    assetName = "friend_7_portrait",
                    borderColor = SanrioAccentPurple,
                    badgeText = getFriendBadge(state, 7),
                    badgeColor = getFriendBadgeColor(state, 7),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(7)) }
                )

                // 11. Мой Дом (над домиком героя у реки)
                SanrioAvatarMapPin(
                    x = screenW * 0.220f,
                    y = screenH * 0.755f,
                    title = "Мой дом",
                    emoji = "🏠",
                    assetName = "building_home",
                    borderColor = SanrioAccentGreen,
                    badgeText = "ДОМОЙ",
                    badgeColor = SanrioAccentGreen,
                    onClick = { onSelectLocation(GameLocation.MyRoom) }
                )
            }
        } else {
            // =========================================================
            // 2. ГОРИЗОНТАЛЬНАЯ ПАНОРАМНАЯ КАРТА (16:9) С ПИНАМИ
            // =========================================================
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val screenW = maxWidth
                val screenH = maxHeight

                // Панорамный широкий арт города
                GameArt(
                    assetName = "map_city_town_horizontal",
                    fallbackEmoji = "🗺️",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 1. Белочка Рыжик (НАД домиком на дубе)
                SanrioAvatarMapPin(
                    x = screenW * 0.16f, y = screenH * 0.10f,
                    title = "Рыжик", emoji = "🐿️", assetName = "friend_2_portrait",
                    borderColor = SanrioAccentOrange, badgeText = getFriendBadge(state, 2), badgeColor = getFriendBadgeColor(state, 2),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(2)) }
                )

                // 2. Магазин / Лавка покупок
                SanrioAvatarMapPin(
                    x = screenW * 0.33f, y = screenH * 0.27f,
                    title = "Лавка", emoji = "🛒", assetName = "building_shop",
                    borderColor = SanrioAccentPink, badgeText = "ЛАВКА", badgeColor = SanrioAccentPink,
                    onClick = { onSelectLocation(GameLocation.Shop) }
                )

                // 3. Городской Банк
                SanrioAvatarMapPin(
                    x = screenW * 0.50f, y = screenH * 0.19f,
                    title = "Банк", emoji = "🏦", assetName = "building_bank",
                    borderColor = SanrioAccentYellow, badgeText = "${state.wallet.savings} м.", badgeColor = SanrioAccentPurple,
                    onClick = { onSelectLocation(GameLocation.Bank) }
                )

                // 4. Клиника Заботы
                SanrioAvatarMapPin(
                    x = screenW * 0.67f, y = screenH * 0.29f,
                    title = "Клиника", emoji = "🏥", assetName = "building_hospital",
                    borderColor = SanrioSkyBlueDark, badgeText = "ОСМОТР", badgeColor = SanrioSkyBlueDark,
                    onClick = { onSelectLocation(GameLocation.Hospital) }
                )

                // 5. Медвежонок Потап
                SanrioAvatarMapPin(
                    x = screenW * 0.18f, y = screenH * 0.47f,
                    title = "Потап", emoji = "🐻", assetName = "friend_1_portrait",
                    borderColor = SanrioAccentPink, badgeText = getFriendBadge(state, 1), badgeColor = getFriendBadgeColor(state, 1),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(1)) }
                )

                // 6. Енотик Тёма
                SanrioAvatarMapPin(
                    x = screenW * 0.10f, y = screenH * 0.57f,
                    title = "Тёма", emoji = "🦝", assetName = "friend_3_portrait",
                    borderColor = SanrioAccentPurple, badgeText = getFriendBadge(state, 3), badgeColor = getFriendBadgeColor(state, 3),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(3)) }
                )

                // 7. Зайчик Сеня
                SanrioAvatarMapPin(
                    x = screenW * 0.33f, y = screenH * 0.63f,
                    title = "Сеня", emoji = "🐰", assetName = "friend_6_portrait",
                    borderColor = SanrioAccentGreen, badgeText = getFriendBadge(state, 6), badgeColor = getFriendBadgeColor(state, 6),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(6)) }
                )

                // 8. Лисичка Алиса
                SanrioAvatarMapPin(
                    x = screenW * 0.42f, y = screenH * 0.65f,
                    title = "Алиса", emoji = "🦊", assetName = "friend_5_portrait",
                    borderColor = SanrioAccentOrange, badgeText = getFriendBadge(state, 5), badgeColor = getFriendBadgeColor(state, 5),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(5)) }
                )

                // 9. Совушка София
                SanrioAvatarMapPin(
                    x = screenW * 0.88f, y = screenH * 0.49f,
                    title = "София", emoji = "🦉", assetName = "friend_4_portrait",
                    borderColor = SanrioAccentYellow, badgeText = getFriendBadge(state, 4), badgeColor = getFriendBadgeColor(state, 4),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(4)) }
                )

                // 10. Щенок Барбос
                SanrioAvatarMapPin(
                    x = screenW * 0.72f, y = screenH * 0.73f,
                    title = "Барбос", emoji = "🐶", assetName = "friend_7_portrait",
                    borderColor = SanrioAccentPurple, badgeText = getFriendBadge(state, 7), badgeColor = getFriendBadgeColor(state, 7),
                    onClick = { onSelectLocation(GameLocation.FriendRoom(7)) }
                )

                // 11. Мой Дом (Коттедж у реки)
                SanrioAvatarMapPin(
                    x = screenW * 0.88f, y = screenH * 0.75f,
                    title = "Мой дом", emoji = "🏠", assetName = "building_home",
                    borderColor = SanrioAccentGreen, badgeText = "ДОМОЙ", badgeColor = SanrioAccentGreen,
                    onClick = { onSelectLocation(GameLocation.MyRoom) }
                )
            }
        }

        // =============================================================
        // 3. ВЕРХНИЙ ИНТЕРФЕЙС (ПЛАВАЮЩИЙ, В СТИЛЕ ЭСКИЗА АВТОРА)
        // =============================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // СЛЕВА (Фиолетовый овал в наброске): Аватар игрока + Время суток
            SanrioPlayerTimeCard(
                timeText = currentTimeStr,
                petSpecies = state.pet.species.id,
                onClick = { onSelectLocation(GameLocation.MyRoom) }
            )

            // СПРАВА (Голубое облачко в наброске): Монеты + Копилка + Режим
            SanrioCurrenciesCard(
                coins = state.wallet.coins,
                savings = state.wallet.savings,
                isPanorama = isPanorama,
                onTogglePanorama = { userPanoramaMode = !userPanoramaMode }
            )
        }

        // =============================================================
        // 4. НИЖНИЙ ХОТБАР (ЗЕЛЁНЫЙ ОВАЛ В НАБРОСКЕ АВТОРА)
        // =============================================================
        SanrioBottomHotbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            onHomeClick = { onSelectLocation(GameLocation.MyRoom) },
            onBankClick = { onSelectLocation(GameLocation.Bank) },
            onShopClick = { onSelectLocation(GameLocation.Shop) },
            onHospitalClick = { onSelectLocation(GameLocation.Hospital) },
            onWardrobeClick = onOpenWardrobe,
            onBudgetClick = onOpenBudget,
            onSettingsClick = onOpenSettings
        )
    }
}

/**
 * Круглый интерактивный пин с аватаркой над домиком в стиле Sanrio 3D.
 */
@Composable
private fun SanrioAvatarMapPin(
    x: Dp,
    y: Dp,
    title: String,
    emoji: String,
    assetName: String,
    borderColor: Color,
    badgeText: String? = null,
    badgeColor: Color = SanrioAccentGreen,
    onClick: () -> Unit,
    onAvatarClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pinScale"
    )

    // Мягкое парение над крышей
    val infiniteTransition = rememberInfiniteTransition(label = "pinFloat")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pinFloatY"
    )

    Box(
        modifier = Modifier
            .offset(x = x - 32.dp, y = y - 36.dp + offsetY.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Круглый аватар персонажа или здания
            Box(
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(3.dp, borderColor),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .size(54.dp)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (onAvatarClick != null) onAvatarClick() else onClick()
                        }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        GameArt(
                            assetName = assetName,
                            fallbackEmoji = emoji,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                // Маленький бейджик готовности квеста / награды
                if (!badgeText.isNullOrBlank()) {
                    Surface(
                        color = badgeColor,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.5.dp, Color.White),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-4).dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Маленькая читаемая табличка с именем домика
            Surface(
                color = Color.White.copy(alpha = 0.94f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, borderColor.copy(alpha = 0.6f)),
                shadowElevation = 3.dp
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Верхняя левая карточка (Аватар игрока + текущее время суток).
 * В эскизе автора: фиолетовый овал в верхнем левом углу.
 */
@Composable
private fun SanrioPlayerTimeCard(
    timeText: String,
    petSpecies: String,
    onClick: () -> Unit = {}
) {
    Surface(
        color = Color.White.copy(alpha = 0.94f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(2.dp, SanrioSkyBlueLight),
        shadowElevation = 6.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            // Аватар игрока/питомца
            Surface(
                shape = CircleShape,
                color = SanrioMintSoft,
                border = BorderStroke(2.dp, SanrioAccentOrange),
                modifier = Modifier.size(38.dp)
            ) {
                GameArt(
                    assetName = "pet_${petSpecies.lowercase()}_portrait",
                    fallbackEmoji = "🐱",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))

            Column {
                Text(
                    text = "Финни и Я",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SanrioTextDark
                )
                Text(
                    text = timeText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SanrioAccentOrange
                )
            }
        }
    }
}

/**
 * Верхняя правая карточка (Валюты: монеты и копилка + переключатель режима).
 * В эскизе автора: голубое облачко в верхнем правом углу.
 */
@Composable
private fun SanrioCurrenciesCard(
    coins: Int,
    savings: Int,
    isPanorama: Boolean,
    onTogglePanorama: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        color = Color.White.copy(alpha = 0.94f),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(2.dp, SanrioSkyBlueLight),
        shadowElevation = 6.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Монеты в кошельке
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🪙", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$coins",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SanrioTextDark
                )
            }

            // Копилка в банке
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🏺", fontSize = 15.sp)
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "$savings",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SanrioAccentPurple
                )
            }

            // Кнопка переключения вида карты 📱 / 🖥️
            Surface(
                color = if (isPanorama) SanrioAccentPurple else SanrioSkyBlueDark,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTogglePanorama()
                }
            ) {
                Text(
                    text = if (isPanorama) "🖥️ 16:9" else "📱 9:16",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Детский нижний хотбар (зелёный овал в наброске автора).
 * Понятные яркие кнопки для быстрого доступа ко всем основным локациям и меню.
 */
@Composable
private fun SanrioBottomHotbar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit,
    onBankClick: () -> Unit,
    onShopClick: () -> Unit,
    onHospitalClick: () -> Unit,
    onWardrobeClick: () -> Unit,
    onBudgetClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(2.dp, SanrioSkyBlueLight),
        shadowElevation = 10.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HotbarButton(emoji = "🏠", label = "Домой", color = SanrioAccentGreen, onClick = onHomeClick)
            HotbarButton(emoji = "🏦", label = "Банк", color = SanrioAccentYellow, onClick = onBankClick)
            HotbarButton(emoji = "🛒", label = "Лавка", color = SanrioAccentPink, onClick = onShopClick)
            HotbarButton(emoji = "🏥", label = "Клиника", color = SanrioSkyBlueDark, onClick = onHospitalClick)
            HotbarButton(emoji = "👗", label = "Наряды", color = SanrioAccentPurple, onClick = onWardrobeClick)
            HotbarButton(emoji = "🎒", label = "Копилка", color = SanrioAccentOrange, onClick = onBudgetClick)
            HotbarButton(emoji = "⚙️", label = "Опции", color = SanrioTextMuted, onClick = onSettingsClick)
        }
    }
}

@Composable
private fun HotbarButton(
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.82f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "hotbarBtnScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = color.copy(alpha = 0.16f),
            border = BorderStroke(1.5.dp, color.copy(alpha = 0.5f)),
            modifier = Modifier.size(38.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = emoji, fontSize = 20.sp)
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = SanrioTextDark,
            textAlign = TextAlign.Center
        )
    }
}

private fun getFriendBadge(state: GameState, friendId: Int): String? {
    val puzzle = state.puzzles[friendId]
    return when (puzzle?.state) {
        PuzzleState.COMPLETED -> "✓"
        PuzzleState.SOLVED_UNCLAIMED -> "🪙"
        else -> "+20"
    }
}

private fun getFriendBadgeColor(state: GameState, friendId: Int): Color {
    val puzzle = state.puzzles[friendId]
    return when (puzzle?.state) {
        PuzzleState.COMPLETED -> SanrioAccentGreen
        PuzzleState.SOLVED_UNCLAIMED -> SanrioAccentOrange
        else -> SanrioSkyBlueDark
    }
}
