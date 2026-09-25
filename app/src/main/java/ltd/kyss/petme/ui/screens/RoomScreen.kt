package ltd.kyss.petme.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.*
import ltd.kyss.petme.ui.viewmodel.GameViewModel

@Composable
fun RoomScreen(
    state: GameState,
    viewModel: GameViewModel,
    onOpenBudget: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenWardrobe: () -> Unit,
    onOpenPuzzle: (Int) -> Unit
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
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Верхняя плашка состояния
            TopGameBar(state = state, onOpenBudget = onOpenBudget)

            // Заголовок локации
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.85f),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = roomTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF37474F)
                    )

                    Button(
                        onClick = { viewModel.changeLocation(GameLocation.CityMap) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("🗺️ В город", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Облачко с советом/репликой помощника
            AdvisorBubble(text = state.advisorTip)

            Spacer(modifier = Modifier.weight(1f))

            // 2D Игровая сцена с персонажами и объектами интерьера
            RoomStage(
                state = state,
                viewModel = viewModel,
                onOpenWardrobe = onOpenWardrobe,
                onOpenShop = onOpenShop,
                onOpenPuzzle = onOpenPuzzle
            )

            // Пол комнаты
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF5D4037))
            )

            // Кнопки управления перемещением героя
            MovementControls(
                onMoveLeft = { viewModel.moveHero(-0.06f) },
                onMoveRight = { viewModel.moveHero(0.06f) }
            )
        }
    }
}

@Composable
fun TopGameBar(state: GameState, onOpenBudget: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Питомец и его настроение
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFECB3)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.pet.species.emoji, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        "${state.pet.name} (${state.pet.growthStage.title})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        "${state.pet.mood.emoji} ${state.pet.mood.title}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Монеты и Копилка
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Монеты в кошельке
                Surface(
                    color = Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${state.wallet.coins}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFF57F17)
                        )
                    }
                }

                // Копилка
                Surface(
                    color = Color(0xFFEDE7F6),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clickable { onOpenBudget() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏺", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "${state.wallet.savings}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF512DA8)
                        )
                    }
                }

                // Кнопка бюджета
                Button(
                    onClick = onOpenBudget,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Бюджет 📊", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdvisorBubble(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFDE7),
        border = BorderStroke(1.dp, Color(0xFFFFD54F)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color(0xFF4E342E),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RoomStage(
    state: GameState,
    viewModel: GameViewModel,
    onOpenWardrobe: () -> Unit,
    onOpenShop: () -> Unit,
    onOpenPuzzle: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // Объекты в своей комнате
        if (state.currentLocation == GameLocation.MyRoom) {
            // Шкаф-гардероб
            InteractiveProp(
                emoji = "🚪👗",
                title = "Гардероб",
                fractionX = 0.15f,
                onClick = onOpenWardrobe
            )

            // Миска для корма
            InteractiveProp(
                emoji = if (state.pet.isHungry) "🥣" else "🥣✨",
                title = if (state.pet.isHungry) "Миска (пусто!)" else "Миска (сыт)",
                fractionX = 0.50f,
                onClick = onOpenShop
            )

            // Копилка на полу
            InteractiveProp(
                emoji = "🏺",
                title = "Копилка (${state.wallet.savings} м.)",
                fractionX = 0.85f,
                onClick = onOpenShop
            )
        }

        // Объекты в магазине
        if (state.currentLocation == GameLocation.Shop) {
            InteractiveProp(
                emoji = "🦝",
                title = "Енотик-продавец",
                fractionX = 0.70f,
                onClick = onOpenShop
            )
        }

        // Объекты в банке
        if (state.currentLocation == GameLocation.Bank) {
            InteractiveProp(
                emoji = "🦫",
                title = "Бобёр-банкир",
                fractionX = 0.70f,
                onClick = onOpenShop
            )
        }

        // В гостях у друга
        if (state.currentLocation is GameLocation.FriendRoom) {
            val friendId = (state.currentLocation as GameLocation.FriendRoom).friendId
            val friend = GameCatalog.friendsList.find { it.id == friendId }
            if (friend != null) {
                InteractiveProp(
                    emoji = friend.emoji,
                    title = "${friend.name} (Задание)",
                    fractionX = 0.75f,
                    onClick = { onOpenPuzzle(friendId) }
                )
            }
        }

        // Питомец (рядом с героем)
        val petX = (state.heroX + 0.12f).coerceIn(0.05f, 0.95f)
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (petX * 300).dp, y = (-10).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.pet.species.emoji, fontSize = 42.sp)
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
                .offset(x = (state.heroX * 300).dp, y = (-10).dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🚶", fontSize = 48.sp)
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
fun InteractiveProp(
    emoji: String,
    title: String,
    fractionX: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = (fractionX * 280).dp)
            .clickable { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(emoji, fontSize = 36.sp)
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 1.dp
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun MovementControls(onMoveLeft: () -> Unit, onMoveRight: () -> Unit) {
    Surface(
        color = Color(0xFF4E342E),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onMoveLeft,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(width = 120.dp, height = 50.dp)
            ) {
                Text("◀ Налево", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            }

            Button(
                onClick = onMoveRight,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(width = 120.dp, height = 50.dp)
            ) {
                Text("Направо ▶", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
            }
        }
    }
}
