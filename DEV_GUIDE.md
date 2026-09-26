# 🎮 Руководство разработчика: Pet Me! (Детская игра на Android)

Добро пожаловать в проект **Pet Me!** — интерактивную мобильную игру для детей на **Kotlin + Jetpack Compose**.

---

## 🚀 Быстрый запуск

### Вариант 1: Через VS Code / Antigravity IDE (Tasks)
Нажмите `Ctrl+Shift+P` -> выберите **Tasks: Run Task**:
- **`Android: Start Emulator (medium_phone)`** — запуск готового эмулятора смартфона.
- **`Android: Run App (Build & Launch)`** — быстрая сборка, установка и запуск на эмуляторе/телефоне.
- **`Android: Build Debug APK`** (`Ctrl+Shift+B`) — компиляция проекта.
- **`Android: Take Screenshot`** — мгновенный снимок экрана с устройства в папку `screenshots/`.
- **`Android: Stream Logcat (Pet Me)`** — просмотр логов приложения в реальном времени.
- **`Android: View Layout Hierarchy (Inspect UI)`** — JSON-дерево интерфейса для отладки верстки.

### Вариант 2: Готовые скрипты в папке `scripts/`
- `scripts\start_emulator.bat` — запуск виртуального телефона.
- `scripts\run_app.bat` — автоматическая проверка устройства, сборка и запуск игры.
- `scripts\take_screenshot.bat` — скриншот в папку `screenshots/`.
- `scripts\view_logcat.bat` — просмотр отфильтрованных логов.

### Вариант 3: Через терминал (Android CLI)
```bash
# Запустить эмулятор
android emulator start medium_phone

# Собрать и запустить приложение
android run

# Сделать скриншот экрана
android screen capture -o screenshots/screen.png

# Посмотреть структуру UI-компонентов
android layout -p
```

---

## 🛠️ Что уже настроено в окружении

1. **Android SDK & Build Tools**:
   - SDK: `C:\Users\IKER\AppData\Local\Android\Sdk`
   - `build-tools 37.0.0` и `36.0.0`
   - `platforms/android-37.0` (API 37)
   - `platform-tools` с `adb` (добавлен в системные переменные терминала)
   - Конфигурация в [local.properties](file:///e:/Hakaton/hahahaton/local.properties).

2. **Эмулятор**:
   - Создан AVD профиль `medium_phone` с образом Android API 36 Google Play Store x86_64.

3. **Сборка проекта**:
   - Проверена сборка через Gradle 9.6.0 (`assembleDebug` успешно собирается).

---

## 🧸 Рекомендации для детской игры ("Pet Me!")

Детские игры требуют максимальной отзывчивости, сочности и визуальной простоты:

### 1. Сочная интерактивность (Bouncy Touch Effects)
Используйте `animateFloatAsState` с `spring(dampingRatio = Spring.DampingRatioMediumBouncy)` при нажатиях на питомца или кнопки:
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.85f else 1.0f,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
)
```

### 2. Тактильный отклик (Haptic Feedback)
Дети любят чувствовать реакцию экрана:
```kotlin
val haptic = LocalHapticFeedback.current
Modifier.clickable {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}
```

### 3. Звуковые эффекты (SoundPool)
Для мгновенного воспроизведения звуков мурлыканья, жевания, радости и звона монет рекомендуется использовать легковесный `SoundPool`.

### 4. Частицы и праздничные эффекты
Плавающие сердечки при поглаживании или конфетти при достижении нового уровня можно рисовать прямо через `Canvas` или использовать Compose Canvas DrawScope.
