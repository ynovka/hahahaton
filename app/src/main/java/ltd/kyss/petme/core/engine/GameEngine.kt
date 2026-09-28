package ltd.kyss.petme.core.engine

import ltd.kyss.petme.core.data.GameCatalog
import ltd.kyss.petme.core.model.*

/**
 * Игровое ядро: чистая логика правил игры, расчетов бюджета,
 * реакций питомца, детских головоломок и перемещения.
 */
object GameEngine {

    const val TREATMENT_COST = 30

    /**
     * Создание начального состояния игры.
     */
    fun createInitialState(): GameState = GameState()

    fun startNewGame(
        state: GameState,
        species: PetSpecies,
        pattern: ColorPattern,
        petName: String
    ): GameState {
        val safeName = petName.trim().take(16).ifBlank { "Финни" }
        return createInitialState().copy(
            isGameStarted = true,
            pet = PetProfile(name = safeName, species = species, pattern = pattern),
            currentLocation = GameLocation.MyRoom,
            advisorTip = "Знакомься: $safeName! Я Подручный: сначала помогу составить план, а потом познакомлю с уроками и заданиями друзей."
        )
    }

    /**
     * Перемещение персонажа в комнате (влево/вправо).
     */
    fun moveHero(state: GameState, deltaX: Float): GameState {
        val newX = (state.heroX + deltaX).coerceIn(0.08f, 0.92f)
        return state.copy(heroX = newX)
    }

    /** Перемещение в точку сцены, выбранную нажатием игрока. */
    fun moveHeroTo(state: GameState, normalizedX: Float): GameState {
        return state.copy(heroX = normalizedX.coerceIn(0.08f, 0.92f))
    }

    /**
     * Переход между комнатами и картой города.
     */
    fun changeLocation(state: GameState, newLocation: GameLocation): GameState {
        if (state.currentLocation == GameLocation.MyRoom &&
            newLocation == GameLocation.CityMap &&
            !state.budget.isConfirmed
        ) {
            return state.copy(advisorTip = "Перед выходом составь финансовый план на этот период. Так ты заранее решишь, сколько можно потратить и отложить.")
        }
        val tip = when (newLocation) {
            GameLocation.MyRoom -> "Ты дома с любимым питомцем! Можно заглянуть в гардероб или выйти в город."
            GameLocation.CityMap -> "Карта города! Выбирай, к кому пойти в гости: к друзьям, в Банк, Магазин или Больницу."
            GameLocation.Bank -> "Банк Финляндии! Тут сидит Бобёр-банкир. Можно пополнить копилку на мечту!"
            GameLocation.Shop -> "Лавка Енотика! Здесь полезный корм, яркие игрушки и модные шапочки."
            GameLocation.Hospital -> "Уютная лечебница! Доктор Сова проверит здоровье питомца."
            is GameLocation.FriendRoom -> {
                val friend = GameCatalog.friendsList.find { it.id == newLocation.friendId }
                "${friend?.name ?: "Друг"}: «${friend?.greetingText ?: "Привет!"}»"
            }
        }
        return state.copy(
            currentLocation = newLocation,
            heroX = 0.2f, // Появляется у входа
            advisorTip = tip
        )
    }

    fun answerLesson(state: GameState, lessonId: String, optionIndex: Int): Pair<GameState, Boolean> {
        val lesson = GameCatalog.financialLessons.find { it.id == lessonId } ?: return state to false
        if (lessonId in state.completedLessonIds) return state to true

        val lessonIndex = GameCatalog.financialLessons.indexOf(lesson)
        val previousLessons = GameCatalog.financialLessons.take(lessonIndex)
        if (previousLessons.any { it.id !in state.completedLessonIds }) {
            return state.copy(advisorTip = "Подручный предлагает идти по порядку: сначала пройди предыдущий урок.") to false
        }

        val answer = lesson.checkOptions.getOrNull(optionIndex)
            ?: return state to false
        if (!answer.isCorrect) {
            return state.copy(advisorTip = "Почти! ${answer.feedback}") to false
        }

        return state.copy(
            completedLessonIds = state.completedLessonIds + lessonId,
            advisorTip = "Урок пройден: «${lesson.title}». ${lesson.shortRule} Теперь можно брать задание у друга!"
        ) to true
    }

    fun careForPet(state: GameState, action: PetCareAction): GameState {
        if (action in state.pet.completedCareActions) {
            return state.copy(advisorTip = "${action.title} уже выполнено в этом периоде. Питомец доволен заботой!")
        }

        val requiredCategory = when (action) {
            PetCareAction.FEED -> ItemCategory.MANDATORY_FOOD
            PetCareAction.WASH -> ItemCategory.MANDATORY_CARE
            PetCareAction.PLAY -> ItemCategory.WANT_TOY
        }
        val item = state.inventory.firstOrNull { it.category == requiredCategory }
            ?: return state.copy(
                advisorTip = when (action) {
                    PetCareAction.FEED -> "Дома нет корма. Купи корм в Лавке Енотика и возвращайся к миске."
                    PetCareAction.WASH -> "Для ухода нужен шампунь или расчёска из Лавки Енотика."
                    PetCareAction.PLAY -> "Сначала купи питомцу игрушку. Игрушки не расходуются и останутся дома."
                }
            )

        val consumesItem = action != PetCareAction.PLAY
        val updatedInventory = if (consumesItem) {
            state.inventory.toMutableList().also { it.remove(item) }
        } else state.inventory

        val updatedPet = when (action) {
            PetCareAction.FEED -> state.pet.copy(isHungry = false, mood = PetMood.HAPPY)
            PetCareAction.WASH -> state.pet.copy(cleanliness = 100, mood = PetMood.HAPPY)
            PetCareAction.PLAY -> state.pet.copy(happiness = 100, mood = PetMood.PLAYFUL)
        }.copy(
            completedCareActions = state.pet.completedCareActions + action,
            growthPoints = state.pet.growthPoints + 1
        )

        return state.copy(
            inventory = updatedInventory,
            pet = updatedPet,
            advisorTip = "${action.emoji} ${action.title}: ${item.title}. Забота выполнена, питомец получил +1 очко роста!"
        )
    }

    /**
     * Раскладывание монет по горшочкам (Обязательное / Желания / Копилка).
     */
    fun allocateCoins(state: GameState, jar: CoinJarType, delta: Int): GameState {
        if (state.budget.isConfirmed) return state // План уже утвержден

        val b = state.budget
        var newFood = b.foodAndCareCoins
        var newFun = b.funAndGamesCoins
        var newPiggy = b.piggyBankCoins

        when (jar) {
            CoinJarType.FOOD_AND_CARE -> newFood = (newFood + delta).coerceAtLeast(0)
            CoinJarType.FUN_AND_GAMES -> newFun = (newFun + delta).coerceAtLeast(0)
            CoinJarType.PIGGY_BANK -> newPiggy = (newPiggy + delta).coerceAtLeast(0)
        }

        // Защита: нельзя распределить больше, чем есть начальных монет
        if (newFood + newFun + newPiggy > b.totalStartingCoins) {
            return state.copy(
                advisorTip = "Ой! Монетки в кошельке закончились. Уменьши другие горшочки, чтобы добавить сюда."
            )
        }

        val updatedBudget = b.copy(
            foodAndCareCoins = newFood,
            funAndGamesCoins = newFun,
            piggyBankCoins = newPiggy
        )

        // Подсказки помощника в зависимости от баланса (Правило 50/30/20)
        val tip = when {
            newFood == 0 -> "Внимание! Миска пустая — питомцу нечего будет кушать! Добавь монеток в Заботу."
            newPiggy > 0 && newPiggy >= b.totalStartingCoins * 0.2f -> "Супер! Ты отложил отличную сумму в копилку на мечту! Копилка звенит от радости! 🏺✨"
            newFun > b.totalStartingCoins * 0.5f -> "Ого, в сундучке радостей гора монет! А на корм и копилку точно хватит?"
            else -> "Отличное распределение! Жми кнопку «Утвердить план», когда будешь готов."
        }

        return state.copy(
            budget = updatedBudget,
            advisorTip = tip
        )
    }

    /**
     * Утверждение плана бюджета на период.
     */
    fun confirmBudget(state: GameState): GameState {
        val b = state.budget
        if (b.isConfirmed) return state
        if (b.totalStartingCoins != state.wallet.coins || b.totalAllocated != state.wallet.coins) {
            return state.copy(advisorTip = "Распредели все ${state.wallet.coins} монет: ни одна монетка не должна остаться без назначения.")
        }
        if (!b.isValid) {
            return state.copy(advisorTip = "Нельзя утвердить пустую миску! Добавь монеты на еду и заботу о питомце.")
        }

        // Переводим запланированные сбережения прямо в копилку
        val updatedSavings = state.wallet.savings + b.piggyBankCoins
        val remainingCoins = state.wallet.coins - b.piggyBankCoins

        return state.copy(
            budget = b.copy(isConfirmed = true),
            wallet = state.wallet.copy(
                coins = remainingCoins,
                savings = updatedSavings
            ),
            pet = state.pet.copy(mood = PetMood.HAPPY),
            transactions = if (b.piggyBankCoins > 0) {
                state.transactions + MoneyTransaction(state.period, "По плану в копилку", -b.piggyBankCoins, MoneyTransactionType.SAVINGS_IN, "🏺")
            } else state.transactions,
            isPeriodFinished = false,
            periodReport = null,
            advisorTip = "План утверждён! В копилку отправилось ${b.piggyBankCoins} м., а на заботу осталось ${b.foodAndCareCoins} м. Теперь можно идти в город."
        )
    }

    fun selectGoal(state: GameState, goalId: String): GameState {
        val goal = state.dreamGoals.find { it.id == goalId } ?: return state
        return state.copy(activeGoalId = goal.id, advisorTip = "Твоя цель: ${goal.title}. Накопленные монеты сохранены.")
    }

    fun depositSavings(state: GameState, amount: Int): GameState {
        if (amount <= 0 || amount > state.wallet.coins || amount > Int.MAX_VALUE - state.wallet.savings) return state
        if (!state.budget.isConfirmed) {
            return state.copy(advisorTip = "Сначала утверди план — тогда Подручный поможет перевести свободные монеты в копилку.")
        }
        if (amount > state.budget.funAndGamesCoins) {
            return state.copy(advisorTip = "В сундучке радостей осталось только ${state.budget.funAndGamesCoins} м. Важные деньги на заботу лучше не трогать.")
        }
        return state.copy(
            wallet = state.wallet.copy(coins = state.wallet.coins - amount, savings = state.wallet.savings + amount),
            budget = state.budget.copy(funAndGamesCoins = state.budget.funAndGamesCoins - amount),
            pet = state.pet.copy(mood = PetMood.PROUD_SAVER),
            transactions = state.transactions + MoneyTransaction(state.period, "Пополнение копилки", -amount, MoneyTransactionType.SAVINGS_IN, "🏺"),
            advisorTip = "В копилку переведено $amount монет. Ты стал ближе к цели!"
        )
    }

    fun withdrawSavings(state: GameState, amount: Int): GameState {
        if (amount <= 0 || amount > state.wallet.savings || amount > Int.MAX_VALUE - state.wallet.coins) return state
        if (!state.budget.isConfirmed) return state
        return state.copy(
            wallet = state.wallet.copy(coins = state.wallet.coins + amount, savings = state.wallet.savings - amount),
            budget = state.budget.copy(foodAndCareCoins = state.budget.foodAndCareCoins + amount),
            transactions = state.transactions + MoneyTransaction(state.period, "Снятие из копилки", amount, MoneyTransactionType.SAVINGS_OUT, "👛"),
            advisorTip = "$amount монет из резерва добавлены в конверт «Забота». В копилке осталось ${state.wallet.savings - amount}."
        )
    }

    fun runHospitalCheckup(state: GameState): GameState {
        if (state.pet.lastCheckupPeriod == state.period) {
            return state.copy(advisorTip = "Доктор Сова уже осматривала питомца в этом периоде.")
        }

        val plannedCareCase = state.period % 2 == 0
        val health = if (plannedCareCase) PetHealth.NEEDS_TREATMENT else state.pet.health
        val tip = if (health == PetHealth.NEEDS_TREATMENT) {
            "Осмотр завершён: питомцу нужна простая процедура за $TREATMENT_COST монет. Это плановая ситуация, а не наказание."
        } else {
            "Осмотр завершён: питомец здоров! Регулярная забота помогает заранее планировать расходы."
        }

        return state.copy(
            pet = state.pet.copy(
                health = health,
                lastCheckupPeriod = state.period,
                mood = if (health == PetHealth.HEALTHY) PetMood.HAPPY else PetMood.SLEEPY
            ),
            advisorTip = tip
        )
    }

    fun treatPet(state: GameState): GameState {
        if (state.pet.health != PetHealth.NEEDS_TREATMENT) {
            return state.copy(advisorTip = "Лечение сейчас не требуется. Доктор Сова советует приходить на осмотр каждый период.")
        }
        if (!state.budget.isConfirmed) {
            return state.copy(advisorTip = "Сначала утверди финансовый план на период.")
        }
        if (!state.wallet.canAfford(TREATMENT_COST) || state.budget.foodAndCareCoins < TREATMENT_COST) {
            val available = state.budget.foodAndCareCoins
            return state.copy(
                advisorTip = "Для лечения нужно $TREATMENT_COST монет из конверта «Забота». Там сейчас $available м. Можно пополнить его из резерва в Банке."
            )
        }

        return state.copy(
            wallet = state.wallet.copy(coins = state.wallet.coins - TREATMENT_COST),
            budget = state.budget.copy(foodAndCareCoins = state.budget.foodAndCareCoins - TREATMENT_COST),
            pet = state.pet.copy(
                health = PetHealth.HEALTHY,
                mood = PetMood.HAPPY,
                growthPoints = state.pet.growthPoints + 1
            ),
            transactions = state.transactions + MoneyTransaction(state.period, "Лечение питомца", -TREATMENT_COST, MoneyTransactionType.EXPENSE, "🏥"),
            advisorTip = "Процедура прошла отлично! Питомец здоров, а ты заранее учёл важный расход на заботу."
        )
    }

    /**
     * Ответ на головоломку друга.
     */
    fun answerPuzzle(state: GameState, friendId: Int, optionId: String): Pair<GameState, Boolean> {
        val puzzle = state.puzzles[friendId] ?: return state to false
        if (puzzle.state != PuzzleState.AVAILABLE) return state to false
        val requiredLesson = GameCatalog.lessonForFriend(friendId)
        if (requiredLesson != null && requiredLesson.id !in state.completedLessonIds) {
            return state.copy(
                advisorTip = "Перед заданием ${GameCatalog.friendsList.find { it.id == friendId }?.name ?: "друга"} пройди урок Подручного «${requiredLesson.title}»."
            ) to false
        }

        val option = puzzle.options.find { it.id == optionId } ?: return state to false

        return if (option.isCorrect) {
            val updatedPuzzle = puzzle.copy(state = PuzzleState.SOLVED_UNCLAIMED)
            val updatedPuzzles = state.puzzles + (friendId to updatedPuzzle)
            val nextState = state.copy(
                puzzles = updatedPuzzles,
                advisorTip = "Правильно! ${option.feedbackText} Забирай награду у друга!"
            )
            nextState to true
        } else {
            val nextState = state.copy(
                advisorTip = "Не совсем так. ${option.feedbackText} Попробуй подумать ещё раз!"
            )
            nextState to false
        }
    }

    /**
     * Получение награды за решенную головоломку (защита от повторного начисления).
     */
    fun claimReward(state: GameState, friendId: Int): GameState {
        val puzzle = state.puzzles[friendId] ?: return state
        if (puzzle.state != PuzzleState.SOLVED_UNCLAIMED) return state

        val updatedPuzzle = puzzle.copy(state = PuzzleState.COMPLETED)
        val updatedWallet = state.wallet.copy(coins = state.wallet.coins + puzzle.rewardCoins)
        val updatedBudget = if (state.budget.isConfirmed) {
            state.budget.copy(funAndGamesCoins = state.budget.funAndGamesCoins + puzzle.rewardCoins)
        } else {
            state.budget
        }
        val updatedPet = state.pet.copy(
            mood = PetMood.PLAYFUL,
            growthPoints = state.pet.growthPoints + 1
        )

        return state.copy(
            puzzles = state.puzzles + (friendId to updatedPuzzle),
            wallet = updatedWallet,
            budget = updatedBudget,
            pet = updatedPet,
            transactions = state.transactions + MoneyTransaction(state.period, "Награда за задание", puzzle.rewardCoins, MoneyTransactionType.INCOME, "🎯"),
            advisorTip = "Ура! Ты получил +${puzzle.rewardCoins} монет за смекалку. Подручный добавил их в сундучок радостей — можешь потратить или перевести в копилку! 🎉"
        )
    }

    /**
     * Покупка товара в магазине.
     */
    fun buyShopItem(state: GameState, itemId: String): GameState {
        val item = state.shopItems.find { it.id == itemId } ?: return state
        if (!state.budget.isConfirmed) {
            return state.copy(advisorTip = "Сначала утверди финансовый план. Он подскажет, сколько монет можно потратить на каждую категорию.")
        }
        if (item.wardrobeItem != null && item.wardrobeItem.id in state.pet.unlockedWardrobeIds) {
            return state.copy(advisorTip = "«${item.title}» уже есть в гардеробе. Выбери другую покупку.")
        }
        if (item.category == ItemCategory.WANT_TOY && state.inventory.any { it.id == item.id }) {
            return state.copy(advisorTip = "«${item.title}» уже дома. Игрушки можно использовать много раз, вторую покупать не нужно.")
        }
        if (!state.wallet.canAfford(item.price)) {
            val missing = item.price - state.wallet.coins
            return state.copy(
                advisorTip = "Не хватает ${missing} монет на «${item.title}». Сходи к друзьям выполнить интересное задание!"
            )
        }

        val paysFromCare = item.category == ItemCategory.MANDATORY_FOOD || item.category == ItemCategory.MANDATORY_CARE
        val availableInEnvelope = if (paysFromCare) state.budget.foodAndCareCoins else state.budget.funAndGamesCoins
        val envelopeTitle = if (paysFromCare) "Забота" else "Радости"
        if (availableInEnvelope < item.price) {
            return state.copy(
                advisorTip = "На «${item.title}» нужно ${item.price} м., а в конверте «$envelopeTitle» осталось $availableInEnvelope м. Проверь план или заработай награду у друзей."
            )
        }

        val newCoins = state.wallet.coins - item.price
        val updatedInventory = state.inventory + item
        val updatedBudget = if (paysFromCare) {
            state.budget.copy(foodAndCareCoins = state.budget.foodAndCareCoins - item.price)
        } else {
            state.budget.copy(funAndGamesCoins = state.budget.funAndGamesCoins - item.price)
        }

        var updatedPet = state.pet

        // Если куплена вещь для гардероба — разблокируем в шкафу!
        if (item.wardrobeItem != null) {
            val unlocked = updatedPet.unlockedWardrobeIds + item.wardrobeItem.id
            updatedPet = updatedPet.copy(unlockedWardrobeIds = unlocked)
        }

        return state.copy(
            wallet = state.wallet.copy(coins = newCoins),
            budget = updatedBudget,
            inventory = updatedInventory,
            pet = updatedPet,
            transactions = state.transactions + MoneyTransaction(state.period, item.title, -item.price, MoneyTransactionType.EXPENSE, item.emoji),
            advisorTip = "Успешная покупка: «${item.title}» за ${item.price} монет! ${item.description}"
        )
    }

    /**
     * Примерка / снятие аксессуара в гардеробе дома.
     */
    fun toggleAccessory(state: GameState, accessoryId: String): GameState {
        val item = GameCatalog.wardrobeAccessories.find { it.id == accessoryId } ?: return state
        val equipped = state.pet.equippedAccessories.toMutableMap()

        if (equipped[item.slot] == accessoryId) {
            equipped.remove(item.slot) // Снимаем
        } else {
            equipped[item.slot] = accessoryId // Надеваем
        }

        return state.copy(
            pet = state.pet.copy(
                equippedAccessories = equipped,
                mood = PetMood.HAPPY
            ),
            advisorTip = "Питомец примерил: ${item.name} ${item.emoji}! Выглядит просто потрясающе!"
        )
    }

    /**
     * Завершение текущего периода и переход к следующему (1..5).
     */
    fun finishPeriod(state: GameState): GameState {
        if (state.isPeriodFinished) return state
        if (!state.budget.isConfirmed) {
            return state.copy(advisorTip = "Сначала нужно утвердить план монет на этот период!")
        }

        // Оцениваем финансовые решения за период (рост питомца)
        var pointsEarned = 0
        val report = StringBuilder("Итоги периода #${state.period}:\n")

        // 1. Покормили ли питомца?
        if (!state.pet.isHungry) {
            pointsEarned += 2
            report.append("✅ Питомец сыт и счастлив (+2 очка роста)\n")
        } else {
            report.append("⚠️ Питомец остался голодным (надо купить корм в магазине)\n")
        }

        // 2. Отложили ли монеты в копилку?
        if (state.budget.piggyBankCoins > 0) {
            pointsEarned += 2
            report.append("✅ Сберегли ${state.budget.piggyBankCoins} монет в копилку (+2 очка роста)\n")
        }

        // 3. Выполнили ли задания друзей?
        val solvedCount = state.puzzles.values.count { it.state == PuzzleState.COMPLETED }
        if (solvedCount > 0) {
            pointsEarned += 1
            report.append("✅ Решены задания у друзей (+1 очко роста)\n")
        }

        if (PetCareAction.WASH in state.pet.completedCareActions) {
            pointsEarned += 1
            report.append("✅ Питомец чистый и ухоженный (+1 очко роста)\n")
        }
        if (PetCareAction.PLAY in state.pet.completedCareActions) {
            pointsEarned += 1
            report.append("✅ Вы нашли время поиграть вместе (+1 очко роста)\n")
        }

        val totalPoints = state.pet.growthPoints + pointsEarned

        // Проверяем стадию роста (Малыш -> Подросший -> Взрослый)
        val newGrowthStage = when {
            totalPoints >= GrowthStage.ADULT.requiredPoints -> GrowthStage.ADULT
            totalPoints >= GrowthStage.JUNIOR.requiredPoints -> GrowthStage.JUNIOR
            else -> GrowthStage.BABY
        }

        val nextPeriod = (state.period + 1).coerceAtMost(5)
        val nextAllowance = if (state.period < 5) 100 else 0
        val newCoins = state.wallet.coins + nextAllowance

        return state.copy(
            period = nextPeriod,
            wallet = state.wallet.copy(coins = newCoins),
            budget = BudgetDistribution.forIncome(newCoins),
            puzzles = GameCatalog.createInitialPuzzles(),
            transactions = if (nextAllowance > 0) {
                state.transactions + MoneyTransaction(nextPeriod, "Карманные деньги", nextAllowance, MoneyTransactionType.INCOME, "🪙")
            } else state.transactions,
            pet = state.pet.copy(
                growthPoints = totalPoints,
                growthStage = newGrowthStage,
                isHungry = true,
                cleanliness = (state.pet.cleanliness - 30).coerceAtLeast(30),
                happiness = (state.pet.happiness - 20).coerceAtLeast(40),
                completedCareActions = emptySet()
            ),
            isPeriodFinished = true,
            periodReport = report.toString(),
            advisorTip = if (state.period < 5) {
                "Ура! Период #${state.period} завершён! Получено +$pointsEarned очков роста. Карманные деньги на новый период: +$nextAllowance монет! ✨"
            } else {
                "Все пять периодов пройдены! Питомец вырос, а твои финансовые решения сохранены в итогах. 🎉"
            }
        )
    }

    /**
     * Сброс в демо-режиме (быстрый рестарт для жюри и тестов).
     */
    fun resetDemo(): GameState = createInitialState()
}
