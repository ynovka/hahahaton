// ============================================================================
// Питомец Финни — Контроллер пользовательского интерфейса (UI Controller)
// Отрисовка экранов, интерактивной карты, 11 комнат, модальных окон,
// обработка событий и звукового сопровождения.
// ============================================================================

import {
  PET_SPECIES,
  COLOR_PATTERNS,
  GROWTH_STAGES,
  DREAM_GOALS,
  SHOP_CATALOG,
  FRIENDS_LIST,
  KID_PUZZLES,
  WARDROBE_ACCESSORIES,
  FINANCIAL_LESSONS,
  ROOM_ACTOR_CONFIGS
} from './data.js';
import { gameState, TREATMENT_COST } from './state.js';
import { sound } from './audio.js';
import { fireConfetti } from './confetti.js';
import { tutorial } from './tutorial.js';

export class UIController {
  constructor() {
    this.isMapPanorama = false;
    this.activeFriendId = 1;
    this.adultMathAnswer = 42;
    this.shopCurrentTab = 'all';
    this.lastRenderedLocation = null;
    this.selectedSpeciesIndex = 0;
    this.setupSwipeStartX = null;
    this.titleSplashDismissed = this.readTitleSplashState();
    this.petPoseTimer = null;
    this.petPoseKey = null;
    this.petPoseLoadingKey = null;
    this.petPoseIndex = 0;

    this.initDOMElements();
    this.bindGlobalEvents();
    this.renderSetupSpeciesGrid();
    this.renderAdvisorLessons();

    // Подписываемся на изменения состояния игры
    gameState.subscribe((state) => this.render(state));
  }

  initDOMElements() {
    // Экраны
    this.topHud = document.getElementById('top-hud');
    this.screenSetup = document.getElementById('screen-setup');
    this.screenMap = document.getElementById('screen-map');
    this.screenRoom = document.getElementById('screen-room');
    this.titleSplash = document.getElementById('title-splash');
    this.btnEnterGame = document.getElementById('btn-enter-game');

    // HUD элементы
    this.hudPetName = document.getElementById('hud-pet-name');
    this.hudPetMood = document.getElementById('hud-pet-mood');
    this.hudPetAvatar = document.getElementById('hud-pet-avatar-img');
    this.hudPeriodNum = document.getElementById('hud-period-num');
    this.hudCoinsVal = document.getElementById('hud-coins-val');
    this.hudSavingsVal = document.getElementById('hud-savings-val');
    this.advisorText = document.getElementById('advisor-text');
    this.hudMenuToggle = document.getElementById('btn-open-hud-menu');
    this.hudMenuPanel = document.getElementById('hud-menu-panel');

    // Настройка питомца
    this.setupPreviewImg = document.getElementById('setup-preview-img');
    this.setupPreviewCard = document.getElementById('setup-preview-card');
    this.setupPetNameInput = document.getElementById('setup-pet-name');
    this.setupPlayerNameInput = document.getElementById('setup-player-name');
    this.setupGenderButtons = document.querySelectorAll('.gender-choice');
    this.speciesGrid = document.getElementById('setup-species-grid');
    this.setupSelectedSpecies = document.getElementById('setup-selected-species');
    this.setupCarousel = document.getElementById('setup-pet-carousel');

    // Карта города
    this.mapBgImg = document.getElementById('map-bg-img');
    this.mapPinsContainer = document.getElementById('map-pins-container');
    this.btnToggleMap = document.getElementById('btn-toggle-map-orientation');
    this.mapToggleText = document.getElementById('map-toggle-text');

    // Комната
    this.roomNavTitle = document.getElementById('room-nav-title');
    this.roomNavEmoji = document.getElementById('room-nav-emoji');
    this.roomStage = document.getElementById('room-stage');
    this.roomBgImg = document.getElementById('room-bg-img');
    this.roomHeroActor = document.getElementById('room-hero-actor');
    this.roomHeroImg = document.getElementById('room-hero-img');
    this.roomPetActor = document.getElementById('room-pet-actor');
    this.roomPetImg = document.getElementById('room-pet-img');
    this.petSpeechText = document.getElementById('pet-speech-text');
    this.petSpeechEmoji = document.getElementById('pet-speech-emoji');
    this.btnRoomExitCity = document.getElementById('btn-room-exit-city');
    this.petAccHead = document.getElementById('pet-acc-head');
    this.petAccNeck = document.getElementById('pet-acc-neck');
    this.petAccGlasses = document.getElementById('pet-acc-glasses');
    this.roomNpcActor = document.getElementById('room-npc-actor');
    this.roomNpcImg = document.getElementById('room-npc-img');
    this.roomNpcBubble = document.getElementById('room-npc-bubble');
    this.roomNpcEmoji = document.getElementById('room-npc-emoji');
    this.roomNpcText = document.getElementById('room-npc-text');
    this.roomDock = document.getElementById('room-dock');
    this.friendIntroStage = document.getElementById('friend-intro-stage');
    this.friendChallengeStage = document.getElementById('friend-challenge-stage');
    this.friendIntroTitle = document.getElementById('friend-intro-title');
    this.friendIntroTheme = document.getElementById('friend-intro-theme');
    this.friendIntroEmblem = document.getElementById('friend-intro-emblem');
    this.friendBeginButton = document.getElementById('btn-friend-begin');
    this.friendChallengeName = document.getElementById('friend-challenge-name');
    this.friendChallengeEmblem = document.getElementById('friend-challenge-emblem');

    // Модальные окна
    this.modals = {
      budget: document.getElementById('modal-budget'),
      shop: document.getElementById('modal-shop'),
      bank: document.getElementById('modal-bank'),
      hospital: document.getElementById('modal-hospital'),
      friend: document.getElementById('modal-friend'),
      friendship: document.getElementById('modal-friendship'),
      wardrobe: document.getElementById('modal-wardrobe'),
      periodSummary: document.getElementById('modal-period-summary'),
      finance: document.getElementById('modal-finance'),
      advisor: document.getElementById('modal-advisor'),
      adult: document.getElementById('modal-adult'),
      settings: document.getElementById('modal-settings'),
      resetConfirm: document.getElementById('modal-reset-confirm')
    };

    // Настройка интерактивной подгонки персонажей
    this.setupActorTweaker();
  }

  bindGlobalEvents() {
    // Свойство onclick здесь намеренно: заставка находится выше всей игры и
    // должна закрываться даже после горячей перезагрузки Vite во время демо.
    if (this.btnEnterGame) this.btnEnterGame.onclick = () => {
      sound.playPop();
      this.dismissTitleSplash();
    };

    // Закрытие модальных окон
    document.querySelectorAll('[data-close]').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        sound.playPop();
        const modalId = e.currentTarget.getAttribute('data-close');
        this.closeModal(modalId);
        const parentBackdrop = e.currentTarget.closest('.modal-backdrop');
        if (parentBackdrop) {
          parentBackdrop.classList.remove('active');
          parentBackdrop.style.display = '';
        }
      });
    });

    document.querySelectorAll('.modal-close-btn').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        sound.playPop();
        const parentBackdrop = e.currentTarget.closest('.modal-backdrop');
        if (parentBackdrop) {
          parentBackdrop.classList.remove('active');
          parentBackdrop.style.display = '';
        }
      });
    });

    // Клик по подложке модального окна закрывает его
    document.querySelectorAll('.modal-backdrop').forEach((backdrop) => {
      backdrop.addEventListener('click', (e) => {
        if (e.target === backdrop) {
          sound.playPop();
          backdrop.classList.remove('active');
        }
      });
    });

    // Нажатие Escape
    window.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        document.querySelectorAll('.modal-backdrop.active').forEach((m) => m.classList.remove('active'));
        this.closeHudMenu();
      }
    });

    // Компактное меню верхней навигации
    this.hudMenuToggle?.addEventListener('click', (e) => {
      e.stopPropagation();
      this.toggleHudMenu();
    });

    document.addEventListener('click', (e) => {
      if (this.hudMenuPanel?.hidden) return;
      if (!this.topHud?.contains(e.target)) this.closeHudMenu();
    });

    // Верхняя панель показывает питомца; действия открываются только через гамбургер.

    document.getElementById('hud-coins-btn')?.addEventListener('click', () => {
      this.closeHudMenu();
      sound.playCoin();
      this.openModal('finance');
    });

    document.getElementById('hud-savings-btn')?.addEventListener('click', () => {
      this.closeHudMenu();
      sound.playCoin();
      this.openModal('bank');
    });

    document.getElementById('btn-open-advisor')?.addEventListener('click', () => {
      this.closeHudMenu();
      sound.playPop();
      this.openModal('advisor');
    });

    document.getElementById('btn-open-finance')?.addEventListener('click', () => {
      this.closeHudMenu();
      sound.playPop();
      this.openModal('finance');
    });

    document.getElementById('btn-open-settings')?.addEventListener('click', () => {
      this.closeHudMenu();
      sound.playPop();
      this.openModal('settings');
    });

    document.getElementById('btn-open-adult')?.addEventListener('click', () => {
      this.closeHudMenu();
      sound.playPop();
      this.generateAdultMathQuestion();
      this.openModal('adult');
    });

    // Клик по хозяину комнаты или служащему (NPC)
    this.roomNpcActor?.addEventListener('click', () => {
      sound.playPop();
      this.roomNpcActor.classList.add('bounce');
      setTimeout(() => this.roomNpcActor?.classList.remove('bounce'), 450);

      const loc = gameState.state.currentLocation;
      if (loc.startsWith('friend_')) {
        const friendId = parseInt(loc.split('_')[1], 10);
        this.openFriendDialogue(friendId);
      } else if (loc === 'shop') {
        this.openModal('shop');
      } else if (loc === 'bank') {
        this.openModal('bank');
      } else if (loc === 'hospital') {
        this.openModal('hospital');
      }
    });

    // Хотбар на карте
    document.getElementById('hotbar-home')?.addEventListener('click', () => {
      sound.playPop();
      gameState.changeLocation('myroom');
    });
    document.getElementById('hotbar-bank')?.addEventListener('click', () => {
      sound.playPop();
      gameState.changeLocation('bank');
    });
    document.getElementById('hotbar-shop')?.addEventListener('click', () => {
      sound.playPop();
      gameState.changeLocation('shop');
    });
    document.getElementById('hotbar-hospital')?.addEventListener('click', () => {
      sound.playPop();
      gameState.changeLocation('hospital');
    });
    document.getElementById('hotbar-wardrobe')?.addEventListener('click', () => {
      sound.playPop();
      this.openModal('wardrobe');
    });
    document.getElementById('hotbar-budget')?.addEventListener('click', () => {
      sound.playPop();
      this.openModal('budget');
    });
    document.getElementById('hotbar-options')?.addEventListener('click', () => {
      sound.playPop();
      this.openModal('settings');
    });

    // Переключатель ориентации карты 📱 / 🖥️
    this.btnToggleMap?.addEventListener('click', () => {
      sound.playPop();
      this.isMapPanorama = !this.isMapPanorama;
      this.renderMapPins(gameState.state);
    });

    // Кнопка выхода из комнаты в город
    this.btnRoomExitCity?.addEventListener('click', () => {
      sound.playPop();
      gameState.changeLocation('citymap');
    });

    // Клик по самому питомцу
    this.roomPetActor?.addEventListener('click', () => {
      sound.playPurr();
      const phrases = ['Мур-мур! Люблю тебя! 💖', 'Гав! Побегаем? 🎾', 'Ура! Ты самый лучший друг! ✨', 'Спасибо за заботу! 🐾'];
      const emojis = ['💖', '🌟', '🐾', '🥰'];
      const randIdx = Math.floor(Math.random() * phrases.length);
      this.petSpeechText.textContent = phrases[randIdx];
      this.petSpeechEmoji.textContent = emojis[randIdx];
    });

    // Карусель выбора питомца: стрелки и свайп по аватару
    document.getElementById('btn-species-prev')?.addEventListener('click', () => this.cycleSpecies(-1));
    document.getElementById('btn-species-next')?.addEventListener('click', () => this.cycleSpecies(1));
    this.setupCarousel?.addEventListener('pointerdown', (e) => {
      this.setupSwipeStartX = e.clientX;
    });
    this.setupCarousel?.addEventListener('pointerup', (e) => {
      if (this.setupSwipeStartX === null) return;
      const delta = e.clientX - this.setupSwipeStartX;
      this.setupSwipeStartX = null;
      if (Math.abs(delta) >= 36) this.cycleSpecies(delta < 0 ? 1 : -1);
    });

    // Экран создания: выбор окраса
    document.querySelectorAll('.pattern-btn').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        sound.playPop();
        document.querySelectorAll('.pattern-btn').forEach((b) => b.classList.remove('selected'));
        e.currentTarget.classList.add('selected');
        const pattern = e.currentTarget.getAttribute('data-pattern');
        const selectedSpecies = document.querySelector('.species-chip.selected')?.getAttribute('data-species') || 'cat';
        this.updateSetupPreview(selectedSpecies, pattern);
      });
    });

    this.setupGenderButtons.forEach((button) => {
      button.addEventListener('click', (event) => {
        sound.playPop();
        this.setupGenderButtons.forEach((choice) => {
          const selected = choice === event.currentTarget;
          choice.classList.toggle('selected', selected);
          choice.setAttribute('aria-pressed', String(selected));
        });
      });
    });

    // Кнопка старта игры
    document.getElementById('btn-start-game')?.addEventListener('click', () => {
      sound.playLevelUp();
      const selectedSpecies = document.querySelector('.species-chip.selected')?.getAttribute('data-species') || 'cat';
      const selectedPattern = document.querySelector('.pattern-btn.selected')?.getAttribute('data-pattern') || 'classic';
      const petName = this.setupPetNameInput.value.trim() || 'Финни';
      const playerName = this.setupPlayerNameInput.value.trim() || 'Юный финансист';
      const playerGender = document.querySelector('.gender-choice.selected')?.getAttribute('data-gender') || 'boy';

      gameState.startNewGame({
        petName,
        species: selectedSpecies,
        pattern: selectedPattern,
        playerName,
        playerGender
      });

      // Сразу после экрана создания персонажа ОБЯЗАТЕЛЬНО запускаем интерактивное обучение
      setTimeout(() => {
        tutorial.start(0);
      }, 150);
    });

    // Бюджет: +/- кнопки
    document.querySelectorAll('.stepper-btn[data-jar]').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        sound.playPop();
        const jar = e.currentTarget.getAttribute('data-jar');
        const delta = parseInt(e.currentTarget.getAttribute('data-delta'), 10);
        gameState.allocateBudget(jar, delta);
      });
    });

    // Бюджет: готовые пресеты
    document.getElementById('btn-preset-5020')?.addEventListener('click', () => {
      sound.playPop();
      gameState.applyPresetBudget('50_30_20');
    });
    document.getElementById('btn-preset-saver')?.addEventListener('click', () => {
      sound.playPop();
      gameState.applyPresetBudget('saver');
    });
    document.getElementById('btn-preset-equal')?.addEventListener('click', () => {
      sound.playPop();
      gameState.applyPresetBudget('equal');
    });

    // Бюджет: Утвердить план
    document.getElementById('btn-confirm-budget')?.addEventListener('click', () => {
      const ok = gameState.confirmBudget();
      if (ok) {
        sound.playSuccess();
        fireConfetti({ count: 50 });
        this.closeModal('budget');
      } else {
        sound.playError();
      }
    });

    // Магазин: вкладки категорий
    document.querySelectorAll('.shop-tab').forEach((tab) => {
      tab.addEventListener('click', (e) => {
        sound.playPop();
        document.querySelectorAll('.shop-tab').forEach((t) => t.classList.remove('active'));
        e.currentTarget.classList.add('active');
        this.shopCurrentTab = e.currentTarget.getAttribute('data-category');
        this.renderShopItems(gameState.state);
      });
    });

    // Банк: пополнение и снятие
    document.getElementById('btn-deposit-10')?.addEventListener('click', () => {
      const res = gameState.depositSavings(10);
      if (res && res.success) {
        sound.playCoin();
        if (res.achieved) {
          sound.playLevelUp();
          fireConfetti({ count: 80 });
        }
      } else {
        sound.playError();
      }
    });

    document.getElementById('btn-deposit-25')?.addEventListener('click', () => {
      const res = gameState.depositSavings(25);
      if (res && res.success) {
        sound.playCoin();
        if (res.achieved) {
          sound.playLevelUp();
          fireConfetti({ count: 80 });
        }
      } else {
        sound.playError();
      }
    });

    document.getElementById('btn-withdraw-10')?.addEventListener('click', () => {
      if (confirm('Внимание: снятие монет из копилки отдалит достижение твоей мечты. Точно снять 10 монет в кошелёк?')) {
        const ok = gameState.withdrawSavings(10);
        if (ok) {
          sound.playCoin();
        } else {
          sound.playError();
        }
      }
    });

    // Больница: осмотр и лечение
    document.getElementById('btn-hospital-checkup')?.addEventListener('click', () => {
      const res = gameState.hospitalCheckup();
      if (res.needsCare) {
        sound.playPop();
      } else {
        sound.playSuccess();
      }
      this.renderHospitalModal(gameState.state);
    });

    document.getElementById('btn-hospital-treat')?.addEventListener('click', () => {
      const res = gameState.treatPet();
      if (res.success) {
        sound.playSuccess();
        fireConfetti({ count: 40 });
      } else {
        sound.playError();
      }
      this.renderHospitalModal(gameState.state);
    });

    // Задание друга: кнопка забрать награду
    document.getElementById('btn-claim-puzzle-reward')?.addEventListener('click', () => {
      const ok = gameState.claimPuzzleReward(this.activeFriendId);
      if (ok) {
        sound.playLevelUp();
        fireConfetti({ count: 70 });
        this.closeModal('friend');
      }
    });

    this.friendBeginButton?.addEventListener('click', () => {
      sound.playPop();
      this.setFriendDialogueStage('challenge');
    });
    document.getElementById('btn-friend-back')?.addEventListener('click', () => {
      sound.playPop();
      this.setFriendDialogueStage('intro');
    });
    document.getElementById('btn-friend-finish')?.addEventListener('click', () => {
      sound.playPop();
      this.closeModal('friend');
    });

    // Итоги периода: начать следующий период
    document.getElementById('btn-start-next-period')?.addEventListener('click', () => {
      sound.playLevelUp();
      this.closeModal('periodSummary');
      setTimeout(() => {
        this.openModal('budget');
      }, 400);
    });

    // Раздел взрослого: проверка ответа на математическую задачу
    document.getElementById('btn-verify-adult-gate')?.addEventListener('click', () => {
      const input = document.getElementById('math-gate-input');
      const val = parseInt(input?.value, 10);
      if (val === this.adultMathAnswer) {
        sound.playSuccess();
        document.getElementById('adult-barrier-gate').style.display = 'none';
        document.getElementById('adult-content-area').style.display = 'flex';
        this.renderAdultStats(gameState.state);
      } else {
        sound.playError();
        alert('Неверный ответ на проверочный вопрос. Попробуйте ещё раз.');
        this.generateAdultMathQuestion();
      }
    });

    // Раздел взрослого: полный сброс через подтверждение внутри интерфейса
    document.getElementById('btn-adult-reset-demo')?.addEventListener('click', () => {
      sound.playPop();
      this.openResetConfirmation();
    });

    document.getElementById('btn-cancel-reset')?.addEventListener('click', () => {
      sound.playPop();
      this.closeModal('resetConfirm');
    });

    document.getElementById('btn-confirm-reset')?.addEventListener('click', () => {
      sound.playPop();
      gameState.resetDemo();
      this.selectedSpeciesIndex = 0;
      document.querySelectorAll('.species-chip').forEach((chip, idx) => chip.classList.toggle('selected', idx === 0));
      this.updateSetupPreview(PET_SPECIES[0]?.id || 'cat', 'classic');
      this.closeAllModals();
    });

    // Настройки: звук
    document.getElementById('btn-toggle-sound')?.addEventListener('click', (e) => {
      const isEnabled = gameState.toggleSound();
      sound.setEnabled(isEnabled);
      sound.playPop();
      e.currentTarget.textContent = isEnabled ? 'Включены' : 'Выключены';
      e.currentTarget.className = isEnabled ? 'clay-btn btn-green' : 'clay-btn btn-ghost';
    });

    // Настройки: шрифт
    document.getElementById('btn-toggle-font')?.addEventListener('click', (e) => {
      const isLarge = gameState.toggleLargeFont();
      sound.playPop();
      document.body.classList.toggle('large-font', isLarge);
      e.currentTarget.textContent = isLarge ? 'Увеличенный' : 'Обычный';
      e.currentTarget.className = isLarge ? 'clay-btn btn-green' : 'clay-btn btn-ghost';
    });

    // Настройки: перезапуск через подтверждение внутри интерфейса
    document.getElementById('btn-settings-restart')?.addEventListener('click', () => {
      sound.playPop();
      this.openResetConfirmation();
    });
  }

  // --- ОТРИСОВКА ИНТЕРФЕЙСА ---

  render(state) {
    // Локация меняется независимо от модальных окон. Закрываем временные
    // окна при переходе, чтобы они не перекрывали новую сцену.
    if (this.lastRenderedLocation !== null && this.lastRenderedLocation !== state.currentLocation) {
      this.closeAllModals();
    }
    this.lastRenderedLocation = state.currentLocation;

    if (!state.isGameStarted) {
      this.topHud.style.display = 'none';
      this.showScreen('screen-setup');
      this.showTitleSplashIfNeeded();
      return;
    }

    this.titleSplashDismissed = true;
    try { window.localStorage.setItem('finny_title_splash_seen', '1'); } catch (_) {}
    try { window.sessionStorage.setItem('finny_title_splash_seen', '1'); } catch (_) {}
    this.hideTitleSplashImmediately();
    this.topHud.style.display = 'flex';

    // 1. Обновляем верхний HUD
    this.hudPetName.textContent = state.pet.name;
    const moodEmojis = {
      happy: '✨ Счастливый',
      hungry: '🥣 Проголодался',
      playful: '🎈 Игривый',
      proud_saver: '🏺 Гордый',
      sleepy: '💤 Уютный'
    };
    this.hudPetMood.textContent = moodEmojis[state.pet.mood] || '✨ Доволен';
    this.hudPetAvatar.src = this.getPetAssetPath(state.pet.species, state.pet.pattern);
    this.hudPeriodNum.textContent = state.period;
    this.hudCoinsVal.textContent = state.wallet.coins;
    this.hudSavingsVal.textContent = state.wallet.savings;
    this.advisorText.textContent = state.advisorTip;

    // 2. Отображаем активный экран
    if (state.currentLocation === 'citymap') {
      this.showScreen('screen-map');
      this.renderMapPins(state);
    } else {
      this.showScreen('screen-room');
      this.renderRoom(state);
    }

    // 3. Обновляем открытые модальные окна
    this.renderBudgetModal(state);
    this.renderBankModal(state);
    this.renderShopItems(state);
    this.renderWardrobeItems(state);
    this.renderFinanceModal(state);
    this.renderAdvisorLessons(state);
  }

  showScreen(screenId) {
    document.querySelectorAll('.screen').forEach((s) => s.classList.remove('active'));
    document.getElementById(screenId)?.classList.add('active');
  }

  readTitleSplashState() {
    try {
      if (window.localStorage.getItem('finny_title_splash_seen') === '1') return true;
    } catch (_) { /* Try tab storage below. */ }
    try {
      return window.sessionStorage.getItem('finny_title_splash_seen') === '1';
    } catch (_) { return false; }
  }

  showTitleSplashIfNeeded() {
    if (!this.titleSplash || this.titleSplashDismissed) return;
    this.titleSplash.hidden = false;
    this.titleSplash.classList.remove('is-leaving');
  }

  dismissTitleSplash() {
    if (!this.titleSplash) return;
    this.titleSplashDismissed = true;
    try { window.localStorage.setItem('finny_title_splash_seen', '1'); } catch (_) {}
    try { window.sessionStorage.setItem('finny_title_splash_seen', '1'); } catch (_) {}
    this.titleSplash.classList.add('is-leaving');
    window.setTimeout(() => {
      if (this.titleSplash) this.titleSplash.hidden = true;
    }, 360);
  }

  hideTitleSplashImmediately() {
    if (!this.titleSplash) return;
    this.titleSplash.classList.remove('is-leaving');
    this.titleSplash.hidden = true;
  }

  toggleHudMenu(force) {
    if (!this.hudMenuPanel || !this.hudMenuToggle) return;
    const shouldOpen = typeof force === 'boolean' ? force : this.hudMenuPanel.hidden;
    this.hudMenuPanel.hidden = !shouldOpen;
    this.hudMenuToggle.setAttribute('aria-expanded', String(shouldOpen));
    this.hudMenuToggle.setAttribute('aria-label', shouldOpen ? 'Закрыть меню' : 'Открыть меню');
    this.hudMenuToggle.classList.toggle('is-open', shouldOpen);
    this.topHud?.classList.toggle('menu-open', shouldOpen);
  }

  closeHudMenu() {
    this.toggleHudMenu(false);
  }

  openResetConfirmation() {
    this.openModal('resetConfirm');
  }

  openModal(modalKey) {
    const modal = this.modals[modalKey] || document.getElementById(modalKey) || document.getElementById(`modal-${modalKey}`);
    if (modal) {
      modal.classList.add('active');
      modal.style.display = '';
    }
  }

  closeModal(modalKey) {
    const modal = this.modals[modalKey] || document.getElementById(modalKey) || document.getElementById(`modal-${modalKey}`);
    if (modal) {
      modal.classList.remove('active');
      modal.style.display = '';
    }
  }

  closeAllModals() {
    document.querySelectorAll('.modal-backdrop.active').forEach((modal) => {
      modal.classList.remove('active');
      modal.style.display = '';
    });
  }

  // Отрисовка списка видов питомца в сетапе
  renderSetupSpeciesGrid() {
    this.speciesGrid.innerHTML = '';
    PET_SPECIES.forEach((p, idx) => {
      const chip = document.createElement('button');
      chip.type = 'button';
      chip.setAttribute('aria-label', `Выбрать питомца: ${p.name}`);
      chip.className = `species-chip ${idx === 0 ? 'selected' : ''}`;
      chip.setAttribute('data-species', p.id);
      chip.innerHTML = `
        <span class="species-emoji">${p.emoji}</span>
        <span class="species-name">${p.name}</span>
      `;
      chip.addEventListener('click', () => {
        sound.playPop();
        this.selectedSpeciesIndex = idx;
        document.querySelectorAll('.species-chip').forEach((c) => c.classList.remove('selected'));
        chip.classList.add('selected');
        const pattern = document.querySelector('.pattern-btn.selected')?.getAttribute('data-pattern') || 'classic';
        this.updateSetupPreview(p.id, pattern);
      });
      this.speciesGrid.appendChild(chip);
    });
    this.updateSetupPreview(PET_SPECIES[0]?.id || 'cat', 'classic');
  }

  updateSetupPreview(speciesId, pattern) {
    const pet = PET_SPECIES.find((p) => p.id === speciesId);
    if (pet) {
      const assetPath = this.getPetAssetPath(pet.id, pattern);
      this.setupPreviewImg.src = assetPath;
      this.setupPreviewImg.alt = pet.name;
      const patternName = COLOR_PATTERNS.find((item) => item.id === pattern)?.name || 'Классический';
      if (this.setupSelectedSpecies) this.setupSelectedSpecies.textContent = `${pet.emoji} ${pet.name} · ${patternName}`;
      if (this.setupPreviewCard) {
        this.setupPreviewCard.dataset.pattern = pattern;
      }
    }
  }

  getPetAssetPath(speciesId, pattern = 'classic') {
    const variant = pattern === 'spotted' ? '_spotted' : '';
    return `assets/characters/pet_${speciesId}${variant}_v2.png`;
  }

  cycleSpecies(direction) {
    if (!PET_SPECIES.length) return;
    this.selectedSpeciesIndex = (this.selectedSpeciesIndex + direction + PET_SPECIES.length) % PET_SPECIES.length;
    const pet = PET_SPECIES[this.selectedSpeciesIndex];
    document.querySelectorAll('.species-chip').forEach((chip, idx) => {
      chip.classList.toggle('selected', idx === this.selectedSpeciesIndex);
    });
    const pattern = document.querySelector('.pattern-btn.selected')?.getAttribute('data-pattern') || 'classic';
    this.updateSetupPreview(pet.id, pattern);
    sound.playPop();
  }

  // Отрисовка пинов на карте города
  renderMapPins(state) {
    this.mapPinsContainer.innerHTML = '';

    if (this.isMapPanorama) {
      this.mapBgImg.src = 'assets/map_city_town_horizontal.png';
      this.mapToggleText.textContent = '9:16';
    } else {
      this.mapBgImg.src = 'assets/map_city_town_vertical.png';
      this.mapToggleText.textContent = '16:9';
    }

    // Координаты для вертикальной и горизонтальной карты
    const pinConfigs = this.isMapPanorama
      ? [
          { id: 'friend_2', title: 'Рыжик', asset: 'characters/friend_2_v2', x: 16, y: 18, type: 'pin-cat' },
          { id: 'shop', title: 'Лавка', asset: 'building_shop', x: 33, y: 35, type: 'pin-shop' },
          { id: 'bank', title: 'Банк', asset: 'building_bank', x: 50, y: 27, type: 'pin-bank' },
          { id: 'hospital', title: 'Клиника', asset: 'building_hospital', x: 67, y: 37, type: 'pin-hospital' },
          { id: 'friend_1', title: 'Потап', asset: 'characters/friend_1_v2', x: 18, y: 55, type: 'pin-panda' },
          { id: 'friend_3', title: 'Тёма', asset: 'characters/friend_3_v2', x: 10, y: 65, type: 'pin-raccoon' },
          { id: 'friend_6', title: 'Сеня', asset: 'characters/friend_6_v2', x: 33, y: 71, type: 'pin-bunny' },
          { id: 'friend_5', title: 'Алиса', asset: 'characters/friend_5_v2', x: 42, y: 73, type: 'pin-fox' },
          { id: 'friend_4', title: 'София', asset: 'characters/friend_4_v2', x: 88, y: 57, type: 'pin-owl' },
          { id: 'friend_7', title: 'Барбос', asset: 'characters/friend_7_v2', x: 72, y: 81, type: 'pin-dog' },
          { id: 'myroom', title: 'Мой дом', asset: 'building_home', x: 88, y: 83, type: 'pin-home' }
        ]
      : [
          { id: 'friend_2', title: 'Рыжик', asset: 'characters/friend_2_v2', x: 14, y: 44, type: 'pin-cat' },
          { id: 'friend_1', title: 'Потап', asset: 'characters/friend_1_v2', x: 16, y: 30, type: 'pin-panda' },
          { id: 'friend_3', title: 'Тёма', asset: 'characters/friend_3_v2', x: 68, y: 27, type: 'pin-raccoon' },
          { id: 'friend_4', title: 'София', asset: 'characters/friend_4_v2', x: 89, y: 31, type: 'pin-owl' },
          { id: 'friend_6', title: 'Сеня', asset: 'characters/friend_6_v2', x: 36, y: 37, type: 'pin-bunny' },
          { id: 'shop', title: 'Лавка', asset: 'building_shop', x: 38, y: 48, type: 'pin-shop' },
          { id: 'bank', title: 'Банк', asset: 'building_bank', x: 74, y: 45, type: 'pin-bank' },
          { id: 'hospital', title: 'Клиника', asset: 'building_hospital', x: 87, y: 56, type: 'pin-hospital' },
          { id: 'friend_5', title: 'Алиса', asset: 'characters/friend_5_v2', x: 70, y: 70, type: 'pin-fox' },
          { id: 'friend_7', title: 'Барбос', asset: 'characters/friend_7_v2', x: 86, y: 78, type: 'pin-dog' },
          { id: 'myroom', title: 'Мой дом', asset: 'building_home', x: 25, y: 80, type: 'pin-home' }
        ];

    pinConfigs.forEach((cfg) => {
      const pin = document.createElement('button');
      pin.type = 'button';
      pin.id = `map-pin-${cfg.id}`;
      pin.dataset.pinId = cfg.id;
      pin.setAttribute('aria-label', `Открыть: ${cfg.title}`);
      pin.className = `map-pin ${cfg.type}`;
      pin.style.left = `${cfg.x}%`;
      pin.style.top = `${cfg.y}%`;

      let badgeHtml = '';
      if (cfg.id.startsWith('friend_')) {
        const friendId = parseInt(cfg.id.split('_')[1], 10);
        const pState = state.puzzles[friendId]?.state;
        if (pState === 'completed') {
          badgeHtml = '<span class="pin-badge done">✓</span>';
        } else if (pState === 'solved_unclaimed') {
          badgeHtml = '<span class="pin-badge reward">🪙 +20</span>';
        } else {
          badgeHtml = '<span class="pin-badge">+20 🪙</span>';
        }
      } else if (cfg.id === 'bank') {
        badgeHtml = `<span class="pin-badge facility">${state.wallet.savings} м.</span>`;
      } else if (cfg.id === 'hospital') {
        badgeHtml = '<span class="pin-badge facility">ОСМОТР</span>';
      } else if (cfg.id === 'shop') {
        badgeHtml = '<span class="pin-badge facility">ЛАВКА</span>';
      } else if (cfg.id === 'myroom') {
        badgeHtml = '<span class="pin-badge done">ДОМОЙ</span>';
      }

      pin.innerHTML = `
        <div class="pin-avatar-wrap">
          <img class="pin-avatar-img" src="assets/${cfg.asset}.png" alt="${cfg.title}">
          ${badgeHtml}
        </div>
        <span class="pin-label">${cfg.title}</span>
      `;

      pin.addEventListener('click', () => {
        sound.playPop();
        gameState.changeLocation(cfg.id);
      });

      this.mapPinsContainer.appendChild(pin);
    });
  }

  // В каждой сцене персонажи (герой, питомец, хозяин комнаты / служащий)
  // представлены отдельными живыми анимированными актерами.
  renderRoomParty(state) {
    if (!this.roomHeroActor || !this.roomPetActor) return;

    const loc = state.currentLocation;
    const isHome = loc === 'myroom';

    this.roomHeroActor.style.display = 'flex';
    const playerGender = state.playerGender === 'girl' ? 'girl' : 'boy';
    this.roomHeroActor.dataset.gender = playerGender;
    this.roomHeroImg.src = playerGender === 'girl'
      ? 'assets/hero_idle.png'
      : 'assets/characters/hero_v2.png';

    const roomPartyConfigs = {
      myroom: {
        hero: { left: '29%', bottom: '19%', faceRight: true },
        npc: null
      },
      friend_1: {
        hero: { left: '74%', bottom: '19%', faceRight: false },
        npc: { left: '28%', bottom: '19%', faceRight: true, asset: 'assets/characters/friend_1_v2.png', emoji: '🐻', phrase: 'Привет, заходи!' }
      },
      friend_2: {
        hero: { left: '72%', bottom: '20%', faceRight: false },
        npc: { left: '28%', bottom: '20%', faceRight: true, asset: 'assets/characters/friend_2_v2.png', emoji: '🐿️', phrase: 'Поболтаем?' }
      },
      friend_3: {
        hero: { left: '26%', bottom: '19%', faceRight: true },
        npc: { left: '72%', bottom: '20%', faceRight: false, asset: 'assets/characters/friend_3_v2.png', emoji: '🦊', phrase: 'Хи-хи, привет!' }
      },
      friend_4: {
        hero: { left: '74%', bottom: '20%', faceRight: false },
        npc: { left: '28%', bottom: '20%', faceRight: true, asset: 'assets/characters/friend_4_v2.png', emoji: '🐰', phrase: 'Рад встрече!' }
      },
      friend_5: {
        hero: { left: '28%', bottom: '19%', faceRight: true },
        npc: { left: '70%', bottom: '20%', faceRight: false, asset: 'assets/characters/friend_5_v2.png', emoji: '🦉', phrase: 'Добрый день!' }
      },
      friend_6: {
        hero: { left: '74%', bottom: '18%', faceRight: false },
        npc: { left: '28%', bottom: '18%', faceRight: true, asset: 'assets/characters/friend_6_v2.png', emoji: '🦔', phrase: 'Заглядывай на чай!' }
      },
      friend_7: {
        hero: { left: '26%', bottom: '19%', faceRight: true },
        npc: { left: '72%', bottom: '20%', faceRight: false, asset: 'assets/characters/friend_7_v2.png', emoji: '🦝', phrase: 'Мастерю новенькое!' }
      },
      shop: {
        hero: { left: '26%', bottom: '19%', faceRight: true },
        npc: { left: '70%', bottom: '21%', faceRight: false, asset: 'assets/characters/worker_shop_v2.png', emoji: '🛒', phrase: 'Свежие товары!' }
      },
      bank: {
        hero: { left: '28%', bottom: '20%', faceRight: true },
        npc: { left: '68%', bottom: '22%', faceRight: false, asset: 'assets/characters/worker_bank_v2.png', emoji: '🏦', phrase: 'Вклады и копилка!' }
      },
      hospital: {
        hero: { left: '72%', bottom: '19%', faceRight: false },
        npc: { left: '28%', bottom: '20%', faceRight: true, asset: 'assets/characters/worker_hospital_v2.png', emoji: '🩺', phrase: 'Проверим здоровье!' }
      }
    };

    const cfg = roomPartyConfigs[loc] || roomPartyConfigs.myroom;

    // Управление живым NPC
    if (this.roomNpcActor) {
      if (cfg.npc) {
        this.roomNpcActor.style.display = 'flex';
        const friendMatch = loc.match(/^friend_(\d+)$/);
        const friend = friendMatch
          ? FRIENDS_LIST.find((item) => item.id === Number(friendMatch[1]))
          : null;
        const employeeNames = {
          shop: 'Продавец Енотик',
          bank: 'Банкир',
          hospital: 'Доктор Сова'
        };
        if (this.roomNpcImg) {
          this.roomNpcImg.src = friend?.idle || cfg.npc.asset;
          this.roomNpcImg.alt = friend?.name || employeeNames[loc] || 'Сотрудник';
        }
        if (this.roomNpcEmoji) this.roomNpcEmoji.textContent = friend?.emoji || cfg.npc.emoji;
        if (this.roomNpcText) this.roomNpcText.textContent = friend?.greeting || cfg.npc.phrase;
      } else {
        this.roomNpcActor.style.display = 'none';
      }
    }

    // Управление питомцем игрока
    this.roomPetActor.style.display = isHome ? 'flex' : 'none';
    if (isHome) {
      const petCfg = ROOM_ACTOR_CONFIGS.myroom;
      this.roomPetActor.style.left = petCfg ? `${petCfg.x}%` : '50%';
      this.roomPetActor.style.bottom = petCfg ? `${petCfg.bottom}px` : '246px';
      this.roomPetActor.className = `pet-actor-container stage-${state.pet.growthStage}`;
      this.roomPetActor.dataset.pattern = state.pet.pattern || 'classic';
      this.updatePetPoseAnimation(state, true);
      const equipped = state.pet.equippedAccessories;
      const renderEquippedAccessory = (layer, accessoryId) => {
        layer.replaceChildren();
        const accessory = WARDROBE_ACCESSORIES.find((item) => item.id === accessoryId);
        if (!accessory?.asset) return;
        const image = document.createElement('img');
        image.src = accessory.asset;
        image.alt = '';
        image.draggable = false;
        layer.appendChild(image);
      };
      renderEquippedAccessory(this.petAccHead, equipped.head);
      renderEquippedAccessory(this.petAccNeck, equipped.neck);
      renderEquippedAccessory(this.petAccGlasses, equipped.glasses);
    } else {
      this.updatePetPoseAnimation(state, false);
    }
  }

  updatePetPoseAnimation(state, isHome) {
    const pet = PET_SPECIES.find((item) => item.id === state.pet.species);
    const pattern = state.pet.pattern || 'classic';
    const frames = pet?.poseFrames || [];

    if (!isHome || pattern !== 'classic' || frames.length < 2) {
      clearInterval(this.petPoseTimer);
      this.petPoseTimer = null;
      this.petPoseKey = null;
      this.petPoseLoadingKey = null;
      this.roomPetImg.src = this.getPetAssetPath(state.pet.species, pattern);
      return;
    }

    const key = `${pet.id}:${pattern}`;
    if (this.petPoseKey === key || this.petPoseLoadingKey === key) return;
    clearInterval(this.petPoseTimer);
    this.petPoseTimer = null;
    this.petPoseKey = null;
    this.petPoseLoadingKey = key;

    Promise.all(frames.map((src) => new Promise((resolve) => {
      const image = new Image();
      image.onload = () => resolve(true);
      image.onerror = () => resolve(false);
      image.src = src;
    }))).then((loaded) => {
      if (this.petPoseLoadingKey !== key) return;
      this.petPoseLoadingKey = null;
      if (loaded.some((ok) => !ok) || state.currentLocation !== 'myroom') {
        this.roomPetImg.src = this.getPetAssetPath(state.pet.species, pattern);
        return;
      }
      this.petPoseKey = key;
      this.petPoseIndex = 0;
      this.roomPetImg.src = frames[this.petPoseIndex];
      this.petPoseTimer = setInterval(() => {
        if (gameState.state.currentLocation !== 'myroom' || gameState.state.pet.species !== pet.id || gameState.state.pet.pattern !== pattern) {
          clearInterval(this.petPoseTimer);
          this.petPoseTimer = null;
          this.petPoseKey = null;
          return;
        }
        this.petPoseIndex = (this.petPoseIndex + 1) % frames.length;
        this.roomPetImg.src = frames[this.petPoseIndex];
      }, 1400);
    });
  }

  // Отрисовка сцены комнаты (11 интерьеров)
  renderRoom(state) {
    const loc = state.currentLocation;

    this.renderRoomParty(state);
    const roomCameras = {
      myroom: [1.12, 'center 54%'],
      friend_1: [1.18, 'center 50%'],
      friend_2: [1.08, 'center 48%'],
      friend_3: [1.16, 'center 51%'],
      friend_4: [1.06, 'center 50%'],
      friend_5: [1.10, 'center 50%'],
      friend_6: [1.08, 'center 51%'],
      friend_7: [1.38, 'center 53%'],
      shop: [1.18, 'center 52%'],
      bank: [1.34, 'center 52%'],
      hospital: [1.12, 'center 53%']
    };
    const [cameraScale, cameraOrigin] = roomCameras[loc] || roomCameras.myroom;
    this.roomStage?.style.setProperty('--room-bg-scale', cameraScale);
    this.roomStage?.style.setProperty('--room-bg-origin', cameraOrigin);
    // В гостях и учреждениях выход есть в нижней панели. Верхнюю копию
    // скрываем, чтобы не дублировать одно и то же действие.
    this.btnRoomExitCity?.classList.toggle('is-hidden', loc !== 'myroom');
    // При горячем обновлении Vite подписка состояния может сработать раньше,
    // чем пересоздан узел сцены. В обычном запуске он уже существует.
    if (this.roomStage) this.roomStage.dataset.location = loc;

    if (loc === 'myroom') {
      this.roomNavTitle.textContent = 'Моя уютная комната';
      this.roomNavEmoji.textContent = '🏠';
      this.roomBgImg.src = state.playerGender === 'girl'
        ? 'assets/bg_room_myroom.png'
        : 'assets/bg_room_myroom_boy.png';

      // Нижний док для дома
      this.roomDock.innerHTML = `
        <button class="dock-btn feed" id="dock-btn-feed">
          <div class="dock-btn-icon">${state.pet.isHungry ? '🥣' : '✨'}</div>
          <span class="dock-btn-label">${state.pet.isHungry ? 'Покормить' : 'Сыт'}</span>
        </button>
        <button class="dock-btn wash" id="dock-btn-wash">
          <div class="dock-btn-icon">🧼</div>
          <span class="dock-btn-label">Уход</span>
        </button>
        <button class="dock-btn play" id="dock-btn-play">
          <div class="dock-btn-icon">🎾</div>
          <span class="dock-btn-label">Поиграть</span>
        </button>
        <button class="dock-btn wardrobe" id="dock-btn-wardrobe">
          <div class="dock-btn-icon">👗</div>
          <span class="dock-btn-label">Наряды</span>
        </button>
        <button class="dock-btn budget" id="dock-btn-budget">
          <div class="dock-btn-icon">🎒</div>
          <span class="dock-btn-label">План</span>
        </button>
        <button class="dock-btn finish" id="dock-btn-finish">
          <div class="dock-btn-icon">📋</div>
          <span class="dock-btn-label">Итоги</span>
        </button>
      `;

      document.getElementById('dock-btn-feed')?.addEventListener('click', () => {
        sound.playPop();
        const res = gameState.carePet('feed');
        if (res.success) {
          sound.playPurr();
          fireConfetti({ count: 30 });
        } else {
          sound.playError();
        }
      });

      document.getElementById('dock-btn-wash')?.addEventListener('click', () => {
        sound.playPop();
        const res = gameState.carePet('wash');
        if (res.success) {
          sound.playPurr();
          fireConfetti({ count: 30 });
        } else {
          sound.playError();
        }
      });

      document.getElementById('dock-btn-play')?.addEventListener('click', () => {
        sound.playPop();
        const res = gameState.carePet('play');
        if (res.success) {
          sound.playPurr();
          fireConfetti({ count: 30 });
        } else {
          sound.playError();
        }
      });

      document.getElementById('dock-btn-wardrobe')?.addEventListener('click', () => {
        sound.playPop();
        this.openModal('wardrobe');
      });

      document.getElementById('dock-btn-budget')?.addEventListener('click', () => {
        sound.playPop();
        this.openModal('budget');
      });

      document.getElementById('dock-btn-finish')?.addEventListener('click', () => {
        sound.playPop();
        const res = gameState.finishPeriod();
        if (res.success) {
          sound.playLevelUp();
          fireConfetti({ count: 80 });
          this.renderPeriodSummaryModal(res.report);
          this.openModal('periodSummary');
        } else {
          sound.playError();
        }
      });

    } else if (loc.startsWith('friend_')) {
      const friendId = parseInt(loc.split('_')[1], 10);
      const friend = FRIENDS_LIST.find((f) => f.id === friendId);

      this.activeFriendId = friendId;
      this.roomNavTitle.textContent = friend ? friend.houseName : 'В гостях';
      this.roomNavEmoji.textContent = friend ? friend.emoji : '🏡';
      this.roomBgImg.src = `assets/bg_room_friend_${friendId}.png`;

      const openFriend = () => {
        sound.playPop();
        this.openFriendDialogue(friendId);
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-pink dock-action-btn" id="room-friend-dialogue-btn">
          💬 Поговорить
        </button>
        <button class="clay-btn btn-purple dock-action-btn" id="room-friend-profile-btn">
          🌟 Дружба (${state.friendships[friendId]?.level || 1} ур.)
        </button>
        <button class="clay-btn btn-ghost dock-action-btn" id="room-friend-exit-btn">
          🚪 В город
        </button>
      `;

      document.getElementById('room-friend-dialogue-btn')?.addEventListener('click', () => {
        sound.playPop();
        this.openFriendDialogue(friendId);
      });

      document.getElementById('room-friend-profile-btn')?.addEventListener('click', () => {
        sound.playPop();
        this.openFriendshipModal(friendId);
      });

      document.getElementById('room-friend-exit-btn')?.addEventListener('click', () => {
        sound.playPop();
        gameState.changeLocation('citymap');
      });

    } else if (loc === 'shop') {
      this.roomNavTitle.textContent = 'Лавка Енотика';
      this.roomNavEmoji.textContent = '🛒';
      this.roomBgImg.src = 'assets/bg_room_shop.png';

      const openShop = () => {
        sound.playPop();
        this.openModal('shop');
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-pink dock-action-btn" id="room-shop-open-btn">
          🛒 Прилавок товаров
        </button>
        <button class="clay-btn btn-ghost dock-action-btn" id="room-shop-exit-btn">
          🚪 В город
        </button>
      `;

      document.getElementById('room-shop-open-btn')?.addEventListener('click', () => {
        sound.playPop();
        this.openModal('shop');
      });
      document.getElementById('room-shop-exit-btn')?.addEventListener('click', () => {
        sound.playPop();
        gameState.changeLocation('citymap');
      });

    } else if (loc === 'bank') {
      this.roomNavTitle.textContent = 'Городской Банк';
      this.roomNavEmoji.textContent = '🏦';
      this.roomBgImg.src = 'assets/bg_room_bank.png';

      const openBank = () => {
        sound.playPop();
        this.openModal('bank');
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-gold dock-action-btn" id="room-bank-open-btn">
          🏦 Касса и копилка
        </button>
        <button class="clay-btn btn-ghost dock-action-btn" id="room-bank-exit-btn">
          🚪 В город
        </button>
      `;

      document.getElementById('room-bank-open-btn')?.addEventListener('click', () => {
        sound.playPop();
        this.openModal('bank');
      });
      document.getElementById('room-bank-exit-btn')?.addEventListener('click', () => {
        sound.playPop();
        gameState.changeLocation('citymap');
      });

    } else if (loc === 'hospital') {
      this.roomNavTitle.textContent = 'Клиника Доктора Совы';
      this.roomNavEmoji.textContent = '🏥';
      this.roomBgImg.src = 'assets/bg_room_hospital.png';

      const openHospital = () => {
        sound.playPop();
        this.openModal('hospital');
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-blue dock-action-btn" id="room-hospital-open-btn">
          🩺 Осмотр врача
        </button>
        <button class="clay-btn btn-ghost dock-action-btn" id="room-hospital-exit-btn">
          🚪 В город
        </button>
      `;

      document.getElementById('room-hospital-open-btn')?.addEventListener('click', () => {
        sound.playPop();
        this.openModal('hospital');
      });
      document.getElementById('room-hospital-exit-btn')?.addEventListener('click', () => {
        sound.playPop();
        gameState.changeLocation('citymap');
      });
    }

    // Применяем персональные координаты и масштаб персонажа для локации
    this.applyRoomActorConfig(loc);
  }

  getStoredActorConfigs() {
    try {
      const version = 'v3';
      const storedVer = localStorage.getItem('room_actor_configs_ver');
      if (storedVer !== version) {
        localStorage.removeItem('room_actor_configs');
        localStorage.setItem('room_actor_configs_ver', version);
        return { ...ROOM_ACTOR_CONFIGS };
      }
      const saved = localStorage.getItem('room_actor_configs');
      return saved ? { ...ROOM_ACTOR_CONFIGS, ...JSON.parse(saved) } : { ...ROOM_ACTOR_CONFIGS };
    } catch {
      return { ...ROOM_ACTOR_CONFIGS };
    }
  }

  saveActorConfig(loc, cfg) {
    const all = this.getStoredActorConfigs();
    all[loc] = { ...cfg };
    try {
      localStorage.setItem('room_actor_configs', JSON.stringify(all));
    } catch {}
  }

  applyRoomActorConfig(loc) {
    const all = this.getStoredActorConfigs();
    const cfg = all[loc] || { x: 75, bottom: 120, width: 180, height: 200, flip: false };
    const heroCfg = cfg.hero || { x: 26, bottom: 120, width: 160, height: 180, flip: false };

    // 1. Позиционирование NPC (или питомца в myroom)
    const npcTarget = (loc === 'myroom') ? this.roomPetActor : this.roomNpcActor;
    const npcImg = (loc === 'myroom') ? this.roomPetImg : this.roomNpcImg;

    if (npcTarget) {
      npcTarget.style.left = `${cfg.x}%`;
      npcTarget.style.right = 'auto';
      npcTarget.style.bottom = `${cfg.bottom}px`;
      if (loc !== 'myroom') {
        npcTarget.style.width = `${cfg.width || 180}px`;
        npcTarget.style.height = `${cfg.height || 200}px`;
      }
    }
    if (npcImg) {
      npcImg.style.transform = cfg.flip ? 'scaleX(-1)' : 'scaleX(1)';
    }

    // 2. Позиционирование героя (игрока) в гостевых комнатах
    if (this.roomHeroActor) {
      if (loc === 'myroom') {
        this.roomHeroActor.style.display = 'none';
      } else {
        this.roomHeroActor.style.display = 'flex';
        this.roomHeroActor.style.left = `${heroCfg.x}%`;
        this.roomHeroActor.style.right = 'auto';
        this.roomHeroActor.style.bottom = `${heroCfg.bottom}px`;
        this.roomHeroActor.style.width = `${heroCfg.width || 160}px`;
        this.roomHeroActor.style.height = `${heroCfg.height || 180}px`;
        if (this.roomHeroImg) {
          this.roomHeroImg.style.transform = heroCfg.flip ? 'scaleX(-1)' : 'scaleX(1)';
        }
      }
    }

    // 3. Синхронизация с элементами панели дебага
    if (this.isTweakerActive && this.tweakerRangeX) {
      const isHero = (this.tweakerTarget === 'hero' && loc !== 'myroom');
      const activeSubCfg = isHero ? heroCfg : cfg;

      // Обновляем визуальную подсветку выбранного персонажа
      this.roomHeroActor?.classList.toggle('tweaker-selected', isHero);
      if (npcTarget) {
        npcTarget.classList.toggle('tweaker-selected', !isHero);
      }

      // Обновляем вкладки целей
      if (this.tweakerTabNpc && this.tweakerTabHero) {
        if (loc === 'myroom') {
          this.tweakerTabNpc.textContent = '🐾 Питомец';
          this.tweakerTabNpc.classList.add('active');
          this.tweakerTabHero.style.display = 'none';
        } else {
          this.tweakerTabNpc.textContent = '🐾 Персонаж (NPC)';
          this.tweakerTabHero.style.display = 'inline-flex';
          this.tweakerTabNpc.classList.toggle('active', !isHero);
          this.tweakerTabHero.classList.toggle('active', isHero);
        }
      }

      const locNames = {
        myroom: 'Моя комната (Питомец)',
        friend_1: 'Миша (Медвежонок)',
        friend_2: 'Рыжик (Белочка)',
        friend_3: 'Рикки (Енот)',
        friend_4: 'София (Сова)',
        friend_5: 'Алиса (Лисичка)',
        friend_6: 'Сеня (Зайка)',
        friend_7: 'Барбос (Щенок)',
        shop: 'Лавка (Енотик)',
        bank: 'Банк (Банкир)',
        hospital: 'Клиника (Доктор Сова)'
      };
      if (this.tweakerLocName) this.tweakerLocName.textContent = locNames[loc] || loc;
      if (this.tweakerRangeX) this.tweakerRangeX.value = activeSubCfg.x;
      if (this.tweakerValX) this.tweakerValX.textContent = `${activeSubCfg.x}%`;
      if (this.tweakerRangeBottom) this.tweakerRangeBottom.value = activeSubCfg.bottom;
      if (this.tweakerValBottom) this.tweakerValBottom.textContent = `${activeSubCfg.bottom}px`;
      if (this.tweakerRangeSize) this.tweakerRangeSize.value = activeSubCfg.width || 180;
      if (this.tweakerValSize) this.tweakerValSize.textContent = `${activeSubCfg.width || 180}px`;
      if (this.tweakerBtnFlip) {
        this.tweakerBtnFlip.textContent = activeSubCfg.flip ? '↔️ Отражено' : '↔️ Отразить';
        this.tweakerBtnFlip.style.background = activeSubCfg.flip ? 'var(--clay-purple)' : '';
        this.tweakerBtnFlip.style.color = activeSubCfg.flip ? '#FFF' : '';
      }
    }
  }

  setupActorTweaker() {
    this.isTweakerActive = false;
    this.tweakerTarget = 'npc';
    this.btnRoomTweakActor = document.getElementById('btn-room-tweak-actor');
    this.actorTweakerPanel = document.getElementById('actor-tweaker-panel');
    this.tweakerLocName = document.getElementById('tweaker-loc-name');
    this.tweakerCloseBtn = document.getElementById('tweaker-close-btn');
    this.tweakerTabNpc = document.getElementById('tweaker-tab-npc');
    this.tweakerTabHero = document.getElementById('tweaker-tab-hero');
    this.tweakerRangeX = document.getElementById('tweaker-range-x');
    this.tweakerValX = document.getElementById('tweaker-val-x');
    this.tweakerRangeBottom = document.getElementById('tweaker-range-bottom');
    this.tweakerValBottom = document.getElementById('tweaker-val-bottom');
    this.tweakerRangeSize = document.getElementById('tweaker-range-size');
    this.tweakerValSize = document.getElementById('tweaker-val-size');
    this.tweakerBtnFlip = document.getElementById('tweaker-btn-flip');
    this.tweakerBtnReset = document.getElementById('tweaker-btn-reset');
    this.tweakerBtnCopy = document.getElementById('tweaker-btn-copy');
    this.roomStage = document.getElementById('room-stage');

    const toggleTweaker = () => {
      sound.playPop();
      this.isTweakerActive = !this.isTweakerActive;
      if (this.actorTweakerPanel) this.actorTweakerPanel.style.display = this.isTweakerActive ? 'flex' : 'none';
      if (this.btnRoomTweakActor) this.btnRoomTweakActor.classList.toggle('active', this.isTweakerActive);
      document.getElementById('screen-room')?.classList.toggle('tweaker-active', this.isTweakerActive);
      if (this.isTweakerActive) {
        this.applyRoomActorConfig(gameState.state.currentLocation);
      }
    };

    this.btnRoomTweakActor?.addEventListener('click', toggleTweaker);
    this.tweakerCloseBtn?.addEventListener('click', toggleTweaker);

    // Переключение между NPC и Героем
    this.tweakerTabNpc?.addEventListener('click', () => {
      sound.playPop();
      this.tweakerTarget = 'npc';
      this.applyRoomActorConfig(gameState.state.currentLocation);
    });

    this.tweakerTabHero?.addEventListener('click', () => {
      sound.playPop();
      this.tweakerTarget = 'hero';
      this.applyRoomActorConfig(gameState.state.currentLocation);
    });

    // Горячие клавиши: F2 или T в комнате
    window.addEventListener('keydown', (e) => {
      if ((e.key === 'F2' || (e.key === 't' && !['INPUT', 'TEXTAREA'].includes(document.activeElement?.tagName))) && gameState.state.currentLocation !== 'citymap') {
        e.preventDefault();
        toggleTweaker();
      }
      if (e.key === 'Escape' && this.isTweakerActive) {
        toggleTweaker();
      }
    });

    const updateCurrent = (updater) => {
      const loc = gameState.state.currentLocation;
      const all = this.getStoredActorConfigs();
      const cfg = { ...(all[loc] || { x: 75, bottom: 120, width: 180, height: 200, flip: false }) };
      if (!cfg.hero) {
        cfg.hero = { x: 26, bottom: 120, width: 160, height: 180, flip: false };
      } else {
        cfg.hero = { ...cfg.hero };
      }

      const targetObj = (this.tweakerTarget === 'hero' && loc !== 'myroom') ? cfg.hero : cfg;
      updater(targetObj);
      this.saveActorConfig(loc, cfg);
      this.applyRoomActorConfig(loc);
    };

    this.tweakerRangeX?.addEventListener('input', (e) => {
      const val = parseInt(e.target.value, 10);
      updateCurrent((obj) => { obj.x = val; });
    });

    this.tweakerRangeBottom?.addEventListener('input', (e) => {
      const val = parseInt(e.target.value, 10);
      updateCurrent((obj) => { obj.bottom = val; });
    });

    this.tweakerRangeSize?.addEventListener('input', (e) => {
      const val = parseInt(e.target.value, 10);
      updateCurrent((obj) => {
        obj.width = val;
        obj.height = Math.round(val * 1.1);
      });
    });

    this.tweakerBtnFlip?.addEventListener('click', () => {
      sound.playPop();
      updateCurrent((obj) => { obj.flip = !obj.flip; });
    });

    this.tweakerBtnReset?.addEventListener('click', () => {
      sound.playPop();
      const loc = gameState.state.currentLocation;
      const defaultCfg = JSON.parse(JSON.stringify(ROOM_ACTOR_CONFIGS[loc] || { x: 75, bottom: 120, width: 180, height: 200, flip: false }));
      this.saveActorConfig(loc, defaultCfg);
      this.applyRoomActorConfig(loc);
    });

    this.tweakerBtnCopy?.addEventListener('click', () => {
      sound.playLevelUp();
      const all = this.getStoredActorConfigs();
      const jsCode = `export const ROOM_ACTOR_CONFIGS = ${JSON.stringify(all, null, 2)};`;
      navigator.clipboard.writeText(jsCode).then(() => {
        const oldText = this.tweakerBtnCopy.textContent;
        this.tweakerBtnCopy.textContent = '✅ Скопировано в буфер!';
        setTimeout(() => { if (this.tweakerBtnCopy) this.tweakerBtnCopy.textContent = oldText; }, 2000);
      }).catch(() => {
        prompt('Скопируйте конфиг:', jsCode);
      });
    });

    // Интерактивное перетаскивание мышкой (Drag & Drop)
    let isDragging = false;

    const startDrag = (e, targetType) => {
      if (!this.isTweakerActive) return;
      if (targetType) {
        this.tweakerTarget = targetType;
        this.applyRoomActorConfig(gameState.state.currentLocation);
      }
      isDragging = true;
      e.stopPropagation();
      e.preventDefault();
    };

    const doDrag = (e) => {
      if (!isDragging || !this.isTweakerActive || !this.roomStage) return;
      const rect = this.roomStage.getBoundingClientRect();
      const clientX = e.touches ? e.touches[0].clientX : e.clientX;
      const clientY = e.touches ? e.touches[0].clientY : e.clientY;

      const rawX = ((clientX - rect.left) / rect.width) * 100;
      const rawBottom = rect.bottom - clientY;

      const newX = Math.round(Math.max(5, Math.min(95, rawX)));
      const newBottom = Math.round(Math.max(20, Math.min(450, rawBottom)));

      updateCurrent((obj) => {
        obj.x = newX;
        obj.bottom = newBottom;
      });
    };

    const stopDrag = () => {
      if (isDragging) {
        isDragging = false;
      }
    };

    this.roomNpcActor?.addEventListener('mousedown', (e) => startDrag(e, 'npc'));
    this.roomNpcActor?.addEventListener('touchstart', (e) => startDrag(e, 'npc'), { passive: false });
    this.roomHeroActor?.addEventListener('mousedown', (e) => startDrag(e, 'hero'));
    this.roomHeroActor?.addEventListener('touchstart', (e) => startDrag(e, 'hero'), { passive: false });
    this.roomPetActor?.addEventListener('mousedown', (e) => startDrag(e, 'npc'));
    this.roomPetActor?.addEventListener('touchstart', (e) => startDrag(e, 'npc'), { passive: false });

    window.addEventListener('mousemove', doDrag);
    window.addEventListener('touchmove', doDrag, { passive: false });
    window.addEventListener('mouseup', stopDrag);
    window.addEventListener('touchend', stopDrag);
  }

  // --- МОДАЛЬНЫЕ ОКНА: ДАННЫЕ И ЛОГИКА ---

  renderBudgetModal(state) {
    const b = state.budget;
    const totalAllocated = b.foodAndCareCoins + b.funAndGamesCoins + b.piggyBankCoins;
    const unallocated = b.totalStartingCoins - totalAllocated;

    document.getElementById('budget-unallocated-val').textContent = `${unallocated} м.`;
    document.getElementById('jar-val-food').textContent = b.foodAndCareCoins;
    document.getElementById('jar-val-fun').textContent = b.funAndGamesCoins;
    document.getElementById('jar-val-piggy').textContent = b.piggyBankCoins;

    const confirmBtn = document.getElementById('btn-confirm-budget');
    if (b.isConfirmed) {
      confirmBtn.textContent = '✓ План расходов утверждён!';
      confirmBtn.className = 'clay-btn btn-ghost';
      confirmBtn.disabled = true;
    } else {
      confirmBtn.textContent = '✅ Утвердить план расходов';
      confirmBtn.className = 'clay-btn btn-green';
      confirmBtn.disabled = false;
    }
  }

  renderShopItems(state) {
    const grid = document.getElementById('shop-items-grid');
    if (!grid) return;
    grid.innerHTML = '';

    const items = this.shopCurrentTab === 'all'
      ? SHOP_CATALOG
      : SHOP_CATALOG.filter((i) => i.category === this.shopCurrentTab);

    items.forEach((item) => {
      const card = document.createElement('div');
      card.className = 'shop-card';

      const isOwned = item.wardrobeId && state.pet.unlockedWardrobeIds.includes(item.wardrobeId);
      const isCarePurchase = item.category === 'food' || item.category === 'care';
      const available = isCarePurchase
        ? (state.budget.remainingFoodAndCareCoins || 0)
        : (state.budget.remainingFunAndGamesCoins || 0);
      const canAfford = state.budget.isConfirmed && available >= item.price && state.wallet.coins >= item.price;
      const budgetName = isCarePurchase ? 'Миска и Забота' : 'Сундучок Радостей';

      let iconHtml = '';
      if (item.asset) {
        iconHtml = `<img class="shop-card-icon" src="${item.asset}" alt="${item.title}">`;
      } else {
        iconHtml = `<div class="shop-card-emoji">${item.emoji}</div>`;
      }

      card.innerHTML = `
        ${iconHtml}
        <h4 class="shop-card-title">${item.title}</h4>
        <p class="shop-card-desc">${item.description}</p>
        <div class="shop-card-footer">
          <span class="shop-price-tag">${item.price} 🪙<small style="display:block; font-size:9px; opacity:.75;">${budgetName}: ${available}</small></span>
          <button type="button" class="clay-btn ${isOwned ? 'btn-ghost' : (canAfford ? 'btn-pink' : 'btn-ghost')}" style="padding: 6px 12px; font-size: 12px;" ${isOwned ? 'disabled' : ''}>
            ${isOwned ? 'Куплено' : 'Купить'}
          </button>
        </div>
      `;

      card.dataset.itemId = item.id;
      card.dataset.category = item.category;
      const buyBtn = card.querySelector('button');
      if (buyBtn) {
        buyBtn.dataset.itemId = item.id;
        buyBtn.dataset.category = item.category;
        buyBtn.classList.add('shop-item-buy-btn');
      }
      if (!isOwned) {
        buyBtn?.addEventListener('click', () => {
          const res = gameState.buyShopItem(item.id);
          if (res.success) {
            sound.playCashRegister();
            fireConfetti({ count: 35 });
          } else {
            sound.playError();
          }
        });
      }

      grid.appendChild(card);
    });
  }

  renderBankModal(state) {
    const goalsContainer = document.getElementById('bank-goals-list');
    if (!goalsContainer) return;
    goalsContainer.innerHTML = '';

    DREAM_GOALS.forEach((goal) => {
      const card = document.createElement('div');
      const isActive = goal.id === state.activeGoalId;
      const progressFraction = Math.min(1, state.wallet.savings / goal.targetCoins);
      const percent = Math.floor(progressFraction * 100);
      const isAchieved = state.wallet.savings >= goal.targetCoins;

      card.className = `goal-card ${isActive ? 'active-goal' : ''}`;
      card.innerHTML = `
        <div class="goal-header">
          <span class="goal-emoji">${goal.emoji}</span>
          <div style="flex: 1;">
            <h4 class="goal-title">${goal.title}</h4>
            <p style="font-size: 11px; color: var(--text-muted);">${goal.description}</p>
          </div>
          ${isActive ? '<span class="clay-pill" style="background: var(--clay-purple); color: white;">Активная</span>' : ''}
        </div>
        <div class="goal-progress-wrap">
          <div class="goal-progress-bar" style="width: ${percent}%;"></div>
        </div>
        <div class="goal-progress-text">
          <span>Накоплено: ${state.wallet.savings} из ${goal.targetCoins} м.</span>
          <span>${percent}%</span>
        </div>
        ${!isActive && !isAchieved ? `<button type="button" class="clay-btn btn-ghost" style="padding: 6px 12px; font-size: 12px;">Выбрать эту цель</button>` : ''}
        ${isAchieved ? `<div style="text-align: center; color: var(--clay-green-shadow); font-weight: 800; font-size: 13px;">🎉 ЦЕЛЬ ДОСТИГНУТА!</div>` : ''}
      `;

      const selectBtn = card.querySelector('button');
      selectBtn?.addEventListener('click', () => {
        sound.playPop();
        gameState.selectGoal(goal.id);
      });

      goalsContainer.appendChild(card);
    });
  }

  renderHospitalModal(state) {
    const statusText = document.getElementById('hospital-status-text');
    const treatBtn = document.getElementById('btn-hospital-treat');
    const checkupBtn = document.getElementById('btn-hospital-checkup');

    if (state.pet.health === 'needs_treatment') {
      statusText.innerHTML = `«Внимание! Питомцу требуется полезная витаминная процедура за <strong>${TREATMENT_COST} монет</strong>. Это отличный пример, почему важно иметь резервный фонд!»`;
      treatBtn.style.display = 'block';
    } else {
      statusText.innerHTML = '«Питомец здоров, весел и полон сил! Регулярный осмотр помогает заранее планировать заботу без неожиданностей.»';
      treatBtn.style.display = 'none';
    }

    if (state.pet.lastCheckupPeriod === state.period) {
      checkupBtn.textContent = '✓ Осмотр в этом периоде уже пройден';
      checkupBtn.disabled = true;
      checkupBtn.className = 'clay-btn btn-ghost';
    } else {
      checkupBtn.textContent = '🩺 Пройти еженедельный осмотр (Бесплатно)';
      checkupBtn.disabled = false;
      checkupBtn.className = 'clay-btn btn-blue';
    }
  }

  shuffleItems(items) {
    const result = [...items];
    for (let i = result.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [result[i], result[j]] = [result[j], result[i]];
    }
    return result;
  }

  setFriendDialogueStage(stage) {
    const showIntro = stage !== 'challenge';
    if (this.friendIntroStage) this.friendIntroStage.hidden = !showIntro;
    if (this.friendChallengeStage) this.friendChallengeStage.hidden = showIntro;
  }

  openFriendDialogue(friendId) {
    this.activeFriendId = friendId;
    const friend = FRIENDS_LIST.find((f) => f.id === friendId);
    const puzzle = KID_PUZZLES[friendId];
    if (!friend || !puzzle) return;

    const requiredLesson = FINANCIAL_LESSONS.find((lesson) => lesson.id === puzzle.lessonId);
    if (!tutorial?.isActive && requiredLesson && !gameState.state.completedLessonIds.includes(requiredLesson.id)) {
      gameState.state.advisorTip = `Подручный ждёт тебя с темой «${requiredLesson.title}». Сначала разберём правило, потом поможем ${friend.name}.`;
      gameState.notify();
      this.openModal('advisor');
      return;
    }

    document.getElementById('friend-modal-icon').textContent = friend.emoji;
    document.getElementById('friend-modal-title').textContent = puzzle.title;
    document.getElementById('friend-dialogue-avatar').src = friend.portrait;
    document.getElementById('friend-dialogue-name').textContent = friend.name;
    document.getElementById('friend-dialogue-text').textContent = friend.greeting;
    document.getElementById('puzzle-topic-chip').textContent = puzzle.theme;
    document.getElementById('puzzle-story-prompt').textContent = puzzle.storyPrompt;
    this.friendIntroTitle.textContent = puzzle.title;
    this.friendIntroTheme.textContent = friend.theme;
    this.friendIntroEmblem.textContent = puzzle.emoji;
    this.friendChallengeName.textContent = friend.name;
    this.friendChallengeEmblem.textContent = puzzle.emoji;
    this.friendBeginButton.textContent = 'Давай разберёмся';
    this.modals.friend.querySelector('.modal-card')?.style.setProperty('--friend-accent', friend.color);
    this.modals.friend.dataset.friendId = String(friendId);

    const optionsContainer = document.getElementById('puzzle-options-list');
    optionsContainer.innerHTML = '';
    const feedbackCard = document.getElementById('puzzle-feedback-card');
    feedbackCard.className = 'puzzle-feedback-card';
    feedbackCard.style.display = 'none';

    const claimBtn = document.getElementById('btn-claim-puzzle-reward');
    claimBtn.style.display = 'none';

    const pState = gameState.state.puzzles[friendId]?.state;
    this.setFriendDialogueStage(pState === 'available' ? 'intro' : 'challenge');

    if (pState === 'completed') {
      feedbackCard.className = 'puzzle-feedback-card success';
      feedbackCard.style.display = 'block';
      feedbackCard.textContent = `✓ Задание выполнено! Награда получена. Помни правило: ${puzzle.successExplanation}`;
    } else if (pState === 'solved_unclaimed') {
      claimBtn.style.display = 'block';
      feedbackCard.className = 'puzzle-feedback-card success';
      feedbackCard.style.display = 'block';
      feedbackCard.textContent = `Правильно! Забирай свои ${puzzle.rewardCoins} монет!`;
    }

    this.shuffleItems(puzzle.options).forEach((opt, index) => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'puzzle-opt-btn';
      btn.dataset.optionId = opt.id;
      btn.dataset.isCorrect = opt.isCorrect ? 'true' : 'false';
      btn.innerHTML = `
        <span class="puzzle-choice-heading"><span class="puzzle-choice-letter">${index === 0 ? 'А' : 'Б'}</span><span class="puzzle-choice-emoji">${opt.emoji}</span></span>
        <span class="puzzle-choice-text">${opt.text}</span>
      `;

      if (pState !== 'available') {
        btn.disabled = true;
      }

      btn.addEventListener('click', () => {
        const res = gameState.answerPuzzle(friendId, opt.id);
        if (res.success) {
          sound.playSuccess();
          btn.classList.add('correct-selected');
          feedbackCard.className = 'puzzle-feedback-card success';
          feedbackCard.textContent = `Верно! ${res.feedback} ${res.explanation}`;
          claimBtn.style.display = 'block';
        } else {
          sound.playError();
          btn.classList.add('wrong-selected');
          feedbackCard.className = 'puzzle-feedback-card error';
          feedbackCard.textContent = `Ой! ${res.feedback} ${res.explanation}`;
        }
      });

      optionsContainer.appendChild(btn);
    });

    this.openModal('friend');
  }

  openFriendshipModal(friendId) {
    const friend = FRIENDS_LIST.find((f) => f.id === friendId);
    const friendship = gameState.state.friendships[friendId] || { level: 1, exp: 30 };
    if (!friend) return;

    document.getElementById('friendship-modal-title').textContent = `Дружба с ${friend.name}`;
    const body = document.getElementById('friendship-modal-body');

    body.innerHTML = `
      <div style="display: flex; align-items: center; gap: 14px;">
        <img src="${friend.portrait}" style="width: 72px; height: 72px; border-radius: 50%; border: 3px solid ${friend.color};" alt="${friend.name}">
        <div>
          <h3 style="font-family: var(--font-heading); font-size: 16px;">${friend.name} (${friend.species})</h3>
          <p style="font-size: 12px; color: var(--text-muted);">${friend.houseName}</p>
          <span class="clay-pill" style="background: var(--clay-purple-bg); color: var(--clay-purple-shadow); margin-top: 4px;">Уровень дружбы: ${friendship.level} ⭐</span>
        </div>
      </div>

      <div class="clay-card">
        <h4 style="font-family: var(--font-heading); font-size: 13px; margin-bottom: 6px;">🎁 Любимые угощения:</h4>
        <div style="display: flex; gap: 8px;">
          ${friend.favoriteItems.map((item) => `<span class="clay-pill" style="background: #FFF9E6;">${item}</span>`).join('')}
        </div>
      </div>

      <div class="clay-card">
        <h4 style="font-family: var(--font-heading); font-size: 13px; margin-bottom: 4px;">✨ Особый бонус дружбы (Перк):</h4>
        <p style="font-size: 12px; color: var(--text-dark);">${friend.perk}</p>
      </div>
    `;

    this.openModal('friendship');
  }

  renderWardrobeItems(state) {
    const grid = document.getElementById('wardrobe-items-grid');
    if (!grid) return;
    grid.innerHTML = '';

    WARDROBE_ACCESSORIES.forEach((acc) => {
      const isUnlocked = state.pet.unlockedWardrobeIds.includes(acc.id);
      const isEquipped = state.pet.equippedAccessories[acc.slot] === acc.id;

      const card = document.createElement('button');
      card.type = 'button';
      card.disabled = !isUnlocked;
      card.setAttribute('aria-label', isUnlocked ? `${isEquipped ? 'Снять' : 'Надеть'}: ${acc.name}` : `${acc.name} недоступен`);
      card.className = `wardrobe-item-card ${isEquipped ? 'equipped' : ''} ${!isUnlocked ? 'locked' : ''}`;
      card.innerHTML = `
        <img class="wardrobe-icon" src="${acc.asset}" alt="" loading="lazy">
        <span class="wardrobe-name">${acc.name}</span>
        <span style="font-size: 10px; font-weight: 800; color: ${isEquipped ? 'var(--clay-pink-shadow)' : 'var(--text-muted)'}; margin-top: 4px;">
          ${!isUnlocked ? '🔒 В лавке' : (isEquipped ? '✓ Надето' : 'Примерить')}
        </span>
      `;

      if (isUnlocked) {
        card.addEventListener('click', () => {
          sound.playPop();
          gameState.toggleAccessory(acc.id);
        });
      }

      grid.appendChild(card);
    });
  }

  renderPeriodSummaryModal(report) {
    const isFinal = Boolean(report.isGameFinished);
    document.getElementById('summary-title').textContent = isFinal
      ? 'Финальные итоги приключения'
      : `Итоги периода #${report.period}`;
    const checksContainer = document.getElementById('period-summary-checks');
    checksContainer.innerHTML = '';

    report.checks.forEach((chk) => {
      const row = document.createElement('div');
      row.className = `summary-check-row ${chk.passed ? 'passed' : 'failed'}`;
      row.innerHTML = `
        <span>${chk.passed ? '✅' : '⚠️'} ${chk.title}</span>
        <span>+${chk.points} очков</span>
      `;
      checksContainer.appendChild(row);
    });

    const stageNote = document.getElementById('summary-stage-note');
    if (isFinal) {
      stageNote.innerHTML = `🌟 <strong>Ты большой молодец!</strong> Ты завершил все 5 планов и помог питомцу вырасти.`;
    } else if (report.hasGrown) {
      stageNote.innerHTML = `🎉 <strong>ПОЗДРАВЛЯЕМ!</strong> Питомец вырос и перешёл на стадию «<strong>${GROWTH_STAGES[report.newStage.toUpperCase()]?.title || 'Взрослый'}</strong>»!`;
    } else {
      stageNote.textContent = `Всего очков развития: ${report.totalPoints}. Питомец счастлив и растёт с каждым периодом!`;
    }

    const allowanceTitle = document.getElementById('summary-allowance-title');
    const nextBtn = document.getElementById('btn-start-next-period');
    const completeBanner = document.getElementById('period-complete-banner');
    if (isFinal) {
      allowanceTitle.textContent = '🏆 Все 5 финансовых планов завершены!';
      nextBtn.hidden = true;
      completeBanner.hidden = false;
      fireConfetti({ count: 120 });
    } else {
      allowanceTitle.textContent = `💰 Карманные деньги на новый период: +${report.nextAllowance} монет!`;
      nextBtn.hidden = false;
      completeBanner.hidden = true;
    }
  }

  renderFinanceModal(state) {
    const planFactCard = document.getElementById('finance-plan-fact-card');
    const txList = document.getElementById('finance-transactions-list');
    if (!planFactCard || !txList) return;

    const b = state.budget;
    planFactCard.innerHTML = `
      <h3 style="font-family: var(--font-heading); font-size: 15px; margin-bottom: 10px;">План текущего периода:</h3>
      <div style="display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 6px;">
        <span>🥣 Миска и Забота (Обязательное):</span>
        <strong>${b.foodAndCareCoins} м. <small style="color:var(--text-muted)">осталось ${b.remainingFoodAndCareCoins || 0}</small></strong>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 6px;">
        <span>🎁 Сундучок Радостей (Желания):</span>
        <strong>${b.funAndGamesCoins} м. <small style="color:var(--text-muted)">осталось ${b.remainingFunAndGamesCoins || 0}</small></strong>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 6px;">
        <span>🏺 Копилка на Мечту (Сбережения):</span>
        <strong>${b.piggyBankCoins} м.</strong>
      </div>
      <div style="margin-top: 10px; padding-top: 8px; border-top: 1.5px dashed #D0C4B4; display: flex; justify-content: space-between;">
        <span>Всего в кошельке сейчас:</span>
        <span style="font-weight: 800; color: #5A3E00;">${state.wallet.coins} 🪙</span>
      </div>
    `;

    txList.innerHTML = '';
    const rev = [...state.transactions].reverse().slice(0, 15);
    rev.forEach((tx) => {
      const item = document.createElement('div');
      item.className = 'clay-card';
      item.style.padding = '8px 12px';
      item.style.display = 'flex';
      item.style.justifyContent = 'space-between';
      item.style.alignItems = 'center';

      const isPositive = tx.amount > 0;
      item.innerHTML = `
        <div style="display: flex; align-items: center; gap: 8px;">
          <span style="font-size: 18px;">${tx.emoji}</span>
          <div>
            <div style="font-size: 12px; font-weight: 700;">${tx.title}</div>
            <div style="font-size: 10px; color: var(--text-muted);">${tx.time}</div>
          </div>
        </div>
        <span style="font-family: var(--font-heading); font-weight: 800; font-size: 13px; color: ${isPositive ? 'var(--clay-green-shadow)' : '#D32F2F'};">
          ${isPositive ? '+' : ''}${tx.amount} м.
        </span>
      `;
      txList.appendChild(item);
    });
  }

  renderAdvisorLessons(state = gameState.state) {
    const list = document.getElementById('advisor-lessons-list');
    if (!list) return;
    list.innerHTML = '';

    const nextLesson = FINANCIAL_LESSONS.find((lesson) => !state.completedLessonIds.includes(lesson.id));

    FINANCIAL_LESSONS.forEach((lesson, index) => {
      const isCompleted = state.completedLessonIds.includes(lesson.id);
      const isAvailable = nextLesson?.id === lesson.id;
      const card = document.createElement('div');
      card.className = 'clay-card';
      card.innerHTML = `
        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 6px;">
          <span style="font-size: 22px;">${lesson.emoji}</span>
          <div style="flex:1;">
            <h4 style="font-family: var(--font-heading); font-size: 14px; font-weight: 700;">${index + 1}. ${lesson.title}</h4>
            <span style="font-size:10px; font-weight:800; color:${isCompleted ? 'var(--clay-green-shadow)' : (isAvailable ? 'var(--clay-purple-shadow)' : 'var(--text-muted)')};">${isCompleted ? '✓ Тема освоена' : (isAvailable ? 'Подручный ждёт ответ' : '🔒 Откроется после предыдущего урока')}</span>
          </div>
        </div>
        <p style="font-size: 12px; font-weight: 700; color: var(--clay-pink-shadow); margin-bottom: 4px;">«${lesson.rule}»</p>
        <p style="font-size: 11px; color: var(--text-dark); line-height: 1.35;">${lesson.fullText}</p>
        ${isAvailable ? `
          <p style="font-size:12px; font-weight:800; margin:10px 0 6px;">${lesson.question}</p>
          <div style="display:flex; flex-direction:column; gap:6px;">
            ${lesson.options.map((option) => `<button type="button" class="clay-btn btn-ghost lesson-answer-btn" data-lesson-id="${lesson.id}" data-option-id="${option.id}" style="text-align:left;">${option.text}</button>`).join('')}
          </div>
        ` : ''}
      `;

      card.querySelectorAll('.lesson-answer-btn').forEach((button) => {
        button.addEventListener('click', () => {
          const result = gameState.answerLesson(lesson.id, button.dataset.optionId);
          if (result.success && result.reason !== 'already_completed') {
            sound.playSuccess();
            fireConfetti({ count: 32 });
          } else if (!result.success) {
            sound.playError();
          }
        });
      });
      list.appendChild(card);
    });
  }

  generateAdultMathQuestion() {
    const a = Math.floor(Math.random() * 6) + 4; // 4..9
    const b = Math.floor(Math.random() * 6) + 3; // 3..8
    this.adultMathAnswer = a * b;
    const taskEl = document.getElementById('math-task-text');
    if (taskEl) taskEl.textContent = `${a} × ${b} = ?`;
    const input = document.getElementById('math-gate-input');
    if (input) input.value = '';
    document.getElementById('adult-barrier-gate').style.display = 'flex';
    document.getElementById('adult-content-area').style.display = 'none';
  }

  renderAdultStats(state) {
    const card = document.getElementById('adult-stats-card');
    if (!card) return;

    const solvedCount = Object.values(state.puzzles).filter((p) => p.state === 'completed').length;
    card.innerHTML = `
      <h3 style="font-family: var(--font-heading); font-size: 14px; margin-bottom: 8px;">📊 Прогресс ребёнка (${state.playerName}):</h3>
      <div style="display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 4px;">
        <span>Текущий период обучения:</span>
        <strong>${state.period} из 5</strong>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 4px;">
        <span>Очки осознанного развития:</span>
        <strong>${state.pet.growthPoints} очков</strong>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 4px;">
        <span>Стадия роста питомца:</span>
        <strong>${GROWTH_STAGES[state.pet.growthStage.toUpperCase()]?.title || 'Малыш'}</strong>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 4px;">
        <span>Всего в копилке на мечту:</span>
        <strong>${state.wallet.savings} монет</strong>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 4px;">
        <span>Решено финансово-грамотных задач:</span>
        <strong>${solvedCount} из 7 друзей</strong>
      </div>
    `;
  }
}
