package ltd.kyss.petme.core.model

/**
 * Состояние головоломки у друга.
 */
enum class PuzzleState {
    AVAILABLE,          // Доступна для прохождения
    SOLVED_UNCLAIMED,   // Решена, но награда ещё не взята
    COMPLETED           // Пройдена, монеты начислены в кошелек
}

/**
 * Вариант ответа / выбора в детской головоломке.
 */
data class PuzzleOption(
    val id: String,
    val title: String,
    val emoji: String,
    val isCorrect: Boolean,
    val feedbackText: String
)

/**
 * Модель интерактивной детской мини-игры / головоломки.
 */
data class KidPuzzle(
    val friendId: Int,
    val title: String,
    val storyPrompt: String,
    val emoji: String,
    val options: List<PuzzleOption>,
    val rewardCoins: Int = 20,
    val state: PuzzleState = PuzzleState.AVAILABLE,
    val successExplanation: String,
    val errorExplanation: String
)
