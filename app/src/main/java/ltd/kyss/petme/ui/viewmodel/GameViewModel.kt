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
import ltd.kyss.petme.core.persistence.GameStateStorage

class GameViewModel(
    private val storage: GameStateStorage
) : ViewModel() {

    private val _state = MutableStateFlow(storage.load() ?: GameEngine.createInitialState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    fun startNewGame(species: PetSpecies, pattern: ColorPattern, petName: String) {
        updateState { GameEngine.startNewGame(it, species, pattern, petName) }
    }

    fun moveHero(deltaX: Float) {
        updateState { GameEngine.moveHero(it, deltaX) }
    }

    fun moveHeroTo(normalizedX: Float) {
        updateState { GameEngine.moveHeroTo(it, normalizedX) }
    }

    fun changeLocation(location: GameLocation) {
        updateState { GameEngine.changeLocation(it, location) }
    }

    fun answerLesson(lessonId: String, optionIndex: Int): Boolean {
        var isCorrect = false
        updateState { current ->
            val (newState, correct) = GameEngine.answerLesson(current, lessonId, optionIndex)
            isCorrect = correct
            newState
        }
        return isCorrect
    }

    fun careForPet(action: PetCareAction) {
        updateState { GameEngine.careForPet(it, action) }
    }

    fun allocateCoins(jar: CoinJarType, delta: Int) {
        updateState { GameEngine.allocateCoins(it, jar, delta) }
    }

    fun confirmBudget(): Boolean {
        var confirmed = false
        updateState {
            GameEngine.confirmBudget(it).also { updated -> confirmed = updated.budget.isConfirmed }
        }
        return confirmed
    }

    fun answerPuzzle(friendId: Int, optionId: String): Boolean {
        var isSuccess = false
        updateState { current ->
            val (newState, success) = GameEngine.answerPuzzle(current, friendId, optionId)
            isSuccess = success
            newState
        }
        return isSuccess
    }

    fun claimPuzzleReward(friendId: Int) {
        updateState { GameEngine.claimReward(it, friendId) }
    }

    fun buyShopItem(itemId: String) {
        updateState { GameEngine.buyShopItem(it, itemId) }
    }

    fun selectGoal(goalId: String) {
        updateState { GameEngine.selectGoal(it, goalId) }
    }

    fun depositSavings(amount: Int) {
        updateState { GameEngine.depositSavings(it, amount) }
    }

    fun withdrawSavings(amount: Int) {
        updateState { GameEngine.withdrawSavings(it, amount) }
    }

    fun runHospitalCheckup() {
        updateState { GameEngine.runHospitalCheckup(it) }
    }

    fun treatPet() {
        updateState { GameEngine.treatPet(it) }
    }

    fun toggleAccessory(accessoryId: String) {
        updateState { GameEngine.toggleAccessory(it, accessoryId) }
    }

    fun finishPeriod() {
        updateState { GameEngine.finishPeriod(it) }
    }

    fun resetDemo() {
        updateState { GameEngine.resetDemo() }
    }

    /** Новая история с чистым сохранением, вызывается только с титульного экрана. */
    fun startFreshGame() {
        storage.clear()
        _state.value = GameEngine.createInitialState()
    }

    fun toggleSound() {
        updateState { it.copy(soundEnabled = !it.soundEnabled) }
    }

    fun toggleLargeFont() {
        updateState { it.copy(largeFontEnabled = !it.largeFontEnabled) }
    }

    override fun onCleared() {
        storage.save(_state.value)
        super.onCleared()
    }

    private fun updateState(reducer: (GameState) -> GameState) {
        _state.update { current ->
            reducer(current).also(storage::save)
        }
    }
}
