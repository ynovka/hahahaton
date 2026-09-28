package ltd.kyss.petme

import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameEngine
import ltd.kyss.petme.core.model.*
import org.junit.Assert.*
import org.junit.Test

class GameEngineTest {

    @Test
    fun newGameUsesChosenPetAndResetsPreviousProgress() {
        val started = GameEngine.startNewGame(
            GameEngine.createInitialState().copy(wallet = Wallet(1, 99)),
            PetSpecies.OWL,
            ColorPattern.SPOTTED,
            "  Умка  "
        )

        assertTrue(started.isGameStarted)
        assertEquals(PetSpecies.OWL, started.pet.species)
        assertEquals(ColorPattern.SPOTTED, started.pet.pattern)
        assertEquals("Умка", started.pet.name)
        assertEquals(Wallet(100, 0), started.wallet)
        assertEquals("Финни", GameEngine.startNewGame(GameEngine.createInitialState(), PetSpecies.CAT, ColorPattern.CLASSIC, " ").pet.name)
    }

    @Test
    fun leavingHomeRequiresConfirmedFinancialPlan() {
        val initial = GameEngine.startNewGame(GameEngine.createInitialState(), PetSpecies.CAT, ColorPattern.CLASSIC, "Финни")
        assertEquals(GameLocation.MyRoom, GameEngine.changeLocation(initial, GameLocation.CityMap).currentLocation)
        val confirmed = GameEngine.confirmBudget(initial)
        assertEquals(GameLocation.CityMap, GameEngine.changeLocation(confirmed, GameLocation.CityMap).currentLocation)
    }

    @Test
    fun lessonRequiresCorrectAnswerAndOpensFriendsTaskInOrder() {
        val initial = GameEngine.createInitialState()

        val wrong = GameEngine.answerLesson(initial, "needs_wants", 1)
        assertFalse(wrong.second)
        assertTrue(wrong.first.completedLessonIds.isEmpty())

        val blockedTask = GameEngine.answerPuzzle(initial, 1, "p1_opt1")
        assertFalse(blockedTask.second)
        assertEquals(PuzzleState.AVAILABLE, blockedTask.first.puzzles.getValue(1).state)

        val passed = GameEngine.answerLesson(initial, "needs_wants", 0)
        assertTrue(passed.second)
        assertTrue("needs_wants" in passed.first.completedLessonIds)
        assertTrue(GameEngine.answerPuzzle(passed.first, 1, "p1_opt1").second)

        val lockedLaterLesson = GameEngine.answerLesson(initial, "budget", 0)
        assertFalse(lockedLaterLesson.second)
    }

    @Test
    fun planMustAllocateAllCoinsAndSavingsLeaveWallet() {
        var state = GameEngine.createInitialState()
        state = GameEngine.allocateCoins(state, CoinJarType.FOOD_AND_CARE, -10)
        assertFalse(GameEngine.confirmBudget(state).budget.isConfirmed)

        state = GameEngine.allocateCoins(state, CoinJarType.PIGGY_BANK, 10)
        state = GameEngine.confirmBudget(state)
        assertTrue(state.budget.isConfirmed)
        assertEquals(40, state.budget.foodAndCareCoins)
        assertEquals(30, state.budget.funAndGamesCoins)
        assertEquals(30, state.wallet.savings)
        assertEquals(70, state.wallet.coins)
        assertEquals(state, GameEngine.confirmBudget(state))
    }

    @Test
    fun shopSpendsOnlyFromTheMatchingEnvelope() {
        var state = GameEngine.confirmBudget(GameEngine.createInitialState())
        state = GameEngine.buyShopItem(state, "food_kibble")

        assertEquals(55, state.wallet.coins)
        assertEquals(25, state.budget.foodAndCareCoins)
        assertEquals(30, state.budget.funAndGamesCoins)

        state = GameEngine.buyShopItem(state, "item_hat_cap")
        assertEquals(35, state.wallet.coins)
        assertEquals(25, state.budget.foodAndCareCoins)
        assertEquals(10, state.budget.funAndGamesCoins)

        val rejectedToy = GameEngine.buyShopItem(state, "toy_mouse")
        assertEquals(state.wallet, rejectedToy.wallet)
        assertEquals(state.budget, rejectedToy.budget)
    }

    @Test
    fun purchasesRequirePlanAndReusableToyCannotBeBoughtTwice() {
        val initial = GameEngine.createInitialState()
        val blockedPurchase = GameEngine.buyShopItem(initial, "food_kibble")
        assertEquals(initial.wallet, blockedPurchase.wallet)
        assertTrue(blockedPurchase.inventory.isEmpty())

        val withToy = GameEngine.buyShopItem(GameEngine.confirmBudget(initial), "toy_ball")
        val duplicate = GameEngine.buyShopItem(withToy, "toy_ball")
        assertEquals(withToy.wallet, duplicate.wallet)
        assertEquals(1, duplicate.inventory.count { it.id == "toy_ball" })
    }

    @Test
    fun rewardGoesIntoJoyEnvelopeAndCannotBeClaimedTwice() {
        var state = GameEngine.confirmBudget(GameEngine.createInitialState())
        state = passLesson(state, "needs_wants")
        val solved = GameEngine.answerPuzzle(state, 1, "p1_opt1").first
        val rewarded = GameEngine.claimReward(solved, 1)

        assertEquals(100, rewarded.wallet.coins)
        assertEquals(50, rewarded.budget.funAndGamesCoins)
        assertEquals(PuzzleState.COMPLETED, rewarded.puzzles.getValue(1).state)
        assertEquals(rewarded, GameEngine.claimReward(rewarded, 1))
    }

    @Test
    fun bankProtectsCareEnvelopeAndReserveCanFundUrgentCare() {
        var state = GameEngine.confirmBudget(GameEngine.createInitialState())
        state = GameEngine.depositSavings(state, 20)

        assertEquals(Wallet(60, 40), state.wallet)
        assertEquals(10, state.budget.funAndGamesCoins)
        val rejectedDeposit = GameEngine.depositSavings(state, 15)
        assertEquals(state.wallet, rejectedDeposit.wallet)
        assertEquals(state.budget, rejectedDeposit.budget)

        state = GameEngine.withdrawSavings(state, 20)
        assertEquals(Wallet(80, 20), state.wallet)
        assertEquals(70, state.budget.foodAndCareCoins)
        assertEquals(10, state.budget.funAndGamesCoins)
    }

    @Test
    fun hospitalTreatmentUsesCareEnvelope() {
        var state = GameEngine.createInitialState().copy(period = 2)
        state = GameEngine.confirmBudget(state)
        state = GameEngine.runHospitalCheckup(state)
        assertEquals(PetHealth.NEEDS_TREATMENT, state.pet.health)

        val treated = GameEngine.treatPet(state)
        assertEquals(state.wallet.coins - GameEngine.TREATMENT_COST, treated.wallet.coins)
        assertEquals(state.budget.foodAndCareCoins - GameEngine.TREATMENT_COST, treated.budget.foodAndCareCoins)
        assertEquals(PetHealth.HEALTHY, treated.pet.health)
    }

    @Test
    fun periodCannotBeFinishedTwiceAndCreatesPlanForNewIncome() {
        val confirmed = GameEngine.confirmBudget(GameEngine.createInitialState())
        val finished = GameEngine.finishPeriod(confirmed)

        assertEquals(finished, GameEngine.finishPeriod(finished))
        assertEquals(2, finished.period)
        assertTrue(finished.puzzles.values.all { it.state == PuzzleState.AVAILABLE })
        assertFalse(finished.budget.isConfirmed)
        assertEquals(finished.wallet.coins, finished.budget.totalAllocated)
    }

    @Test
    fun petCareUsesSuppliesAndOnlyGivesGrowthOncePerPeriod() {
        var state = GameEngine.confirmBudget(GameEngine.createInitialState()).copy(pet = PetProfile(isHungry = true))
        state = GameEngine.buyShopItem(state, "food_kibble")
        val fed = GameEngine.careForPet(state, PetCareAction.FEED)

        assertFalse(fed.pet.isHungry)
        assertFalse(fed.inventory.any { it.id == "food_kibble" })
        assertEquals(fed.pet, GameEngine.careForPet(fed, PetCareAction.FEED).pet)
    }

    @Test
    fun movementStaysInsideRoomBounds() {
        var state = GameEngine.createInitialState()
        state = GameEngine.moveHero(state, -10f)
        assertTrue(state.heroX >= 0.08f)
        state = GameEngine.moveHeroTo(state, 2f)
        assertEquals(0.92f, state.heroX)
    }

    private fun passLesson(state: ltd.kyss.petme.core.engine.GameState, lessonId: String): ltd.kyss.petme.core.engine.GameState {
        val lesson = GameCatalog.financialLessons.first { it.id == lessonId }
        val answerIndex = lesson.checkOptions.indexOfFirst { it.isCorrect }
        return GameEngine.answerLesson(state, lessonId, answerIndex).first
    }
}
