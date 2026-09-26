package ltd.kyss.petme.core.persistence

import android.content.Context
import ltd.kyss.petme.core.engine.GameState

/**
 * Небольшое локальное хранилище. commit() намеренно синхронный: после нажатия
 * на действие игрок может сразу закрыть приложение и прогресс не потеряется.
 */
class GameStateStorage(context: Context) {
    private val preferences = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun load(): GameState? {
        val raw = preferences.getString(SAVE_KEY, null) ?: return null
        return GameStateJsonCodec.decode(raw) ?: run {
            clear()
            null
        }
    }

    fun save(state: GameState) {
        runCatching {
            preferences.edit().putString(SAVE_KEY, GameStateJsonCodec.encode(state)).commit()
        }
    }

    fun clear() {
        preferences.edit().remove(SAVE_KEY).commit()
    }

    private companion object {
        const val PREFERENCES_NAME = "pet_me_save"
        const val SAVE_KEY = "game_state_v1"
    }
}
