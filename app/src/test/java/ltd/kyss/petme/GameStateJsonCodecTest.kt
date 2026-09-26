package ltd.kyss.petme

import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameEngine
import ltd.kyss.petme.core.model.*
import ltd.kyss.petme.core.persistence.GameStateJsonCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameStateJsonCodecTest {

    @Test
    fun roundTripKeepsEveryChangingPartOfTheGame() {
        var state = GameEngine.startNewGame(
            GameEngine.createInitialState(),
            PetSpecies.FOX,
            ColorPattern.SPOTTED,
            "Рыжуля"
        )
        state = GameEngine.buyShopItem(state, "food_kibble")
        state = GameEngine.careForPet(state, PetCareAction.FEED)
        state = GameEngine.answerPuzzle(state, 1, "p1_opt1").first
        state = GameEngine.claimReward(state, 1)
        state = GameEngine.confirmBudget(state)
        state = GameEngine.changeLocation(state, GameLocation.CityMap)
        state = GameEngine.changeLocation(state, GameLocation.FriendRoom(3))
        state = state.copy(
            heroX = 0.74f,
            activeGoalId = "goal_hammock",
            completedLessonIds = setOf("saving", "budget"),
            soundEnabled = false,
            largeFontEnabled = true,
            isAdultBarrierPassed = true,
            periodReport = "Проверочный отчёт",
            pet = state.pet.copy(
                cleanliness = 91,
                happiness = 84,
                health = PetHealth.NEEDS_TREATMENT,
                completedCareActions = setOf(PetCareAction.FEED, PetCareAction.PLAY),
                unlockedWardrobeIds = setOf("hat_cap"),
                equippedAccessories = mapOf(AccessorySlot.HEAD to "hat_cap")
            ),
            inventory = listOf(GameCatalog.shopCatalog.first { it.id == "toy_ball" })
        )

        val restored = GameStateJsonCodec.decode(GameStateJsonCodec.encode(state))

        assertEquals(state, restored)
    }

    @Test
    fun invalidOrIncompatibleSaveDoesNotRestore() {
        assertNull(GameStateJsonCodec.decode("not a save file"))
        assertNull(GameStateJsonCodec.decode("{\"schemaVersion\": 999}"))
    }
}
