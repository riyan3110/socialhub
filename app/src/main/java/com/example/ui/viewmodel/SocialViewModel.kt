package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ContentType
import com.example.data.SocialPlatform
import com.example.data.database.SocialDatabase
import com.example.data.entities.CrossPostEntity
import com.example.data.entities.RecentBrowsingEntity
import com.example.data.entities.SocialAccountEntity
import com.example.data.entities.SocialFeedEntity
import com.example.data.repository.SocialRepository
import com.example.service.GeminiAssistantService
import com.example.data.preferences.AccentColor
import com.example.data.preferences.AppThemeMode
import com.example.data.preferences.PreferencesRepository
import com.example.data.preferences.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SocialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SocialRepository
    private val preferencesRepository = PreferencesRepository(application)
    private val geminiService = GeminiAssistantService()

    val userSettings: StateFlow<UserSettings> = preferencesRepository.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    val selectedPlatform = MutableStateFlow<SocialPlatform?>(null)
    val selectedContentType = MutableStateFlow(ContentType.ALL)
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("Semua")

    val isAiSummaryLoading = MutableStateFlow(false)
    val aiSummaryText = MutableStateFlow<String?>(null)

    val smartReplies = MutableStateFlow<List<String>>(emptyList())
    val isSmartRepliesLoading = MutableStateFlow(false)

    init {
        val dao = SocialDatabase.getInstance(application).socialDao()
        repository = SocialRepository(dao)

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    val favoriteIds: StateFlow<Set<String>> = repository.favoritePlatforms
        .map { list -> list.map { it.platformId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = setOf("TWITTER_X", "TELEGRAM", "DISCORD", "GITHUB")
        )

    val recentVisits: StateFlow<List<RecentBrowsingEntity>> = repository.recentBrowsing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(platformId: String) {
        viewModelScope.launch {
            val isCurrentlyFavorite = favoriteIds.value.contains(platformId)
            repository.toggleFavorite(platformId, isCurrentlyFavorite)
        }
    }

    fun addRecentVisit(platformId: String, title: String, url: String) {
        viewModelScope.launch {
            repository.addRecentVisit(platformId, title, url)
        }
    }

    fun clearRecentVisits() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun onCategorySelected(category: String) {
        selectedCategory.value = category
    }

    val accounts: StateFlow<List<SocialAccountEntity>> = repository.allAccounts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val crossPosts: StateFlow<List<CrossPostEntity>> = repository.allCrossPosts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val feeds: StateFlow<List<SocialFeedEntity>> = combine(
        repository.allFeeds,
        selectedPlatform,
        selectedContentType,
        searchQuery
    ) { allFeedsList, platform, contentType, query ->
        allFeedsList.filter { feed ->
            val matchesPlatform = platform == null || feed.platform.equals(platform.id, ignoreCase = true)
            val matchesContentType = when (contentType) {
                ContentType.ALL -> true
                ContentType.MESSAGES -> feed.postType.equals("MESSAGE", ignoreCase = true)
                ContentType.POSTS -> feed.postType.equals("POST", ignoreCase = true)
                ContentType.STORIES -> feed.postType.equals("STORY", ignoreCase = true)
                ContentType.REELS -> feed.postType.equals("REELS", ignoreCase = true)
                ContentType.NOTIFICATIONS -> feed.postType.equals("NOTIFICATION", ignoreCase = true)
            }
            val matchesQuery = query.isBlank() ||
                    feed.authorName.contains(query, ignoreCase = true) ||
                    feed.authorHandle.contains(query, ignoreCase = true) ||
                    feed.content.contains(query, ignoreCase = true)

            matchesPlatform && matchesContentType && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val messages: StateFlow<List<SocialFeedEntity>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onPlatformSelected(platform: SocialPlatform?) {
        selectedPlatform.value = platform
    }

    fun onContentTypeSelected(contentType: ContentType) {
        selectedContentType.value = contentType
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun toggleBookmark(feed: SocialFeedEntity) {
        viewModelScope.launch {
            repository.toggleBookmark(feed)
        }
    }

    fun toggleLike(feed: SocialFeedEntity) {
        viewModelScope.launch {
            repository.toggleLike(feed)
        }
    }

    fun markAsRead(feedId: Long) {
        viewModelScope.launch {
            repository.markAsRead(feedId)
        }
    }

    fun toggleAccountSync(accountId: Long, currentSync: Boolean) {
        viewModelScope.launch {
            repository.toggleAccountSync(accountId, currentSync)
        }
    }

    fun publishCrossPost(
        caption: String,
        mediaUri: String?,
        selectedPlatforms: List<SocialPlatform>,
        isScheduled: Boolean = false,
        scheduledTime: String? = null,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            repository.publishCrossPost(
                caption = caption,
                mediaUri = mediaUri,
                selectedPlatforms = selectedPlatforms,
                isScheduled = isScheduled,
                scheduledTime = scheduledTime
            )
            onComplete()
        }
    }

    fun generateFeedSummary() {
        viewModelScope.launch {
            isAiSummaryLoading.value = true
            val currentFeeds = feeds.value
            val summary = geminiService.generateFeedSummary(currentFeeds)
            aiSummaryText.value = summary
            isAiSummaryLoading.value = false
        }
    }

    fun generateSmartReplies(messageContent: String, authorName: String) {
        viewModelScope.launch {
            isSmartRepliesLoading.value = true
            val replies = geminiService.generateSmartReplies(messageContent, authorName)
            smartReplies.value = replies
            isSmartRepliesLoading.value = false
        }
    }

    fun enhanceCaption(caption: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val enhanced = geminiService.enhanceCaption(caption)
            onResult(enhanced)
        }
    }

    // --- Settings Updaters ---
    fun updateThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            preferencesRepository.updateThemeMode(mode)
        }
    }

    fun updateAccentColor(color: AccentColor) {
        viewModelScope.launch {
            preferencesRepository.updateAccentColor(color)
        }
    }

    fun updateUseDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateUseDynamicColor(enabled)
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateNotificationsEnabled(enabled)
        }
    }

    fun updatePushAlerts(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updatePushAlerts(enabled)
        }
    }

    fun updateSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateSoundEnabled(enabled)
        }
    }

    fun updateBrowserMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.updateBrowserMode(mode)
        }
    }

    fun updateBlockAds(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateBlockAds(enabled)
        }
    }

    fun updateClearCacheExit(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateClearCacheExit(enabled)
        }
    }

    fun updateAiAutoSummarize(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAiAutoSummarize(enabled)
        }
    }

    fun updateLanguage(lang: String) {
        viewModelScope.launch {
            preferencesRepository.updateLanguage(lang)
        }
    }

    fun updateAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAnalyticsEnabled(enabled)
        }
    }

    fun updateSecureBrowsing(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateSecureBrowsing(enabled)
        }
    }
}
