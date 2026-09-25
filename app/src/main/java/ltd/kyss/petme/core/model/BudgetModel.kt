package ltd.kyss.petme.core.model

/**
 * Игровые "ёмкости" / сундучки для раскладывания монет (детский бюджет).
 */
enum class CoinJarType(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val isMandatory: Boolean
) {
    FOOD_AND_CARE(
        id = "food_care",
        title = "Миска и Забота",
        subtitle = "Обязательное: вкусный корм, витамины, шампунь",
        emoji = "🥣",
        isMandatory = true
    ),
    FUN_AND_GAMES(
        id = "fun_games",
        title = "Сундучок Радостей",
        subtitle = "Желания: новые мячики, вкусняшки, развлечения",
        emoji = "🎁",
        isMandatory = false
    ),
    PIGGY_BANK(
        id = "piggy_bank",
        title = "Копилка на Мечту",
        subtitle = "Сбережения: копим на домик или игровую площадку",
        emoji = "🏺",
        isMandatory = false
    )
}

/**
 * Большая цель накопления питомца (по ТЗ минимум 3 цели).
 */
data class DreamGoal(
    val id: String,
    val title: String,
    val targetCoins: Int,
    val emoji: String,
    val description: String,
    val isAchieved: Boolean = false
)

/**
 * Кошелек игрока: доступные монеты и монеты в копилке.
 */
data class Wallet(
    val coins: Int = 100,            // Доступные монеты для трат и бюджета
    val savings: Int = 0            // Монеты, надежно спрятанные в копилке на цель
) {
    fun canAfford(amount: Int): Boolean = coins >= amount
    fun canWithdrawFromSavings(amount: Int): Boolean = savings >= amount
}

/**
 * Распределение монет по игровым горшочкам в начале периода.
 */
data class BudgetDistribution(
    val totalStartingCoins: Int = 100,
    val foodAndCareCoins: Int = 50,
    val funAndGamesCoins: Int = 30,
    val piggyBankCoins: Int = 20,
    val isConfirmed: Boolean = false
) {
    val totalAllocated: Int get() = foodAndCareCoins + funAndGamesCoins + piggyBankCoins
    val unallocated: Int get() = totalStartingCoins - totalAllocated
    val isValid: Boolean get() = totalAllocated <= totalStartingCoins && foodAndCareCoins > 0

    // Проверка правила 50/30/20 в детской форме
    val foodRatio: Float get() = if (totalStartingCoins > 0) foodAndCareCoins.toFloat() / totalStartingCoins else 0f
    val funRatio: Float get() = if (totalStartingCoins > 0) funAndGamesCoins.toFloat() / totalStartingCoins else 0f
    val savingsRatio: Float get() = if (totalStartingCoins > 0) piggyBankCoins.toFloat() / totalStartingCoins else 0f
}
