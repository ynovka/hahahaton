package ltd.kyss.petme.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import ltd.kyss.petme.ui.viewmodel.GameViewModel

/**
 * Основной игровой экран 2D комнаты.
 * Полностью адаптивен под вертикальную ориентацию телефонов и планшетов (от 320dp до 1000dp+).
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
    val location = state.currentLocation

    // Заголовок комнаты и фоновые цвета
    val (roomTitle, wallColorTop, wallColorBottom) = when (location) {
        GameLocation.MyRoom -> Triple("🏠 Моя уютная комната", Color(0xFFFFF8E7), Color(0xFFFFECC8))
        GameLocation.Bank -> Triple("🏦 Банк Финляндии", Color(0xFFE8F5E9), Color(0xFFC8E6C9))
        GameLocation.Shop -> Triple("🛒 Лавка Енотика", Color(0xFFFFF3E0), Color(0xFFFFE0B2))
        GameLocation.Hospital -> Triple("🏥 Лечебница Доктора Совы", Color(0xFFE1F5FE), Color(0xFFB3E5FC))
        is GameLocation.FriendRoom -> {
            val friend = GameCatalog.friendsList.find { it.id == location.friendId }
            Triple("🏡 ${friend?.houseName ?: "В гостях"}", Color(0xFFF3E5F5), Color(0xFFE1BEE7))
        }
        else -> Triple("Комната", Color.White, Color.LightGray)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(wallColorTop, wallColorBottom, Color(0xFF8D6E63))
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 840.dp) // Адаптивное ограничение для планшетов
        ) {
            // Верхняя плашка состояния (адаптивна для узких и широких экранов)
            TopGameBar(
                state = state,
                onOpenBudget = onOpenBudget,
                onOpenAdvisor = onOpenAdvisor,
                onOpenSettings = onOpenSettings,
                onOpenFinance = onOpenFinance
            )

            // Компактная строка локации: игровая сцена должна занимать большую часть экрана.
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.88f),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = roomTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F),
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )

                    FilledTonalButton(
                        onClick = { viewModel.changeLocation(GameLocation.CityMap) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFFFFCCBC),
                            contentColor = Color(0xFF8D2B12)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 9.dp, vertical = 3.dp),
                        modifier = Modifier.heightIn(min = 34.dp)
                    ) {
                        Text(
                            if (location == GameLocation.MyRoom) "🚪 В город" else "🗺️ Карта",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Облачко с советом/репликой помощника Финни
            AdvisorBubble(text = state.advisorTip)

            if (location == GameLocation.MyRoom && state.budget.isConfirmed && !state.isPeriodFinished) {
                OutlinedButton(
                    onClick = onFinishPeriod,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("📋 Подвести итоги периода #${state.period}", fontWeight = FontWeight.Bold)
                }
            }

            // 2D Игровая сцена с персонажами и объектами интерьера (адаптивная ширина!)
            RoomStage(
                modifier = Modifier.weight(1f),
                state = state,
                viewModel = viewModel,
                onOpenWardrobe = onOpenWardrobe,
                onOpenShop = onOpenShop,
                onOpenBank = onOpenBank,
                onOpenHospital = onOpenHospital,
                onOpenCare = onOpenCare,
                onOpenFriendDialogue = onOpenFriendDialogue
            )

        }
    }
}

@Composable
fun TopGameBar(
    state: GameState,
    onOpenBudget: () -> Unit,
    onOpenAdvisor: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFinance: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color.White.copy(alpha = 0.94f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PetProfileHeader(state = state)
            Spacer(modifier = Modifier.weight(1f))
            CoinsPill(coins = state.wallet.coins, onClick = onOpenFinance)
            Spacer(modifier = Modifier.width(4.dp))
            SavingsPill(savings = state.wallet.savings, onClick = onOpenBudget)
            IconButton(onClick = onOpenAdvisor, modifier = Modifier.size(30.dp)) {
                Text("💡", fontSize = 15.sp)
            }
            IconButton(onClick = onOpenSettings, modifier = Modifier.size(30.dp)) {
                Text("⚙️", fontSize = 15.sp)
            }
            FilledTonalButton(
                onClick = onOpenBudget,
                contentPadding = PaddingValues(horizontal = 7.dp, vertical = 2.dp),
                modifier = Modifier.heightIn(min = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFFC8E6C9),
                    contentColor = Color(0xFF1B5E20)
                )
            ) {
                Text("План", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PetProfileHeader(state: GameState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFECB3)),
            contentAlignment = Alignment.Center
        ) {
            GameArt(
                assetName = "pet_${state.pet.species.id}_portrait",
                fallbackEmoji = state.pet.species.emoji,
                modifier = Modifier.size(28.dp),
                fallbackSize = 19.sp
            )
        }
        Spacer(modifier = Modifier.width(5.dp))
        Column {
            Text(
                state.pet.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                maxLines = 1
            )
            Text(
                "${state.pet.mood.emoji} ${state.pet.mood.title}",
                fontSize = 9.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun CoinsPill(coins: Int, onClick: () -> Unit) {
    Surface(
        color = Color(0xFFFFF9C4),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🪙", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                "$coins",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFFF57F17)
            )
        }
    }
}

@Composable
private fun SavingsPill(savings: Int, onClick: () -> Unit) {
    Surface(
        color = Color(0xFFEDE7F6),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🏺", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                "$savings",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF512DA8)
            )
        }
    }
}

@Composable
fun AdvisorBubble(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFFDE7),
        border = BorderStroke(1.dp, Color(0xFFFFD54F)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                fontSize = 10.sp,
                color = Color(0xFF4E342E),
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

/**
 * 2D сцена комнаты с динамическим масштабированием ширины:
 * Герой и питомец перемещаются пропорционально ширине экрана,
 * интерактивные объекты расставлены равномерно по всей комнате.
 */
@Composable
fun RoomStage(
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        // Динамическая адаптивная ширина под любой экран (от 320dp смартфона до 900dp планшета)
        val stageUsableWidth = (maxWidth - 75.dp).coerceAtLeast(200.dp)
        val animatedHeroX by animateFloatAsState(
            targetValue = state.heroX,
            animationSpec = tween(durationMillis = 430),
            label = "heroWalk"
        )
        val animatedPetX by animateFloatAsState(
            targetValue = (state.heroX + 0.12f).coerceIn(0.04f, 0.96f),
            animationSpec = tween(durationMillis = 560),
            label = "petFollow"
        )
        var isWalking by remember { mutableStateOf(false) }
        var facingRight by remember { mutableStateOf(true) }
        var previousTargetX by remember { mutableFloatStateOf(state.heroX) }

        LaunchedEffect(state.heroX) {
            if (state.heroX != previousTargetX) {
                facingRight = state.heroX > previousTargetX
                previousTargetX = state.heroX
                isWalking = true
                delay(620)
                isWalking = false
            }
        }

        // Вся свободная сцена принимает нажатие: герой идёт в выбранную точку.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.08f))))
                .pointerInput(Unit) {
                    detectTapGestures { point ->
                        if (size.width > 0) viewModel.moveHeroTo(point.x / size.width.toFloat())
                    }
                }
        ) {
            val bgAssetName = when (state.currentLocation) {
                GameLocation.MyRoom -> "bg_room_myroom"
                GameLocation.Bank -> "bg_room_bank"
                GameLocation.Hospital -> "bg_room_hospital"
                GameLocation.Shop -> "bg_room_shop"
                is GameLocation.FriendRoom -> "bg_room_friend_${(state.currentLocation as GameLocation.FriendRoom).friendId}"
                else -> null
            }

            // Атмосферный фон для комнат друзей
            if (state.currentLocation is GameLocation.FriendRoom) {
                val friendId = (state.currentLocation as GameLocation.FriendRoom).friendId
                val friendColors = when (friendId) {
                    1 -> listOf(Color(0xFFFFF8E1), Color(0xFFFFE082)) // Медовая берлога
                    2 -> listOf(Color(0xFFE8F5E9), Color(0xFFA5D6A7)) // Дуб и мох
                    3 -> listOf(Color(0xFFECEFF1), Color(0xFFB0BEC5)) // Мастерская
                    4 -> listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9)) // Книжная башня
                    5 -> listOf(Color(0xFFFBE9E7), Color(0xFFFFAB91)) // Нора с камином
                    6 -> listOf(Color(0xFFE0F2F1), Color(0xFF80CBC4)) // Морковный домик
                    else -> listOf(Color(0xFFE1F5FE), Color(0xFF81D4FA)) // Полянка Барбоса
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Brush.verticalGradient(friendColors))
                )
                // Пол для ходьбы
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(0.28f)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF8D6E63), Color(0xFF4E342E))
                            )
                        )
                )
            }

            if (bgAssetName != null) {
                GameArt(
                    assetName = bgAssetName,
                    fallbackEmoji = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    contentDescription = "Room Background"
                )
            }
        }

        // Объекты в своей комнате
        if (state.currentLocation == GameLocation.MyRoom) {
            // Шкаф-гардероб (слева)
            InteractiveProp(
                assetName = "prop_wardrobe",
                emoji = "🗄️",
                title = "Гардероб",
                xOffset = stageUsableWidth * 0.12f,
                onClick = onOpenWardrobe
            )

            // Миска для корма (по центру)
            InteractiveProp(
                assetName = if (state.pet.isHungry) "prop_bowl_empty" else "prop_bowl_full",
                emoji = if (state.pet.isHungry) "🥣" else "🥣✨",
                title = if (state.pet.isHungry) "Миска (пусто!)" else "Миска (сыт)",
                xOffset = stageUsableWidth * 0.50f,
                onClick = onOpenCare
            )

            // Копилка на полу (справа)
            InteractiveProp(
                assetName = "prop_piggy_bank",
                emoji = "🏺",
                title = "Копилка (${state.wallet.savings} м.)",
                xOffset = stageUsableWidth * 0.85f,
                onClick = onOpenBank
            )
        }

        // Объекты в магазине
        if (state.currentLocation == GameLocation.Shop) {
            InteractiveProp(
                assetName = "worker_shop_idle",
                emoji = "🦝",
                title = "Енотик-продавец",
                xOffset = stageUsableWidth * 0.70f,
                onClick = onOpenShop
            )
        }

        // Объекты в банке
        if (state.currentLocation == GameLocation.Bank) {
            InteractiveProp(
                assetName = "worker_bank_idle",
                emoji = "🦫",
                title = "Бобёр-банкир",
                xOffset = stageUsableWidth * 0.70f,
                onClick = onOpenBank
            )
        }

        if (state.currentLocation == GameLocation.Hospital) {
            InteractiveProp(
                assetName = "worker_hospital_idle",
                emoji = "🦉",
                title = "Доктор Сова",
                xOffset = stageUsableWidth * 0.70f,
                onClick = onOpenHospital
            )
        }

        // В гостях у друга
        if (state.currentLocation is GameLocation.FriendRoom) {
            val friendId = (state.currentLocation as GameLocation.FriendRoom).friendId
            val friend = GameCatalog.friendsList.find { it.id == friendId }
            if (friend != null) {
                InteractiveProp(
                    assetName = "friend_${friend.id}_idle",
                    emoji = friend.emoji,
                    title = "Поговорить: ${friend.name}",
                    xOffset = stageUsableWidth * 0.75f,
                    onClick = { onOpenFriendDialogue(friendId) }
                )
            }
        }

        // Питомец (бегает рядом с героем)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = stageUsableWidth * animatedPetX, y = (-18).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedGameArt(
                    animationPrefix = "pet_${state.pet.species.id}_walk",
                    staticAssetName = "pet_${state.pet.species.id}_idle",
                    fallbackEmoji = state.pet.species.emoji,
                    isAnimating = isWalking,
                    flipHorizontally = !facingRight,
                    modifier = Modifier.size(54.dp),
                    fallbackSize = 42.sp
                )
                Text(
                    state.pet.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723)
                )
            }
        }

        // Управляемый герой (игрок)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = stageUsableWidth * animatedHeroX, y = (-18).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedGameArt(
                    animationPrefix = "hero_walk",
                    staticAssetName = "hero_idle",
                    fallbackEmoji = "🚶",
                    isAnimating = isWalking,
                    flipHorizontally = !facingRight,
                    modifier = Modifier.size(62.dp),
                    fallbackSize = 48.sp
                )
                Text(
                    "Ты",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            }
        }
    }
}

@Composable
fun BoxScope.InteractiveProp(
    assetName: String,
    emoji: String,
    title: String,
    xOffset: Dp,
    bottomPadding: Dp = 38.dp,
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
            modifier = Modifier.padding(bottom = bottomPadding).widthIn(max = 100.dp)
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.92f),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier
                        .widthIn(max = 96.dp)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(Modifier.height(3.dp))
            GameArt(
                assetName = assetName,
                fallbackEmoji = emoji,
                modifier = Modifier.size(48.dp),
                fallbackSize = 38.sp
            )
        }
    }
}
