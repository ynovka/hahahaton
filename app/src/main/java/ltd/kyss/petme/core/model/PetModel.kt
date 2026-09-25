package ltd.kyss.petme.core.model

/**
 * 7 видов питомцев по ТЗ (каждый с 2 вариантами окраса = 14 комбинаций).
 */
enum class PetSpecies(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String
) {
    CAT("cat", "Котёнок", "Любит мягкие пледы и ловить солнечных зайчиков", "🐱"),
    DOG("dog", "Щенок", "Всегда рад побегать и громко виляет хвостом", "🐶"),
    FOX("fox", "Лисёнок", "Любопытный непоседа с пушистым хвостиком", "🦊"),
    PANDA("panda", "Панда", "Уютный лакомка, обожает сочный бамбук", "🐼"),
    BUNNY("bunny", "Зайчик", "Быстрый как стрела, любит морковные снеки", "🐰"),
    RACCOON("raccoon", "Енотик", "Ловкий мастер находить интересные вещицы", "🦝"),
    OWL("owl", "Совёнок", "Мудрый взгляд, но всегда готов к шалостям", "🦉")
}

/**
 * Варианты окраса / шерстки питомца.
 */
enum class ColorPattern(
    val id: String,
    val title: String
) {
    CLASSIC("classic", "Классический"),
    SPOTTED("spotted", "Пятнистый / Полосатый")
}

/**
 * 3 стадии роста питомца по ТЗ Департамента финансов.
 */
enum class GrowthStage(
    val title: String,
    val requiredPoints: Int,
    val visualScale: Float
) {
    BABY("Малыш", 0, 0.8f),
    JUNIOR("Подросший", 5, 1.0f),
    ADULT("Взрослый", 10, 1.2f)
}

/**
 * Эмоциональное состояние питомца в игре (реакция на действия ребенка).
 */
enum class PetMood(
    val title: String,
    val emoji: String,
    val reactionText: String
) {
    HAPPY("Счастливый", "✨", "Радостно прыгает и мурчит!"),
    HUNGRY("Проголодался", "🥣", "Смотрит на пустую миску и ждёт вкусняшку."),
    PLAYFUL("Игривый", "🎈", "Принёс мячик и хочет веселиться!"),
    PROUD_SAVER("Гордый накопитель", "🏺", "Любуется звонкой копилкой на мечту!"),
    SLEEPY("Уютный", "💤", "Свернулся клубочком на коврике.")
}

/**
 * Типы предметов гардероба.
 */
enum class AccessorySlot {
    HEAD,       // Шапочки, кепки, бантики
    NECK,       // Ошейники с колокольчиком, шарфики
    GLASSES     // Модные очки
}

/**
 * Предмет гардероба для кастомизации питомца.
 */
data class WardrobeItem(
    val id: String,
    val name: String,
    val slot: AccessorySlot,
    val emoji: String,
    val price: Int,
    val isUnlocked: Boolean = false
)

/**
 * Полный профиль питомца.
 */
data class PetProfile(
    val name: String = "Финни",
    val species: PetSpecies = PetSpecies.CAT,
    val pattern: ColorPattern = ColorPattern.CLASSIC,
    val growthStage: GrowthStage = GrowthStage.BABY,
    val growthPoints: Int = 0,
    val mood: PetMood = PetMood.HAPPY,
    val isHungry: Boolean = false,
    val equippedAccessories: Map<AccessorySlot, String> = emptyMap(), // slot -> itemId
    val unlockedWardrobeIds: Set<String> = emptySet()
)
