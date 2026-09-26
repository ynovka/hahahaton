package ltd.kyss.petme

import ltd.kyss.petme.core.engine.GameEngine
import ltd.kyss.petme.core.model.*
import org.junit.Assert.*
import org.junit.Test

class GameEngineTest {

    @Test
    fun newGameUsesChosenPetAndSafeName() {
        val initial = GameEngine.createInitialState()
        assertFalse(initial.isGameStarted)

        val started = GameEngine.startNewGame(initial, PetSpecies.OWL, ColorPattern.SPOTTED, "  Умка  ")
        assertTrue(started.isGameStarted)
        assertEquals(PetSpecies.OWL, started.pet.species)
        assertEquals(ColorPattern.SPOTTED, started.pet.pattern)
        assertEquals("Умка", started.pet.name)

        val fallback = GameEngine.startNewGame(initial, PetSpecies.CAT, ColorPattern.CLASSIC, "   ")
        assertEquals("Финни", fallback.pet.name)
    }

    @Test
    fun confirmingBudgetTwiceDoesNotCreateMoney() {
        val confirmed = GameEngine.confirmBudget(GameEngine.createInitialState())
        assertEquals(confirmed, GameEngine.confirmBudget(confirmed))
    }

    @Test
    fun confirmingBudgetPreservesIncomeEarnedBeforeConfirmation() {
        val initial = GameEngine.createInitialState()
        val rewarded = GameEngine.claimReward(GameEngine.answerPuzzle(initial, 1, "p1_opt1").first, 1)
        val confirmed = GameEngine.confirmBudget(rewarded)
        assertEquals(rewarded.wallet.coins + rewarded.wallet.savings, confirmed.wallet.coins + confirmed.wallet.savings)
        assertEquals(100, confirmed.wallet.coins)
    }

    @Test
    fun confirmingBudgetDoesNotRestoreAlreadySpentCoins() {
        val purchased = GameEngine.buyShopItem(GameEngine.createInitialState(), "food_kibble")
        val result = GameEngine.confirmBudget(purchased)
        assertEquals(purchased.wallet, result.wallet)
        assertFalse(result.budget.isConfirmed)
    }

    @Test
    fun bankTransfersConserveMoneyAndRejectInvalidAmounts() {
        val initial = GameEngine.createInitialState()
        val saved = GameEngine.depositSavings(initial, 30)
        assertEquals(Wallet(70, 30), saved.wallet)
        assertEquals(Wallet(80, 20), GameEngine.withdrawSavings(saved, 10).wallet)
        listOf(-1, 0, 101, Int.MAX_VALUE).forEach { amount ->
            assertEquals(initial, GameEngine.depositSavings(initial, amount))
        }
        listOf(-1, 0, 31, Int.MAX_VALUE).forEach { amount ->
            assertEquals(saved, GameEngine.withdrawSavings(saved, amount))
        }
    }

    @Test
    fun changingGoalPreservesSavingsAndRejectsUnknownGoal() {
        val saved = GameEngine.depositSavings(GameEngine.createInitialState(), 20)
        val goal = saved.dreamGoals.last()
        val changed = GameEngine.selectGoal(saved, goal.id)
        assertEquals(goal.id, changed.activeGoalId)
        assertEquals(saved.wallet, changed.wallet)
        assertEquals(saved, GameEngine.selectGoal(saved, "missing"))
    }

    @Test
    fun hospitalCheckupIsOncePerPeriodAndDoesNotChargeMoney() {
        val initial = GameEngine.createInitialState()
        val checked = GameEngine.runHospitalCheckup(initial)
        assertEquals(initial.wallet, checked.wallet)
        assertEquals(1, checked.pet.lastCheckupPeriod)
        assertEquals(PetHealth.HEALTHY, checked.pet.health)
        assertEquals(checked, GameEngine.runHospitalCheckup(checked).copy(advisorTip = checked.advisorTip))
    }

    @Test
    fun plannedHospitalCaseCanBeTreatedForExactCost() {
        val periodTwo = GameEngine.createInitialState().copy(period = 2)
        val diagnosed = GameEngine.runHospitalCheckup(periodTwo)
        assertEquals(PetHealth.NEEDS_TREATMENT, diagnosed.pet.health)

        val treated = GameEngine.treatPet(diagnosed)
        assertEquals(periodTwo.wallet.coins - GameEngine.TREATMENT_COST, treated.wallet.coins)
        assertEquals(PetHealth.HEALTHY, treated.pet.health)
        assertEquals(diagnosed.pet.growthPoints + 1, treated.pet.growthPoints)
        assertEquals(treated, GameEngine.treatPet(treated).copy(advisorTip = treated.advisorTip))
    }

    @Test
    fun hospitalTreatmentDoesNotSpendMoneyWhenUnaffordable() {
        val diagnosed = GameEngine.runHospitalCheckup(
            GameEngine.createInitialState().copy(period = 2, wallet = Wallet(coins = 10, savings = 0))
        )
        val result = GameEngine.treatPet(diagnosed)
        assertEquals(diagnosed.wallet, result.wallet)
        assertEquals(PetHealth.NEEDS_TREATMENT, result.pet.health)
    }

    @Test
    fun testInitialState() {
        val state = GameEngine.createInitialState()
        assertEquals(1, state.period)
        assertEquals(100, state.wallet.coins)
        assertEquals(0, state.wallet.savings)
        assertEquals(PetSpecies.CAT, state.pet.species)
        assertEquals(GrowthStage.BABY, state.pet.growthStage)
        assertEquals(GameLocation.MyRoom, state.currentLocation)
        assertEquals(7, state.puzzles.size)
    }

    @Test
    fun testBudgetAllocationAndConfirmation() {
        var state = GameEngine.createInitialState()

        // Распределяем: 40 на еду, 30 на радости, 30 в копилку
        state = GameEngine.allocateCoins(state, CoinJarType.FOOD_AND_CARE, -10) // 50 - 10 = 40
        state = GameEngine.allocateCoins(state, CoinJarType.PIGGY_BANK, 10)     // 20 + 10 = 30

        assertEquals(40, state.budget.foodAndCareCoins)
        assertEquals(30, state.budget.funAndGamesCoins)
        assertEquals(30, state.budget.piggyBankCoins)

        // Утверждаем бюджет
        state = GameEngine.confirmBudget(state)
        assertTrue(state.budget.isConfirmed)

        // 30 монет должны были уйти в копилку (savings), 70 остаться в кошельке (coins)
        assertEquals(30, state.wallet.savings)
        assertEquals(70, state.wallet.coins)
    }

    @Test
    fun testFriendPuzzleSolvingAndClaimingReward() {
        var state = GameEngine.createInitialState()

        // Друг 1 (Мишка): правильный ответ на ярмарке - хлеб, яблоки и мыло ("p1_opt1")
        val (stateAfterAnswer, isSuccess) = GameEngine.answerPuzzle(state, 1, "p1_opt1")
        assertTrue(isSuccess)
        assertEquals(PuzzleState.SOLVED_UNCLAIMED, stateAfterAnswer.puzzles[1]?.state)

        // Забираем награду
        val initialCoins = stateAfterAnswer.wallet.coins
        state = GameEngine.claimReward(stateAfterAnswer, 1)

        assertEquals(PuzzleState.COMPLETED, state.puzzles[1]?.state)
        assertEquals(initialCoins + 20, state.wallet.coins)

        // Повторное нажатие НЕ должно выдавать монеты еще раз (защита от дублирования)
        val stateAfterSecondClaim = GameEngine.claimReward(state, 1)
        assertEquals(state.wallet.coins, stateAfterSecondClaim.wallet.coins)
    }

    @Test
    fun testShopPurchaseAndFeeding() {
        var state = GameEngine.createInitialState()
        // Делаем питомца голодным
        state = state.copy(pet = state.pet.copy(isHungry = true))

        // Покупаем корм за 25 монет
        val coinsBefore = state.wallet.coins
        state = GameEngine.buyShopItem(state, "food_kibble")

        assertEquals(coinsBefore - 25, state.wallet.coins)
        assertFalse(state.pet.isHungry) // Питомец накормлен!
        assertEquals(PetMood.HAPPY, state.pet.mood)
    }

    @Test
    fun testPetGrowthProgression() {
        var state = GameEngine.createInitialState()
        state = GameEngine.confirmBudget(state)

        // Завершаем периоды, зарабатываем очки роста
        // Период 1: питомец не голодный, копилка пополнена -> получаем очки
        state = state.copy(pet = state.pet.copy(isHungry = false))
        state = GameEngine.finishPeriod(state)
        assertEquals(2, state.period)
        assertTrue(state.pet.growthPoints > 0)

        // Если очков >= 5, стадия становится JUNIOR (Подросший)
        state = state.copy(pet = state.pet.copy(growthPoints = 6))
        state = GameEngine.confirmBudget(state)
        state = GameEngine.finishPeriod(state)

        assertEquals(GrowthStage.JUNIOR, state.pet.growthStage)

        // Если очков >= 10, стадия становится ADULT (Взрослый)
        state = state.copy(pet = state.pet.copy(growthPoints = 11))
        state = GameEngine.confirmBudget(state)
        state = GameEngine.finishPeriod(state)

        assertEquals(GrowthStage.ADULT, state.pet.growthStage)
    }

    @Test
    fun testHeroMovementBounds() {
        var state = GameEngine.createInitialState()

        // Двигаем персонажа сильно влево
        state = GameEngine.moveHero(state, -10.0f)
        assertTrue(state.heroX >= 0.08f)

        // Двигаем персонажа сильно вправо
        state = GameEngine.moveHero(state, 10.0f)
        assertTrue(state.heroX <= 0.92f)
    }
}
