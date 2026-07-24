package com.example.data.preferences

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "social_hub_preferences")

enum class AppThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

enum class AccentColor(
    val displayName: String,
    val primaryColor: Color,
    val lightContainerColor: Color,
    val darkContainerColor: Color,
    val lightOnContainerColor: Color,
    val darkOnContainerColor: Color
) {
    PURPLE(
        displayName = "Purple",
        primaryColor = Color(0xFF7C3AED),
        lightContainerColor = Color(0xFFEDE9FE),
        darkContainerColor = Color(0xFF3B0764),
        lightOnContainerColor = Color(0xFF5B21B6),
        darkOnContainerColor = Color(0xFFDDD6FE)
    ),
    BLUE(
        displayName = "Blue",
        primaryColor = Color(0xFF2563EB),
        lightContainerColor = Color(0xFFDBEAFE),
        darkContainerColor = Color(0xFF1E3A8A),
        lightOnContainerColor = Color(0xFF1E40AF),
        darkOnContainerColor = Color(0xFFBFDBFE)
    ),
    GREEN(
        displayName = "Green",
        primaryColor = Color(0xFF059669),
        lightContainerColor = Color(0xFFD1FAE5),
        darkContainerColor = Color(0xFF064E3B),
        lightOnContainerColor = Color(0xFF047857),
        darkOnContainerColor = Color(0xFFA7F3D0)
    ),
    ORANGE(
        displayName = "Orange",
        primaryColor = Color(0xFFEA580C),
        lightContainerColor = Color(0xFFFFEDD5),
        darkContainerColor = Color(0xFF7C2D12),
        lightOnContainerColor = Color(0xFFC2410C),
        darkOnContainerColor = Color(0xFFFED7AA)
    ),
    RED(
        displayName = "Red",
        primaryColor = Color(0xFFDC2626),
        lightContainerColor = Color(0xFFFEE2E2),
        darkContainerColor = Color(0xFF7F1D1D),
        lightOnContainerColor = Color(0xFFB91C1C),
        darkOnContainerColor = Color(0xFFFECACA)
    ),
    PINK(
        displayName = "Pink",
        primaryColor = Color(0xFFDB2777),
        lightContainerColor = Color(0xFFFCE7F3),
        darkContainerColor = Color(0xFF831843),
        lightOnContainerColor = Color(0xFFBE185D),
        darkOnContainerColor = Color(0xFFFBCFE8)
    ),
    CYAN(
        displayName = "Cyan",
        primaryColor = Color(0xFF0891B2),
        lightContainerColor = Color(0xFFCFFAFE),
        darkContainerColor = Color(0xFF164E63),
        lightOnContainerColor = Color(0xFF0E7490),
        darkOnContainerColor = Color(0xFFA5F3FC)
    );

    companion object {
        fun fromName(name: String): AccentColor {
            return entries.find { it.name.equals(name, ignoreCase = true) } ?: PURPLE
        }
    }
}

data class UserSettings(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val accentColor: AccentColor = AccentColor.PURPLE,
    val useDynamicColor: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val pushAlertsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val browserMode: String = "SMART", // SMART, EMBEDDED, CUSTOM_TABS
    val blockAds: Boolean = true,
    val clearCacheOnExit: Boolean = false,
    val aiAutoSummarize: Boolean = true,
    val aiModelTier: String = "Gemini 2.5 Flash",
    val appLanguage: String = "id", // id, en
    val analyticsEnabled: Boolean = true,
    val secureBrowsing: Boolean = true
)

class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val PUSH_ALERTS = booleanPreferencesKey("push_alerts")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val BROWSER_MODE = stringPreferencesKey("browser_mode")
        val BLOCK_ADS = booleanPreferencesKey("block_ads")
        val CLEAR_CACHE_EXIT = booleanPreferencesKey("clear_cache_exit")
        val AI_AUTO_SUMMARIZE = booleanPreferencesKey("ai_auto_summarize")
        val AI_MODEL_TIER = stringPreferencesKey("ai_model_tier")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
        val SECURE_BROWSING = booleanPreferencesKey("secure_browsing")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        val themeModeStr = prefs[PreferencesKeys.THEME_MODE] ?: AppThemeMode.SYSTEM.name
        val accentColorStr = prefs[PreferencesKeys.ACCENT_COLOR] ?: AccentColor.PURPLE.name

        UserSettings(
            themeMode = try { AppThemeMode.valueOf(themeModeStr) } catch (e: Exception) { AppThemeMode.SYSTEM },
            accentColor = AccentColor.fromName(accentColorStr),
            useDynamicColor = prefs[PreferencesKeys.USE_DYNAMIC_COLOR] ?: false,
            notificationsEnabled = prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            pushAlertsEnabled = prefs[PreferencesKeys.PUSH_ALERTS] ?: true,
            soundEnabled = prefs[PreferencesKeys.SOUND_ENABLED] ?: true,
            browserMode = prefs[PreferencesKeys.BROWSER_MODE] ?: "SMART",
            blockAds = prefs[PreferencesKeys.BLOCK_ADS] ?: true,
            clearCacheOnExit = prefs[PreferencesKeys.CLEAR_CACHE_EXIT] ?: false,
            aiAutoSummarize = prefs[PreferencesKeys.AI_AUTO_SUMMARIZE] ?: true,
            aiModelTier = prefs[PreferencesKeys.AI_MODEL_TIER] ?: "Gemini 2.5 Flash",
            appLanguage = prefs[PreferencesKeys.APP_LANGUAGE] ?: "id",
            analyticsEnabled = prefs[PreferencesKeys.ANALYTICS_ENABLED] ?: true,
            secureBrowsing = prefs[PreferencesKeys.SECURE_BROWSING] ?: true
        )
    }

    suspend fun updateThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun updateAccentColor(color: AccentColor) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ACCENT_COLOR] = color.name
        }
    }

    suspend fun updateUseDynamicColor(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.USE_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updatePushAlerts(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.PUSH_ALERTS] = enabled
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateBrowserMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.BROWSER_MODE] = mode
        }
    }

    suspend fun updateBlockAds(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.BLOCK_ADS] = enabled
        }
    }

    suspend fun updateClearCacheExit(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.CLEAR_CACHE_EXIT] = enabled
        }
    }

    suspend fun updateAiAutoSummarize(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.AI_AUTO_SUMMARIZE] = enabled
        }
    }

    suspend fun updateLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.APP_LANGUAGE] = lang
        }
    }

    suspend fun updateAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ANALYTICS_ENABLED] = enabled
        }
    }

    suspend fun updateSecureBrowsing(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.SECURE_BROWSING] = enabled
        }
    }
}
