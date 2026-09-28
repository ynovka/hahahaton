// ============================================================================
// Single Source of Truth: Менеджер состояния игры «Питомец Финни»
// Сохранение в LocalStorage, полная экономика, стадии роста, 5 периодов.
// ============================================================================

import {
  PET_SPECIES,
  COLOR_PATTERNS,
  GROWTH_STAGES,
  DREAM_GOALS,
  SHOP_CATALOG,
  FRIENDS_LIST,
  KID_PUZZLES,
  WARDROBE_ACCESSORIES
} from './data.js';

const STORAGE_KEY = 'petme_finny_game_state_v1';
export const TREATMENT_COST = 30;

function createDefaultPuzzles() {
  const map = {};
  for (let id = 1; id <= 7; id++) {
    map[id] = {
      state: 'available' // 'available' | 'solved_unclaimed' | 'completed'
    };
  }
  return map;
}

function createDefaultFriendships() {
  const map = {};
  for (let id = 1; id <= 7; id++) {
    map[id] = {
      level: 1,
      exp: 30, // 0..100
      isPerkActive: false
    };
  }
  return map;
}

function createInitialState() {
  return {
    isGameStarted: false,
    playerName: 'Юный финансист',
    pet: {
      name: 'Финни',
      species: 'cat',
      pattern: 'classic',
      growthStage: 'baby', // 'baby' | 'junior' | 'adult'
      growthPoints: 0,
      mood: 'happy', // 'happy' | 'hungry' | 'playful' | 'proud_saver' | 'sleepy'
      isHungry: true,
      cleanliness: 70,
      happiness: 70,
      health: 'healthy', // 'healthy' | 'needs_treatment'
      lastCheckupPeriod: 0,
      completedCareActions: [], // ['feed', 'wash', 'play']
      equippedAccessories: {
        head: null,
        neck: null,
        glasses: null
      },
      unlockedWardrobeIds: ['hat_party'] // стартовый подарочный колпачок
    },
    wallet: {
      coins: 100,
      savings: 0
    },
    budget: {
      totalStartingCoins: 100,
      foodAndCareCoins: 50,
      funAndGamesCoins: 30,
      piggyBankCoins: 20,
      isConfirmed: false
    },
    period: 1, // 1..5
    isGameFinished: false,
    currentLocation: 'setup', // 'setup' | 'myroom' | 'citymap' | 'shop' | 'bank' | 'hospital' | 'friend_1'..'friend_7'
    puzzles: createDefaultPuzzles(),
    friendships: createDefaultFriendships(),
    inventory: [
      { id: 'food_kibble', count: 1 } // стартовая порция полезного корма
    ],
    activeGoalId: 'goal_castle',
    achievedGoalIds: [],
    transactions: [
      {
        period: 1,
        title: 'Стартовые карманные деньги',
        amount: 100,
        type: 'income',
        emoji: '🪙',
        time: 'Период #1'
      }
    ],
    completedLessonIds: [],
    advisorTip: 'Привет! Давай сначала настроим питомца и разложим монетки по горшочкам!',
    soundEnabled: true,
    largeFontEnabled: false,
    periodReports: []
  };
}

class GameStateManager {
  constructor() {
    this.state = this.load();
    this.listeners = new Set();
  }

  subscribe(listener) {
    this.listeners.add(listener);
    listener(this.state);
    return () => this.listeners.delete(listener);
  }

  notify() {
    this.save();
    for (const listener of this.listeners) {
      listener(this.state);
    }
  }

  load() {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (raw) {
        const parsed = JSON.parse(raw);
        // Проверяем минимальную валидность
        if (parsed && typeof parsed.period === 'number') {
          parsed.isGameFinished = Boolean(parsed.isGameFinished || parsed.periodReports?.some((report) => report.isGameFinished));
          return parsed;
        }
      }
    } catch (e) {
      console.warn('Не удалось прочитать сохранение, создаем новое', e);
    }
    return createInitialState();
  }

  save() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.state));
    } catch (e) {
      console.error('Ошибка записи сохранения в localStorage', e);
    }
  }

  // --- ДЕЙСТВИЯ ИГРЫ ---

  // Старт новой игры (после экрана создания питомца)
  startNewGame({ petName, species, pattern, playerName }) {
    const s = this.state;
    const safePetName = (petName || 'Финни').trim().slice(0, 16);
    const safePlayerName = (playerName || 'Юный финансист').trim().slice(0, 20);

    s.isGameStarted = true;
    s.playerName = safePlayerName;
    s.pet.name = safePetName;
    s.pet.species = species || 'cat';
    s.pet.pattern = pattern || 'classic';
    s.pet.isHungry = true;
    s.isGameFinished = false;
    s.currentLocation = 'myroom';
    s.advisorTip = `Знакомься: твой питомец ${safePetName}! Разложи монетки по горшочкам и подтверди план, чтобы выйти в город.`;

    this.notify();
  }

  // Переход между комнатами и картой города
  changeLocation(locationId) {
    const s = this.state;

    // Защита: нельзя выйти из дома в город, пока не составлен бюджет на период!
    if (s.currentLocation === 'myroom' && locationId === 'citymap' && !s.budget.isConfirmed) {
      s.advisorTip = 'Перед прогулкой по городу нужно утвердить план расходов! Нажми «Утвердить план».';
      this.notify();
      return false;
    }

    s.currentLocation = locationId;

    if (locationId === 'myroom') {
      s.advisorTip = `Ты дома с ${s.pet.name}! Покорми питомца, примери наряды или составь план.`;
    } else if (locationId === 'citymap') {
      s.advisorTip = 'Карта города! Выбирай, к кому пойти: в Лавку, Банк, Клинику или в гости к друзьям!';
    } else if (locationId === 'shop') {
      s.advisorTip = 'Лавка Енотика! Здесь можно купить полезный корм, шампунь, игрушки и модные аксессуары.';
    } else if (locationId === 'bank') {
      s.advisorTip = 'Городской Банк! Здесь Бобёр-банкир поможет пополнить копилку на твою большую мечту.';
    } else if (locationId === 'hospital') {
      s.advisorTip = 'Клиника Доктора Совы! Пройди бесплатный осмотр и позаботься о здоровье питомца.';
    } else if (locationId.startsWith('friend_')) {
      const friendId = parseInt(locationId.split('_')[1], 10);
      const friend = FRIENDS_LIST.find((f) => f.id === friendId);
      s.advisorTip = friend ? `${friend.name}: «${friend.greeting}»` : 'В гостях у друга!';
    }

    this.notify();
    return true;
  }

  // Раскладывание монет по горшочкам (Детский бюджет)
  allocateBudget(jarType, delta) {
    const b = this.state.budget;
    if (b.isConfirmed) return false;

    let newFood = b.foodAndCareCoins;
    let newFun = b.funAndGamesCoins;
    let newPiggy = b.piggyBankCoins;

    if (jarType === 'food') newFood = Math.max(0, newFood + delta);
    if (jarType === 'fun') newFun = Math.max(0, newFun + delta);
    if (jarType === 'piggy') newPiggy = Math.max(0, newPiggy + delta);

    if (newFood + newFun + newPiggy > b.totalStartingCoins) {
      this.state.advisorTip = 'Монеты в кошельке закончились! Уменьши другие горшочки, чтобы добавить сюда.';
      this.notify();
      return false;
    }

    b.foodAndCareCoins = newFood;
    b.funAndGamesCoins = newFun;
    b.piggyBankCoins = newPiggy;

    if (newFood === 0) {
      this.state.advisorTip = 'Осторожно! Пустая миска: питомец останется голодным. Добавь монеток на еду!';
    } else if (newPiggy >= b.totalStartingCoins * 0.2) {
      this.state.advisorTip = 'Отличная привычка! Ты откладываешь в копилку не менее 20% дохода! 🏺✨';
    } else if (newFun > b.totalStartingCoins * 0.5) {
      this.state.advisorTip = 'Ого, на развлечения выделено больше половины монет! А на корм и копилку точно хватит?';
    } else {
      this.state.advisorTip = 'Хороший план! Жми «Утвердить план», когда всё разложишь.';
    }

    this.notify();
    return true;
  }

  // Применение готового шаблона бюджета (например 50/30/20)
  applyPresetBudget(presetName) {
    const b = this.state.budget;
    if (b.isConfirmed) return;
    const total = b.totalStartingCoins;

    if (presetName === '50_30_20') {
      b.foodAndCareCoins = Math.floor(total * 0.5);
      b.funAndGamesCoins = Math.floor(total * 0.3);
      b.piggyBankCoins = total - b.foodAndCareCoins - b.funAndGamesCoins;
      this.state.advisorTip = 'Применено классическое правило 50/30/20: 50% на нужное, 30% на радости, 20% в копилку!';
    } else if (presetName === 'saver') {
      b.foodAndCareCoins = Math.floor(total * 0.4);
      b.funAndGamesCoins = Math.floor(total * 0.2);
      b.piggyBankCoins = total - b.foodAndCareCoins - b.funAndGamesCoins;
      this.state.advisorTip = 'Режим Супер-Копилки: максимальная сумма направлена на твою мечту!';
    } else if (presetName === 'equal') {
      const third = Math.floor(total / 3);
      b.foodAndCareCoins = third;
      b.funAndGamesCoins = third;
      b.piggyBankCoins = total - third * 2;
      this.state.advisorTip = 'Равномерное распределение по всем горшочкам!';
    }

    this.notify();
  }

  // Утверждение бюджета
  confirmBudget() {
    const s = this.state;
    const b = s.budget;
    if (b.isConfirmed) return false;

    if (b.foodAndCareCoins <= 0) {
      s.advisorTip = 'Нельзя утвердить пустую миску! Добавь хотя бы 20 монет на еду питомцу.';
      this.notify();
      return false;
    }

    const totalAllocated = b.foodAndCareCoins + b.funAndGamesCoins + b.piggyBankCoins;
    if (totalAllocated > s.wallet.coins) {
      s.advisorTip = 'В кошельке меньше монет, чем запланировано. Уменьши суммы в горшочках.';
      this.notify();
      return false;
    }

    // Переводим запланированные в копилку монеты прямо на счёт сбережений
    const savingsAmount = b.piggyBankCoins;
    s.wallet.savings += savingsAmount;
    s.wallet.coins -= savingsAmount;

    b.isConfirmed = true;
    s.pet.mood = 'happy';

    if (savingsAmount > 0) {
      s.transactions.push({
        period: s.period,
        title: 'По плану в копилку на мечту',
        amount: -savingsAmount,
        type: 'savings_in',
        emoji: '🏺',
        time: `Период #${s.period}`
      });
    }

    s.advisorTip = `План утверждён! В копилку отправлено ${savingsAmount} м. Теперь двери города открыты! 🚪✨`;
    this.checkGoalAchievement();
    this.notify();
    return true;
  }

  // Забота о питомце (Покормить / Умыть / Поиграть)
  carePet(action) {
    const s = this.state;

    if (s.pet.completedCareActions.includes(action)) {
      const titles = { feed: 'Кормление', wash: 'Купание', play: 'Игры' };
      s.advisorTip = `${titles[action] || 'Это действие'} уже выполнено в этом периоде! Питомец полностью доволен!`;
      this.notify();
      return { success: false, reason: 'already_done' };
    }

    if (action === 'feed') {
      // Ищем еду в инвентаре
      const foodItem = s.inventory.find((i) => {
        const itemInfo = SHOP_CATALOG.find((cat) => cat.id === i.id);
        return itemInfo && itemInfo.category === 'food';
      });

      if (!foodItem || foodItem.count <= 0) {
        s.advisorTip = 'Дома нет корма! Сходи в Лавку Енотика и купи полезный корм или вкусный паштет.';
        this.notify();
        return { success: false, reason: 'no_item' };
      }

      // Тратим 1 порцию
      foodItem.count--;
      if (foodItem.count <= 0) {
        s.inventory = s.inventory.filter((i) => i.id !== foodItem.id);
      }

      s.pet.isHungry = false;
      s.pet.mood = 'happy';
      s.pet.growthPoints += 1;
      s.pet.completedCareActions.push('feed');
      s.advisorTip = `🥣 ${s.pet.name} вкусно покушал, довольно облизывается и получил +1 очко роста!`;
      this.notify();
      return { success: true };
    }

    if (action === 'wash') {
      const careItem = s.inventory.find((i) => {
        const itemInfo = SHOP_CATALOG.find((cat) => cat.id === i.id);
        return itemInfo && itemInfo.category === 'care';
      });

      if (!careItem) {
        s.advisorTip = 'Для ухода нужен шампунь или расчёска из Лавки Енотика!';
        this.notify();
        return { success: false, reason: 'no_item' };
      }

      s.pet.cleanliness = 100;
      s.pet.mood = 'happy';
      s.pet.growthPoints += 1;
      s.pet.completedCareActions.push('wash');
      s.advisorTip = `🫧 ${s.pet.name} вымыт ароматной пенкой, шёрстка блестит (+1 очко роста)!`;
      this.notify();
      return { success: true };
    }

    if (action === 'play') {
      const toyItem = s.inventory.find((i) => {
        const itemInfo = SHOP_CATALOG.find((cat) => cat.id === i.id);
        return itemInfo && itemInfo.category === 'toy';
      });

      if (!toyItem) {
        s.advisorTip = 'Купи в Лавке Енотика мячик или заводную мышку, чтобы весело играть!';
        this.notify();
        return { success: false, reason: 'no_item' };
      }

      s.pet.happiness = 100;
      s.pet.mood = 'playful';
      s.pet.growthPoints += 1;
      s.pet.completedCareActions.push('play');
      s.advisorTip = `🎾 Ты весело поиграл с ${s.pet.name}! Питомец прыгает от радости (+1 очко роста)!`;
      this.notify();
      return { success: true };
    }

    return { success: false };
  }

  // Покупка товара в магазине
  buyShopItem(itemId) {
    const s = this.state;
    const item = SHOP_CATALOG.find((i) => i.id === itemId);
    if (!item) return { success: false, reason: 'not_found' };

    // Если это предмет гардероба и он уже куплен
    if (item.wardrobeId && s.pet.unlockedWardrobeIds.includes(item.wardrobeId)) {
      s.advisorTip = `«${item.title}» уже есть в гардеробе! Выбери что-нибудь новенькое.`;
      this.notify();
      return { success: false, reason: 'already_owned' };
    }

    // Проверяем, хватает ли монет
    if (s.wallet.coins < item.price) {
      const missing = item.price - s.wallet.coins;
      s.advisorTip = `Не хватает ${missing} монет на «${item.title}». Сходи к друзьям и реши интересные задачки!`;
      this.notify();
      return { success: false, reason: 'no_money', missing };
    }

    // Списываем монеты
    s.wallet.coins -= item.price;

    // Добавляем в инвентарь или гардероб
    if (item.wardrobeId) {
      s.pet.unlockedWardrobeIds.push(item.wardrobeId);
    } else {
      const existing = s.inventory.find((i) => i.id === item.id);
      if (existing) {
        existing.count = (existing.count || 1) + 1;
      } else {
        s.inventory.push({ id: item.id, count: 1 });
      }
    }

    s.transactions.push({
      period: s.period,
      title: item.title,
      amount: -item.price,
      type: 'expense',
      emoji: item.emoji,
      time: `Период #${s.period}`
    });

    s.advisorTip = `Успешная покупка: «${item.title}» за ${item.price} монет! ${item.description}`;
    this.notify();
    return { success: true, item };
  }

  // Пополнение сбережений (в Банке)
  depositSavings(amount) {
    const s = this.state;
    if (amount <= 0) return false;
    if (s.wallet.coins < amount) {
      s.advisorTip = `В кошельке только ${s.wallet.coins} монет. Нельзя положить больше, чем есть!`;
      this.notify();
      return false;
    }

    s.wallet.coins -= amount;
    s.wallet.savings += amount;
    s.pet.mood = 'proud_saver';

    s.transactions.push({
      period: s.period,
      title: 'Пополнение копилки в Банке',
      amount: -amount,
      type: 'savings_in',
      emoji: '🏺',
      time: `Период #${s.period}`
    });

    s.advisorTip = `В копилку положено ${amount} монет! Ты стал ещё ближе к своей мечте! ✨`;
    const achieved = this.checkGoalAchievement();
    this.notify();
    return { success: true, achieved };
  }

  // Снятие части сбережений (в Банке)
  withdrawSavings(amount) {
    const s = this.state;
    if (amount <= 0) return false;
    if (s.wallet.savings < amount) {
      s.advisorTip = `В копилке сейчас ${s.wallet.savings} монет.`;
      this.notify();
      return false;
    }

    s.wallet.savings -= amount;
    s.wallet.coins += amount;

    s.transactions.push({
      period: s.period,
      title: 'Снятие из копилки',
      amount: amount,
      type: 'savings_out',
      emoji: '👛',
      time: `Период #${s.period}`
    });

    s.advisorTip = `${amount} монет возвращены в кошелёк. Помни: из-за этого цель будет копиться чуть дольше.`;
    this.notify();
    return { success: true };
  }

  // Выбор цели накопления
  selectGoal(goalId) {
    const goal = DREAM_GOALS.find((g) => g.id === goalId);
    if (!goal) return;
    this.state.activeGoalId = goalId;
    this.state.advisorTip = `Выбрана цель накопления: «${goal.title}» (${goal.targetCoins} монет). Копилка сохранена!`;
    this.checkGoalAchievement();
    this.notify();
  }

  // Проверка достижения цели
  checkGoalAchievement() {
    const s = this.state;
    const goal = DREAM_GOALS.find((g) => g.id === s.activeGoalId);
    if (!goal) return false;

    if (s.wallet.savings >= goal.targetCoins && !s.achievedGoalIds.includes(goal.id)) {
      s.achievedGoalIds.push(goal.id);
      s.advisorTip = `🎉 УРА! Твоя мечта достигнута: «${goal.title}»! Питомец на седьмом небе от счастья!`;
      return true;
    }
    return false;
  }

  // Осмотр в больнице
  hospitalCheckup() {
    const s = this.state;
    if (s.pet.lastCheckupPeriod === s.period) {
      s.advisorTip = 'Доктор Сова уже осмотрела питомца в этом периоде. Всё под контролем!';
      this.notify();
      return { alreadyChecked: true };
    }

    s.pet.lastCheckupPeriod = s.period;

    // Плановая ситуация по ТЗ: в чётных периодах требуется витаминная процедура
    const needsCare = s.period % 2 === 0;
    if (needsCare) {
      s.pet.health = 'needs_treatment';
      s.pet.mood = 'sleepy';
      s.advisorTip = `Осмотр завершён: питомцу нужна простая витаминная процедура за ${TREATMENT_COST} монет. Резерв на здоровье помогает быть готовым!`;
    } else {
      s.pet.health = 'healthy';
      s.pet.mood = 'happy';
      s.advisorTip = 'Осмотр завершён: питомец абсолютно здоров, бодр и весел! Профилактика — лучший способ беречь бюджет.';
    }

    this.notify();
    return { needsCare, health: s.pet.health };
  }

  // Лечение в больнице
  treatPet() {
    const s = this.state;
    if (s.pet.health !== 'needs_treatment') {
      s.advisorTip = 'Лечение сейчас не требуется! Питомец чувствует себя превосходно.';
      this.notify();
      return { success: false, reason: 'not_needed' };
    }

    if (s.wallet.coins < TREATMENT_COST) {
      const missing = TREATMENT_COST - s.wallet.coins;
      s.advisorTip = `Для процедуры нужно ${TREATMENT_COST} монет. Не хватает ${missing} м. Заработай у друзей!`;
      this.notify();
      return { success: false, reason: 'no_money', missing };
    }

    s.wallet.coins -= TREATMENT_COST;
    s.pet.health = 'healthy';
    s.pet.mood = 'happy';
    s.pet.growthPoints += 1;

    s.transactions.push({
      period: s.period,
      title: 'Лечение и витамины в клинике',
      amount: -TREATMENT_COST,
      type: 'expense',
      emoji: '🏥',
      time: `Период #${s.period}`
    });

    s.advisorTip = 'Процедура прошла на отлично! Питомец снова полон сил и энергии (+1 очко роста)! 🩹✨';
    this.notify();
    return { success: true };
  }

  // Ответ на задание друга
  answerPuzzle(friendId, optionId) {
    const s = this.state;
    const puzzle = KID_PUZZLES[friendId];
    if (!puzzle) return { success: false };

    const pState = s.puzzles[friendId];
    if (pState.state === 'completed') {
      s.advisorTip = 'Это задание уже выполнено и награда получена! Но ты всегда можешь освежить правило.';
      this.notify();
      return { success: false, reason: 'already_completed' };
    }

    const option = puzzle.options.find((o) => o.id === optionId);
    if (!option) return { success: false };

    if (option.isCorrect) {
      pState.state = 'solved_unclaimed';
      s.advisorTip = `Правильно! ${option.feedback} Забирай награду у друга! 🪙`;
      this.notify();
      return { success: true, feedback: option.feedback, explanation: puzzle.successExplanation };
    } else {
      s.advisorTip = `Не совсем так. ${option.feedback} Подумай ещё разок!`;
      this.notify();
      return { success: false, feedback: option.feedback, explanation: puzzle.errorExplanation };
    }
  }

  // Получение награды за решенное задание (защита от повторного начисления!)
  claimPuzzleReward(friendId) {
    const s = this.state;
    const pState = s.puzzles[friendId];
    const puzzle = KID_PUZZLES[friendId];
    if (!pState || !puzzle) return false;

    if (pState.state !== 'solved_unclaimed') {
      return false;
    }

    pState.state = 'completed';
    s.wallet.coins += puzzle.rewardCoins;
    s.pet.growthPoints += 1;
    s.pet.mood = 'playful';

    // Прокачка дружбы
    const friendship = s.friendships[friendId];
    if (friendship) {
      friendship.exp = Math.min(100, friendship.exp + 40);
      if (friendship.exp >= 100 && friendship.level < 3) {
        friendship.level++;
        friendship.exp = 20;
        friendship.isPerkActive = true;
      }
    }

    s.transactions.push({
      period: s.period,
      title: `Награда: ${puzzle.title}`,
      amount: puzzle.rewardCoins,
      type: 'income',
      emoji: '🎯',
      time: `Период #${s.period}`
    });

    s.advisorTip = `Ура! +${puzzle.rewardCoins} монет за смекалку! Питомец гордится тобой! 🎉`;
    this.notify();
    return true;
  }

  // Примерка / снятие наряда в гардеробе
  toggleAccessory(accessoryId) {
    const s = this.state;
    const acc = WARDROBE_ACCESSORIES.find((a) => a.id === accessoryId);
    if (!acc) return;

    if (!s.pet.unlockedWardrobeIds.includes(acc.id)) {
      s.advisorTip = 'Этот наряд ещё не куплен! Загляни в Лавку Енотика.';
      this.notify();
      return;
    }

    const current = s.pet.equippedAccessories[acc.slot];
    if (current === acc.id) {
      s.pet.equippedAccessories[acc.slot] = null; // снять
      s.advisorTip = `Сняли: ${acc.name}.`;
    } else {
      s.pet.equippedAccessories[acc.slot] = acc.id; // надеть
      s.advisorTip = `Питомец примерил: ${acc.name} ${acc.emoji}! Выглядит просто потрясающе!`;
    }

    s.pet.mood = 'happy';
    this.notify();
  }

  // Завершение текущего финансового периода
  finishPeriod() {
    const s = this.state;

    if (s.isGameFinished) {
      s.advisorTip = 'Ты уже прошёл все 5 финансовых планов! Открой итоги периода, чтобы посмотреть свои достижения.';
      this.notify();
      return { success: false, reason: 'game_finished' };
    }

    if (!s.budget.isConfirmed) {
      s.advisorTip = 'Сначала нужно составить и утвердить план расходов на этот период!';
      this.notify();
      return { success: false, reason: 'budget_not_confirmed' };
    }

    // Подсчет очков роста за период
    let points = 0;
    const checks = [];

    // 1. Питомец был накормлен
    if (!s.pet.isHungry) {
      points += 2;
      checks.push({ title: 'Питомец был сыт и ухожен', points: 2, passed: true });
    } else {
      checks.push({ title: 'Питомец остался голодным (купи корм в лавке)', points: 0, passed: false });
    }

    // 2. Отложены сбережения в копилку
    if (s.budget.piggyBankCoins > 0) {
      points += 2;
      checks.push({ title: `Отложено ${s.budget.piggyBankCoins} м. в копилку`, points: 2, passed: true });
    } else {
      checks.push({ title: 'В копилку ничего не было отложено', points: 0, passed: false });
    }

    // 3. Решены задания друзей
    const solved = Object.values(s.puzzles).filter((p) => p.state === 'completed').length;
    if (solved > 0) {
      points += solved;
      checks.push({ title: `Решено заданий у друзей: ${solved}`, points: solved, passed: true });
    }

    // 4. Дополнительный уход
    if (s.pet.completedCareActions.includes('wash')) {
      points += 1;
      checks.push({ title: 'Купание и расчёсывание', points: 1, passed: true });
    }
    if (s.pet.completedCareActions.includes('play')) {
      points += 1;
      checks.push({ title: 'Весёлые развивающие игры', points: 1, passed: true });
    }

    s.pet.growthPoints += points;

    // Определение новой стадии роста
    const oldStage = s.pet.growthStage;
    let newStage = 'baby';
    if (s.pet.growthPoints >= GROWTH_STAGES.ADULT.minPoints) {
      newStage = 'adult';
    } else if (s.pet.growthPoints >= GROWTH_STAGES.JUNIOR.minPoints) {
      newStage = 'junior';
    }
    s.pet.growthStage = newStage;
    const hasGrown = oldStage !== newStage;

    // Начисление карманных денег на следующий период
    const nextAllowance = s.period < 5 ? 100 : 0;
    const nextPeriod = Math.min(5, s.period + 1);
    const isGameFinished = s.period >= 5;
    s.isGameFinished = isGameFinished;

    s.wallet.coins += nextAllowance;

    // Формируем отчёт
    const report = {
      period: s.period,
      pointsEarned: points,
      totalPoints: s.pet.growthPoints,
      checks,
      oldStage,
      newStage,
      hasGrown,
      nextAllowance,
      isGameFinished
    };

    s.periodReports.push(report);

    if (nextAllowance > 0) {
      s.transactions.push({
        period: nextPeriod,
        title: 'Карманные деньги на новый период',
        amount: nextAllowance,
        type: 'income',
        emoji: '🪙',
        time: `Период #${nextPeriod}`
      });
    }

    // Сброс на новый период
    if (!isGameFinished) {
      s.period = nextPeriod;
      s.budget = {
        totalStartingCoins: s.wallet.coins,
        foodAndCareCoins: Math.floor(s.wallet.coins * 0.5),
        funAndGamesCoins: Math.floor(s.wallet.coins * 0.3),
        piggyBankCoins: Math.floor(s.wallet.coins * 0.2),
        isConfirmed: false
      };
      s.pet.isHungry = true;
      s.pet.cleanliness = Math.max(30, s.pet.cleanliness - 35);
      s.pet.happiness = Math.max(30, s.pet.happiness - 25);
      s.pet.completedCareActions = [];
      s.puzzles = createDefaultPuzzles(); // новые задания на период
      s.currentLocation = 'myroom';
      s.advisorTip = `Период #${nextPeriod} начался! Тебе начислено +${nextAllowance} монет. Составь новый план расходов! ✨`;
    } else {
      s.advisorTip = 'Поздравляем! Все 5 финансовых периодов пройдены! Твой питомец вырос, а ты освоил все правила финансовой грамотности! 👑🏆';
    }

    this.notify();
    return { success: true, report };
  }

  // Полный сброс в исходное состояние (демо / взрослый раздел)
  resetDemo() {
    this.state = createInitialState();
    this.notify();
  }

  // Переключение звука
  toggleSound() {
    this.state.soundEnabled = !this.state.soundEnabled;
    this.notify();
    return this.state.soundEnabled;
  }

  // Переключение увеличенного шрифта (Accessibility)
  toggleLargeFont() {
    this.state.largeFontEnabled = !this.state.largeFontEnabled;
    this.notify();
    return this.state.largeFontEnabled;
  }
}

export const gameState = new GameStateManager();
