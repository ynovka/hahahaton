package ltd.kyss.petme.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ltd.kyss.petme.core.engine.GameEngine
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.CoinJarType
import ltd.kyss.petme.core.model.ColorPattern
import ltd.kyss.petme.core.model.GameLocation
import ltd.kyss.petme.core.model.PetSpecies
import ltd.kyss.petme.core.model.PetCareAction

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameEngine.createInitialState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    fun startNewGame(species: PetSpecies, pattern: ColorPattern, petName: String) {
        _state.update { GameEngine.startNewGame(it, species, pattern, petName) }
    }

    fun moveHero(deltaX: Float) {
        _state.update { GameEngine.moveHero(it, deltaX) }
    }

    fun moveHeroTo(normalizedX: Float) {
        _state.update { GameEngine.moveHeroTo(it, normalizedX) }
    }

    fun changeLocation(location: GameLocation) {
        _state.update { GameEngine.changeLocation(it, location) }
    }

    fun completeLesson(lessonId: String) {
        _state.update { GameEngine.completeLesson(it, lessonId) }
    }

    fun careForPet(action: PetCareAction) {
        _state.update { GameEngine.careForPet(it, action) }
    }

    fun allocateCoins(jar: CoinJarType, delta: Int) {
        _state.update { GameEngine.allocateCoins(it, jar, delta) }
    }

    fun confirmBudget() {
        _state.update { GameEngine.confirmBudget(it) }
    }

    fun answerPuzzle(friendId: Int, optionId: String): Boolean {
        var isSuccess = false
        _state.update { current ->
            val (newState, success) = GameEngine.answerPuzzle(current, friendId, optionId)
            isSuccess = success
            newState
        }
        return isSuccess
    }

    fun claimPuzzleReward(friendId: Int) {
        _state.update { GameEngine.claimReward(it, friendId) }
    }

    fun buyShopItem(itemId: String) {
        _state.update { GameEngine.buyShopItem(it, itemId) }
    }

    fun selectGoal(goalId: String) {
        _state.update { GameEngine.selectGoal(it, goalId) }
    }

    fun depositSavings(amount: Int) {
        _state.update { GameEngine.depositSavings(it, amount) }
    }

    fun withdrawSavings(amount: Int) {
        _state.update { GameEngine.withdrawSavings(it, amount) }
    }

    fun runHospitalCheckup() {
        _state.update { GameEngine.runHospitalCheckup(it) }
    }

    fun treatPet() {
        _state.update { GameEngine.treatPet(it) }
    }

    fun toggleAccessory(accessoryId: String) {
        _state.update { GameEngine.toggleAccessory(it, accessoryId) }
    }

    fun finishPeriod() {
        _state.update { GameEngine.finishPeriod(it) }
    }

    fun resetDemo() {
        _state.update { GameEngine.resetDemo() }
    }

    fun toggleSound() {
        _state.update { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleLargeFont() {
        _state.update { it.copy(largeFontEnabled = !it.largeFontEnabled) }
    }
}
