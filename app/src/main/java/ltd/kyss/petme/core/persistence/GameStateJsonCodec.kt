package ltd.kyss.petme.core.persistence

import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.engine.GameState
import ltd.kyss.petme.core.model.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Версионированный формат сохранения. В файл попадает только изменяемое состояние:
 * задания, товары и цели восстанавливаются из GameCatalog по своим ID.
 */
object GameStateJsonCodec {
    private const val SCHEMA_VERSION = 1

    fun encode(state: GameState): String = JSONObject()
        .put("schemaVersion", SCHEMA_VERSION)
        .put("isGameStarted", state.isGameStarted)
        .put("period", state.period)
        .put("pet", encodePet(state.pet))
        .put("wallet", JSONObject()
            .put("coins", state.wallet.coins)
            .put("savings", state.wallet.savings)
        )
        .put("budget", JSONObject()
            .put("totalStartingCoins", state.budget.totalStartingCoins)
            .put("foodAndCareCoins", state.budget.foodAndCareCoins)
            .put("funAndGamesCoins", state.budget.funAndGamesCoins)
            .put("piggyBankCoins", state.budget.piggyBankCoins)
            .put("isConfirmed", state.budget.isConfirmed)
        )
        .put("location", encodeLocation(state.currentLocation))
        .put("heroX", state.heroX.toDouble())
        .put("puzzleStates", JSONObject().apply {
            state.puzzles.forEach { (friendId, puzzle) -> put(friendId.toString(), puzzle.state.name) }
        })
        .put("activeGoalId", state.activeGoalId)
        .put("inventoryIds", JSONArray(state.inventory.map { it.id }))
        .put("transactions", JSONArray().apply {
            state.transactions.forEach { transaction ->
                put(JSONObject()
                    .put("period", transaction.period)
                    .put("title", transaction.title)
                    .put("amount", transaction.amount)
                    .put("type", transaction.type.name)
                    .put("emoji", transaction.emoji)
                )
            }
        })
        .put("completedLessonIds", JSONArray(state.completedLessonIds.toList()))
        .put("advisorTip", state.advisorTip)
        .put("isPeriodFinished", state.isPeriodFinished)
        .put("periodReport", state.periodReport ?: JSONObject.NULL)
        .put("soundEnabled", state.soundEnabled)
        .put("largeFontEnabled", state.largeFontEnabled)
        .put("isAdultBarrierPassed", state.isAdultBarrierPassed)
        .toString()

    /**
     * Возвращает null, если файл повреждён либо создан другой версией приложения.
     * Вызывающий код тогда начинает новую игру, а не падает на старте.
     */
    fun decode(raw: String): GameState? = runCatching {
        val root = JSONObject(raw)
        require(root.getInt("schemaVersion") == SCHEMA_VERSION)

        val defaultState = GameState()
        val pet = decodePet(root.getJSONObject("pet"))
        val walletJson = root.getJSONObject("wallet")
        val budgetJson = root.getJSONObject("budget")
        val location = decodeLocation(root.getJSONObject("location"))
        val puzzles = decodePuzzles(root.getJSONObject("puzzleStates"), defaultState.puzzles)
        val inventory = decodeInventory(root.getJSONArray("inventoryIds"))
        val transactions = decodeTransactions(root.getJSONArray("transactions"))
        val completedLessons = decodeStringSet(root.getJSONArray("completedLessonIds"))
        require(completedLessons.all { lessonId -> GameCatalog.financialLessons.any { it.id == lessonId } })

        GameState(
            isGameStarted = root.getBoolean("isGameStarted"),
            period = root.getInt("period").coerceIn(1, 5),
            pet = pet,
            wallet = Wallet(
                coins = walletJson.getInt("coins").coerceAtLeast(0),
                savings = walletJson.getInt("savings").coerceAtLeast(0)
            ),
            budget = BudgetDistribution(
                totalStartingCoins = budgetJson.getInt("totalStartingCoins").coerceAtLeast(0),
                foodAndCareCoins = budgetJson.getInt("foodAndCareCoins").coerceAtLeast(0),
                funAndGamesCoins = budgetJson.getInt("funAndGamesCoins").coerceAtLeast(0),
                piggyBankCoins = budgetJson.getInt("piggyBankCoins").coerceAtLeast(0),
                isConfirmed = budgetJson.getBoolean("isConfirmed")
            ),
            currentLocation = location,
            heroX = root.getDouble("heroX").toFloat().coerceIn(0.08f, 0.92f),
            puzzles = puzzles,
            shopItems = GameCatalog.shopCatalog,
            dreamGoals = GameCatalog.dreamGoals,
            activeGoalId = root.getString("activeGoalId").also { goalId ->
                require(GameCatalog.dreamGoals.any { it.id == goalId })
            },
            inventory = inventory,
            transactions = transactions,
            completedLessonIds = completedLessons,
            advisorTip = root.getString("advisorTip"),
            isPeriodFinished = root.getBoolean("isPeriodFinished"),
            periodReport = root.optionalString("periodReport"),
            soundEnabled = root.getBoolean("soundEnabled"),
            largeFontEnabled = root.getBoolean("largeFontEnabled"),
            isAdultBarrierPassed = root.getBoolean("isAdultBarrierPassed")
        )
    }.getOrNull()

    private fun encodePet(pet: PetProfile): JSONObject = JSONObject()
        .put("name", pet.name)
        .put("species", pet.species.name)
        .put("pattern", pet.pattern.name)
        .put("growthStage", pet.growthStage.name)
        .put("growthPoints", pet.growthPoints)
        .put("mood", pet.mood.name)
        .put("isHungry", pet.isHungry)
        .put("health", pet.health.name)
        .put("lastCheckupPeriod", pet.lastCheckupPeriod)
        .put("cleanliness", pet.cleanliness)
        .put("happiness", pet.happiness)
        .put("completedCareActions", JSONArray(pet.completedCareActions.map { it.name }))
        .put("equippedAccessories", JSONObject().apply {
            pet.equippedAccessories.forEach { (slot, accessoryId) -> put(slot.name, accessoryId) }
        })
        .put("unlockedWardrobeIds", JSONArray(pet.unlockedWardrobeIds.toList()))

    private fun decodePet(json: JSONObject): PetProfile {
        val unlocked = decodeStringSet(json.getJSONArray("unlockedWardrobeIds"))
        val knownAccessoryIds = GameCatalog.wardrobeAccessories.map { it.id }.toSet()
        require(unlocked.all(knownAccessoryIds::contains))

        val equippedJson = json.getJSONObject("equippedAccessories")
        val equipped = buildMap {
            equippedJson.keys().forEach { key ->
                val slot = enumValue<AccessorySlot>(key)
                val accessoryId = equippedJson.getString(key)
                require(accessoryId in knownAccessoryIds)
                put(slot, accessoryId)
            }
        }
        require(equipped.values.all { it in unlocked })

        return PetProfile(
            name = json.getString("name").trim().take(16).ifBlank { "Финни" },
            species = enumValue(json.getString("species")),
            pattern = enumValue(json.getString("pattern")),
            growthStage = enumValue(json.getString("growthStage")),
            growthPoints = json.getInt("growthPoints").coerceAtLeast(0),
            mood = enumValue(json.getString("mood")),
            isHungry = json.getBoolean("isHungry"),
            health = enumValue(json.getString("health")),
            lastCheckupPeriod = json.getInt("lastCheckupPeriod").coerceIn(0, 5),
            cleanliness = json.getInt("cleanliness").coerceIn(0, 100),
            happiness = json.getInt("happiness").coerceIn(0, 100),
            completedCareActions = decodeStringSet(json.getJSONArray("completedCareActions"))
                .mapTo(linkedSetOf()) { enumValue<PetCareAction>(it) },
            equippedAccessories = equipped,
            unlockedWardrobeIds = unlocked
        )
    }

    private fun encodeLocation(location: GameLocation): JSONObject = JSONObject().apply {
        when (location) {
            GameLocation.MyRoom -> put("type", "myRoom")
            GameLocation.CityMap -> put("type", "cityMap")
            GameLocation.Bank -> put("type", "bank")
            GameLocation.Shop -> put("type", "shop")
            GameLocation.Hospital -> put("type", "hospital")
            is GameLocation.FriendRoom -> {
                put("type", "friendRoom")
                put("friendId", location.friendId)
            }
        }
    }

    private fun decodeLocation(json: JSONObject): GameLocation = when (json.getString("type")) {
        "myRoom" -> GameLocation.MyRoom
        "cityMap" -> GameLocation.CityMap
        "bank" -> GameLocation.Bank
        "shop" -> GameLocation.Shop
        "hospital" -> GameLocation.Hospital
        "friendRoom" -> {
            val friendId = json.getInt("friendId")
            require(GameCatalog.friendsList.any { it.id == friendId })
            GameLocation.FriendRoom(friendId)
        }
        else -> error("Unknown game location")
    }

    private fun decodePuzzles(
        json: JSONObject,
        initialPuzzles: Map<Int, KidPuzzle>
    ): Map<Int, KidPuzzle> = initialPuzzles.mapValues { (friendId, puzzle) ->
        val savedState = enumValue<PuzzleState>(json.getString(friendId.toString()))
        puzzle.copy(state = savedState)
    }.also { restored ->
        require(json.length() == restored.size)
    }

    private fun decodeInventory(json: JSONArray): List<ShopItem> {
        val catalogById = GameCatalog.shopCatalog.associateBy { it.id }
        return buildList {
            for (index in 0 until json.length()) {
                val itemId = json.getString(index)
                add(requireNotNull(catalogById[itemId]) { "Unknown shop item" })
            }
        }
    }

    private fun decodeTransactions(json: JSONArray): List<MoneyTransaction> = buildList {
        for (index in 0 until json.length()) {
            val transaction = json.getJSONObject(index)
            add(
                MoneyTransaction(
                    period = transaction.getInt("period").coerceIn(1, 5),
                    title = transaction.getString("title"),
                    amount = transaction.getInt("amount"),
                    type = enumValue(transaction.getString("type")),
                    emoji = transaction.getString("emoji")
                )
            )
        }
    }

    private fun decodeStringSet(json: JSONArray): Set<String> = buildSet {
        for (index in 0 until json.length()) add(json.getString(index))
    }

    private fun JSONObject.optionalString(key: String): String? =
        if (has(key) && !isNull(key)) getString(key) else null

    private inline fun <reified T : Enum<T>> enumValue(name: String): T =
        enumValues<T>().firstOrNull { it.name == name }
            ?: error("Unknown enum value")
}
