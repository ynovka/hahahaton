// ============================================================================
// Игровой каталог контента «Питомец Финни»
// Включает 7 видов питомцев, 7 друзей, 7 обучающих заданий по 3 темам ТЗ,
// магазин (8+ товаров), 3 цели накопления, гардероб и уроки финансовой грамотности.
// ============================================================================

export const PET_SPECIES = [
  {
    id: 'cat',
    name: 'Котёнок',
    emoji: '🐱',
    desc: 'Любит мягкие пледы, солнечные лучики и тихое мурлыканье.',
    portrait: 'assets/pet_cat_portrait.png',
    idle: 'assets/pet_cat_idle.png',
    favoriteFood: 'Вкусный паштет с рыбкой'
  },
  {
    id: 'dog',
    name: 'Щенок',
    emoji: '🐶',
    desc: 'Преданный друг, весело виляет хвостиком и готов бегать за мячом.',
    portrait: 'assets/pet_dog_portrait.png',
    idle: 'assets/pet_dog_idle.png',
    favoriteFood: 'Хрустящий мясной корм'
  },
  {
    id: 'fox',
    name: 'Лисёнок',
    emoji: '🦊',
    desc: 'Любопытный озорник с пушистым рыжим хвостом и умным взглядом.',
    portrait: 'assets/pet_fox_portrait.png',
    idle: 'assets/pet_fox_idle.png',
    favoriteFood: 'Ягодное лакомство'
  },
  {
    id: 'panda',
    name: 'Панда',
    emoji: '🐼',
    desc: 'Добрый пухляш, обожает сочный бамбук и уютный дневной сон.',
    portrait: 'assets/pet_panda_portrait.png',
    idle: 'assets/pet_panda_idle.png',
    favoriteFood: 'Свежие побеги и витамины'
  },
  {
    id: 'bunny',
    name: 'Зайчик',
    emoji: '🐰',
    desc: 'Шустрый попрыгун с длинными ушками, любит хрустящие морковки.',
    portrait: 'assets/pet_bunny_portrait.png',
    idle: 'assets/pet_bunny_idle.png',
    favoriteFood: 'Морковные хрустяшки'
  },
  {
    id: 'raccoon',
    name: 'Енотик',
    emoji: '🦝',
    desc: 'Ловкий непоседа и чистюля, всегда находит интересные вещицы.',
    portrait: 'assets/pet_raccoon_portrait.png',
    idle: 'assets/pet_raccoon_idle.png',
    favoriteFood: 'Сладкие ореховые батончики'
  },
  {
    id: 'owl',
    name: 'Совёнок',
    emoji: '🦉',
    desc: 'Мудрый птенец с большими круглыми глазами и добрым сердцем.',
    portrait: 'assets/pet_owl_portrait.png',
    idle: 'assets/pet_owl_idle.png',
    favoriteFood: 'Зерновые снеки'
  }
];

export const COLOR_PATTERNS = [
  { id: 'classic', name: 'Классический' },
  { id: 'spotted', name: 'Пятнистый / Полосатый' }
];

export const GROWTH_STAGES = {
  BABY: { id: 'baby', title: 'Малыш', minPoints: 0, scale: 0.85, badge: '🍼 Малыш' },
  JUNIOR: { id: 'junior', title: 'Подросший', minPoints: 5, scale: 1.0, badge: '🌟 Подросший' },
  ADULT: { id: 'adult', title: 'Взрослый', minPoints: 10, scale: 1.18, badge: '👑 Взрослый' }
};

export const DREAM_GOALS = [
  {
    id: 'goal_castle',
    title: 'Кошачий/Щенячий Замок',
    targetCoins: 100,
    emoji: '🏰',
    description: 'Двухэтажный домик с мягкой лежанкой, смотровой башенкой и когтеточкой!'
  },
  {
    id: 'goal_playground',
    title: 'Весёлая Игровая Площадка',
    targetCoins: 150,
    emoji: '🎡',
    description: 'Качели, туннель и полоса препятствий для активных тренировок питомца!'
  },
  {
    id: 'goal_hammock',
    title: 'Райский Уголок с Гамаком',
    targetCoins: 200,
    emoji: '🌴',
    description: 'Уютный подвесной гамак с тёплыми гирляндами и мягким пушистым ковриком!'
  }
];

export const WARDROBE_ACCESSORIES = [
  { id: 'hat_party', name: 'Праздничный колпак', slot: 'head', emoji: '🎉', price: 15, asset: 'assets/item_hat_party.png' },
  { id: 'hat_cap', name: 'Спортивная кепка', slot: 'head', emoji: '🧢', price: 20, asset: null },
  { id: 'hat_crown', name: 'Золотая корона', slot: 'head', emoji: '👑', price: 50, asset: null },
  { id: 'neck_bell', name: 'Колокольчик на ленте', slot: 'neck', emoji: '🔔', price: 15, asset: null },
  { id: 'neck_scarf', name: 'Тёплый вязаный шарфик', slot: 'neck', emoji: '🧣', price: 25, asset: null },
  { id: 'neck_bow', name: 'Красный атласный бантик', slot: 'neck', emoji: '🎀', price: 20, asset: 'assets/item_neck_bow.png' },
  { id: 'glasses_sun', name: 'Крутые солнечные очки', slot: 'glasses', emoji: '🕶️', price: 30, asset: null },
  { id: 'glasses_smart', name: 'Очки профессора', slot: 'glasses', emoji: '👓', price: 35, asset: null }
];

export const SHOP_CATALOG = [
  // Обязательные расходы: Еда
  {
    id: 'food_kibble',
    title: 'Хрустящий полезный корм',
    category: 'food',
    categoryName: 'Обязательное: Еда',
    isMandatory: true,
    price: 25,
    emoji: '🥣',
    asset: 'assets/item_food_kibble.png',
    description: 'Сытный сбалансированный обед с витаминами для энергии на весь день.'
  },
  {
    id: 'food_delight',
    title: 'Премиум паштет с рыбкой и мясом',
    category: 'food',
    categoryName: 'Обязательное: Еда',
    isMandatory: true,
    price: 35,
    emoji: '🥫',
    asset: null,
    description: 'Нежнейшее лакомство для питомца из отборных натуральных продуктов.'
  },

  // Обязательные расходы: Уход и гигиена
  {
    id: 'care_shampoo',
    title: 'Фруктовый шампунь с мягкой пеной',
    category: 'care',
    categoryName: 'Обязательное: Уход',
    isMandatory: true,
    price: 20,
    emoji: '🧴',
    asset: 'assets/item_care_shampoo.png',
    description: 'Делает шерстку шелковистой, чистой и ароматной как спелая клубника.'
  },
  {
    id: 'care_brush',
    title: 'Мягкая массажная расчёска',
    category: 'care',
    categoryName: 'Обязательное: Уход',
    isMandatory: true,
    price: 25,
    emoji: '🪮',
    asset: null,
    description: 'Бережно распутывает узелки и делает питомцу расслабляющий массаж.'
  },

  // Желания: Игрушки и развлечения
  {
    id: 'toy_ball',
    title: 'Светящийся мяч-попрыгунчик',
    category: 'toy',
    categoryName: 'Желания: Игрушки',
    isMandatory: false,
    price: 15,
    emoji: '🎾',
    asset: 'assets/item_toy_ball.png',
    description: 'Ярко отскакивает от пола и стен, питомец гоняется за ним с восторгом!'
  },
  {
    id: 'toy_mouse',
    title: 'Заводная мышка на колёсиках',
    category: 'toy',
    categoryName: 'Желания: Игрушки',
    isMandatory: false,
    price: 20,
    emoji: '🐭',
    asset: null,
    description: 'Ездит по полу кругами, тренируя реакцию, скорость и ловкость.'
  },

  // Желания: Гардероб и наряды
  {
    id: 'item_hat_party',
    title: 'Праздничный колпак',
    category: 'wardrobe',
    categoryName: 'Наряды: Гардероб',
    isMandatory: false,
    price: 15,
    emoji: '🎉',
    asset: 'assets/item_hat_party.png',
    wardrobeId: 'hat_party',
    description: 'Яркий праздничный колпачок с помпоном для отличного настроения!'
  },
  {
    id: 'item_hat_cap',
    title: 'Спортивная кепка',
    category: 'wardrobe',
    categoryName: 'Наряды: Гардероб',
    isMandatory: false,
    price: 20,
    emoji: '🧢',
    wardrobeId: 'hat_cap',
    description: 'Защищает от жаркого солнца и придаёт крутой спортивный вид.'
  },
  {
    id: 'item_neck_bow',
    title: 'Красный атласный бантик',
    category: 'wardrobe',
    categoryName: 'Наряды: Гардероб',
    isMandatory: false,
    price: 20,
    emoji: '🎀',
    asset: 'assets/item_neck_bow.png',
    wardrobeId: 'neck_bow',
    description: 'Превращает питомца в настоящего элегантного джентльмена или леди!'
  },
  {
    id: 'item_glasses_sun',
    title: 'Крутые солнечные очки',
    category: 'wardrobe',
    categoryName: 'Наряды: Гардероб',
    isMandatory: false,
    price: 30,
    emoji: '🕶️',
    wardrobeId: 'glasses_sun',
    description: 'Тёмные очки со стильной оправой — в них питомец выглядит как кинозвезда!'
  }
];

export const FRIENDS_LIST = [
  {
    id: 1,
    name: 'Мишка Потап',
    species: 'Медвежонок',
    emoji: '🐻',
    houseName: 'Берлога со сладостями',
    portrait: 'assets/friend_1_portrait.png',
    idle: 'assets/friend_1_idle.png',
    bg: 'assets/bg_room_friend_1.png',
    theme: 'Планирование бюджета',
    greeting: 'Привет, сосед! Я как раз собираюсь на городскую ярмарку. Поможешь мне выбрать покупки с умом?',
    perk: 'Крепкий Запас (+50 к вместимости копилки)',
    favoriteItems: ['🍯 Медок', '🍎 Яблочки', '🪵 Еловые шишки'],
    color: '#FFB74D'
  },
  {
    id: 2,
    name: 'Белочка Рыжик',
    species: 'Белочка',
    emoji: '🐿️',
    houseName: 'Домик на высоком дубе',
    portrait: 'assets/friend_2_portrait.png',
    idle: 'assets/friend_2_idle.png',
    bg: 'assets/bg_room_friend_2.png',
    theme: 'Формирование сбережений',
    greeting: 'Ура, ты заглянул в гости! Я собрала сегодня целую корзину золотых лесных орешков!',
    perk: 'Золотой Орешек (+10% бонусных монет в копилке)',
    favoriteItems: ['🌰 Орешки', '🍄 Грибочки', '🌲 Желуди'],
    color: '#FF8A65'
  },
  {
    id: 3,
    name: 'Енотик Тёма',
    species: 'Енот',
    emoji: '🦝',
    houseName: 'Мастерская находок',
    portrait: 'assets/friend_3_portrait.png',
    idle: 'assets/friend_3_idle.png',
    bg: 'assets/bg_room_friend_3.png',
    theme: 'Платежи и покупки',
    greeting: 'Смотри, какие интересные вещи я приметил в каталоге! Только вот цены очень хитрые...',
    perk: 'Искусство Торга (Скидка 15% на игрушки)',
    favoriteItems: ['🧼 Мыльные пузыри', '🍇 Виноград', '✨ Блестяшки'],
    color: '#90A4AE'
  },
  {
    id: 4,
    name: 'Совушка София',
    species: 'Сова',
    emoji: '🦉',
    houseName: 'Книжная башня',
    portrait: 'assets/friend_4_portrait.png',
    idle: 'assets/friend_4_idle.png',
    bg: 'assets/bg_room_friend_4.png',
    theme: 'Финансовая безопасность',
    greeting: 'Здравствуй, юный друг! В мире денег нужно быть не только экономным, но и очень бдительным!',
    perk: 'Мудрый Совет (Подсказка в сложных ситуациях)',
    favoriteItems: ['📚 Книги сказок', '🍵 Травяной чай', '🕯️ Тёплая свеча'],
    color: '#BA68C8'
  },
  {
    id: 5,
    name: 'Лисичка Алиса',
    species: 'Лисичка',
    emoji: '🦊',
    houseName: 'Уютная нора с камином',
    portrait: 'assets/friend_5_portrait.png',
    idle: 'assets/friend_5_idle.png',
    bg: 'assets/bg_room_friend_5.png',
    theme: 'Точный расчёт монет',
    greeting: 'Привет! Хочу купить новую волшебную раскраску, но запуталась в монетах без сдачи. Выручишь?',
    perk: 'Быстрый Кэшбэк (+2 монеты при каждой покупке)',
    favoriteItems: ['🎨 Краски', '🍓 Земляника', '🎀 Ленточки'],
    color: '#FF7043'
  },
  {
    id: 6,
    name: 'Зайчик Сеня',
    species: 'Зайка',
    emoji: '🐰',
    houseName: 'Морковный домик',
    portrait: 'assets/friend_6_portrait.png',
    idle: 'assets/friend_6_idle.png',
    bg: 'assets/bg_room_friend_6.png',
    theme: 'Подушка безопасности',
    greeting: 'Ой-ой! Я катался на самокате по парку, и у меня спустило колесо! Что же теперь делать?',
    perk: 'Страховой Резерв (Бесплатный осмотр в больнице)',
    favoriteItems: ['🥕 Хрустящая морковка', '🛴 Самокаты', '🌿 Клевер'],
    color: '#81C784'
  },
  {
    id: 7,
    name: 'Щенок Барбос',
    species: 'Щенок',
    emoji: '🐶',
    houseName: 'Дом с зелёной полянкой',
    portrait: 'assets/friend_7_portrait.png',
    idle: 'assets/friend_7_idle.png',
    bg: 'assets/bg_room_friend_7.png',
    theme: 'Лимит бюджета',
    greeting: 'Гав-гав! Завтра у меня праздник, мама выделила 40 монет на угощения для всех друзей! Помоги составить меню!',
    perk: 'Праздничный Бонус (+10 монет карманных денег каждый период)',
    favoriteItems: ['🦴 Сахарная косточка', '🎾 Теннисный мяч', '🎈 Шарики'],
    color: '#4DD0E1'
  }
];

export const KID_PUZZLES = {
  1: {
    friendId: 1,
    title: 'Умная корзинка Мишки',
    theme: 'Планирование бюджета: нужное и желаемое',
    storyPrompt: 'Мишка Потап пришёл на ярмарку. В кармане ровно столько монет, сколько нужно на самое главное. Что обязательно нужно купить в первую очередь?',
    emoji: '🧺',
    options: [
      {
        id: 'p1_opt1',
        text: 'Хлеб, яблоки и мыло',
        emoji: '🍎',
        isCorrect: true,
        feedback: 'Точно в цель! Это полезная еда и гигиена — главные обязательные потребности!'
      },
      {
        id: 'p1_opt2',
        text: 'Огромную хлопушку и 5 леденцов',
        emoji: '🧨',
        isCorrect: false,
        feedback: 'Хлопушка бабахнет за секунду, а дома не останется ни ужина, ни мыла!'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Золотое правило: сначала покупаем необходимое (еду, здоровье и уход), а сладости и хлопушки — только если остались лишние монеты!',
    errorExplanation: 'Если спустить весь бюджет на минутные забавы, питомец и ты останетесь голодными.'
  },

  2: {
    friendId: 2,
    title: 'Запасы Белочки Рыжика',
    theme: 'Формирование сбережений: копилка',
    storyPrompt: 'Рыжик собрала 10 золотых орешков. Как поступить, чтобы зимой не остаться без запасов в снежную бурю?',
    emoji: '🌰',
    options: [
      {
        id: 'p2_opt1',
        text: 'Съесть все 10 орешков прямо сейчас!',
        emoji: '😋',
        isCorrect: false,
        feedback: 'Сейчас сладко, но зимой в лесу будет холодно и совсем пусто!'
      },
      {
        id: 'p2_opt2',
        text: 'Спрятать 3-4 орешка в дупло-копилку',
        emoji: '🏺',
        isCorrect: true,
        feedback: 'Умница! Часть съели с удовольствием, а надёжный запас остался на будущее!'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Правило копилки: если откладывать часть от каждого дохода, твои сбережения растут сами собой, и ты готов к любым холодам!',
    errorExplanation: 'Тратить всё до последней копейки рискованно: всегда должен быть запас на завтра.'
  },

  3: {
    friendId: 3,
    title: 'Выгодная полка Енотика',
    theme: 'Платежи и покупки: сравнение цен',
    storyPrompt: 'В лавке продаются ореховые батончики одинакового веса: 1 большой батончик за 25 монет или 2 маленьких по 15 монет каждый. Что выгоднее взять?',
    emoji: '🏷️',
    options: [
      {
        id: 'p3_opt1',
        text: '1 большой за 25 монет',
        emoji: '👍',
        isCorrect: true,
        feedback: 'Верно! 25 монет выгоднее, ведь 15 + 15 = 30 монет. Ты сохранил 5 монет!'
      },
      {
        id: 'p3_opt2',
        text: '2 маленьких по 15 монет',
        emoji: '👎',
        isCorrect: false,
        feedback: '15 + 15 = 30 монет. Ты переплатишь 5 монет просто за две лишние обёртки!'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Сравнивая итоговую стоимость перед покупкой, ты сохраняешь деньги в кошельке для других полезных вещей!',
    errorExplanation: 'Всегда полезно сложить цены и проверить, не переплачиваешь ли ты за красивую упаковку.'
  },

  4: {
    friendId: 4,
    title: 'Мудрый совет Совушки',
    theme: 'Финансовая безопасность: распознай ловушку',
    storyPrompt: 'Незнакомец в парке шепчет: «Отдай мне 20 монет, а я завтра превращу их в 200 монет волшебством!». Как поступить?',
    emoji: '🦉',
    options: [
      {
        id: 'p4_opt1',
        text: 'Отказаться и рассказать родителям',
        emoji: '🛡️',
        isCorrect: true,
        feedback: 'Правильно! Быстрых чудесных богатств не бывает, это опасная уловка мошенников!'
      },
      {
        id: 'p4_opt2',
        text: 'Отдать монетки и ждать чуда',
        emoji: '💸',
        isCorrect: false,
        feedback: 'Ой! Незнакомец просто исчезнет вместе со всеми твоими монетками!'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Финансовая бдительность: никогда не отдавай деньги и личные секреты незнакомцам, обещающим лёгкие миллионы!',
    errorExplanation: 'Лёгких денег без труда не бывает. Если кто-то сулит горы золота — сразу зови взрослого.'
  },

  5: {
    friendId: 5,
    title: 'Точный расчёт Лисички',
    theme: 'Платежи и покупки: оплата без сдачи',
    storyPrompt: 'Красивый альбом для рисования в книжной лавке стоит ровно 18 монет. Какой набор монет отдать продавцу без сдачи?',
    emoji: '👛',
    options: [
      {
        id: 'p5_opt1',
        text: '10 + 5 + 2 + 1 монеты',
        emoji: '🪙',
        isCorrect: true,
        feedback: '10 + 5 = 15, 15 + 2 = 17, 17 + 1 = 18! Идеальный точный расчёт!'
      },
      {
        id: 'p5_opt2',
        text: '10 + 10 монет',
        emoji: '🪙',
        isCorrect: false,
        feedback: 'Это 20 монет. Продавцу придётся долго искать сдачу, а монетки могут потеряться в кармане.'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Умение быстро складывать монеты разного достоинства помогает уверенно расплачиваться в любой лавке!',
    errorExplanation: 'Точный счёт тренирует математику и экономит время у кассы.'
  },

  6: {
    friendId: 6,
    title: 'Подушка безопасности Зайки',
    theme: 'Формирование сбережений: непредвиденные расходы',
    storyPrompt: 'У самоката лопнула шина, срочный ремонт в мастерской стоит 15 монет. Откуда правильнее и безопаснее всего взять монеты?',
    emoji: '🛴',
    options: [
      {
        id: 'p6_opt1',
        text: 'Из специальной копилки безопасности',
        emoji: '🏺',
        isCorrect: true,
        feedback: 'Ура! Для таких неожиданных случаев подушка безопасности и создаётся!'
      },
      {
        id: 'p6_opt2',
        text: 'Продать любимый самокат за копейки',
        emoji: '😢',
        isCorrect: false,
        feedback: 'Очень жаль продавать весь самокат из-за одной только лопнувшей шины!'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Подушка безопасности защищает тебя от неприятностей: неожиданная поломка чинится легко и без слёз!',
    errorExplanation: 'Без небольшого запаса на непредвиденные траты даже лопнувшее колесо становится катастрофой.'
  },

  7: {
    friendId: 7,
    title: 'Праздник у Щенка Барбоса',
    theme: 'Планирование бюджета: лимит расходов',
    storyPrompt: 'Мама дала Барбосу ровно 40 монет на праздничный стол. Выбери угощения, которые точно поместятся в этот лимит:',
    emoji: '🎂',
    options: [
      {
        id: 'p7_opt1',
        text: 'Ягодный пирог (25 м.) + Яблочный сок (15 м.)',
        emoji: '🥧',
        isCorrect: true,
        feedback: '25 + 15 = 40 монет! Вкусно, празднично и ровно по карману без долгов!'
      },
      {
        id: 'p7_opt2',
        text: 'Гигантский золотой торт (50 м.)',
        emoji: '🎂',
        isCorrect: false,
        feedback: 'Он стоит 50 монет, а в кошельке только 40. Денег не хватит, придётся уйти ни с чем!'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Праздник удался на славу, потому что ты уложился в выделенный бюджет и порадовал всех друзей!',
    errorExplanation: 'Мечтать о гигантском торте здорово, но покупать нужно то, на что действительно хватает средств.'
  }
};

export const FINANCIAL_LESSONS = [
  {
    id: 'needs_wants',
    title: 'Нужное и желаемое',
    emoji: '🧺',
    rule: 'Сначала обязательное (еда, здоровье, уход), затем развлечения.',
    fullText: 'Все покупки делятся на две корзинки: «Обязательное» (без чего нельзя жить здоровым и сытым) и «Желания» (радости, игрушки, сладости). Сначала обеспечь миску питомца, а на остаток выбирай радости!'
  },
  {
    id: 'saving',
    title: 'Копилка на мечту',
    emoji: '🏺',
    rule: 'Откладывай небольшую часть с каждого дохода.',
    fullText: 'Если копить по чуть-чуть каждый день, даже самая большая мечта — собственный замок или игровая площадка — станет реальностью!'
  },
  {
    id: 'price_check',
    title: 'Сравнение цен',
    emoji: '🏷️',
    rule: 'Считай общую цену товара, а не только смотри на яркую этикетку.',
    fullText: 'Маркетологи любят делать красивые маленькие пакетики. Сложи стоимость и вес, чтобы выбрать по-настоящему выгодную покупку.'
  },
  {
    id: 'safety',
    title: 'Финансовая безопасность',
    emoji: '🛡️',
    rule: 'Не отдавай деньги незнакомцам и проверяй сомнительные предложения со взрослыми.',
    fullText: 'Если незнакомец обещает золотые горы или просит код из смс — это мошенник. Никогда не соглашайся без совета родителей!'
  },
  {
    id: 'emergency',
    title: 'Подушка безопасности',
    emoji: '🛟',
    rule: 'Храни запас на непредвиденные случаи: ремонт или лечение.',
    fullText: 'В жизни случаются неожиданности: сломался самокат или заболел животик у питомца. Резервный фонд выручает в трудную минуту!'
  },
  {
    id: 'budget_rule',
    title: 'Правило трёх горшочков (50/30/20)',
    emoji: '📊',
    rule: '50% на нужное, 30% на радости, 20% в копилку.',
    fullText: 'Самый простой способ управлять карманными деньгами — разложить их по трём кучкам: на обязательные дела, на весёлые забавы и в копилку на большую цель!'
  }
];
