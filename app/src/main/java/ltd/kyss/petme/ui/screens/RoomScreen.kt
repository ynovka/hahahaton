package ltd.kyss.petme.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.*
import ltd.kyss.petme.ui.components.AnimatedGameArt
import ltd.kyss.petme.ui.components.GameArt
import ltd.kyss.petme.ui.theme.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

/**
 * 2D игровая комната в стиле Hello Kitty Island Adventure / Sanrio 3D.
 * Поддерживает вертикальную (телефон) и горизонтальную (планшет / альбомная) ориентации.
 */
@Composable
fun RoomScreen(
    state: GameState,
    viewModel: GameViewModel,
    onOpenBudget: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenBank: () -> Unit,
    onOpenHospital: () -> Unit,
    onOpenWardrobe: () -> Unit,
    onOpenCare: () -> Unit,
    onOpenAdvisor: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFinance: () -> Unit,
    onFinishPeriod: () -> Unit,
    onOpenFriendDialogue: (Int) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val location = state.currentLocation

    // Заголовок текущей локации
    val (roomTitle, roomEmoji) = when (location) {
        GameLocation.MyRoom -> Pair("Моя уютная комната", "🏠")
        GameLocation.Bank -> Pair("Городской Банк", "🏦")
        GameLocation.Shop -> Pair("Лавка Енотика", "🛒")
        GameLocation.Hospital -> Pair("Больница Доктора Совы", "🏥")
        is GameLocation.FriendRoom -> {
            val friend = GameCatalog.friendsList.find { it.id == location.friendId }
            Pair(friend?.houseName ?: "В гостях", friend?.emoji ?: "🏡")
        }
        else -> Pair("Комната", "🏠")
    }

    // Выбор фонового ассета под ориентацию экрана
    val bgAssetName = when (location) {
        GameLocation.MyRoom -> if (isLandscape) "bg_room_myroom_horiz" else "bg_room_myroom"
        GameLocation.Bank -> "bg_room_bank"
        GameLocation.Hospital -> "bg_room_hospital"
        GameLocation.Shop -> "bg_room_shop"
        is GameLocation.FriendRoom -> null // Кастомный градиент для друзей
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9EC))
    ) {
        // Фоновое изображение комнаты (на весь экран без обрезки)
        if (bgAssetName != null) {
            GameArt(
                assetName = bgAssetName,
                fallbackEmoji = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "Room Background"
            )
        } else if (location is GameLocation.FriendRoom) {
            // Тематический градиент комнаты друга
            val friendId = location.friendId
            val friendGradients = when (friendId) {
                1 -> listOf(Color(0xFFFFF9C4), Color(0xFFFFE082), Color(0xFFFFD54F)) // Потап: мед
                2 -> listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7)) // Рыжик: дубрава
                3 -> listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC), Color(0xFFB0BEC5)) // Тёма: мастерская
                4 -> listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9), Color(0xFFB39DDB)) // София: книжная
                5 -> listOf(Color(0xFFFBE9E7), Color(0xFFFFCCBC), Color(0xFFFFAB91)) // Алиса: нора
                6 -> listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB), Color(0xFF80CBC4)) // Сеня: мята
                else -> listOf(Color(0xFFE1F5FE), Color(0xFFB3E5FC), Color(0xFF81D4FA)) // Барбос: поляна
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(friendGradients))
            )
            // Деревянный пол для комнат друзей
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.30f)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFC79A63), Color(0xFFA4753F), Color(0xFF7E5425))
                        )
                    )
            )
        }

        // Область сцены с персонажами (нажатие перемещает героя)
        RoomInteractiveStage(
            modifier = Modifier.fillMaxSize(),
            state = state,
            viewModel = viewModel,
            onOpenWardrobe = onOpenWardrobe,
            onOpenShop = onOpenShop,
            onOpenBank = onOpenBank,
            onOpenHospital = onOpenHospital,
            onOpenCare = onOpenCare,
            onOpenFriendDialogue = onOpenFriendDialogue
        )

        // Верхний плавающий UI (HUD)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .widthIn(max = 860.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Главная панель статуса (Sanrio Glassmorphic Pill)
            SanrioTopGameBar(
                state = state,
                onOpenBudget = onOpenBudget,
                onOpenAdvisor = onOpenAdvisor,
                onOpenSettings = onOpenSettings,
                onOpenFinance = onOpenFinance
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Панель навигации (Заголовок локации + кнопка «В город»)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.94f),
                border = BorderStroke(2.dp, SanrioCardBorder),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(roomEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = roomTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SanrioTextDark
                        )
                    }

                    // Кнопка возврата на карту
                    Surface(
                        color = SanrioSkyBlue,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable {
                            viewModel.changeLocation(GameLocation.CityMap)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                if (location == GameLocation.MyRoom) "🚪 В город" else "🗺️ Карта",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Плавающее облачко-подсказка советника
            SanrioAdvisorBubble(text = state.advisorTip)

            if (location == GameLocation.MyRoom && state.budget.isConfirmed && !state.isPeriodFinished) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = SanrioAccentGreen,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onFinishPeriod)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "📋 Подвести итоги периода #${state.period}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Плавающая верхняя плашка в стиле Sanrio.
 */
@Composable
fun SanrioTopGameBar(
    state: GameState,
    onOpenBudget: () -> Unit,
    onOpenAdvisor: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFinance: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = 0.96f),
        border = BorderStroke(2.dp, SanrioCardBorder),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар питомца с круглой рамкой
            SanrioPetHeader(state = state)

            Spacer(modifier = Modifier.weight(1f))

            // Плашка монет (Лапко-монетки)
            SanrioCoinsPill(coins = state.wallet.coins, onClick = onOpenFinance)

            Spacer(modifier = Modifier.width(6.dp))

            // Плашка сбережений (Копилка)
            SanrioSavingsPill(savings = state.wallet.savings, onClick = onOpenBudget)

            Spacer(modifier = Modifier.width(4.dp))

            // Круглая кнопка подсказки
            IconButton(
                onClick = onOpenAdvisor,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SanrioGoldBg)
            ) {
                Text("💡", fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.width(3.dp))

            // Круглая кнопка настроек
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(SanrioSkyBlueLight)
            ) {
                Text("⚙️", fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.width(3.dp))

            // Кнопка Плана / Бюджета
            Surface(
                color = SanrioAccentGreen,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.clickable(onClick = onOpenBudget)
            ) {
                Text(
                    "План",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun SanrioPetHeader(state: GameState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(SanrioGoldBg)
                .border(2.dp, SanrioGoldCoin, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            GameArt(
                assetName = "pet_${state.pet.species.id}_portrait",
                fallbackEmoji = state.pet.species.emoji,
                modifier = Modifier.size(32.dp),
                fallbackSize = 22.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                state.pet.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SanrioTextDark,
                maxLines = 1
            )
            Surface(
                color = SanrioGreenBg,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    "${state.pet.mood.emoji} ${state.pet.mood.title}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF00796B),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun SanrioCoinsPill(coins: Int, onClick: () -> Unit) {
    Surface(
        color = SanrioGoldBg,
        border = BorderStroke(1.5.dp, SanrioGoldCoin),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameArt(
                assetName = "icon_paw_coin",
                fallbackEmoji = "🪙",
                modifier = Modifier.size(16.dp),
                fallbackSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "$coins",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SanrioGoldText
            )
        }
    }
}

@Composable
private fun SanrioSavingsPill(savings: Int, onClick: () -> Unit) {
    Surface(
        color = SanrioPurpleBg,
        border = BorderStroke(1.5.dp, SanrioAccentPurple),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameArt(
                assetName = "jar_savings",
                fallbackEmoji = "🏺",
                modifier = Modifier.size(16.dp),
                fallbackSize = 13.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "$savings",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = SanrioAccentPurple
            )
        }
    }
}

@Composable
fun SanrioAdvisorBubble(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFDE7),
        border = BorderStroke(1.5.dp, Color(0xFFFFD54F)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                color = Color(0xFF4E342E),
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

/**
 * 2D игровая сцена с персонажами на полу.
 */
@Composable
fun RoomInteractiveStage(
    modifier: Modifier = Modifier,
    state: GameState,
    viewModel: GameViewModel,
    onOpenWardrobe: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenBank: () -> Unit,
    onOpenHospital: () -> Unit,
    onOpenCare: () -> Unit,
    onOpenFriendDialogue: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { point ->
                    if (size.width > 0) {
                        viewModel.moveHeroTo((point.x / size.width.toFloat()).coerceIn(0.1f, 0.9f))
                    }
                }
            }
    ) {
        val usableWidth = (maxWidth - 80.dp).coerceAtLeast(200.dp)

        val animatedHeroX by animateFloatAsState(
            targetValue = state.heroX,
            animationSpec = tween(durationMillis = 480),
            label = "heroWalk"
        )
        val animatedPetX by animateFloatAsState(
            targetValue = (state.heroX + 0.14f).coerceIn(0.08f, 0.92f),
            animationSpec = tween(durationMillis = 580),
            label = "petFollow"
        )

        var isWalking by remember { mutableStateOf(false) }
        var facingRight by remember { mutableStateOf(true) }
        var previousTargetX by remember { mutableFloatStateOf(state.heroX) }

        // Мягкое дыхание персонажей в покое
        val infiniteTransition = rememberInfiniteTransition(label = "idleBreathe")
        val breatheScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathe"
        )

        LaunchedEffect(state.heroX) {
            if (state.heroX != previousTargetX) {
                facingRight = state.heroX > previousTargetX
                previousTargetX = state.heroX
                isWalking = true
                delay(650)
                isWalking = false
            }
        }

        // Интерактивный нижний док в своей комнате (Sanrio Action Dock)
        if (state.currentLocation == GameLocation.MyRoom) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .wrapContentWidth(),
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.95f),
                border = BorderStroke(2.dp, SanrioCardBorder),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Гардероб
                    Surface(
                        color = SanrioPurpleBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SanrioAccentPurple.copy(alpha = 0.5f)),
                        modifier = Modifier.clickable(onClick = onOpenWardrobe)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🗄️", fontSize = 16.sp)
                            Spacer(Modifier.width(5.dp))
                            Text("Гардероб", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SanrioAccentPurple)
                        }
                    }

                    // Миска / Уход
                    Surface(
                        color = if (state.pet.isHungry) SanrioOrangeBg else SanrioGreenBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            if (state.pet.isHungry) SanrioAccentOrange else SanrioAccentGreen
                        ),
                        modifier = Modifier.clickable(onClick = onOpenCare)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(if (state.pet.isHungry) "🥣" else "✨", fontSize = 16.sp)
                            Spacer(Modifier.width(5.dp))
                            Text(
                                if (state.pet.isHungry) "Покормить!" else "Уход",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.pet.isHungry) SanrioGoldText else Color(0xFF00796B)
                            )
                        }
                    }

                    // Копилка
                    Surface(
                        color = SanrioGoldBg,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, SanrioGoldCoin),
                        modifier = Modifier.clickable(onClick = onOpenBank)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GameArt("jar_savings", fallbackEmoji = "🏺", modifier = Modifier.size(16.dp), fallbackSize = 13.sp)
                            Spacer(Modifier.width(5.dp))
                            Text("Копилка (${state.wallet.savings})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SanrioGoldText)
                        }
                    }
                }
            }
        }

        // Сотрудник магазина
        if (state.currentLocation == GameLocation.Shop) {
            SanrioInteractiveProp(
                assetName = "worker_shop_idle",
                emoji = "🦝",
                title = "Магазин покупок",
                badgeColor = SanrioAccentPink,
                xOffset = usableWidth * 0.70f,
                bottomPadding = 80.dp,
                propSize = 90.dp,
                onClick = onOpenShop
            )
        }

        // Сотрудник банка
        if (state.currentLocation == GameLocation.Bank) {
            SanrioInteractiveProp(
                assetName = "worker_bank_idle",
                emoji = "🦫",
                title = "Касса банка",
                badgeColor = SanrioGoldCoin,
                xOffset = usableWidth * 0.70f,
                bottomPadding = 80.dp,
                propSize = 90.dp,
                onClick = onOpenBank
            )
        }

        // Врач больницы
        if (state.currentLocation == GameLocation.Hospital) {
            SanrioInteractiveProp(
                assetName = "worker_hospital_idle",
                emoji = "🦉",
                title = "Осмотр врача",
                badgeColor = SanrioSkyBlueDark,
                xOffset = usableWidth * 0.70f,
                bottomPadding = 80.dp,
                propSize = 90.dp,
                onClick = onOpenHospital
            )
        }

        // В гостях у друга
        if (state.currentLocation is GameLocation.FriendRoom) {
            val friendId = (state.currentLocation as GameLocation.FriendRoom).friendId
            val friend = GameCatalog.friendsList.find { it.id == friendId }
            if (friend != null) {
                SanrioInteractiveProp(
                    assetName = "friend_${friend.id}_idle",
                    emoji = friend.emoji,
                    title = "Поговорить: ${friend.name}",
                    badgeColor = SanrioSkyBlueDark,
                    xOffset = usableWidth * 0.70f,
                    bottomPadding = 80.dp,
                    propSize = 94.dp,
                    onClick = { onOpenFriendDialogue(friendId) }
                )
            }
        }

        // Питомец (бегает рядом с героем)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = usableWidth * animatedPetX, y = (-82).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    // Мягкая овальная тень на полу под лапками
                    Canvas(
                        modifier = Modifier
                            .size(54.dp, 10.dp)
                            .offset(y = 3.dp)
                    ) {
                        drawOval(color = Color.Black.copy(alpha = 0.22f))
                    }
                    AnimatedGameArt(
                        animationPrefix = "pet_${state.pet.species.id}_walk",
                        staticAssetName = "pet_${state.pet.species.id}_idle",
                        fallbackEmoji = state.pet.species.emoji,
                        isAnimating = isWalking,
                        flipHorizontally = !facingRight,
                        modifier = Modifier
                            .size(68.dp)
                            .graphicsLayer(scaleY = breatheScale),
                        fallbackSize = 48.sp
                    )
                }
                Spacer(Modifier.height(2.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SanrioCardBorder),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        state.pet.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SanrioTextDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
        }

        // Управляемый герой-ребёнок
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = usableWidth * animatedHeroX, y = (-84).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomCenter) {
                    // Мягкая овальная тень на полу под ботиночками
                    Canvas(
                        modifier = Modifier
                            .size(64.dp, 12.dp)
                            .offset(y = 4.dp)
                    ) {
                        drawOval(color = Color.Black.copy(alpha = 0.25f))
                    }
                    AnimatedGameArt(
                        animationPrefix = "hero_walk",
                        staticAssetName = "hero_idle",
                        fallbackEmoji = "🚶",
                        isAnimating = isWalking,
                        flipHorizontally = !facingRight,
                        modifier = Modifier
                            .size(92.dp)
                            .graphicsLayer(scaleY = breatheScale),
                        fallbackSize = 64.sp
                    )
                }
                Spacer(Modifier.height(2.dp))
                Surface(
                    color = SanrioSkyBlue,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        "Ты",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

/**
 * Интерактивный объект / персонаж в стиле Sanrio (с тенью под ногами и плавающей карточкой).
 */
@Composable
fun BoxScope.SanrioInteractiveProp(
    assetName: String,
    emoji: String,
    title: String,
    xOffset: Dp,
    bottomPadding: Dp = 90.dp,
    propSize: Dp = 68.dp,
    badgeColor: Color = SanrioSkyBlueDark,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = xOffset)
            .clickable { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = bottomPadding)
                .widthIn(max = 120.dp)
        ) {
            // Плавающий бейдж с названием
            Surface(
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, badgeColor.copy(alpha = 0.6f)),
                shadowElevation = 3.dp
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            // Персонаж с овальной тенью под ногами
            Box(contentAlignment = Alignment.BottomCenter) {
                Canvas(
                    modifier = Modifier
                        .size(propSize * 0.75f, 10.dp)
                        .offset(y = 4.dp)
                ) {
                    drawOval(color = Color.Black.copy(alpha = 0.20f))
                }
                GameArt(
                    assetName = assetName,
                    fallbackEmoji = emoji,
                    modifier = Modifier.size(propSize),
                    fallbackSize = 48.sp
                )
            }
        }
    }
}
