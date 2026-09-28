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
  FINANCIAL_LESSONS
} from './data.js';
import { gameState, TREATMENT_COST } from './state.js';
import { sound } from './audio.js';
import { fireConfetti } from './confetti.js';

export class UIController {
  constructor() {
    this.isMapPanorama = false;
    this.activeFriendId = 1;
    this.adultMathAnswer = 42;
    this.shopCurrentTab = 'all';

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

    // HUD элементы
    this.hudPetName = document.getElementById('hud-pet-name');
    this.hudPetMood = document.getElementById('hud-pet-mood');
    this.hudPetAvatar = document.getElementById('hud-pet-avatar-img');
    this.hudPeriodNum = document.getElementById('hud-period-num');
    this.hudCoinsVal = document.getElementById('hud-coins-val');
    this.hudSavingsVal = document.getElementById('hud-savings-val');
    this.advisorText = document.getElementById('advisor-text');

    // Настройка питомца
    this.setupPreviewImg = document.getElementById('setup-preview-img');
    this.setupPetNameInput = document.getElementById('setup-pet-name');
    this.setupPlayerNameInput = document.getElementById('setup-player-name');
    this.speciesGrid = document.getElementById('setup-species-grid');

    // Карта города
    this.mapBgImg = document.getElementById('map-bg-img');
    this.mapPinsContainer = document.getElementById('map-pins-container');
    this.btnToggleMap = document.getElementById('btn-toggle-map-orientation');
    this.mapToggleText = document.getElementById('map-toggle-text');

    // Комната
    this.roomNavTitle = document.getElementById('room-nav-title');
    this.roomNavEmoji = document.getElementById('room-nav-emoji');
    this.roomBgImg = document.getElementById('room-bg-img');
    this.roomPetActor = document.getElementById('room-pet-actor');
    this.roomPetImg = document.getElementById('room-pet-img');
    this.petSpeechText = document.getElementById('pet-speech-text');
    this.petSpeechEmoji = document.getElementById('pet-speech-emoji');
    this.petAccHead = document.getElementById('pet-acc-head');
    this.petAccNeck = document.getElementById('pet-acc-neck');
    this.petAccGlasses = document.getElementById('pet-acc-glasses');
    this.roomCharacterActor = document.getElementById('room-character-actor');
    this.roomCharacterImg = document.getElementById('room-character-img');
    this.roomActionPrompt = document.getElementById('room-action-prompt');
    this.promptEmoji = document.getElementById('prompt-emoji');
    this.promptText = document.getElementById('prompt-text');
    this.roomDock = document.getElementById('room-dock');

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
      settings: document.getElementById('modal-settings')
    };
  }

  bindGlobalEvents() {
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
      }
    });

    // Кнопки верхнего HUD
    document.getElementById('hud-pet-btn')?.addEventListener('click', () => {
      sound.playPurr();
      this.openModal('wardrobe');
    });

    document.getElementById('hud-coins-btn')?.addEventListener('click', () => {
      sound.playCoin();
      this.openModal('finance');
    });

    document.getElementById('hud-savings-btn')?.addEventListener('click', () => {
      sound.playCoin();
      this.openModal('bank');
    });

    document.getElementById('btn-open-advisor')?.addEventListener('click', () => {
      sound.playPop();
      this.openModal('advisor');
    });

    document.getElementById('btn-open-finance')?.addEventListener('click', () => {
      sound.playPop();
      this.openModal('finance');
    });

    document.getElementById('btn-open-settings')?.addEventListener('click', () => {
      sound.playPop();
      this.openModal('settings');
    });

    document.getElementById('btn-open-adult')?.addEventListener('click', () => {
      sound.playPop();
      this.generateAdultMathQuestion();
      this.openModal('adult');
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
    document.getElementById('btn-room-exit-city')?.addEventListener('click', () => {
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
      this.roomPetActor.classList.add('bounced');
      setTimeout(() => this.roomPetActor.classList.remove('bounced'), 400);
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

    // Кнопка старта игры
    document.getElementById('btn-start-game')?.addEventListener('click', () => {
      sound.playLevelUp();
      const selectedSpecies = document.querySelector('.species-chip.selected')?.getAttribute('data-species') || 'cat';
      const selectedPattern = document.querySelector('.pattern-btn.selected')?.getAttribute('data-pattern') || 'classic';
      const petName = this.setupPetNameInput.value.trim() || 'Финни';
      const playerName = this.setupPlayerNameInput.value.trim() || 'Юный финансист';

      gameState.startNewGame({
        petName,
        species: selectedSpecies,
        pattern: selectedPattern,
        playerName
      });

      // Открываем модалку бюджета в начале
      setTimeout(() => {
        this.openModal('budget');
      }, 500);
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

    // Раздел взрослого: полный сброс
    document.getElementById('btn-adult-reset-demo')?.addEventListener('click', () => {
      if (confirm('Сбросить весь игровой процесс и начать демо с самого начала?')) {
        sound.playPop();
        gameState.resetDemo();
        this.closeModal('adult');
      }
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

    // Настройки: перезапуск
    document.getElementById('btn-settings-restart')?.addEventListener('click', () => {
      if (confirm('Начать игру заново?')) {
        sound.playPop();
        gameState.resetDemo();
        this.closeModal('settings');
      }
    });
  }

  // --- ОТРИСОВКА ИНТЕРФЕЙСА ---

  render(state) {
    if (!state.isGameStarted) {
      this.topHud.style.display = 'none';
      this.showScreen('screen-setup');
      return;
    }

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
    this.hudPetAvatar.src = `assets/pet_${state.pet.species}_portrait.png`;
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
  }

  showScreen(screenId) {
    document.querySelectorAll('.screen').forEach((s) => s.classList.remove('active'));
    document.getElementById(screenId)?.classList.add('active');
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

  // Отрисовка списка видов питомца в сетапе
  renderSetupSpeciesGrid() {
    this.speciesGrid.innerHTML = '';
    PET_SPECIES.forEach((p, idx) => {
      const chip = document.createElement('div');
      chip.className = `species-chip ${idx === 0 ? 'selected' : ''}`;
      chip.setAttribute('data-species', p.id);
      chip.innerHTML = `
        <span class="species-emoji">${p.emoji}</span>
        <span class="species-name">${p.name}</span>
      `;
      chip.addEventListener('click', () => {
        sound.playPop();
        document.querySelectorAll('.species-chip').forEach((c) => c.classList.remove('selected'));
        chip.classList.add('selected');
        const pattern = document.querySelector('.pattern-btn.selected')?.getAttribute('data-pattern') || 'classic';
        this.updateSetupPreview(p.id, pattern);
      });
      this.speciesGrid.appendChild(chip);
    });
  }

  updateSetupPreview(speciesId, pattern) {
    const pet = PET_SPECIES.find((p) => p.id === speciesId);
    if (pet) {
      this.setupPreviewImg.src = pet.portrait;
    }
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
          { id: 'friend_2', title: 'Рыжик', asset: 'friend_2_portrait', x: 16, y: 18, type: 'pin-squirrel' },
          { id: 'shop', title: 'Лавка', asset: 'building_shop', x: 33, y: 35, type: 'pin-shop' },
          { id: 'bank', title: 'Банк', asset: 'building_bank', x: 50, y: 27, type: 'pin-bank' },
          { id: 'hospital', title: 'Клиника', asset: 'building_hospital', x: 67, y: 37, type: 'pin-hospital' },
          { id: 'friend_1', title: 'Потап', asset: 'friend_1_portrait', x: 18, y: 55, type: 'pin-bear' },
          { id: 'friend_3', title: 'Тёма', asset: 'friend_3_portrait', x: 10, y: 65, type: 'pin-raccoon' },
          { id: 'friend_6', title: 'Сеня', asset: 'friend_6_portrait', x: 33, y: 71, type: 'pin-bunny' },
          { id: 'friend_5', title: 'Алиса', asset: 'friend_5_portrait', x: 42, y: 73, type: 'pin-fox' },
          { id: 'friend_4', title: 'София', asset: 'friend_4_portrait', x: 88, y: 57, type: 'pin-owl' },
          { id: 'friend_7', title: 'Барбос', asset: 'friend_7_portrait', x: 72, y: 81, type: 'pin-dog' },
          { id: 'myroom', title: 'Мой дом', asset: 'building_home', x: 88, y: 83, type: 'pin-home' }
        ]
      : [
          { id: 'friend_2', title: 'Рыжик', asset: 'friend_2_portrait', x: 14, y: 44, type: 'pin-squirrel' },
          { id: 'friend_1', title: 'Потап', asset: 'friend_1_portrait', x: 16, y: 30, type: 'pin-bear' },
          { id: 'friend_3', title: 'Тёма', asset: 'friend_3_portrait', x: 68, y: 27, type: 'pin-raccoon' },
          { id: 'friend_4', title: 'София', asset: 'friend_4_portrait', x: 89, y: 31, type: 'pin-owl' },
          { id: 'friend_6', title: 'Сеня', asset: 'friend_6_portrait', x: 36, y: 37, type: 'pin-bunny' },
          { id: 'shop', title: 'Лавка', asset: 'building_shop', x: 38, y: 48, type: 'pin-shop' },
          { id: 'bank', title: 'Банк', asset: 'building_bank', x: 74, y: 45, type: 'pin-bank' },
          { id: 'hospital', title: 'Клиника', asset: 'building_hospital', x: 87, y: 56, type: 'pin-hospital' },
          { id: 'friend_5', title: 'Алиса', asset: 'friend_5_portrait', x: 70, y: 70, type: 'pin-fox' },
          { id: 'friend_7', title: 'Барбос', asset: 'friend_7_portrait', x: 86, y: 78, type: 'pin-dog' },
          { id: 'myroom', title: 'Мой дом', asset: 'building_home', x: 25, y: 80, type: 'pin-home' }
        ];

    pinConfigs.forEach((cfg) => {
      const pin = document.createElement('div');
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

  // Отрисовка сцены комнаты (11 интерьеров)
  renderRoom(state) {
    const loc = state.currentLocation;

    if (loc === 'myroom') {
      this.roomNavTitle.textContent = 'Моя уютная комната';
      this.roomNavEmoji.textContent = '🏠';
      this.roomBgImg.src = 'assets/bg_room_myroom.png';

      // Показываем питомца дома
      this.roomPetActor.style.display = 'flex';
      this.roomPetActor.className = `pet-actor-container stage-${state.pet.growthStage}`;
      this.roomPetImg.src = `assets/pet_${state.pet.species}_idle.png`;

      // Надетые аксессуары
      const equipped = state.pet.equippedAccessories;
      this.petAccHead.textContent = equipped.head ? WARDROBE_ACCESSORIES.find((a) => a.id === equipped.head)?.emoji || '' : '';
      this.petAccNeck.textContent = equipped.neck ? WARDROBE_ACCESSORIES.find((a) => a.id === equipped.neck)?.emoji || '' : '';
      this.petAccGlasses.textContent = equipped.glasses ? WARDROBE_ACCESSORIES.find((a) => a.id === equipped.glasses)?.emoji || '' : '';

      this.roomCharacterActor.style.display = 'none';
      this.roomActionPrompt.style.display = 'none';

      // Нижний док для дома
      this.roomDock.innerHTML = `
        <button class="dock-btn feed" id="dock-btn-feed">
          <div class="dock-btn-icon">${state.pet.isHungry ? '🥣' : '✨'}</div>
          <span class="dock-btn-label">${state.pet.isHungry ? 'Покормить' : 'Сыт'}</span>
        </button>
        <button class="dock-btn wash" id="dock-btn-wash">
          <div class="dock-btn-icon">🫧</div>
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

      this.roomPetActor.style.display = 'none';
      this.roomCharacterActor.style.display = 'flex';
      this.roomCharacterImg.src = `assets/friend_${friendId}_idle.png`;

      this.roomActionPrompt.style.display = 'flex';
      this.promptEmoji.textContent = '💬';
      this.promptText.textContent = `Поговорить с ${friend?.name || 'Другом'}`;

      this.roomActionPrompt.onclick = () => {
        sound.playPop();
        this.openFriendDialogue(friendId);
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-pink" id="room-friend-dialogue-btn" style="flex: 1;">
          💬 Поговорить с ${friend?.name || 'другом'}
        </button>
        <button class="clay-btn btn-purple" id="room-friend-profile-btn" style="flex: 1;">
          🌟 Дружба (${state.friendships[friendId]?.level || 1} ур.)
        </button>
        <button class="clay-btn btn-ghost" id="room-friend-exit-btn">
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

      this.roomPetActor.style.display = 'none';
      this.roomCharacterActor.style.display = 'flex';
      this.roomCharacterImg.src = 'assets/worker_shop_idle.png';

      this.roomActionPrompt.style.display = 'flex';
      this.promptEmoji.textContent = '🛒';
      this.promptText.textContent = 'Заглянуть на прилавок товаров';

      this.roomActionPrompt.onclick = () => {
        sound.playPop();
        this.openModal('shop');
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-pink" id="room-shop-open-btn" style="flex: 1;">
          🛒 Открыть прилавок товаров
        </button>
        <button class="clay-btn btn-ghost" id="room-shop-exit-btn">
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

      this.roomPetActor.style.display = 'none';
      this.roomCharacterActor.style.display = 'flex';
      this.roomCharacterImg.src = 'assets/worker_bank_idle.png';

      this.roomActionPrompt.style.display = 'flex';
      this.promptEmoji.textContent = '🏦';
      this.promptText.textContent = 'Подойти к кассе Банкира';

      this.roomActionPrompt.onclick = () => {
        sound.playPop();
        this.openModal('bank');
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-gold" id="room-bank-open-btn" style="flex: 1;">
          🏦 Касса и копилка на мечту
        </button>
        <button class="clay-btn btn-ghost" id="room-bank-exit-btn">
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

      this.roomPetActor.style.display = 'none';
      this.roomCharacterActor.style.display = 'flex';
      this.roomCharacterImg.src = 'assets/worker_hospital_idle.png';

      this.roomActionPrompt.style.display = 'flex';
      this.promptEmoji.textContent = '🩺';
      this.promptText.textContent = 'Подойти к Доктору Сове';

      this.roomActionPrompt.onclick = () => {
        sound.playPop();
        this.openModal('hospital');
      };

      this.roomDock.innerHTML = `
        <button class="clay-btn btn-blue" id="room-hospital-open-btn" style="flex: 1;">
          🩺 Пройти осмотр врача
        </button>
        <button class="clay-btn btn-ghost" id="room-hospital-exit-btn">
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
      const canAfford = state.wallet.coins >= item.price;

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
          <span class="shop-price-tag">${item.price} 🪙</span>
          <button type="button" class="clay-btn ${isOwned ? 'btn-ghost' : (canAfford ? 'btn-pink' : 'btn-ghost')}" style="padding: 6px 12px; font-size: 12px;" ${isOwned ? 'disabled' : ''}>
            ${isOwned ? 'Куплено' : 'Купить'}
          </button>
        </div>
      `;

      const buyBtn = card.querySelector('button');
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
        ${!isActive ? `<button type="button" class="clay-btn btn-ghost" style="padding: 6px 12px; font-size: 12px;">Выбрать эту цель</button>` : ''}
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

  openFriendDialogue(friendId) {
    this.activeFriendId = friendId;
    const friend = FRIENDS_LIST.find((f) => f.id === friendId);
    const puzzle = KID_PUZZLES[friendId];
    if (!friend || !puzzle) return;

    document.getElementById('friend-modal-icon').textContent = friend.emoji;
    document.getElementById('friend-modal-title').textContent = `В гостях у ${friend.name}`;
    document.getElementById('friend-dialogue-avatar').src = friend.portrait;
    document.getElementById('friend-dialogue-text').textContent = friend.greeting;
    document.getElementById('puzzle-story-prompt').textContent = puzzle.storyPrompt;

    const optionsContainer = document.getElementById('puzzle-options-list');
    optionsContainer.innerHTML = '';
    const feedbackCard = document.getElementById('puzzle-feedback-card');
    feedbackCard.className = 'puzzle-feedback-card';
    feedbackCard.style.display = 'none';

    const claimBtn = document.getElementById('btn-claim-puzzle-reward');
    claimBtn.style.display = 'none';

    const pState = gameState.state.puzzles[friendId]?.state;

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

    puzzle.options.forEach((opt) => {
      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'puzzle-opt-btn';
      btn.innerHTML = `<span>${opt.emoji}</span> <span>${opt.text}</span>`;

      if (pState === 'completed') {
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

      const card = document.createElement('div');
      card.className = `wardrobe-item-card ${isEquipped ? 'equipped' : ''} ${!isUnlocked ? 'locked' : ''}`;
      card.innerHTML = `
        <span class="wardrobe-icon">${acc.emoji}</span>
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
    document.getElementById('summary-title').textContent = `Итоги периода #${report.period}`;
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
    if (report.hasGrown) {
      stageNote.innerHTML = `🎉 <strong>ПОЗДРАВЛЯЕМ!</strong> Питомец вырос и перешёл на стадию «<strong>${GROWTH_STAGES[report.newStage.toUpperCase()]?.title || 'Взрослый'}</strong>»!`;
    } else {
      stageNote.textContent = `Всего очков развития: ${report.totalPoints}. Питомец счастлив и растёт с каждым периодом!`;
    }

    const allowanceTitle = document.getElementById('summary-allowance-title');
    if (report.nextAllowance > 0) {
      allowanceTitle.textContent = `💰 Карманные деньги на новый период: +${report.nextAllowance} монет!`;
    } else {
      allowanceTitle.textContent = '🏆 Все 5 периодов успешно пройдены!';
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
        <strong>${b.foodAndCareCoins} м.</strong>
      </div>
      <div style="display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 6px;">
        <span>🎁 Сундучок Радостей (Желания):</span>
        <strong>${b.funAndGamesCoins} м.</strong>
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

  renderAdvisorLessons() {
    const list = document.getElementById('advisor-lessons-list');
    if (!list) return;
    list.innerHTML = '';

    FINANCIAL_LESSONS.forEach((lesson) => {
      const card = document.createElement('div');
      card.className = 'clay-card';
      card.innerHTML = `
        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 6px;">
          <span style="font-size: 22px;">${lesson.emoji}</span>
          <h4 style="font-family: var(--font-heading); font-size: 14px; font-weight: 700;">${lesson.title}</h4>
        </div>
        <p style="font-size: 12px; font-weight: 700; color: var(--clay-pink-shadow); margin-bottom: 4px;">«${lesson.rule}»</p>
        <p style="font-size: 11px; color: var(--text-dark); line-height: 1.35;">${lesson.fullText}</p>
      `;
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
