// ============================================================================
// ИНТЕРАКТИВНЫЙ ОБУЧАЮЩИЙ ДЕМО-ТУР (SPOTLIGHT & GUIDE)
// Сквозной тур: каждая кнопка 100% кликабельна, SVG-маска с прозрачным вырезом,
// пошаговое объяснение всех игровых механик для детей и жюри.
// ============================================================================

import { gameState } from './state.js';
import { sound } from './audio.js';
import { fireConfetti } from './confetti.js';

export function closeAllActiveModals() {
  document.querySelectorAll('.modal-backdrop.active').forEach((modal) => {
    modal.classList.remove('active');
    modal.style.display = '';
  });
}

export const TUTORIAL_STEPS = [
  // --------------------------------------------------------------------------
  // ЭТАП 1: Знакомство и забота о питомце (3 микро-шага)
  // --------------------------------------------------------------------------
  {
    id: 'hud_stats',
    stageNum: 1,
    totalStages: 7,
    stageTitle: 'Знакомство',
    microStep: 1,
    totalMicroSteps: 3,
    title: 'Познакомься со своим питомцем!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Вверху находится твоя главная панель! Здесь видно имя питомца, его самочувствие (сейчас он проголодался 🥣), номер игрового периода и запас монеток в кошельке.',
    actionText: 'Осмотри верхнюю панель и нажми «Далее ➔»!',
    getTarget: () => document.getElementById('top-hud') || document.querySelector('.hud-container'),
    onLeave: () => {}
  },
  {
    id: 'feed_pet',
    stageNum: 1,
    totalStages: 7,
    stageTitle: 'Забота',
    microStep: 2,
    totalMicroSteps: 3,
    title: 'Покорми питомца вкусным обедом!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Внизу экрана расположены кнопки заботы. Твой друг проголодался! Нажми на миску «Покормить», чтобы накормить питомца и поднять ему настроение.',
    actionText: 'Нажми на миску «Покормить» внизу!',
    getTarget: () => document.getElementById('dock-btn-feed'),
    onLeave: () => {
      if (gameState.state.pet.isHungry) {
        gameState.carePet('feed');
      }
    }
  },
  {
    id: 'pet_reaction',
    stageNum: 1,
    totalStages: 7,
    stageTitle: 'Забота',
    microStep: 3,
    totalMicroSteps: 3,
    title: 'Питомец сыт и счастлив! 💖',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Посмотри, как радуется твой друг! Забота о питомце — это ежедневная ответственность. Теперь перейдём к финансовому планированию — составим бюджет!',
    actionText: 'Полюбуйся довольным питомцем и нажми «Далее ➔»!',
    getTarget: () => document.getElementById('room-pet-actor') || document.getElementById('hud-pet-btn'),
    onLeave: () => {}
  },

  // --------------------------------------------------------------------------
  // ЭТАП 2: Детский Бюджет (Три Горшочка, 4 микро-шага)
  // --------------------------------------------------------------------------
  {
    id: 'open_budget',
    stageNum: 2,
    totalStages: 7,
    stageTitle: 'Бюджет',
    microStep: 1,
    totalMicroSteps: 4,
    title: 'Главное правило денег: План расходов!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Прежде чем тратить монетки и идти гулять, каждый умный финансист составляет план! Это убережет от ненужных покупок и поможет накопить на мечту.',
    actionText: 'Нажми на кнопку «План» (рюкзачок) внизу!',
    getTarget: () => document.getElementById('dock-btn-budget'),
    onLeave: () => {
      const budgetModal = document.getElementById('modal-budget');
      if (budgetModal && !budgetModal.classList.contains('active')) {
        budgetModal.classList.add('active');
        budgetModal.style.display = 'flex';
      }
    }
  },
  {
    id: 'jars_explain',
    stageNum: 2,
    totalStages: 7,
    stageTitle: 'Бюджет',
    microStep: 2,
    totalMicroSteps: 4,
    title: 'Куда идут монетки? 3 Горшочка!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Все 100 монет делятся по 3 горшочкам:<br>🥣 <strong>Миска и Забота (50%)</strong> — главное (еда, гигиена, здоровье);<br>🎁 <strong>Сундучок Радостей (30%)</strong> — игрушки и веселье;<br>🏺 <strong>Копилка на Мечту (20%)</strong> — сбережения на будущее!',
    actionText: 'Познакомься с 3 горшочками и нажми «Далее ➔»!',
    getTarget: () => document.querySelector('.budget-jars-row') || document.getElementById('modal-budget'),
    onEnter: () => {
      const budgetModal = document.getElementById('modal-budget');
      if (budgetModal && !budgetModal.classList.contains('active')) {
        budgetModal.classList.add('active');
        budgetModal.style.display = 'flex';
      }
    },
    onLeave: () => {}
  },
  {
    id: 'budget_preset_rule',
    stageNum: 2,
    totalStages: 7,
    stageTitle: 'Бюджет',
    microStep: 3,
    totalMicroSteps: 4,
    title: 'Золотое правило 50 / 30 / 20',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Кнопками «+10» и «-10» можно двигать монетки. А чтобы разложить монеты идеально по финансовой науке, нажми кнопку «Правило 50/30/20»!',
    actionText: 'Нажми кнопку «Правило 50/30/20»!',
    getTarget: () => document.getElementById('btn-preset-5020'),
    onEnter: () => {
      const budgetModal = document.getElementById('modal-budget');
      if (budgetModal && !budgetModal.classList.contains('active')) {
        budgetModal.classList.add('active');
        budgetModal.style.display = 'flex';
      }
    },
    onLeave: () => {
      gameState.applyPresetBudget('50_30_20');
    }
  },
  {
    id: 'confirm_budget',
    stageNum: 2,
    totalStages: 7,
    stageTitle: 'Бюджет',
    microStep: 4,
    totalMicroSteps: 4,
    title: 'Утверди план расходов на период!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Отлично! Все 100 монет распределены, свободных монет 0. Теперь нажми большую зелёную кнопку: это закрепит бюджет и откроет выход на улицу!',
    actionText: 'Нажми зелёную кнопку «Утвердить план расходов»!',
    getTarget: () => document.getElementById('btn-confirm-budget'),
    onEnter: () => {
      const budgetModal = document.getElementById('modal-budget');
      if (budgetModal && !budgetModal.classList.contains('active')) {
        budgetModal.classList.add('active');
        budgetModal.style.display = 'flex';
      }
      document.getElementById('btn-confirm-budget')?.scrollIntoView({ block: 'nearest', behavior: 'auto' });
    },
    onLeave: () => {
      if (!gameState.state.budget.isConfirmed) {
        gameState.confirmBudget();
      }
      closeAllActiveModals();
    }
  },

  // --------------------------------------------------------------------------
  // ЭТАП 3: Выход в город на прогулку (1 микро-шаг)
  // --------------------------------------------------------------------------
  {
    id: 'exit_city',
    stageNum: 3,
    totalStages: 7,
    stageTitle: 'В город',
    microStep: 1,
    totalMicroSteps: 1,
    title: 'Выходим на прогулку!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Ура! Питомец накормлен, а план утверждён. Замочек с двери снят! Нажми кнопку «В город» в верхнем правом углу комнаты.',
    actionText: 'Нажми кнопку «В город» вверху!',
    getTarget: () => document.getElementById('btn-room-exit-city'),
    onEnter: () => {
      closeAllActiveModals();
    },
    onLeave: () => {
      if (gameState.state.currentLocation !== 'citymap') {
        gameState.changeLocation('citymap');
      }
    }
  },

  // --------------------------------------------------------------------------
  // ЭТАП 4: Лавка Енотика — Умные покупки и конверты (5 микро-шагов)
  // --------------------------------------------------------------------------
  {
    id: 'pin_shop',
    stageNum: 4,
    totalStages: 7,
    stageTitle: 'Лавка',
    microStep: 1,
    totalMicroSteps: 5,
    title: 'Заглянем в Лавку Енотика!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Вот карта нашего города! Здесь есть полезные здания и домики друзей. Первым делом давай заглянем за покупками в Лавку Енотика. Нажми на домик Лавки!',
    actionText: 'Нажми на домик «Лавка» на карте города!',
    getTarget: () => document.getElementById('map-pin-shop') || document.querySelector('.map-pin.pin-shop'),
    onEnter: () => {
      closeAllActiveModals();
      if (gameState.state.currentLocation !== 'citymap') {
        gameState.changeLocation('citymap');
      }
    },
    onLeave: () => {
      if (gameState.state.currentLocation !== 'shop') {
        gameState.changeLocation('shop');
      }
    }
  },
  {
    id: 'shop_counter',
    stageNum: 4,
    totalStages: 7,
    stageTitle: 'Лавка',
    microStep: 2,
    totalMicroSteps: 5,
    title: 'Открываем прилавок товаров!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Мы в гостях у Енотика. Чтобы увидеть витрину с ценами и товарами, нажми на кнопку «Прилавок товаров» внизу.',
    actionText: 'Нажми кнопку «🛒 Прилавок товаров» внизу!',
    getTarget: () => document.getElementById('room-shop-open-btn'),
    onEnter: () => {
      if (gameState.state.currentLocation !== 'shop') {
        gameState.changeLocation('shop');
      }
    },
    onLeave: () => {
      const shopModal = document.getElementById('modal-shop');
      if (shopModal && !shopModal.classList.contains('active')) {
        shopModal.classList.add('active');
        shopModal.style.display = 'flex';
      }
    }
  },
  {
    id: 'shop_envelopes_rule',
    stageNum: 4,
    totalStages: 7,
    stageTitle: 'Лавка',
    microStep: 3,
    totalMicroSteps: 5,
    title: 'Как работают конверты покупок?',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Смотри внимательно: под ценой каждого товара написано, из какого горшочка спишутся монетки! Еда оплачивается из «Миски», а игрушки — из «Радостей». Нельзя потратить больше, чем отложено в этом горшочке!',
    actionText: 'Изучи ценники и конверты, затем нажми «Далее ➔»!',
    getTarget: () => document.querySelector('#shop-items-grid .shop-card .shop-price-tag') || document.querySelector('#shop-items-grid .shop-card'),
    onEnter: () => {
      const shopModal = document.getElementById('modal-shop');
      if (shopModal && !shopModal.classList.contains('active')) {
        shopModal.classList.add('active');
        shopModal.style.display = 'flex';
      }
    },
    onLeave: () => {}
  },
  {
    id: 'shop_buy_item',
    stageNum: 4,
    totalStages: 7,
    stageTitle: 'Лавка',
    microStep: 4,
    totalMicroSteps: 5,
    title: 'Купи полезный товар для питомца!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Давай сделаем первую запланированную покупку! Нажми кнопку «Купить» у товара: монетки спишутся строго из соответствующего горшочка, а предмет появится в рюкзачке.',
    actionText: 'Нажми кнопку «Купить» у любого доступного товара!',
    getTarget: () => document.querySelector('#shop-items-grid .shop-card button:not([disabled])') || document.querySelector('.shop-item-buy-btn'),
    onEnter: () => {
      const shopModal = document.getElementById('modal-shop');
      if (shopModal && !shopModal.classList.contains('active')) {
        shopModal.classList.add('active');
        shopModal.style.display = 'flex';
      }
    },
    onLeave: () => {}
  },
  {
    id: 'shop_exit',
    stageNum: 4,
    totalStages: 7,
    stageTitle: 'Лавка',
    microStep: 5,
    totalMicroSteps: 5,
    title: 'Покупка сделана! Возвращаемся в город',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Отлично! Покупка совершена строго в рамках бюджета, без долгов и переплат. Закрываем магазин крестиком «✕» и выходим на карту города!',
    actionText: 'Нажми на крестик «✕» или кнопку выхода в город!',
    getTarget: () => document.querySelector('[data-close="modal-shop"]') || document.getElementById('room-shop-exit-btn') || document.getElementById('tutorial-btn-next'),
    onLeave: () => {
      closeAllActiveModals();
      gameState.changeLocation('citymap');
    }
  },

  // --------------------------------------------------------------------------
  // ЭТАП 5: Городской Банк — Копилка и сложный процент (5 микро-шагов)
  // --------------------------------------------------------------------------
  {
    id: 'pin_bank',
    stageNum: 5,
    totalStages: 7,
    stageTitle: 'Банк',
    microStep: 1,
    totalMicroSteps: 5,
    title: 'Идём в Городской Банк!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Банк — это место, где твои сбережения не просто хранятся в надёжном сейфе, но и приносят новые монетки!',
    actionText: 'Нажми на здание «Банк» на карте города!',
    getTarget: () => document.getElementById('map-pin-bank') || document.querySelector('.map-pin.pin-bank'),
    onEnter: () => {
      closeAllActiveModals();
      if (gameState.state.currentLocation !== 'citymap') {
        gameState.changeLocation('citymap');
      }
    },
    onLeave: () => {
      if (gameState.state.currentLocation !== 'bank') {
        gameState.changeLocation('bank');
      }
    }
  },
  {
    id: 'bank_counter',
    stageNum: 5,
    totalStages: 7,
    stageTitle: 'Банк',
    microStep: 2,
    totalMicroSteps: 5,
    title: 'Касса Бобра-банкира',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Бобёр-банкир заведует сейфом твоей копилки. Нажми «Касса и копилка», чтобы посмотреть свои сбережения!',
    actionText: 'Нажми кнопку «🏦 Касса и копилка» внизу!',
    getTarget: () => document.getElementById('room-bank-open-btn'),
    onEnter: () => {
      if (gameState.state.currentLocation !== 'bank') {
        gameState.changeLocation('bank');
      }
    },
    onLeave: () => {
      const bankModal = document.getElementById('modal-bank');
      if (bankModal && !bankModal.classList.contains('active')) {
        bankModal.classList.add('active');
        bankModal.style.display = 'flex';
      }
    }
  },
  {
    id: 'bank_interest_rule',
    stageNum: 5,
    totalStages: 7,
    stageTitle: 'Банк',
    microStep: 3,
    totalMicroSteps: 5,
    title: 'Сложный процент: +10% дохода от банка!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Главное чудо банка: в конце каждого периода банк бесплатно начисляет +10% от всей суммы твоей копилки! Чем больше отложишь — тем больше вырастет твой капитал.',
    actionText: 'Ознакомься со сложным процентом и нажми «Далее ➔»!',
    getTarget: () => document.querySelector('#modal-bank .clay-card') || document.getElementById('bank-goals-list'),
    onEnter: () => {
      const bankModal = document.getElementById('modal-bank');
      if (bankModal && !bankModal.classList.contains('active')) {
        bankModal.classList.add('active');
        bankModal.style.display = 'flex';
      }
    },
    onLeave: () => {}
  },
  {
    id: 'bank_deposit',
    stageNum: 5,
    totalStages: 7,
    stageTitle: 'Банк',
    microStep: 4,
    totalMicroSteps: 5,
    title: 'Пополни копилку на 10 монет!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Давай сделаем первый вклад! Нажми кнопку «+10 в копилку», чтобы монетки начали приносить пассивный доход.',
    actionText: 'Нажми кнопку «+10 в копилку»!',
    getTarget: () => document.getElementById('btn-deposit-10'),
    onEnter: () => {
      const bankModal = document.getElementById('modal-bank');
      if (bankModal && !bankModal.classList.contains('active')) {
        bankModal.classList.add('active');
        bankModal.style.display = 'flex';
      }
    },
    onLeave: () => {
      gameState.depositToSavings(10);
    }
  },
  {
    id: 'bank_exit',
    stageNum: 5,
    totalStages: 7,
    stageTitle: 'Банк',
    microStep: 5,
    totalMicroSteps: 5,
    title: 'Монетки в безопасности и растут!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Отлично! Твоя копилка пополнена и в конце периода банк начислит на неё прибыль. Закрываем банк крестиком «✕» и идём в гости к друзьям!',
    actionText: 'Нажми на крестик «✕» или кнопку выхода в город!',
    getTarget: () => document.querySelector('[data-close="modal-bank"]') || document.getElementById('room-bank-exit-btn') || document.getElementById('tutorial-btn-next'),
    onLeave: () => {
      closeAllActiveModals();
      gameState.changeLocation('citymap');
    }
  },

  // --------------------------------------------------------------------------
  // ЭТАП 6: В гостях у Белочки Рыжика (Викторина и Награды, 6 микро-шагов)
  // --------------------------------------------------------------------------
  {
    id: 'pin_friend',
    stageNum: 6,
    totalStages: 7,
    stageTitle: 'В гостях',
    microStep: 1,
    totalMicroSteps: 6,
    title: 'В гости к Белочке Рыжику!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Соседи в городке очень дружелюбные. Они делятся финансовыми секретами и дают задачки с наградой в монетках!',
    actionText: 'Нажми на домик Белочки Рыжика на карте!',
    getTarget: () => document.getElementById('map-pin-friend_2') || document.querySelector('.map-pin[aria-label*="Рыжик"]'),
    onEnter: () => {
      closeAllActiveModals();
      if (gameState.state.currentLocation !== 'citymap') {
        gameState.changeLocation('citymap');
      }
    },
    onLeave: () => {
      if (gameState.state.currentLocation !== 'friend_2') {
        gameState.changeLocation('friend_2');
      }
    }
  },
  {
    id: 'friend_talk',
    stageNum: 6,
    totalStages: 7,
    stageTitle: 'В гостях',
    microStep: 2,
    totalMicroSteps: 6,
    title: 'Поговори с Рыжиком!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Вот мы и в домике Белочки Рыжика! Нажми кнопку «💬 Поговорить», чтобы узнать, какую задачку приготовил друг.',
    actionText: 'Нажми кнопку «💬 Поговорить» внизу!',
    getTarget: () => document.getElementById('room-friend-dialogue-btn'),
    onEnter: () => {
      if (gameState.state.currentLocation !== 'friend_2') {
        gameState.changeLocation('friend_2');
      }
    },
    onLeave: () => {
      document.getElementById('room-friend-dialogue-btn')?.click();
    }
  },
  {
    id: 'friend_intro_begin',
    stageNum: 6,
    totalStages: 7,
    stageTitle: 'В гостях',
    microStep: 3,
    totalMicroSteps: 6,
    title: 'Белочка Рыжик просит совета!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Рыжик встретил нас у порога: он получил первые карманные монетки и не знает, как поступить. Нажми «Давай разберёмся», чтобы помочь другу!',
    actionText: 'Нажми кнопку «Давай разберёмся»!',
    getTarget: () => document.getElementById('btn-friend-begin') || document.querySelector('.puzzle-opt-btn'),
    onEnter: () => {
      if (gameState.state.completedLessonIds) {
        ['saving', 'needs_wants', 'price_check', 'safety', 'exact_payment', 'emergency', 'budget_rule'].forEach((id) => {
          if (!gameState.state.completedLessonIds.includes(id)) {
            gameState.state.completedLessonIds.push(id);
          }
        });
      }
      if (gameState.state.puzzles && gameState.state.puzzles[2]) {
        if (gameState.state.puzzles[2].state === 'completed') {
          gameState.state.puzzles[2].state = 'available';
        }
      }
      const modal = document.getElementById('modal-friend');
      if (!modal || !modal.classList.contains('active')) {
        document.getElementById('room-friend-dialogue-btn')?.click();
      }
    },
    onLeave: () => {}
  },
  {
    id: 'friend_pick_answer',
    stageNum: 6,
    totalStages: 7,
    stageTitle: 'В гостях',
    microStep: 4,
    totalMicroSteps: 6,
    title: 'Реши финансовую задачку друга!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Рыжик получил 10 монет. Если спустить всё на сиюминутную забаву — завтра останешься ни с чем! Умный финансист всегда оставляет часть на будущее: выбери вариант с копилкой!',
    actionText: 'Нажми на правильный вариант: «Отложить 3–4 монеты в копилку»!',
    getTarget: () => document.querySelector('.puzzle-opt-btn[data-is-correct="true"]') || document.getElementById('btn-claim-puzzle-reward') || document.querySelector('.puzzle-opt-btn'),
    onEnter: () => {
      const modal = document.getElementById('modal-friend');
      if (!modal || !modal.classList.contains('active')) {
        document.getElementById('room-friend-dialogue-btn')?.click();
      }
    },
    onLeave: () => {}
  },
  {
    id: 'friend_claim_reward',
    stageNum: 6,
    totalStages: 7,
    stageTitle: 'В гостях',
    microStep: 5,
    totalMicroSteps: 6,
    title: 'Умница! Забери награду (+20 монет)!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Верно! Ты помог другу сберечь средства. За полезный финансовый совет Рыжик благодарит тебя и вручает +20 золотых монет в кошелёк!',
    actionText: 'Нажми золотую кнопку «🪙 Забрать награду (+20 монет)!»!',
    getTarget: () => document.getElementById('btn-claim-puzzle-reward') || document.querySelector('[data-close="modal-friend"]') || document.getElementById('tutorial-btn-next'),
    onEnter: () => {
      const claim = document.getElementById('btn-claim-puzzle-reward');
      if (claim && claim.style.display === 'none') {
        claim.style.display = 'block';
      }
    },
    onLeave: () => {
      if (gameState.state.puzzles && gameState.state.puzzles[2]?.state === 'solved_unclaimed') {
        gameState.claimPuzzleReward(2);
      }
    }
  },
  {
    id: 'friend_go_home',
    stageNum: 6,
    totalStages: 7,
    stageTitle: 'В гостях',
    microStep: 6,
    totalMicroSteps: 6,
    title: 'Награда в кошельке! Возвращаемся Домой',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Ура, монетки заработаны, а дружба стала крепче! День подходит к концу, пора возвращаться в нашу уютную комнату к питомцу.',
    actionText: 'Нажми на кнопку выхода или «Домой»!',
    getTarget: () => document.getElementById('room-friend-exit-btn') || document.getElementById('hotbar-home') || document.getElementById('tutorial-btn-next'),
    onEnter: () => {
      closeAllActiveModals();
    },
    onLeave: () => {
      closeAllActiveModals();
      gameState.changeLocation('myroom');
    }
  },

  // --------------------------------------------------------------------------
  // ЭТАП 7: Завершение периода и взросление (2 микро-шага)
  // --------------------------------------------------------------------------
  {
    id: 'finish_period_btn',
    stageNum: 7,
    totalStages: 7,
    stageTitle: 'Итоги',
    microStep: 1,
    totalMicroSteps: 2,
    title: 'Подводим итоги периода!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'Мы сделали всё: накормили питомца, распределили бюджет, сделали умные покупки, приумножили монетки в банке и помогли другу! Пора завершить период.',
    actionText: 'Нажми на кнопку «Итоги» (планшет) внизу!',
    getTarget: () => document.getElementById('dock-btn-finish'),
    onEnter: () => {
      closeAllActiveModals();
      if (gameState.state.currentLocation !== 'myroom') {
        gameState.changeLocation('myroom');
      }
    },
    onLeave: () => {
      gameState.finishPeriod();
    }
  },
  {
    id: 'finish_summary_view',
    stageNum: 7,
    totalStages: 7,
    stageTitle: 'Финал 🏆',
    microStep: 2,
    totalMicroSteps: 2,
    title: 'Питомец взрослеет! Банк платит доход!',
    mentorName: 'Совушка София',
    mentorAvatar: 'assets/characters/friend_4_v2.png',
    desc: 'В отчёте видно начисленные проценты по вкладу в банке (+10%), питомец растёт и взрослеет, а в кошелёк приходят новые карманные деньги на следующий период!',
    actionText: 'Нажми «Далее ➔» для завершения обучения!',
    getTarget: () => document.querySelector('[data-close="modal-period-summary"]') || document.getElementById('btn-start-next-period') || document.getElementById('tutorial-btn-next'),
    onEnter: () => {},
    onLeave: () => {
      closeAllActiveModals();
    }
  }
];

class TutorialController {
  constructor() {
    this.isActive = false;
    this.currentStepIndex = 0;
    this.overlay = null;
    this.svgMask = null;
    this.svgPath = null;
    this.spotlightHole = null;
    this.card = null;
    this.titleEl = null;
    this.descEl = null;
    this.actionTextEl = null;
    this.stepIndicatorEl = null;
    this.progressFillEl = null;
    this.arrowPointer = null;
    this.nextBtn = null;
    this.skipBtn = null;
    this.mentorAvatar = null;
    this.mentorName = null;
    this.currentTargetEl = null;
    this.boundTargetClickHandler = null;
    this.updatePositionScheduled = false;
    this.retryTimeout = null;
  }

  clearRetryTimeout() {
    if (this.retryTimeout) {
      clearTimeout(this.retryTimeout);
      this.retryTimeout = null;
    }
  }

  init() {
    this.overlay = document.getElementById('tutorial-overlay');
    if (!this.overlay) return;

    this.svgMask = document.getElementById('tutorial-svg-mask');
    this.svgPath = document.getElementById('tutorial-svg-path');
    this.spotlightHole = document.getElementById('tutorial-spotlight-hole');
    this.card = document.getElementById('tutorial-card');
    this.titleEl = document.getElementById('tutorial-card-title');
    this.descEl = document.getElementById('tutorial-card-desc');
    this.actionTextEl = document.getElementById('tutorial-action-text');
    this.stepIndicatorEl = document.getElementById('tutorial-step-indicator');
    this.progressFillEl = document.getElementById('tutorial-progress-fill');
    this.arrowPointer = document.getElementById('tutorial-arrow-pointer');
    this.nextBtn = document.getElementById('tutorial-btn-next');
    this.skipBtn = document.getElementById('tutorial-btn-skip');
    this.mentorAvatar = document.getElementById('tutorial-mentor-avatar');
    this.mentorName = document.querySelector('.tutorial-mentor-name');

    // Кнопка пропуска обучения
    this.skipBtn?.addEventListener('click', (e) => {
      e.stopPropagation();
      sound.playPop();
      this.stop();
    });

    // Кнопка перехода к следующему микро-шагу
    this.nextBtn?.addEventListener('click', (e) => {
      e.stopPropagation();
      sound.playPop();
      this.advance();
    });

    // Клик по затемненной области SVG мимо цели — легкая подсказка-пульсация
    this.svgPath?.addEventListener('click', (e) => {
      e.stopPropagation();
      this.nudgeAttention();
    });

    // Авто-обновление позиции подсветки при ресайзе или скролле
    window.addEventListener('resize', () => this.scheduleReposition());
    window.addEventListener('scroll', () => this.scheduleReposition(), true);

    gameState.subscribe(() => {
      if (this.isActive) {
        setTimeout(() => this.scheduleReposition(), 80);
      }
    });

    document.getElementById('btn-quick-tutorial')?.addEventListener('click', () => {
      sound.playPop();
      this.start(0);
    });

    document.getElementById('btn-start-tutorial')?.addEventListener('click', () => {
      sound.playPop();
      this.start(0);
    });
  }

  start(fromStep = 0) {
    if (!this.overlay) this.init();
    this.isActive = true;
    this.currentStepIndex = fromStep;

    if (this.overlay) this.overlay.style.display = 'block';

    sound.playLevelUp();
    this.renderCurrentStep();
  }

  stop() {
    this.isActive = false;
    this.clearRetryTimeout();
    this.clearTargetHighlight();
    if (this.overlay) this.overlay.style.display = 'none';
  }

  advance() {
    if (!this.isActive) return;
    this.clearRetryTimeout();

    const currentStep = TUTORIAL_STEPS[this.currentStepIndex];
    if (currentStep && typeof currentStep.onLeave === 'function') {
      try {
        currentStep.onLeave();
      } catch (err) {
        console.warn('Tutorial step onLeave error:', err);
      }
    }

    this.clearTargetHighlight();
    this.currentStepIndex++;

    if (this.currentStepIndex >= TUTORIAL_STEPS.length) {
      this.complete();
    } else {
      sound.playPop();
      this.renderCurrentStep();
    }
  }

  complete() {
    this.clearTargetHighlight();
    closeAllActiveModals();
    fireConfetti({ count: 120 });
    sound.playLevelUp();

    if (this.titleEl) this.titleEl.textContent = '🎉 Обучение успешно завершено!';
    if (this.descEl) this.descEl.innerHTML = 'Ты в совершенстве освоил все правила игры! <strong>Заботься о друге, планируй бюджет по 3 горшочкам, приумножай монетки в банке и дружи с соседями!</strong>';
    if (this.actionTextEl) this.actionTextEl.textContent = 'Удачной игры и больших финансовых побед!';
    if (this.stepIndicatorEl) this.stepIndicatorEl.textContent = 'Финал 🏆';
    if (this.progressFillEl) this.progressFillEl.style.width = '100%';
    if (this.arrowPointer) this.arrowPointer.className = 'tutorial-arrow-pointer';

    if (this.spotlightHole) this.spotlightHole.style.display = 'none';
    if (this.svgPath) {
      const W = window.innerWidth;
      const H = window.innerHeight;
      this.svgPath.setAttribute('d', `M 0 0 H ${W} V ${H} H 0 Z`);
    }

    if (this.card) {
      this.card.style.top = '50%';
      this.card.style.bottom = 'auto';
      this.card.style.transform = 'translate(-50%, -50%)';
    }

    if (this.nextBtn) {
      this.nextBtn.style.display = 'inline-block';
      this.nextBtn.textContent = '🚀 Играть самостоятельно';
      const onDone = (e) => {
        e.stopPropagation();
        this.nextBtn.removeEventListener('click', onDone);
        this.stop();
      };
      this.nextBtn.addEventListener('click', onDone);
    }
  }

  renderCurrentStep() {
    if (!this.isActive) return;
    const step = TUTORIAL_STEPS[this.currentStepIndex];
    if (!step) return;

    if (typeof step.onEnter === 'function') {
      try {
        step.onEnter();
      } catch (err) {
        console.warn('Tutorial step onEnter error:', err);
      }
    }

    if (this.spotlightHole) this.spotlightHole.style.display = 'block';
    if (this.card) this.card.style.transform = 'translateX(-50%)';

    // Форматируем индикатор прогресса: «Этап X/7: Название · Шаг Y из Z»
    const indicatorText = `Этап ${step.stageNum}/${step.totalStages}: ${step.stageTitle} · Шаг ${step.microStep} из ${step.totalMicroSteps}`;
    if (this.stepIndicatorEl) this.stepIndicatorEl.textContent = indicatorText;

    if (this.titleEl) this.titleEl.textContent = step.title;
    if (this.descEl) this.descEl.innerHTML = step.desc;
    if (this.actionTextEl) this.actionTextEl.textContent = step.actionText;
    if (this.mentorName) this.mentorName.textContent = step.mentorName;
    if (this.mentorAvatar) this.mentorAvatar.src = step.mentorAvatar;

    if (this.progressFillEl) {
      const overallPercent = Math.round(((this.currentStepIndex + 1) / TUTORIAL_STEPS.length) * 100);
      this.progressFillEl.style.width = `${overallPercent}%`;
    }

    if (this.nextBtn) {
      this.nextBtn.style.display = 'inline-block';
      this.nextBtn.textContent = 'Далее ➔';
    }

    this.clearRetryTimeout();
    this.repositionSpotlight(step);

    // Дополнительный пересчет после завершения CSS-анимаций открытия окон (280мс)
    setTimeout(() => {
      if (this.isActive && TUTORIAL_STEPS[this.currentStepIndex] === step) {
        this.repositionSpotlight(step);
      }
    }, 280);
  }

  scheduleReposition() {
    if (this.updatePositionScheduled || !this.isActive) return;
    this.updatePositionScheduled = true;
    requestAnimationFrame(() => {
      this.updatePositionScheduled = false;
      const step = TUTORIAL_STEPS[this.currentStepIndex];
      if (step) this.repositionSpotlight(step);
    });
  }

  repositionSpotlight(step, retryCount = 0) {
    if (!this.isActive) return;
    if (TUTORIAL_STEPS[this.currentStepIndex] !== step) return; // Защита от устаревших таймеров

    const target = step.getTarget ? step.getTarget() : null;
    const W = window.innerWidth;
    const H = window.innerHeight;

    if (!target || !target.getBoundingClientRect) {
      if (retryCount < 8) {
        this.clearRetryTimeout();
        this.retryTimeout = setTimeout(() => {
          if (this.isActive && TUTORIAL_STEPS[this.currentStepIndex] === step) {
            this.repositionSpotlight(step, retryCount + 1);
          }
        }, 80);
      }
      return;
    }

    try {
      target.scrollIntoView({ block: 'nearest', behavior: 'auto' });
    } catch (_) {}

    const rect = target.getBoundingClientRect();
    if (rect.width === 0 || rect.height === 0) {
      if (retryCount < 8) {
        this.clearRetryTimeout();
        this.retryTimeout = setTimeout(() => {
          if (this.isActive && TUTORIAL_STEPS[this.currentStepIndex] === step) {
            this.repositionSpotlight(step, retryCount + 1);
          }
        }, 80);
      }
      return;
    }

    this.clearRetryTimeout();

    // Если цель изменилась — перенавешиваем слушатель клика
    if (this.currentTargetEl !== target) {
      this.clearTargetHighlight();
      this.currentTargetEl = target;
      target.classList.add('tutorial-target-highlighted');

      const expectedStep = step;
      this.boundTargetClickHandler = () => {
        setTimeout(() => {
          if (this.isActive && TUTORIAL_STEPS[this.currentStepIndex] === expectedStep) {
            this.advance();
          }
        }, 220);
      };
      target.addEventListener('click', this.boundTargetClickHandler, { once: true });
    }

    const pad = 8;
    const x = Math.max(0, rect.left - pad);
    const y = Math.max(0, rect.top - pad);
    const w = rect.width + pad * 2;
    const h = rect.height + pad * 2;
    const r = Math.min(18, Math.max(4, Math.floor(h / 2), Math.floor(w / 2)));

    // 1. Позиционируем подсвечивающий контур
    if (this.spotlightHole) {
      this.spotlightHole.style.display = 'block';
      this.spotlightHole.style.left = `${x}px`;
      this.spotlightHole.style.top = `${y}px`;
      this.spotlightHole.style.width = `${w}px`;
      this.spotlightHole.style.height = `${h}px`;
      const computedRadius = window.getComputedStyle(target).borderRadius;
      this.spotlightHole.style.borderRadius = computedRadius && computedRadius !== '0px' ? computedRadius : '16px';
    }

    // 2. Строим SVG-маску с прозрачным вырезом прямо над кнопкой:
    // область выреза на 100% открыта для реальных кликов пользователя!
    if (this.svgPath) {
      const outer = `M 0 0 H ${W} V ${H} H 0 Z`;
      const inner = `M ${x + r} ${y} ` +
        `H ${x + w - r} ` +
        `Q ${x + w} ${y} ${x + w} ${y + r} ` +
        `V ${y + h - r} ` +
        `Q ${x + w} ${y + h} ${x + w - r} ${y + h} ` +
        `H ${x + r} ` +
        `Q ${x} ${y + h} ${x} ${y + h - r} ` +
        `V ${y + r} ` +
        `Q ${x} ${y} ${x + r} ${y} Z`;
      this.svgPath.setAttribute('d', `${outer} ${inner}`);
    }

    // 3. Умное размещение карточки: строго в противоположной половине экрана
    if (this.card) {
      const windowH = window.innerHeight;
      const targetCenterY = rect.top + rect.height / 2;

      if (targetCenterY > windowH / 2) {
        // Целевой элемент в нижней половине экрана -> карточку ставим сверху
        this.card.style.top = '16px';
        this.card.style.bottom = 'auto';
        if (this.arrowPointer) {
          this.arrowPointer.className = 'tutorial-arrow-pointer pointing-down';
        }
      } else {
        // Целевой элемент в верхней половине экрана -> карточку ставим снизу
        this.card.style.top = 'auto';
        this.card.style.bottom = '20px';
        if (this.arrowPointer) {
          this.arrowPointer.className = 'tutorial-arrow-pointer pointing-up';
        }
      }
    }
  }

  nudgeAttention() {
    if (!this.card) return;
    this.card.classList.remove('tutorial-card-nudge');
    void this.card.offsetWidth;
    this.card.classList.add('tutorial-card-nudge');
    if (this.spotlightHole) {
      this.spotlightHole.classList.remove('tutorial-spotlight-nudge');
      void this.spotlightHole.offsetWidth;
      this.spotlightHole.classList.add('tutorial-spotlight-nudge');
    }
  }

  clearTargetHighlight() {
    if (this.currentTargetEl) {
      this.currentTargetEl.classList.remove('tutorial-target-highlighted');
      if (this.boundTargetClickHandler) {
        this.currentTargetEl.removeEventListener('click', this.boundTargetClickHandler);
        this.boundTargetClickHandler = null;
      }
      this.currentTargetEl = null;
    }
  }
}

export const tutorial = new TutorialController();
if (typeof window !== 'undefined') {
  window.tutorial = tutorial;
  window.TUTORIAL_STEPS = TUTORIAL_STEPS;
}
