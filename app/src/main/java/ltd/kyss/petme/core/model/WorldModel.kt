package ltd.kyss.petme.core.model

/**
 * Локации игрового мира.
 */
sealed class GameLocation {
    object MyRoom : GameLocation()
    object CityMap : GameLocation()
    object Bank : GameLocation()
    object Shop : GameLocation()
    object Hospital : GameLocation()
    data class FriendRoom(val friendId: Int) : GameLocation()
}

data class FinancialLesson(
    val id: String,
    val title: String,
    val emoji: String,
    val explanation: String,
    val shortRule: String,
    val relatedFriendId: Int? = null
)

/**
 * Персонаж-друг, живущий в одном из домов города (с карточкой дружбы).
 */
data class FriendCharacter(
    val id: Int,
    val name: String,
    val species: String,
    val emoji: String,
    val houseName: String,
    val greetingText: String,
    val puzzlePrompt: String,
    val rewardCoins: Int = 20,
    // Система дружбы в стиле Hello Kitty
    val friendshipLevel: Int = 1,
    val friendshipExp: Int = 20, // 0..100
    val favoriteItems: List<String> = listOf("🍎", "🪙", "⭐"),
    val perkTitle: String = "Помощник в покупках",
    val perkDescription: String = "Шанс получить скидку 10% в лавке Енотика",
    val isPerkUnlocked: Boolean = false,
    val nextLevelReward: String = "Праздничный бантик",
    val nextLevelRewardEmoji: String = "🎀"
)

/**
 * Категории товаров в магазине.
 */
enum class ItemCategory {
    MANDATORY_FOOD,     // Корм для питомца
    MANDATORY_CARE,     // Уход и гигиена
    WANT_TOY,           // Игрушки и развлечения
    WARDROBE_ACCESSORY  // Предметы гардероба
}

/**
 * Товар в магазине.
 */
data class ShopItem(
    val id: String,
    val title: String,
    val category: ItemCategory,
    val price: Int,
    val emoji: String,
    val description: String,
    val wardrobeItem: WardrobeItem? = null // Если это вещь для примерки
)

/**
 * Интерактивный объект в комнате (к которому подходит герой).
 */
data class RoomHotspot(
    val id: String,
    val title: String,
    val emoji: String,
    val xPositionFraction: Float, // 0.0 .. 1.0 на полу комнаты
    val actionDescription: String
)
