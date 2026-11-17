package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.AppLanguage
import com.example.domain.AudioMode
import com.example.domain.ControlLayoutMode
import com.example.domain.GameSettings
import com.example.domain.GraphicsQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "fishing_master_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val BGM_VOL = floatPreferencesKey("bgm_vol")
        val SFX_VOL = floatPreferencesKey("sfx_vol")
        val AUDIO_MODE = stringPreferencesKey("audio_mode")
        val GRAPHICS = stringPreferencesKey("graphics_quality")
        val TARGET_FPS = intPreferencesKey("target_fps")
        val CAMERA_SENS = floatPreferencesKey("camera_sens")
        val CONTROL_LAYOUT = stringPreferencesKey("control_layout")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFY_EVENTS = booleanPreferencesKey("notify_events")
        val NOTIFY_FRIENDS = booleanPreferencesKey("notify_friends")
        val NOTIFY_MESSAGES = booleanPreferencesKey("notify_messages")
        val REDUCE_WEATHER = booleanPreferencesKey("reduce_weather")
        val COLORBLIND = booleanPreferencesKey("colorblind")
        val HAPTIC = booleanPreferencesKey("haptic")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
    }

    val settingsFlow: Flow<GameSettings> = context.settingsDataStore.data.map { prefs ->
        GameSettings(
            bgmVolume = prefs[Keys.BGM_VOL] ?: 0.7f,
            sfxVolume = prefs[Keys.SFX_VOL] ?: 0.85f,
            audioMode = AudioMode.entries.find { it.name == prefs[Keys.AUDIO_MODE] } ?: AudioMode.NORMAL,
            graphicsQuality = GraphicsQuality.entries.find { it.name == prefs[Keys.GRAPHICS] } ?: GraphicsQuality.TINGGI,
            targetFps = prefs[Keys.TARGET_FPS] ?: 60,
            cameraSensitivity = prefs[Keys.CAMERA_SENS] ?: 0.6f,
            controlLayout = ControlLayoutMode.entries.find { it.name == prefs[Keys.CONTROL_LAYOUT] } ?: ControlLayoutMode.MODERN,
            language = AppLanguage.entries.find { it.name == prefs[Keys.LANGUAGE] } ?: AppLanguage.ID,
            notifyEvents = prefs[Keys.NOTIFY_EVENTS] ?: true,
            notifyFriends = prefs[Keys.NOTIFY_FRIENDS] ?: true,
            notifyMessages = prefs[Keys.NOTIFY_MESSAGES] ?: true,
            reduceWeatherEffects = prefs[Keys.REDUCE_WEATHER] ?: false,
            colorblindMode = prefs[Keys.COLORBLIND] ?: false,
            hapticFeedback = prefs[Keys.HAPTIC] ?: true,
            textScale = prefs[Keys.TEXT_SCALE] ?: 1.0f
        )
    }

    suspend fun updateSettings(transform: (GameSettings) -> GameSettings) {
        context.settingsDataStore.edit { prefs ->
            val current = GameSettings(
                bgmVolume = prefs[Keys.BGM_VOL] ?: 0.7f,
                sfxVolume = prefs[Keys.SFX_VOL] ?: 0.85f,
                audioMode = AudioMode.entries.find { it.name == prefs[Keys.AUDIO_MODE] } ?: AudioMode.NORMAL,
                graphicsQuality = GraphicsQuality.entries.find { it.name == prefs[Keys.GRAPHICS] } ?: GraphicsQuality.TINGGI,
                targetFps = prefs[Keys.TARGET_FPS] ?: 60,
                cameraSensitivity = prefs[Keys.CAMERA_SENS] ?: 0.6f,
                controlLayout = ControlLayoutMode.entries.find { it.name == prefs[Keys.CONTROL_LAYOUT] } ?: ControlLayoutMode.MODERN,
                language = AppLanguage.entries.find { it.name == prefs[Keys.LANGUAGE] } ?: AppLanguage.ID,
                notifyEvents = prefs[Keys.NOTIFY_EVENTS] ?: true,
                notifyFriends = prefs[Keys.NOTIFY_FRIENDS] ?: true,
                notifyMessages = prefs[Keys.NOTIFY_MESSAGES] ?: true,
                reduceWeatherEffects = prefs[Keys.REDUCE_WEATHER] ?: false,
                colorblindMode = prefs[Keys.COLORBLIND] ?: false,
                hapticFeedback = prefs[Keys.HAPTIC] ?: true,
                textScale = prefs[Keys.TEXT_SCALE] ?: 1.0f
            )
            val updated = transform(current)
            prefs[Keys.BGM_VOL] = updated.bgmVolume
            prefs[Keys.SFX_VOL] = updated.sfxVolume
            prefs[Keys.AUDIO_MODE] = updated.audioMode.name
            prefs[Keys.GRAPHICS] = updated.graphicsQuality.name
            prefs[Keys.TARGET_FPS] = updated.targetFps
            prefs[Keys.CAMERA_SENS] = updated.cameraSensitivity
            prefs[Keys.CONTROL_LAYOUT] = updated.controlLayout.name
            prefs[Keys.LANGUAGE] = updated.language.name
            prefs[Keys.NOTIFY_EVENTS] = updated.notifyEvents
            prefs[Keys.NOTIFY_FRIENDS] = updated.notifyFriends
            prefs[Keys.NOTIFY_MESSAGES] = updated.notifyMessages
            prefs[Keys.REDUCE_WEATHER] = updated.reduceWeatherEffects
            prefs[Keys.COLORBLIND] = updated.colorblindMode
            prefs[Keys.HAPTIC] = updated.hapticFeedback
            prefs[Keys.TEXT_SCALE] = updated.textScale
        }
    }
}
