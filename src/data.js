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
    portrait: 'assets/characters/pet_cat_v2.png',
    idle: 'assets/characters/pet_cat_v2.png',
    poseFrames: [1, 2, 3].map((pose) => `assets/characters/pet-poses/cat_pose${pose}.png`),
    favoriteFood: 'Вкусный паштет с рыбкой'
  },
  {
    id: 'dog',
    name: 'Щенок',
    emoji: '🐶',
    desc: 'Преданный друг, весело виляет хвостиком и готов бегать за мячом.',
    portrait: 'assets/characters/pet_dog_v2.png',
    idle: 'assets/characters/pet_dog_v2.png',
    poseFrames: [1, 2, 3].map((pose) => `assets/characters/pet-poses/dog_pose${pose}.png`),
    favoriteFood: 'Хрустящий мясной корм'
  },
  {
    id: 'fox',
    name: 'Лисёнок',
    emoji: '🦊',
    desc: 'Любопытный озорник с пушистым рыжим хвостом и умным взглядом.',
    portrait: 'assets/characters/pet_fox_v2.png',
    idle: 'assets/characters/pet_fox_v2.png',
    poseFrames: [1, 2, 3].map((pose) => `assets/characters/pet-poses/fox_pose${pose}.png`),
    favoriteFood: 'Ягодное лакомство'
  },
  {
    id: 'panda',
    name: 'Панда',
    emoji: '🐼',
    desc: 'Добрый пухляш, обожает сочный бамбук и уютный дневной сон.',
    portrait: 'assets/characters/pet_panda_v2.png',
    idle: 'assets/characters/pet_panda_v2.png',
    poseFrames: [1, 2, 3].map((pose) => `assets/characters/pet-poses/panda_pose${pose}.png`),
    favoriteFood: 'Свежие побеги и витамины'
  },
  {
    id: 'bunny',
    name: 'Зайчик',
    emoji: '🐰',
    desc: 'Шустрый попрыгун с длинными ушками, любит хрустящие морковки.',
    portrait: 'assets/characters/pet_bunny_v2.png',
    idle: 'assets/characters/pet_bunny_v2.png',
    poseFrames: [1, 2, 3].map((pose) => `assets/characters/pet-poses/bunny_pose${pose}.png`),
    favoriteFood: 'Морковные хрустяшки'
  },
  {
    id: 'raccoon',
    name: 'Енотик',
    emoji: '🦝',
    desc: 'Ловкий непоседа и чистюля, всегда находит интересные вещицы.',
    portrait: 'assets/characters/pet_raccoon_v2.png',
    idle: 'assets/characters/pet_raccoon_v2.png',
    poseFrames: [1, 2, 3].map((pose) => `assets/characters/pet-poses/raccoon_pose${pose}.png`),
    favoriteFood: 'Сладкие ореховые батончики'
  },
  {
    id: 'owl',
    name: 'Совёнок',
    emoji: '🦉',
    desc: 'Мудрый птенец с большими круглыми глазами и добрым сердцем.',
    portrait: 'assets/characters/pet_owl_v2.png',
    idle: 'assets/characters/pet_owl_v2.png',
    poseFrames: [1, 2, 3].map((pose) => `assets/characters/pet-poses/owl_pose${pose}.png`),
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
  { id: 'hat_party', name: 'Праздничный колпак', slot: 'head', price: 15, asset: 'assets/wardrobe/hat-party.svg' },
  { id: 'hat_cap', name: 'Спортивная кепка', slot: 'head', price: 20, asset: 'assets/wardrobe/hat-cap.svg' },
  { id: 'hat_crown', name: 'Золотая корона', slot: 'head', price: 50, asset: 'assets/wardrobe/hat-crown.svg' },
  { id: 'neck_bell', name: 'Колокольчик на ленте', slot: 'neck', price: 15, asset: 'assets/wardrobe/neck-bell.svg' },
  { id: 'neck_scarf', name: 'Тёплый вязаный шарфик', slot: 'neck', price: 25, asset: 'assets/wardrobe/neck-scarf.svg' },
  { id: 'neck_bow', name: 'Красный атласный бантик', slot: 'neck', price: 20, asset: 'assets/wardrobe/neck-bow.svg' },
  { id: 'glasses_sun', name: 'Крутые солнечные очки', slot: 'glasses', price: 30, asset: 'assets/wardrobe/glasses-sun.svg' },
  { id: 'glasses_smart', name: 'Очки профессора', slot: 'glasses', price: 35, asset: 'assets/wardrobe/glasses-smart.svg' }
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
    asset: 'assets/wardrobe/hat-party.svg',
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
    asset: 'assets/wardrobe/neck-bow.svg',
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
    name: 'Панда Потап',
    species: 'Панда',
    emoji: '🐼',
    houseName: 'Берлога со сладостями',
    portrait: 'assets/characters/friend_1_v2.png',
    idle: 'assets/characters/friend_1_v2.png',
    bg: 'assets/bg_room_friend_1.png',
    theme: 'Планирование бюджета',
    greeting: 'Привет, сосед! Я как раз собираюсь на городскую ярмарку. Поможешь мне выбрать покупки с умом?',
    perk: 'Крепкий Запас (+50 к вместимости копилки)',
    favoriteItems: ['🎋 Бамбук', '🍎 Яблочки', '🍯 Медок'],
    color: '#FFB74D'
  },
  {
    id: 2,
    name: 'Котик Рыжик',
    species: 'Котёнок',
    emoji: '🐱',
    houseName: 'Домик под старым дубом',
    portrait: 'assets/characters/friend_2_v2.png',
    idle: 'assets/characters/friend_2_v2.png',
    bg: 'assets/bg_room_friend_2.png',
    theme: 'Формирование сбережений',
    greeting: 'Ура, ты заглянул в гости! Я коплю монетки на уютную лежанку. Поможешь составить запас?',
    perk: 'Кошачий запас (+10% бонусных монет в копилке)',
    favoriteItems: ['🐟 Рыбка', '🥛 Молоко', '🧶 Клубок'],
    color: '#FF8A65'
  },
  {
    id: 3,
    name: 'Енотик Тёма',
    species: 'Енот',
    emoji: '🦝',
    houseName: 'Мастерская находок',
    portrait: 'assets/characters/friend_3_v2.png',
    idle: 'assets/characters/friend_3_v2.png',
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
    portrait: 'assets/characters/friend_4_v2.png',
    idle: 'assets/characters/friend_4_v2.png',
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
    portrait: 'assets/characters/friend_5_v2.png',
    idle: 'assets/characters/friend_5_v2.png',
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
    portrait: 'assets/characters/friend_6_v2.png',
    idle: 'assets/characters/friend_6_v2.png',
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
    portrait: 'assets/characters/friend_7_v2.png',
    idle: 'assets/characters/friend_7_v2.png',
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
    lessonId: 'needs_wants',
    title: 'Умная корзинка панды Потапа',
    theme: 'Планирование бюджета: нужное и желаемое',
    storyPrompt: 'Панда Потап пришёл на ярмарку. В кармане ровно столько монет, сколько нужно на самое главное. Что обязательно нужно купить в первую очередь?',
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
    lessonId: 'saving',
    title: 'Копилка котика Рыжика',
    theme: 'Формирование сбережений: копилка',
    storyPrompt: 'Рыжик получил 10 монет. Как поступить, чтобы накопить на важную покупку и не остаться без запаса?',
    emoji: '🪙',
    options: [
      {
        id: 'p2_opt1',
        text: 'Потратить все 10 монет на игрушку прямо сейчас!',
        emoji: '😋',
        isCorrect: false,
        feedback: 'Сейчас игрушка порадует, но на важную покупку запаса уже не останется.'
      },
      {
        id: 'p2_opt2',
        text: 'Отложить 3–4 монеты в копилку',
        emoji: '🏺',
        isCorrect: true,
        feedback: 'Умница! Ты оставил часть монет на будущее и сохранил запас.'
      }
    ],
    rewardCoins: 20,
    successExplanation: 'Правило копилки: если откладывать часть от каждого дохода, сбережения растут, и ты готов к важным покупкам!',
    errorExplanation: 'Тратить всё до последней копейки рискованно: всегда должен быть запас на завтра.'
  },

  3: {
    friendId: 3,
    lessonId: 'price_check',
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
    lessonId: 'safety',
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
    lessonId: 'exact_payment',
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
    lessonId: 'emergency',
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
    lessonId: 'budget_rule',
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

// Короткий курс Подручного. После каждого понятного правила ребёнок отвечает
// на один вопрос, а затем применяет тему в задании друга.
export const FINANCIAL_LESSONS = [
  {
    id: 'needs_wants', title: 'Нужное и желаемое', emoji: '🧺',
    rule: 'Сначала обязательное: еда, здоровье и уход. Потом — развлечения.',
    fullText: 'Покупки делятся на обязательные и желанные. Сначала позаботься о том, без чего питомец не будет здоров и счастлив.',
    question: 'Что сначала положим в корзину?',
    options: [{ id: 'need', text: 'Корм и шампунь', isCorrect: true }, { id: 'want', text: 'Игрушку и конфеты', isCorrect: false }]
  },
  {
    id: 'saving', title: 'Копилка на мечту', emoji: '🏺',
    rule: 'Откладывай небольшую часть каждого дохода.',
    fullText: 'Даже маленькие взносы складываются в большую мечту. Копилка помогает не тратить всё сразу.',
    question: 'Как помочь мечте стать ближе?',
    options: [{ id: 'save', text: 'Отложить часть монет', isCorrect: true }, { id: 'all', text: 'Потратить всё сегодня', isCorrect: false }]
  },
  {
    id: 'price_check', title: 'Сравнение цен', emoji: '🏷️',
    rule: 'Сравнивай общую цену и количество товара.',
    fullText: 'Яркая упаковка не делает покупку выгодной. Перед оплатой сложи цены и проверь, сколько товара получишь.',
    question: 'Что сделать перед выбором одинаковых товаров?',
    options: [{ id: 'compare', text: 'Сравнить цену и количество', isCorrect: true }, { id: 'first', text: 'Взять самую яркую упаковку', isCorrect: false }]
  },
  {
    id: 'safety', title: 'Финансовая безопасность', emoji: '🛡️',
    rule: 'Не отдавай деньги и секреты незнакомцам.',
    fullText: 'Обещания лёгких денег и просьбы назвать код — повод остановиться и позвать взрослого, которому доверяешь.',
    question: 'Незнакомец просит код и обещает подарок. Что делать?',
    options: [{ id: 'adult', text: 'Ничего не сообщать и позвать взрослого', isCorrect: true }, { id: 'code', text: 'Назвать код ради подарка', isCorrect: false }]
  },
  {
    id: 'exact_payment', title: 'Точный расчёт', emoji: '🪙',
    rule: 'Складывай монеты и проверяй сумму до оплаты.',
    fullText: 'Точный счёт помогает уверенно платить и понимать, хватит ли денег на покупку без лишней сдачи.',
    question: 'Сколько будет 10 + 5 + 2 + 1?',
    options: [{ id: '18', text: '18 монет', isCorrect: true }, { id: '20', text: '20 монет', isCorrect: false }]
  },
  {
    id: 'emergency', title: 'Подушка безопасности', emoji: '🛟',
    rule: 'Держи запас на лечение и внезапный ремонт.',
    fullText: 'Неожиданности случаются у всех. Небольшой резерв помогает спокойно решить проблему без паники и долгов.',
    question: 'Для чего нужен резерв?',
    options: [{ id: 'reserve', text: 'Для непредвиденной важной траты', isCorrect: true }, { id: 'party', text: 'Чтобы купить всё на празднике', isCorrect: false }]
  },
  {
    id: 'budget_rule', title: 'Три конверта', emoji: '📊',
    rule: '50% на нужное, 30% на радости, 20% в копилку.',
    fullText: 'Конверты помогают увидеть, на что уже можно тратить, а какие монеты лучше сохранить для цели.',
    question: 'Куда отправить часть монет на большую мечту?',
    options: [{ id: 'piggy', text: 'В копилку', isCorrect: true }, { id: 'fun', text: 'В сундучок радостей', isCorrect: false }]
  },
  {
    id: 'income_expense', title: 'Доходы и расходы', emoji: '↕️',
    rule: 'Доход прибавляет монеты, расход их уменьшает.',
    fullText: 'Карманные деньги и награда за задание — доходы. Корм, игрушка и лечение — расходы. История помогает заметить разницу.',
    question: 'Что из этого является доходом?',
    options: [{ id: 'reward', text: 'Награда за решённое задание', isCorrect: true }, { id: 'food', text: 'Покупка корма', isCorrect: false }]
  },
  {
    id: 'goal', title: 'Цель накопления', emoji: '🎯',
    rule: 'У цели есть название, сумма и маленькие шаги.',
    fullText: 'Выбери мечту, узнай её стоимость и пополняй копилку посильными частями. Так большой путь становится понятным.',
    question: 'Как удобнее копить на большую мечту?',
    options: [{ id: 'steps', text: 'Откладывать регулярно маленькими частями', isCorrect: true }, { id: 'wait', text: 'Надеяться найти всю сумму сразу', isCorrect: false }]
  },
  {
    id: 'thoughtful_purchase', title: 'Осознанная покупка', emoji: '🛍️',
    rule: 'Перед покупкой спроси: нужна ли она и есть ли деньги в конверте?',
    fullText: 'Пауза перед оплатой помогает выбрать вещь, которая действительно принесёт пользу или радость и не сломает план.',
    question: 'Что проверить перед покупкой игрушки?',
    options: [{ id: 'plan', text: 'Есть ли деньги в Сундучке Радостей', isCorrect: true }, { id: 'rush', text: 'Купить сразу, не глядя на цену', isCorrect: false }]
  },
  {
    id: 'bank', title: 'Банк и копилка', emoji: '🏦',
    rule: 'Банк помогает хранить деньги на цель и видеть прогресс.',
    fullText: 'Переводи свободные монеты из Сундучка Радостей в копилку, если хочешь приблизить мечту быстрее.',
    question: 'Из какого конверта можно добавить монеты в копилку?',
    options: [{ id: 'fun', text: 'Из Сундучка Радостей', isCorrect: true }, { id: 'care', text: 'Из денег на корм и лечение', isCorrect: false }]
  },
  {
    id: 'review', title: 'План и факт', emoji: '📋',
    rule: 'В конце периода сравни план с тем, что получилось.',
    fullText: 'Отчёт не ругает за ошибку: он показывает, что изменить в следующем периоде, чтобы хватило и на заботу, и на мечту.',
    question: 'Зачем смотреть отчёт в конце периода?',
    options: [{ id: 'learn', text: 'Чтобы улучшить следующий план', isCorrect: true }, { id: 'hide', text: 'Чтобы не знать, куда ушли монеты', isCorrect: false }]
  }
];

// ============================================================================
// Конфигурация позиционирования персонажей и питомцев по локациям
// x: позиция слева в % (0 - 100)
// bottom: высота от пола в px
// width/height: размер спрайта персонажа в px
// flip: зеркальное отражение по горизонтали (true/false)
// ============================================================================
export const ROOM_ACTOR_CONFIGS = {
  myroom: {
    x: 61,
    bottom: 236,
    width: 170,
    height: 170,
    flip: false,
    hero: {
      x: 26,
      bottom: 120,
      width: 160,
      height: 180,
      flip: false
    }
  },
  friend_1: {
    x: 55,
    bottom: 276,
    width: 180,
    height: 200,
    flip: false
  },
  friend_2: {
    x: 44,
    bottom: 346,
    width: 210,
    height: 231,
    flip: false,
    hero: {
      x: 9,
      bottom: 154,
      width: 180,
      height: 198,
      flip: false
    }
  },
  friend_3: {
    x: 46,
    bottom: 232,
    width: 175,
    height: 195,
    flip: false,
    hero: {
      x: 28,
      bottom: 80,
      width: 160,
      height: 180,
      flip: false
    }
  },
  friend_4: {
    x: 55,
    bottom: 360,
    width: 180,
    height: 200,
    flip: false,
    hero: {
      x: 25,
      bottom: 68,
      width: 160,
      height: 180,
      flip: false
    }
  },
  friend_5: {
    x: 53,
    bottom: 254,
    width: 170,
    height: 190,
    flip: false
  },
  friend_6: {
    x: 57,
    bottom: 350,
    width: 210,
    height: 231,
    flip: false,
    hero: {
      x: 26,
      bottom: 120,
      width: 195,
      height: 215,
      flip: false
    }
  },
  friend_7: {
    x: 53,
    bottom: 248,
    width: 340,
    height: 374,
    flip: false,
    hero: {
      x: 22,
      bottom: 87,
      width: 340,
      height: 374,
      flip: false
    }
  },
  shop: {
    x: 56,
    bottom: 192,
    width: 215,
    height: 237,
    flip: true,
    hero: {
      x: 20,
      bottom: 296,
      width: 170,
      height: 187,
      flip: false
    }
  },
  bank: {
    x: 59,
    bottom: 248,
    width: 190,
    height: 210,
    flip: false
  },
  hospital: {
    x: 65,
    bottom: 250,
    width: 205,
    height: 226,
    flip: true,
    hero: {
      x: 35,
      bottom: 100,
      width: 190,
      height: 209,
      flip: false
    }
  }
};


