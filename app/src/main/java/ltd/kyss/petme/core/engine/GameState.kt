package ltd.kyss.petme.core.engine

import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.model.*

/**
 * Единое неизменяемое состояние всей игры (Single Source of Truth).
 */
data class GameState(
    val isGameStarted: Boolean = false,
    val period: Int = 1,
    val pet: PetProfile = PetProfile(),
    val wallet: Wallet = Wallet(coins = 100, savings = 0),
    val budget: BudgetDistribution = BudgetDistribution(totalStartingCoins = 100),
    val currentLocation: GameLocation = GameLocation.MyRoom,
    val heroX: Float = 0.3f, // Положение персонажа в комнате (0.1 .. 0.9)
    val puzzles: Map<Int, KidPuzzle> = GameCatalog.createInitialPuzzles(),
    val shopItems: List<ShopItem> = GameCatalog.shopCatalog,
    val dreamGoals: List<DreamGoal> = GameCatalog.dreamGoals,
    val activeGoalId: String = "goal_castle",
    val inventory: List<ShopItem> = emptyList(),
    val advisorTip: String = "Привет! Давай разложим монетки по горшочкам: на вкусный корм, радости и в копилку на мечту! ✨",
    val isPeriodFinished: Boolean = false,
    val periodReport: String? = null,
    val soundEnabled: Boolean = true,
    val largeFontEnabled: Boolean = false,
    val isAdultBarrierPassed: Boolean = false
) {
    val activeGoal: DreamGoal? get() = dreamGoals.find { it.id == activeGoalId }
    val goalProgressFraction: Float
        get() {
            val goal = activeGoal ?: return 0f
            return (wallet.savings.toFloat() / goal.targetCoins).coerceIn(0f, 1f)
        }
}
