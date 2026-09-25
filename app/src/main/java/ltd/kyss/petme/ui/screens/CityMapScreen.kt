package ltd.kyss.petme.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.GameLocation
import ltd.kyss.petme.core.model.PuzzleState
import kotlin.math.roundToInt

/**
 * Интерактивная 2D карта города с плавным перемещением пальцем (drag/pan),
 * масштабированием двумя пальцами (pinch-to-zoom), двойным тапом
 * и многослойным эффектом параллакса для отдельных домов (2.5D перспектива улиц и наклон крыш).
 */
@Composable
fun CityMapScreen(
    state: GameState,
    onSelectLocation: (GameLocation) -> Unit,
    onOpenFriendProfile: (Int) -> Unit = {}
) {
    val density = LocalDensity.current

    // Масштаб карты (от 0.6x до 2.0x)
    var rawScale by remember { mutableFloatStateOf(1.0f) }
    // Смещение камеры карты
    var rawOffset by remember { mutableStateOf(Offset.Zero) }

    // Размеры экрана для расчета границ прокрутки
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    // Размеры игрового мира города в dp (просторная карта 1350 x 1050 dp)
    val mapWidthDp = 1350.dp
    val mapHeightDp = 1050.dp
    val mapWidthPx = with(density) { mapWidthDp.toPx() }
    val mapHeightPx = with(density) { mapHeightDp.toPx() }

    // Плавная анимация зума и смещения для кинематографичной мягкости
    val animatedScale by animateFloatAsState(
        targetValue = rawScale,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 550f),
        label = "scaleAnim"
    )
    val animatedOffset by animateOffsetAsState(
        targetValue = rawOffset,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 550f),
        label = "offsetAnim"
    )

    // Анимация фонового покачивания бейджей и дыма (idle breath)
    val infiniteTransition = rememberInfiniteTransition(label = "mapIdleBreath")
    val idleBounce by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleBounce"
    )
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudDrift"
    )

    // Функция центровки камеры на конкретную точку мира (xDp, yDp)
    fun focusOn(targetXDp: Dp, targetYDp: Dp, targetZoom: Float = 1.25f) {
        val centerXDp = mapWidthDp / 2
        val centerYDp = mapHeightDp / 2
        val targetOffsetX = with(density) { (centerXDp - targetXDp).toPx() } * targetZoom
        val targetOffsetY = with(density) { (centerYDp - targetYDp).toPx() } * targetZoom

        val boundX = ((mapWidthPx * targetZoom - viewportSize.width).coerceAtLeast(0f) / 2f) + 150f
        val boundY = ((mapHeightPx * targetZoom - viewportSize.height).coerceAtLeast(0f) / 2f) + 150f

        rawScale = targetZoom
        rawOffset = Offset(
            x = targetOffsetX.coerceIn(-boundX, boundX),
            y = targetOffsetY.coerceIn(-boundY, boundY)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF81D4FA)) // Небо
            .onSizeChanged { viewportSize = it }
            // Жест двойного тапа для быстрого приближения/отдаления
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        if (rawScale > 1.2f) {
                            rawScale = 0.95f
                        } else {
                            rawScale = 1.6f
                        }
                    }
                )
            }
            // Мультитач: перемещение одним пальцем и масштабирование двумя пальцами (pinch-to-zoom)
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    val newScale = (rawScale * zoom).coerceIn(0.60f, 2.05f)
                    rawScale = newScale

                    // Безопасные динамические границы скролла с запасом
                    val maxBoundX = ((mapWidthPx * newScale - viewportSize.width).coerceAtLeast(0f) / 2f) + 200f
                    val maxBoundY = ((mapHeightPx * newScale - viewportSize.height).coerceAtLeast(0f) / 2f) + 200f

                    rawOffset = Offset(
                        x = (rawOffset.x + pan.x).coerceIn(-maxBoundX, maxBoundX),
                        y = (rawOffset.y + pan.y).coerceIn(-maxBoundY, maxBoundY)
                    )
                }
            }
    ) {
        // ==========================================
        // СЛОЙ 1: ПАРАЛЛАКС ДАЛЬНЕГО ФОНА (Облака, солнце)
        // Скорость смещения 0.15x (кажется бесконечно далеким)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffset.x * 0.15f + cloudDrift * 0.3f
                    translationY = animatedOffset.y * 0.15f
                }
        ) {
            DistantCloudsLayer()
        }

        // ==========================================
        // СЛОЙ 2: ПАРАЛЛАКС СРЕДНЕГО ПЛАНА (Горы и холмы)
        // Скорость смещения 0.40x
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffset.x * 0.40f
                    translationY = animatedOffset.y * 0.40f
                }
        ) {
            MidgroundHillsLayer()
        }

        // ==========================================
        // СЛОЙ 3: ОСНОВНАЯ КАРТА ГОРОДА (Земля, дороги, здания)
        // Полное смещение 1.0x со скейлом
        // ==========================================
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(mapWidthDp, mapHeightDp)
                .graphicsLayer {
                    translationX = animatedOffset.x
                    translationY = animatedOffset.y
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7), Color(0xFF81C784))
                    ),
                    shape = RoundedCornerShape(48.dp)
                )
                .border(8.dp, Color(0xFF388E3C), RoundedCornerShape(48.dp))
        ) {
            // Дорожная сеть, клумбы, аллеи и фонтан
            CityRoadsOverlay()

            // ----------------------------------------------------
            // 1. СВОЯ КВАРТИРА (Мой Дом) - уютный левый район
            // ----------------------------------------------------
            BuildingNode(
                xDp = 220.dp,
                yDp = 460.dp,
                title = "Мой Дом",
                subtitle = "Твоя комната",
                emoji = "🏠",
                badge = "Ты здесь ✨",
                badgeColor = Color(0xFF2E7D32),
                containerColor = Color(0xFFFFF9C4),
                isPlayerHome = true,
                cameraOffset = animatedOffset,
                idleBounceOffset = idleBounce,
                onClick = { onSelectLocation(GameLocation.MyRoom) }
            )

            // ----------------------------------------------------
            // 2. БАНК ГОРОДА (Бобёр-банкир) - Северная площадь
            // ----------------------------------------------------
            BuildingNode(
                xDp = 650.dp,
                yDp = 170.dp,
                title = "Банк",
                subtitle = "Копилка и уроки",
                emoji = "🏦",
                badge = "Копилка 🏺",
                badgeColor = Color(0xFF7E57C2),
                containerColor = Color(0xFFEDE7F6),
                cameraOffset = animatedOffset,
                idleBounceOffset = idleBounce,
                onClick = { onSelectLocation(GameLocation.Bank) }
            )

            // ----------------------------------------------------
            // 3. МАГАЗИН (Лавка покупок) - Центральный проспект
            // ----------------------------------------------------
            BuildingNode(
                xDp = 940.dp,
                yDp = 460.dp,
                title = "Лавка",
                subtitle = "Корм и гардероб",
                emoji = "🛒",
                badge = "Товары 🛍️",
                badgeColor = Color(0xFFFB8C00),
                containerColor = Color(0xFFFFE0B2),
                cameraOffset = animatedOffset,
                idleBounceOffset = idleBounce,
                onClick = { onSelectLocation(GameLocation.Shop) }
            )

            // ----------------------------------------------------
            // 4. БОЛЬНИЦА (Лечебница Доктора Совы) - Парковый квартал
            // ----------------------------------------------------
            BuildingNode(
                xDp = 1050.dp,
                yDp = 180.dp,
                title = "Больница",
                subtitle = "Осмотр питомца",
                emoji = "🏥",
                badge = "Здоровье ❤️",
                badgeColor = Color(0xFFE53935),
                containerColor = Color(0xFFFFCDD2),
                cameraOffset = animatedOffset,
                idleBounceOffset = idleBounce,
                onClick = { onSelectLocation(GameLocation.Hospital) }
            )

            // ----------------------------------------------------
            // 5. ДОМА 7 ДРУЗЕЙ (Расставлены по улицам города)
            // ----------------------------------------------------
            val friendCoordinates = listOf(
                Pair(180.dp, 170.dp),  // Друг 1 (Мишка) - Северная опушка
                Pair(420.dp, 150.dp),  // Друг 2 (Белочка) - Дубовая роща
                Pair(1140.dp, 460.dp), // Друг 3 (Енотик) - Ремесленный угол
                Pair(1080.dp, 780.dp), // Друг 4 (Совушка) - Тихая поляна
                Pair(780.dp, 790.dp),  // Друг 5 (Лисичка) - Солнечная горка
                Pair(480.dp, 800.dp),  // Друг 6 (Зайка) - Морковный сад
                Pair(190.dp, 760.dp)   // Друг 7 (Щенок) - Игровой двор
            )

            GameCatalog.friendsList.forEachIndexed { index, friend ->
                val (posX, posY) = friendCoordinates.getOrElse(index) { Pair(200.dp, 200.dp) }
                val puzzle = state.puzzles[friend.id]

                val statusBadge = when (puzzle?.state) {
                    PuzzleState.COMPLETED -> "✅ Сделано"
                    PuzzleState.SOLVED_UNCLAIMED -> "🎁 Награда!"
                    else -> "⭐ +${friend.rewardCoins}м"
                }

                val badgeColor = when (puzzle?.state) {
                    PuzzleState.COMPLETED -> Color(0xFF388E3C)
                    PuzzleState.SOLVED_UNCLAIMED -> Color(0xFFF57C00)
                    else -> Color(0xFF1976D2)
                }

                BuildingNode(
                    xDp = posX,
                    yDp = posY,
                    title = friend.name,
                    subtitle = friend.houseName,
                    emoji = friend.emoji,
                    badge = statusBadge,
                    badgeColor = badgeColor,
                    containerColor = Color.White,
                    cameraOffset = animatedOffset,
                    idleBounceOffset = idleBounce,
                    onClick = { onOpenFriendProfile(friend.id) }
                )
            }
        }

        // ==========================================
        // СЛОЙ 4: ПЕРЕДНИЙ АТМОСФЕРНЫЙ ПАРАЛЛАКС (Близкие облака и птицы)
        // Скорость смещения 1.45x (плывут быстрее города прямо перед глазами)
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffset.x * 1.45f + cloudDrift * 0.8f
                    translationY = animatedOffset.y * 1.45f
                }
        ) {
            ForegroundAtmosphereLayer()
        }

        // ==========================================
        // СЛОЙ 5: ПАРЯЩИЙ ИНТЕРФЕЙС УПРАВЛЕНИЯ КАРТОЙ (HUD)
        // ==========================================
        TopMapHud(
            state = state,
            scale = animatedScale,
            onFocusHome = { focusOn(220.dp, 460.dp, targetZoom = 1.35f) },
            onFocusCenter = { focusOn(650.dp, 460.dp, targetZoom = 1.15f) },
            onFocusBank = { focusOn(650.dp, 170.dp, targetZoom = 1.35f) },
            onFocusShop = { focusOn(940.dp, 460.dp, targetZoom = 1.35f) },
            onFocusHospital = { focusOn(1050.dp, 180.dp, targetZoom = 1.35f) },
            onZoomIn = { rawScale = (rawScale + 0.25f).coerceAtMost(2.0f) },
            onZoomOut = { rawScale = (rawScale - 0.25f).coerceAtLeast(0.60f) }
        )
    }
}

/**
 * Интерактивное здание/домик с многослойным индивидуальным параллаксом:
 * 1) Z-depth улицы: дома на верхних и нижних улицах двигаются с разной пространственной глубиной.
 * 2) 2.5D Roof Tilt: фасад и крыша наклоняются относительно основания при сдвиге камеры.
 * 3) Floating Badge: статус-бейдж парит над крышей с максимальной высотой и эффектом покачивания.
 */
@Composable
fun BuildingNode(
    xDp: Dp,
    yDp: Dp,
    title: String,
    subtitle: String,
    emoji: String,
    badge: String,
    badgeColor: Color,
    containerColor: Color,
    isPlayerHome: Boolean = false,
    cameraOffset: Offset,
    idleBounceOffset: Float = 0f,
    onClick: () -> Unit
) {
    // 1. Z-depth параллакс улицы (Северные дома y ~ 170dp глубже, Южные y ~ 780dp ближе)
    // При панорамировании камеры передние дома смещаются быстрее задних!
    val depthFactor = ((yDp.value - 460f) / 500f).coerceIn(-1f, 1f)
    val depthOffsetX = cameraOffset.x * (depthFactor * 0.10f)
    val depthOffsetY = cameraOffset.y * (depthFactor * 0.06f)

    // 2. 2.5D микро-параллакс корпуса и крыши (наклон перспективы)
    val tiltMultiplier = 0.030f
    val facadeTiltX = -cameraOffset.x * tiltMultiplier
    val facadeTiltY = -cameraOffset.y * (tiltMultiplier * 0.5f)

    val roofTiltX = -cameraOffset.x * (tiltMultiplier * 1.5f)
    val roofTiltY = -cameraOffset.y * (tiltMultiplier * 0.8f)

    // 3. Парящий статус-бейдж над зданием
    val badgeTiltX = -cameraOffset.x * (tiltMultiplier * 2.2f)
    val badgeTiltY = -cameraOffset.y * (tiltMultiplier * 1.1f)

    Box(
        modifier = Modifier
            .offset(x = xDp, y = yDp)
            .graphicsLayer {
                translationX = depthOffsetX
                translationY = depthOffsetY
            }
            .clickable { onClick() }
    ) {
        // СЛОЙ А: ТЕНЬ НА ЗЕМЛЕ (остается плотно на грунте без наклона)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 12.dp)
                .size(width = 115.dp, height = 24.dp)
                .background(Color(0x33000000), shape = CircleShape)
        )

        // СЛОЙ Б: КОРПУС ЗДАНИЯ С 2.5D НАКЛОНОМ ПЕРСПЕКТИВЫ
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = containerColor,
            border = BorderStroke(
                width = if (isPlayerHome) 3.dp else 2.dp,
                color = if (isPlayerHome) Color(0xFF4CAF50) else badgeColor.copy(alpha = 0.6f)
            ),
            shadowElevation = if (isPlayerHome) 10.dp else 6.dp,
            modifier = Modifier
                .width(132.dp)
                .graphicsLayer {
                    translationX = facadeTiltX
                    translationY = facadeTiltY
                }
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // СЛОЙ В: КРЫША И АВАТАР ДОМИКА (дополнительный параллакс высоты)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.graphicsLayer {
                        translationX = roofTiltX
                        translationY = roofTiltY
                    }
                ) {
                    Text(emoji, fontSize = 42.sp)

                    // Мерцающие звездочки для дома игрока
                    if (isPlayerHome) {
                        Text(
                            "✨",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 6.dp, y = (-4).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Название здания
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF263238),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )

                // Подзаголовок
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }

        // СЛОЙ Г: ПАРЯЩИЙ СТАТУС-БЕЙДЖ НАД КРЫШЕЙ (максимальный параллакс высоты)
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = badgeColor,
            shadowElevation = 5.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .graphicsLayer {
                    translationX = badgeTiltX
                    translationY = badgeTiltY + idleBounceOffset
                }
        ) {
            Text(
                text = badge,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

/**
 * Фоновый слой облаков и солнца дальнего плана.
 */
@Composable
fun DistantCloudsLayer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 35.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text("☁️", fontSize = 54.sp, modifier = Modifier.offset(y = 15.dp))
        Text("☁️", fontSize = 72.sp, modifier = Modifier.offset(y = (-10).dp))
        Text("☀️", fontSize = 64.sp)
        Text("☁️", fontSize = 50.sp, modifier = Modifier.offset(y = 25.dp))
    }
}

/**
 * Фоновый слой холмов и хвойного леса.
 */
@Composable
fun MidgroundHillsLayer() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 50.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text("⛰️", fontSize = 84.sp)
            Text("🌲🌲", fontSize = 62.sp)
            Text("⛰️", fontSize = 105.sp)
            Text("🌲🌲🌲", fontSize = 54.sp)
        }
    }
}

/**
 * Передний атмосферный слой (облачка и птицы, парящие прямо над экраном).
 */
@Composable
fun ForegroundAtmosphereLayer() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "🕊️",
            fontSize = 28.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 80.dp, y = 140.dp)
        )
        Text(
            "🕊️",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-120).dp, y = (-60).dp)
        )
        Text(
            "☁️",
            fontSize = 90.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 40.dp, y = (-80).dp)
                .graphicsLayer { alpha = 0.45f }
        )
        Text(
            "☁️",
            fontSize = 80.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-60).dp, y = 90.dp)
                .graphicsLayer { alpha = 0.40f }
        )
    }
}

/**
 * Дорожная сеть города, центральная площадь с фонтаном и парковые зоны.
 */
@Composable
fun CityRoadsOverlay() {
    // 1. Горизонтальная центральная аллея
    Surface(
        color = Color(0xFFD7CCC8),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color(0xFFBCAAA4)),
        modifier = Modifier
            .offset(x = 100.dp, y = 490.dp)
            .size(width = 1150.dp, height = 46.dp)
    ) {}

    // 2. Северная дорога
    Surface(
        color = Color(0xFFD7CCC8),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color(0xFFBCAAA4)),
        modifier = Modifier
            .offset(x = 120.dp, y = 220.dp)
            .size(width = 1080.dp, height = 38.dp)
    ) {}

    // 3. Южная дорога
    Surface(
        color = Color(0xFFD7CCC8),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color(0xFFBCAAA4)),
        modifier = Modifier
            .offset(x = 120.dp, y = 820.dp)
            .size(width = 1080.dp, height = 38.dp)
    ) {}

    // 4. Вертикальные соединяющие перекрестки
    Surface(
        color = Color(0xFFD7CCC8),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color(0xFFBCAAA4)),
        modifier = Modifier
            .offset(x = 330.dp, y = 220.dp)
            .size(width = 38.dp, height = 638.dp)
    ) {}

    Surface(
        color = Color(0xFFD7CCC8),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color(0xFFBCAAA4)),
        modifier = Modifier
            .offset(x = 980.dp, y = 220.dp)
            .size(width = 38.dp, height = 638.dp)
    ) {}

    // 5. Центральная круглая площадь с фонтаном
    Surface(
        shape = CircleShape,
        color = Color(0xFFE0E0E0),
        border = BorderStroke(4.dp, Color(0xFFBDBDBD)),
        shadowElevation = 4.dp,
        modifier = Modifier
            .offset(x = 615.dp, y = 425.dp)
            .size(170.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF80DEEA),
                border = BorderStroke(3.dp, Color(0xFF00ACC1)),
                modifier = Modifier.size(110.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("⛲", fontSize = 42.sp)
                }
            }
        }
    }

    // Декоративные парковые элементы на газонах
    Text("🌳", fontSize = 38.sp, modifier = Modifier.offset(x = 390.dp, y = 350.dp))
    Text("🌳", fontSize = 38.sp, modifier = Modifier.offset(x = 880.dp, y = 350.dp))
    Text("🌲", fontSize = 42.sp, modifier = Modifier.offset(x = 520.dp, y = 320.dp))
    Text("🌲", fontSize = 42.sp, modifier = Modifier.offset(x = 760.dp, y = 320.dp))
    Text("🌷🌷", fontSize = 26.sp, modifier = Modifier.offset(x = 570.dp, y = 620.dp))
    Text("🌷🌷", fontSize = 26.sp, modifier = Modifier.offset(x = 740.dp, y = 620.dp))
    Text("🪵", fontSize = 28.sp, modifier = Modifier.offset(x = 420.dp, y = 660.dp))
    Text("🏮", fontSize = 24.sp, modifier = Modifier.offset(x = 290.dp, y = 460.dp))
    Text("🏮", fontSize = 24.sp, modifier = Modifier.offset(x = 1020.dp, y = 460.dp))
}

/**
 * Парящий HUD поверх карты: быстрый переход по районам, баланс монет,
 * кнопки приближения/отдаления и подсказка по жестам.
 */
@Composable
fun TopMapHud(
    state: GameState,
    scale: Float,
    onFocusHome: () -> Unit,
    onFocusCenter: () -> Unit,
    onFocusBank: () -> Unit,
    onFocusShop: () -> Unit,
    onFocusHospital: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Верхняя карточка с монетами и заголовком
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.94f),
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 700.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "🗺️ Город Финляндии",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            "Тяни пальцем • Приближай щипком 🤏",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    // Монеты
                    Surface(
                        color = Color(0xFFFFF9C4),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F))
                    ) {
                        Text(
                            "🪙 ${state.wallet.coins}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFF57F17),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Горизонтальный скролл чипсов быстрого перемещения
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NavigationChip(label = "🏠 Мой Дом", color = Color(0xFF4CAF50), onClick = onFocusHome)
                    NavigationChip(label = "⛲ Площадь", color = Color(0xFF00ACC1), onClick = onFocusCenter)
                    NavigationChip(label = "🏦 Банк", color = Color(0xFF7E57C2), onClick = onFocusBank)
                    NavigationChip(label = "🛒 Лавка", color = Color(0xFFFB8C00), onClick = onFocusShop)
                    NavigationChip(label = "🏥 Больница", color = Color(0xFFE53935), onClick = onFocusHospital)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Нижний ряд: подсказка слева и кнопки зума справа
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Подсказка масштаба
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.88f),
                shadowElevation = 3.dp
            ) {
                Text(
                    text = "🔍 ${(scale * 100).roundToInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Кнопки зума [+] и [-]
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onZoomIn,
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                ) {
                    Text("➕", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onZoomOut,
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                ) {
                    Text("➖", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Компактный чип навигации для быстрого полета камеры к объекту.
 */
@Composable
fun NavigationChip(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
