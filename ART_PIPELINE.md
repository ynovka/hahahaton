# Подключение артов без изменения игровой логики

Все финальные PNG кладутся в:

    app/src/main/res/drawable-nodpi/

Имена только строчными латинскими буквами, цифрами и подчёркиваниями. После добавления
файлов нужно собрать приложение заново. Код автоматически заменит эмодзи-заглушку,
если найдёт ресурс с ожидаемым именем.

## Главный герой

- hero_idle.png — спокойная стойка.
- hero_walk_01.png ... hero_walk_06.png — полный цикл ходьбы.

Все шесть кадров должны иметь одинаковый размер холста. Ступни находятся в одной точке.
Персонаж рисуется смотрящим вправо; движение влево отражается кодом.

## Питомцы

Вместо species подставляется: cat, dog, fox, panda, bunny, raccoon или owl.

- pet_species_portrait.png — выбор питомца и интерфейс.
- pet_species_idle.png — питомец стоит в комнате.
- pet_species_walk_01.png ... pet_species_walk_06.png — цикл ходьбы.

Пример:

    pet_cat_portrait.png
    pet_cat_idle.png
    pet_cat_walk_01.png
    ...
    pet_cat_walk_06.png

## Друзья

ID друзей уже зафиксированы в GameCatalog: 1..7.

- friend_1_portrait.png ... friend_7_portrait.png — карта и диалоги.
- friend_1_idle.png ... friend_7_idle.png — персонаж в квартире.
- friend_1_talk_01.png ... friend_1_talk_04.png — будущая анимация разговора.

## Работники и предметы

- worker_bank_idle.png
- worker_shop_idle.png
- worker_hospital_idle.png
- prop_wardrobe.png
- prop_bowl_empty.png
- prop_bowl_full.png
- prop_piggy_bank.png

## Карта

- building_home.png
- building_bank.png
- building_shop.png
- building_hospital.png

Дома друзей на карте используют friend_1_portrait.png ... friend_7_portrait.png.

## Технические требования

- PNG с прозрачным фоном.
- Цветовой профиль sRGB.
- Один масштаб и направление света внутри одного набора.
- Исходный холст персонажа: 512×512 px.
- Не обрезать тень, уши, хвост и крайние кадры движения.
- Опорная точка всех кадров персонажа — середина между ступнями.
- Перед передачей проверить анимацию на прозрачном и тёмном фоне.

Компоненты подключения находятся в ui/components/GameArt.kt. Художнику не нужно менять
GameEngine, GameState, GameViewModel или каталоги механик.
