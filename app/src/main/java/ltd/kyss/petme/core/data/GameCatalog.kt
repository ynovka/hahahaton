package ltd.kyss.petme.core.data

import ltd.kyss.petme.core.model.*

object GameCatalog {

    val financialLessons = listOf(
        FinancialLesson(
            "needs_wants", "Нужное и желаемое", "🧺",
            "Сначала оплачивают то, без чего нельзя обойтись: еду, здоровье и уход. Развлечения выбирают только из оставшихся денег.",
            "Сначала нужное, затем желания.", 1,
            "У Финни осталось 25 монет. Что купить в первую очередь?",
            listOf(
                LessonCheckOption("Корм для питомца", true, "Верно: еда и здоровье важнее желаний."),
                LessonCheckOption("Светящийся мячик", false, "Мячик радует, но сначала Финни нужно поесть."),
                LessonCheckOption("Праздничный колпак", false, "Наряд подождёт, пока закрыты важные нужды.")
            ),
            advisorHint = "Если денег не хватит на всё, подумай, что понадобится дома уже сегодня."
        ),
        FinancialLesson(
            "saving", "Копилка и регулярные сбережения", "🏺",
            "Если откладывать небольшую часть каждого дохода, большая цель становится достижимой без отказа от всех радостей.",
            "Откладывай часть каждого дохода.", 2,
            "Тебе дали 50 монет. Какой план поможет приблизиться к мечте?",
            listOf(
                LessonCheckOption("Потратить все 50 сегодня", false, "Тогда на будущую мечту ничего не останется."),
                LessonCheckOption("Отложить 10 монет, а остальное распределить", true, "Отлично: даже маленький регулярный взнос растит копилку."),
                LessonCheckOption("Спрятать все деньги и ничего не планировать", false, "Копить важно, но полезные траты тоже нужно заранее учесть.")
            ),
            advisorHint = "Не обязательно тратить всё сразу: часть можно использовать сейчас, а часть сохранить на будущее."
        ),
        FinancialLesson(
            "compare", "Сравнение цен", "🏷️",
            "Сравнивай полную стоимость и количество товара. Яркая упаковка не всегда означает выгодную покупку.",
            "Считай общую цену перед покупкой.", 3,
            "Один большой пакет стоит 24 монеты. Два маленьких стоят по 15. Как выгоднее?",
            listOf(
                LessonCheckOption("Взять большой пакет за 24", true, "Верно: 24 меньше, чем 15 + 15 = 30."),
                LessonCheckOption("Взять два маленьких за 30", false, "Посчитай вместе: 15 + 15 = 30, это дороже."),
                LessonCheckOption("Выбрать самый яркий пакет", false, "Сначала сравни цену и количество, а потом дизайн.")
            ),
            advisorHint = "Сложи цены маленьких упаковок и сравни результат с ценой большой."
        ),
        FinancialLesson(
            "safety", "Финансовая безопасность", "🛡️",
            "Нельзя отдавать деньги и данные незнакомцам, даже если они обещают быстрый выигрыш. В сомнительной ситуации позови взрослого.",
            "Слишком выгодное обещание нужно проверить.", 4,
            "Незнакомец обещает превратить 20 монет в 200 уже завтра. Что делать?",
            listOf(
                LessonCheckOption("Отдать монеты, чтобы не упустить шанс", false, "Обещание слишком лёгких денег — повод остановиться."),
                LessonCheckOption("Отказаться и рассказать взрослому", true, "Верно: деньги и личные данные незнакомым людям не отдают."),
                LessonCheckOption("Сообщить номер карты питомца", false, "Никакие данные нельзя сообщать незнакомцам.")
            ),
            advisorHint = "Если незнакомец обещает много денег почти без усилий, остановись и позови взрослого."
        ),
        FinancialLesson(
            "exact_change", "Точный расчёт", "🧮",
            "Сложение монет помогает проверить стоимость, сдачу и понять, хватает ли денег на покупку.",
            "Посчитай сумму до оплаты.", 5,
            "Альбом стоит 18 монет. Какими монетами можно заплатить без сдачи?",
            listOf(
                LessonCheckOption("10 + 5 + 3", true, "Да: 10 + 5 + 3 = 18."),
                LessonCheckOption("10 + 5 + 5", false, "Это 20 монет, получится переплата."),
                LessonCheckOption("10 + 3", false, "Это только 13 монет, суммы не хватает.")
            ),
            advisorHint = "Начни с самой крупной монеты и посчитай, сколько ещё нужно добавить до цены."
        ),
        FinancialLesson(
            "emergency", "Резерв на неожиданности", "🛟",
            "Небольшой запас денег помогает оплатить срочный ремонт или лечение, не отказываясь от важной цели.",
            "Резерв защищает от неожиданных расходов.", 6,
            "Питомцу понадобился внеплановый осмотр. Откуда безопаснее взять деньги?",
            listOf(
                LessonCheckOption("Из резерва в копилке", true, "Верно: резерв существует именно для важных неожиданностей."),
                LessonCheckOption("Потратить деньги, отложенные на корм", false, "Тогда появится новая важная проблема — питомец останется без еды."),
                LessonCheckOption("Взять у незнакомца без условий", false, "Незнакомые предложения денег могут быть небезопасны.")
            ),
            advisorHint = "Вспомни, для чего хранят небольшой запас: иногда срочная трата появляется неожиданно."
        ),
        FinancialLesson(
            "budget", "План расходов", "📊",
            "Раздели деньги на обязательные расходы, желания и накопления. План можно менять до подтверждения.",
            "Планируй до того, как тратить.", 7,
            "Какой план из 100 монет оставляет место и заботе, и мечте?",
            listOf(
                LessonCheckOption("50 на заботу, 30 на радости, 20 в копилку", true, "Отличный стартовый план 50/30/20."),
                LessonCheckOption("0 на заботу, 100 на игрушки", false, "В таком плане Финни останется без важного ухода."),
                LessonCheckOption("100 на игрушки, а план потом", false, "План нужен до покупок, чтобы не потратить всё случайно.")
            ),
            advisorHint = "Сложи цены всего набора и проверь, что сумма не больше доступного бюджета."
        )
    )

    fun lessonForFriend(friendId: Int): FinancialLesson? =
        financialLessons.find { it.relatedFriendId == friendId }

    val dreamGoals = listOf(
        DreamGoal(
            id = "goal_castle",
            title = "Кошачий/Щенячий Замок",
            targetCoins = 100,
            emoji = "🏰",
            description = "Двухэтажный домик с мягкой лежанкой и когтеточкой!"
        ),
        DreamGoal(
            id = "goal_playground",
            title = "Весёлая Игровая Площадка",
            targetCoins = 150,
            emoji = "🎡",
            description = "Качели, туннель и полоса препятствий для активных игр!"
        ),
        DreamGoal(
            id = "goal_hammock",
            title = "Райский Уголок с Гамаком",
            targetCoins = 200,
            emoji = "🌴",
            description = "Уютное место для сна с гирляндами и мягким ковриком!"
        )
    )

    val wardrobeAccessories = listOf(
        WardrobeItem("hat_party", "Праздничный колпак", AccessorySlot.HEAD, "🎉", 15),
        WardrobeItem("hat_cap", "Спортивная кепка", AccessorySlot.HEAD, "🧢", 20),
        WardrobeItem("hat_crown", "Золотая корона", AccessorySlot.HEAD, "👑", 50),
        WardrobeItem("neck_bell", "Ошейник с колокольчиком", AccessorySlot.NECK, "🔔", 15),
        WardrobeItem("neck_scarf", "Тёплый шарфик", AccessorySlot.NECK, "🧣", 25),
        WardrobeItem("neck_bow", "Красный бантик", AccessorySlot.NECK, "🎀", 20),
        WardrobeItem("glasses_sun", "Крутые солнечные очки", AccessorySlot.GLASSES, "🕶️", 30),
        WardrobeItem("glasses_smart", "Очки учёного", AccessorySlot.GLASSES, "👓", 35)
    )

    val shopCatalog: List<ShopItem> = listOf(
        // Обязательные расходы: Еда (минимум 2 варианта по ТЗ)
        ShopItem("food_kibble", "Хрустящий полезный корм", ItemCategory.MANDATORY_FOOD, 25, "🥣", "Сытный обед с витаминами для энергии на весь день!"),
        ShopItem("food_delight", "Премиум паштет с рыбкой/мясом", ItemCategory.MANDATORY_FOOD, 35, "🥫", "Нежнейшее лакомство, от которого питомец громко мурчит!"),

        // Обязательные расходы: Уход (минимум 2 варианта по ТЗ)
        ShopItem("care_shampoo", "Фруктовый шампунь с пеной", ItemCategory.MANDATORY_CARE, 20, "🧴", "Шёрстка становится шелковистой и приятно пахнет клубникой!"),
        ShopItem("care_brush", "Мягкая расчёска-массажка", ItemCategory.MANDATORY_CARE, 25, "🪮", "Бережно распутывает узелки и делает питомцу приятный массаж."),

        // Желания: Игрушки и радости
        ShopItem("toy_ball", "Светящийся попрыгунчик", ItemCategory.WANT_TOY, 15, "🎾", "Мячик отскакивает от стен, питомец гоняется за ним с восторгом!"),
        ShopItem("toy_mouse", "Заводная мышка на колёсиках", ItemCategory.WANT_TOY, 20, "🐭", "Бегает по полу кругами, тренируя ловкость и реакцию!"),

        // Предметы гардероба
        ShopItem("item_hat_cap", "Спортивная кепка", ItemCategory.WARDROBE_ACCESSORY, 20, "🧢", "Защищает от солнца и выглядит очень стильно!", wardrobeAccessories[1]),
        ShopItem("item_neck_bow", "Красный бантик", ItemCategory.WARDROBE_ACCESSORY, 20, "🎀", "Превращает питомца в настоящего джентльмена или леди!", wardrobeAccessories[5]),
        ShopItem("item_glasses_sun", "Крутые солнечные очки", ItemCategory.WARDROBE_ACCESSORY, 30, "🕶️", "Питомец в них выглядит как суперзвезда!", wardrobeAccessories[6])
    )

    val friendsList: List<FriendCharacter> = listOf(
        FriendCharacter(
            id = 1,
            name = "Мишка Потап",
            species = "Медвежонок",
            emoji = "🐻",
            houseName = "Берлога со сладостями",
            greetingText = "Я иду на ярмарку. Хочется купить хлопушку и леденцы, но дома закончились еда и мыло. С чего начать?",
            puzzlePrompt = "Что положим в корзину сначала?",
            rewardCoins = 20,
            friendshipLevel = 1,
            friendshipExp = 40,
            favoriteItems = listOf("🍯", "🍎", "🪵"),
            perkTitle = "Крепкий Запас",
            perkDescription = "Увеличивает вместимость копилки на 50 монет",
            isPerkUnlocked = false,
            nextLevelReward = "Кепка туриста",
            nextLevelRewardEmoji = "🧢"
        ),
        FriendCharacter(
            id = 2,
            name = "Белочка Рыжик",
            species = "Белочка",
            emoji = "🐿️",
            houseName = "Домик на высоком дубе",
            greetingText = "Я нашла десять орешков! Хочу съесть все, но зимой находить еду трудно. Как поступить?",
            puzzlePrompt = "Что сделаем с орешками?",
            rewardCoins = 20,
            friendshipLevel = 1,
            friendshipExp = 60,
            favoriteItems = listOf("🌰", "🍄", "🌲"),
            perkTitle = "Золотой Орешек",
            perkDescription = "+10% к монетам в копилке в конце каждого периода",
            isPerkUnlocked = false,
            nextLevelReward = "Тёплый шарфик",
            nextLevelRewardEmoji = "🧣"
        ),
        FriendCharacter(
            id = 3,
            name = "Енотик Тёма",
            species = "Енот",
            emoji = "🦝",
            houseName = "Мастерская находок",
            greetingText = "Мне нужны батончики одинакового веса: один большой стоит 25 монет, а два маленьких — по 15. Какая покупка дешевле?",
            puzzlePrompt = "Что купим?",
            rewardCoins = 20,
            friendshipLevel = 1,
            friendshipExp = 30,
            favoriteItems = listOf("🧼", "🍇", "✨"),
            perkTitle = "Искусство Торга",
            perkDescription = "Скидка 15% на игрушки в лавке покупок",
            isPerkUnlocked = false,
            nextLevelReward = "Солнечные очки",
            nextLevelRewardEmoji = "🕶️"
        ),
        FriendCharacter(
            id = 4,
            name = "Совушка София",
            species = "Сова",
            emoji = "🦉",
            houseName = "Книжная башня",
            greetingText = "В парке незнакомец предложил отдать 20 монет и обещает завтра вернуть 200. Можно ли ему доверять?",
            puzzlePrompt = "Что мне ответить?",
            rewardCoins = 20,
            friendshipLevel = 1,
            friendshipExp = 50,
            favoriteItems = listOf("📚", "🍵", "🕯️"),
            perkTitle = "Мудрый Совет",
            perkDescription = "Автоматическая подсказка в сложных задачах",
            isPerkUnlocked = false,
            nextLevelReward = "Очки учёного",
            nextLevelRewardEmoji = "👓"
        ),
        FriendCharacter(
            id = 5,
            name = "Лисичка Алиса",
            species = "Лисичка",
            emoji = "🦊",
            houseName = "Уютная нора с камином",
            greetingText = "Альбом стоит 18 монет. Я хочу заплатить ровно 18, чтобы не ждать сдачу. Поможешь сложить монеты?",
            puzzlePrompt = "Какой набор даёт ровно 18?",
            rewardCoins = 20,
            friendshipLevel = 1,
            friendshipExp = 25,
            favoriteItems = listOf("🎨", "🍓", "🎀"),
            perkTitle = "Быстрый Кэшбэк",
            perkDescription = "Возврат +2 монет при любой покупке в лавке",
            isPerkUnlocked = false,
            nextLevelReward = "Красный бантик",
            nextLevelRewardEmoji = "🎀"
        ),
        FriendCharacter(
            id = 6,
            name = "Зайчик Сеня",
            species = "Зайка",
            emoji = "🐰",
            houseName = "Морковный домик",
            greetingText = "У самоката лопнула шина. Ремонт стоит 15 монет, и кататься пока нельзя. Где взять деньги?",
            puzzlePrompt = "Как оплатить ремонт?",
            rewardCoins = 20,
            friendshipLevel = 1,
            friendshipExp = 70,
            favoriteItems = listOf("🥕", "🛴", "🌿"),
            perkTitle = "Страховой Резерв",
            perkDescription = "Бесплатный еженедельный осмотр в больнице",
            isPerkUnlocked = false,
            nextLevelReward = "Ошейник с колокольчиком",
            nextLevelRewardEmoji = "🔔"
        ),
        FriendCharacter(
            id = 7,
            name = "Щенок Барбос",
            species = "Щенок",
            emoji = "🐶",
            houseName = "Дом с зелёной полянкой",
            greetingText = "На угощение для дня рождения у меня 40 монет. Хочу купить и пирог, и сок для друзей. Поможешь выбрать?",
            puzzlePrompt = "Что купим к празднику?",
            rewardCoins = 20,
            friendshipLevel = 1,
            friendshipExp = 80,
            favoriteItems = listOf("🦴", "🎾", "🎈"),
            perkTitle = "Праздничный Бонус",
            perkDescription = "+10 дополнительных монет карманных денег каждый период",
            isPerkUnlocked = false,
            nextLevelReward = "Золотая корона",
            nextLevelRewardEmoji = "👑"
        )
    )

    fun createInitialPuzzles(): Map<Int, KidPuzzle> = mapOf(
        1 to KidPuzzle(
            friendId = 1,
            title = "Умная корзинка Мишки",
            storyPrompt = "Мишка хочет накупить всего подряд на ярмарке. Что обязательно нужно взять сначала?",
            emoji = "🧺",
            options = listOf(
                PuzzleOption("p1_opt1", "Хлеб, яблоки и мыло", "🍎", true, "Точно! Это полезные продукты и гигиена — самое главное!"),
                PuzzleOption("p1_opt2", "Огромную хлопушку и 5 леденцов", "🧨", false, "Хлопушка взорвётся за секунду, а обедать будет нечем!")
            ),
            rewardCoins = 20,
            successExplanation = "Отлично! Сначала покупаем необходимое (еду и уход), а сладости — если останутся монеты.",
            errorExplanation = "Если потратить всё на сладости и хлопушки, дома не останется полезной еды."
        ),
        2 to KidPuzzle(
            friendId = 2,
            title = "Запасы Белочки Рыжика",
            storyPrompt = "Рыжик нашла 10 золотых орешков. Как поступить, чтобы зимой не голодать?",
            emoji = "🌰",
            options = listOf(
                PuzzleOption("p2_opt1", "Съесть все 10 прямо сейчас!", "😋", false, "Сейчас вкусно, но зимой в лесу будет пусто и холодно!"),
                PuzzleOption("p2_opt2", "Спрятать 3-4 орешка в дупло-копилку", "🏺", true, "Умница! Часть съели с радостью, а запас остался на будущее!")
            ),
            rewardCoins = 20,
            successExplanation = "Правило копилки: если откладывать часть с каждого дохода, у тебя всегда будут сбережения!",
            errorExplanation = "Тратить всё до последней копейки опасно: завтра может случиться день без находок."
        ),
        3 to KidPuzzle(
            friendId = 3,
            title = "Выгодная полка Енотика",
            storyPrompt = "В лавке продаются батончики одинакового веса: 1 большой за 25 монет или 2 маленьких по 15 монет. Что выгоднее?",
            emoji = "🏷️",
            options = listOf(
                PuzzleOption("p3_opt1", "1 большой за 25 монет", "👍", true, "Верно! 25 монет меньше, чем 15 + 15 = 30 монет!"),
                PuzzleOption("p3_opt2", "2 маленьких по 15 монет", "👎", false, "15 + 15 = 30 монет. Это на 5 монет дороже!")
            ),
            rewardCoins = 20,
            successExplanation = "Сравнивая общую цену, ты сберег 5 монет на следующую покупку!",
            errorExplanation = "Всегда полезно сложить цены и проверить, нет ли переплаты за упаковку."
        ),
        4 to KidPuzzle(
            friendId = 4,
            title = "Мудрый совет Совушки",
            storyPrompt = "Незнакомец в парке шепчет: «Отдай мне 20 монет, а я завтра превращу их в 200 монет!». Что делать?",
            emoji = "🦉",
            options = listOf(
                PuzzleOption("p4_opt1", "Отказаться и рассказать родителям", "🛡️", true, "Правильно! Быстрых чудес не бывает, это уловка обманщиков!"),
                PuzzleOption("p4_opt2", "Отдать монетки и ждать чуда", "💸", false, "Ой! Незнакомец просто убежит с твоими монетками!")
            ),
            rewardCoins = 20,
            successExplanation = "Финансовая безопасность: никогда не отдавай деньги незнакомцам, которые обещают лёгкие богатства!",
            errorExplanation = "Бесплатный сыр бывает только в мышеловке. Всегда советуйся со взрослыми."
        ),
        5 to KidPuzzle(
            friendId = 5,
            title = "Точный расчёт Лисички",
            storyPrompt = "Красивый альбом для рисования стоит 18 монет. Какими монетками оплатить без сдачи?",
            emoji = "👛",
            options = listOf(
                PuzzleOption("p5_opt1", "10 + 5 + 2 + 1 монеты", "🪙", true, "10 + 5 = 15, 15 + 2 = 17, 17 + 1 = 18! Идеально!"),
                PuzzleOption("p5_opt2", "10 + 10 монет", "🪙", false, "Это 20 монет, продавцу придётся искать сдачу.")
            ),
            rewardCoins = 20,
            successExplanation = "Точный счёт помогает быстро расплачиваться и не терять сдачу в карманах!",
            errorExplanation = "Считать монеты разного достоинства — отличная тренировка для ума!"
        ),
        6 to KidPuzzle(
            friendId = 6,
            title = "Подушка безопасности Зайки",
            storyPrompt = "У самоката лопнула шина, ремонт стоит 15 монет. Откуда правильнее взять деньги?",
            emoji = "🛴",
            options = listOf(
                PuzzleOption("p6_opt1", "Из специальной копилки на непредвиденные случаи", "🏺", true, "Ура! Для этого копилка безопасности и создавалась!"),
                PuzzleOption("p6_opt2", "Продать любимый самокат на запчасти", "😢", false, "Жалко продавать самокат из-за одной только шины!")
            ),
            rewardCoins = 20,
            successExplanation = "Подушка безопасности спасает, когда случаются неожиданные поломки или срочные траты.",
            errorExplanation = "Без накоплений на черный день любая мелочь может стать большой проблемой."
        ),
        7 to KidPuzzle(
            friendId = 7,
            title = "Праздник у Щенка Барбоса",
            storyPrompt = "На угощения есть ровно 40 монет. Выбери набор, который поместится в эту сумму:",
            emoji = "🎂",
            options = listOf(
                PuzzleOption("p7_opt1", "Пирог с ягодами (25 м.) + Яблочный сок (15 м.)", "🥧", true, "25 + 15 = 40 монет! Вкусно, празднично и ровно по карману!"),
                PuzzleOption("p7_opt2", "Золотой трёхъярусный торт (50 м.)", "🎂", false, "Он стоит 50 монет, а у Барбоса всего 40. Денег не хватит!")
            ),
            rewardCoins = 20,
            successExplanation = "Праздник удался на славу, потому что ты уложился в бюджет без долгов!",
            errorExplanation = "Всегда приятно помечтать о гигантском торте, но покупать нужно то, на что хватает средств."
        )
    )
}
