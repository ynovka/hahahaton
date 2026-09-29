// ============================================================================
// Питомец Финни — Точка входа веб-приложения (Application Entry Point)
// ============================================================================

import { gameState } from './state.js';
import { sound } from './audio.js';
import { UIController } from './ui.js';
import { tutorial } from './tutorial.js';

window.addEventListener('DOMContentLoaded', () => {
  // Разблокировка Web Audio API по первому клику/тапу пользователя
  const unlockAudio = () => {
    sound.init();
    window.removeEventListener('click', unlockAudio);
    window.removeEventListener('touchstart', unlockAudio);
  };
  window.addEventListener('click', unlockAudio);
  window.addEventListener('touchstart', unlockAudio);

  // Инициализация UI контроллера и системы обучения
  const ui = new UIController();
  tutorial.init();

  // Применяем сохраненные настройки доступности
  if (gameState.state.largeFontEnabled) {
    document.body.classList.add('large-font');
  }
  sound.setEnabled(gameState.state.soundEnabled);

  console.log('🐾 «Питомец Финни» успешно запущен! Приятной и полезной игры!');
});
