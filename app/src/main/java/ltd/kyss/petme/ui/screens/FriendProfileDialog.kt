package ltd.kyss.petme.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.FriendCharacter
import ltd.kyss.petme.ui.theme.SanrioAccentPurple

/**
 * Карточка профиля друга в стиле Hello Kitty Island Adventure.
 * Архитектурный каркас готов для подключения арта от художника №4!
 */
@Composable
fun FriendProfileDialog(
    initialFriendId: Int = 1,
    state: GameState,
    onStartPuzzle: (Int) -> Unit,
    onOpenLessons: () -> Unit,
    onDismiss: () -> Unit
) {
    var currentIndex by remember {
        mutableStateOf(
            GameCatalog.friendsList.indexOfFirst { it.id == initialFriendId }.coerceAtLeast(0)
        )
    }

    val friends = GameCatalog.friendsList
    val currentFriend = friends[currentIndex]
    val lesson = GameCatalog.lessonForFriend(currentFriend.id)
    val lessonCompleted = lesson?.id in state.completedLessonIds

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFF0F8FF),
            border = BorderStroke(3.dp, Color(0xFF90CAF9)),
            shadowElevation = 10.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 480.dp)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Верхний бар с кнопкой закрытия
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "🌟 Карточка Дружбы",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Блок с персонажем и стрелками перелистывания (как в Hello Kitty)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Стрелка влево
                    IconButton(
                        onClick = {
                            currentIndex = if (currentIndex > 0) currentIndex - 1 else friends.size - 1
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE3F2FD), CircleShape)
                    ) {
                        Text("◀", fontSize = 18.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                    }

                    // Портрет персонажа в белой рамке
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        border = BorderStroke(2.5.dp, Color(0xFFBBDEFB)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.size(130.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                ltd.kyss.petme.ui.components.GameArt(
                                    assetName = "friend_${currentFriend.id}_portrait",
                                    fallbackEmoji = currentFriend.emoji,
                                    modifier = Modifier.size(86.dp),
                                    fallbackSize = 58.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Surface(
                                    color = Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        currentFriend.species,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1976D2),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Стрелка вправо
                    IconButton(
                        onClick = {
                            currentIndex = if (currentIndex < friends.size - 1) currentIndex + 1 else 0
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE3F2FD), CircleShape)
                    ) {
                        Text("▶", fontSize = 18.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Имя и бейдж уровня дружбы (сердечко со шкалой)
                Text(
                    currentFriend.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF263238)
                )

                Spacer(modifier = Modifier.height(4.dp))

                FriendshipLevelBadge(level = currentFriend.friendshipLevel, exp = currentFriend.friendshipExp)

                Spacer(modifier = Modifier.height(14.dp))

                // Блок "ЛЮБИТ" (3 любимых предмета)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "ЛЮБИТ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            currentFriend.favoriteItems.forEach { itemEmoji ->
                                Surface(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(itemEmoji, fontSize = 22.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Блок "СПОСОБНОСТЬ ДРУГА" (Финансовый перк)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (currentFriend.isPerkUnlocked) Color(0xFFE8F5E9) else Color(0xFFEEEEEE),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(if (currentFriend.isPerkUnlocked) "✨" else "🔒", fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                "СПОСОБНОСТЬ: ${currentFriend.perkTitle.uppercase()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentFriend.isPerkUnlocked) Color(0xFF2E7D32) else Color(0xFF757575)
                            )
                            Text(
                                currentFriend.perkDescription,
                                fontSize = 12.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Блок "НАГРАДА ЗА СЛЕДУЮЩИЙ УРОВЕНЬ"
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF9C4),
                    border = BorderStroke(1.dp, Color(0xFFFFE082)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(currentFriend.nextLevelRewardEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "ПОДАРОК ЗА 2 УРОВЕНЬ ДРУЖБЫ:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                currentFriend.nextLevelReward,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBF360C)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Задание открывается после тематического урока Подручного.
                Button(
                    onClick = {
                        onDismiss()
                        if (lessonCompleted) onStartPuzzle(currentFriend.id) else onOpenLessons()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (lessonCompleted) Color(0xFF1E88E5) else SanrioAccentPurple),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        if (lessonCompleted) "⭐ Задание друга (+${currentFriend.rewardCoins} монет)"
                        else "🪽 Сначала урок Подручного",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FriendshipLevelBadge(level: Int, exp: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFFFEBEE),
        border = BorderStroke(1.dp, Color(0xFFFFCDD2))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💖 Уровень $level", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFC2185B))
            Spacer(modifier = Modifier.width(8.dp))
            // Мини прогресс-бар дружбы
            LinearProgressIndicator(
                progress = { exp / 100f },
                color = Color(0xFFE91E63),
                trackColor = Color(0xFFFFCDD2),
                modifier = Modifier
                    .width(70.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
