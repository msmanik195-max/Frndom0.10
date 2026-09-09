package com.example.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("frndom_app_settings", Context.MODE_PRIVATE)

    // User-level preferences
    private val _autoPlayVideos = MutableStateFlow(prefs.getBoolean(KEY_AUTOPLAY_VIDEOS, true))
    val autoPlayVideos: StateFlow<Boolean> = _autoPlayVideos.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true))
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _dataSaverEnabled = MutableStateFlow(prefs.getBoolean(KEY_DATA_SAVER_ENABLED, false))
    val dataSaverEnabled: StateFlow<Boolean> = _dataSaverEnabled.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean(KEY_DARK_MODE, false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Platform-level Admin Feature Toggles
    private val _isPostingEnabled = MutableStateFlow(prefs.getBoolean(KEY_POSTING_ENABLED, true))
    val isPostingEnabled: StateFlow<Boolean> = _isPostingEnabled.asStateFlow()

    private val _isStoryCreationEnabled = MutableStateFlow(prefs.getBoolean(KEY_STORY_CREATION_ENABLED, true))
    val isStoryCreationEnabled: StateFlow<Boolean> = _isStoryCreationEnabled.asStateFlow()

    private val _isReelsUploadEnabled = MutableStateFlow(prefs.getBoolean(KEY_REELS_UPLOAD_ENABLED, true))
    val isReelsUploadEnabled: StateFlow<Boolean> = _isReelsUploadEnabled.asStateFlow()

    private val _isCommentsEnabled = MutableStateFlow(prefs.getBoolean(KEY_COMMENTS_ENABLED, true))
    val isCommentsEnabled: StateFlow<Boolean> = _isCommentsEnabled.asStateFlow()

    private val _isReactionsEnabled = MutableStateFlow(prefs.getBoolean(KEY_REACTIONS_ENABLED, true))
    val isReactionsEnabled: StateFlow<Boolean> = _isReactionsEnabled.asStateFlow()

    private val _isChatEnabled = MutableStateFlow(prefs.getBoolean(KEY_CHAT_ENABLED, true))
    val isChatEnabled: StateFlow<Boolean> = _isChatEnabled.asStateFlow()

    private val _isMarketplaceEnabled = MutableStateFlow(prefs.getBoolean(KEY_MARKETPLACE_ENABLED, true))
    val isMarketplaceEnabled: StateFlow<Boolean> = _isMarketplaceEnabled.asStateFlow()

    private val _isDepositsEnabled = MutableStateFlow(prefs.getBoolean(KEY_DEPOSITS_ENABLED, true))
    val isDepositsEnabled: StateFlow<Boolean> = _isDepositsEnabled.asStateFlow()

    private val _isWithdrawalsEnabled = MutableStateFlow(prefs.getBoolean(KEY_WITHDRAWALS_ENABLED, true))
    val isWithdrawalsEnabled: StateFlow<Boolean> = _isWithdrawalsEnabled.asStateFlow()

    private val _isVerificationRequestsEnabled = MutableStateFlow(prefs.getBoolean(KEY_VERIFICATION_REQUESTS_ENABLED, true))
    val isVerificationRequestsEnabled: StateFlow<Boolean> = _isVerificationRequestsEnabled.asStateFlow()

    private val _isMonetizationApplicationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_MONETIZATION_APPLICATIONS_ENABLED, true))
    val isMonetizationApplicationsEnabled: StateFlow<Boolean> = _isMonetizationApplicationsEnabled.asStateFlow()

    private val _isPageCreationEnabled = MutableStateFlow(prefs.getBoolean(KEY_PAGE_CREATION_ENABLED, true))
    val isPageCreationEnabled: StateFlow<Boolean> = _isPageCreationEnabled.asStateFlow()

    private val _isGroupCreationEnabled = MutableStateFlow(prefs.getBoolean(KEY_GROUP_CREATION_ENABLED, true))
    val isGroupCreationEnabled: StateFlow<Boolean> = _isGroupCreationEnabled.asStateFlow()

    private val _isRegistrationEnabled = MutableStateFlow(prefs.getBoolean(KEY_REGISTRATION_ENABLED, true))
    val isRegistrationEnabled: StateFlow<Boolean> = _isRegistrationEnabled.asStateFlow()

    private val _isMaintenanceModeEnabled = MutableStateFlow(prefs.getBoolean(KEY_MAINTENANCE_MODE, false))
    val isMaintenanceModeEnabled: StateFlow<Boolean> = _isMaintenanceModeEnabled.asStateFlow()

    private val _maintenanceNotice = MutableStateFlow(
        prefs.getString(KEY_MAINTENANCE_MESSAGE, "সিস্টেম রক্ষণাবেক্ষণ চলছে। সাময়িকভাবে কিছু ফিচার সীমাবদ্ধ থাকতে পারে।") ?: ""
    )
    val maintenanceNotice: StateFlow<String> = _maintenanceNotice.asStateFlow()

    private val _isAnnouncementBannerEnabled = MutableStateFlow(prefs.getBoolean(KEY_ANNOUNCEMENT_BANNER_ENABLED, false))
    val isAnnouncementBannerEnabled: StateFlow<Boolean> = _isAnnouncementBannerEnabled.asStateFlow()

    private val _announcementBannerText = MutableStateFlow(
        prefs.getString(KEY_ANNOUNCEMENT_BANNER_TEXT, "Welcome to Frndom! Connect, Share and Earn.") ?: ""
    )
    val announcementBannerText: StateFlow<String> = _announcementBannerText.asStateFlow()

    // Setters
    fun setAutoPlayVideos(enabled: Boolean) {
        _autoPlayVideos.value = enabled
        prefs.edit().putBoolean(KEY_AUTOPLAY_VIDEOS, enabled).apply()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun setDataSaverEnabled(enabled: Boolean) {
        _dataSaverEnabled.value = enabled
        prefs.edit().putBoolean(KEY_DATA_SAVER_ENABLED, enabled).apply()
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    // Platform Feature Setters
    fun setPostingEnabled(enabled: Boolean) {
        _isPostingEnabled.value = enabled
        prefs.edit().putBoolean(KEY_POSTING_ENABLED, enabled).apply()
    }

    fun setStoryCreationEnabled(enabled: Boolean) {
        _isStoryCreationEnabled.value = enabled
        prefs.edit().putBoolean(KEY_STORY_CREATION_ENABLED, enabled).apply()
    }

    fun setReelsUploadEnabled(enabled: Boolean) {
        _isReelsUploadEnabled.value = enabled
        prefs.edit().putBoolean(KEY_REELS_UPLOAD_ENABLED, enabled).apply()
    }

    fun setCommentsEnabled(enabled: Boolean) {
        _isCommentsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_COMMENTS_ENABLED, enabled).apply()
    }

    fun setReactionsEnabled(enabled: Boolean) {
        _isReactionsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_REACTIONS_ENABLED, enabled).apply()
    }

    fun setChatEnabled(enabled: Boolean) {
        _isChatEnabled.value = enabled
        prefs.edit().putBoolean(KEY_CHAT_ENABLED, enabled).apply()
    }

    fun setMarketplaceEnabled(enabled: Boolean) {
        _isMarketplaceEnabled.value = enabled
        prefs.edit().putBoolean(KEY_MARKETPLACE_ENABLED, enabled).apply()
    }

    fun setDepositsEnabled(enabled: Boolean) {
        _isDepositsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_DEPOSITS_ENABLED, enabled).apply()
    }

    fun setWithdrawalsEnabled(enabled: Boolean) {
        _isWithdrawalsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_WITHDRAWALS_ENABLED, enabled).apply()
    }

    fun setVerificationRequestsEnabled(enabled: Boolean) {
        _isVerificationRequestsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_VERIFICATION_REQUESTS_ENABLED, enabled).apply()
    }

    fun setMonetizationApplicationsEnabled(enabled: Boolean) {
        _isMonetizationApplicationsEnabled.value = enabled
        prefs.edit().putBoolean(KEY_MONETIZATION_APPLICATIONS_ENABLED, enabled).apply()
    }

    fun setPageCreationEnabled(enabled: Boolean) {
        _isPageCreationEnabled.value = enabled
        prefs.edit().putBoolean(KEY_PAGE_CREATION_ENABLED, enabled).apply()
    }

    fun setGroupCreationEnabled(enabled: Boolean) {
        _isGroupCreationEnabled.value = enabled
        prefs.edit().putBoolean(KEY_GROUP_CREATION_ENABLED, enabled).apply()
    }

    fun setRegistrationEnabled(enabled: Boolean) {
        _isRegistrationEnabled.value = enabled
        prefs.edit().putBoolean(KEY_REGISTRATION_ENABLED, enabled).apply()
    }

    fun setMaintenanceModeEnabled(enabled: Boolean) {
        _isMaintenanceModeEnabled.value = enabled
        prefs.edit().putBoolean(KEY_MAINTENANCE_MODE, enabled).apply()
    }

    fun setMaintenanceNotice(message: String) {
        _maintenanceNotice.value = message
        prefs.edit().putString(KEY_MAINTENANCE_MESSAGE, message).apply()
    }

    fun setAnnouncementBannerEnabled(enabled: Boolean) {
        _isAnnouncementBannerEnabled.value = enabled
        prefs.edit().putBoolean(KEY_ANNOUNCEMENT_BANNER_ENABLED, enabled).apply()
    }

    fun setAnnouncementBannerText(text: String) {
        _announcementBannerText.value = text
        prefs.edit().putString(KEY_ANNOUNCEMENT_BANNER_TEXT, text).apply()
    }

    companion object {
        private const val KEY_AUTOPLAY_VIDEOS = "autoplay_videos"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DATA_SAVER_ENABLED = "data_saver_enabled"
        private const val KEY_DARK_MODE = "dark_mode_enabled"

        // Platform keys
        private const val KEY_POSTING_ENABLED = "platform_posting_enabled"
        private const val KEY_STORY_CREATION_ENABLED = "platform_story_creation_enabled"
        private const val KEY_REELS_UPLOAD_ENABLED = "platform_reels_upload_enabled"
        private const val KEY_COMMENTS_ENABLED = "platform_comments_enabled"
        private const val KEY_REACTIONS_ENABLED = "platform_reactions_enabled"
        private const val KEY_CHAT_ENABLED = "platform_chat_enabled"
        private const val KEY_MARKETPLACE_ENABLED = "platform_marketplace_enabled"
        private const val KEY_DEPOSITS_ENABLED = "platform_deposits_enabled"
        private const val KEY_WITHDRAWALS_ENABLED = "platform_withdrawals_enabled"
        private const val KEY_VERIFICATION_REQUESTS_ENABLED = "platform_verification_requests_enabled"
        private const val KEY_MONETIZATION_APPLICATIONS_ENABLED = "platform_monetization_applications_enabled"
        private const val KEY_PAGE_CREATION_ENABLED = "platform_page_creation_enabled"
        private const val KEY_GROUP_CREATION_ENABLED = "platform_group_creation_enabled"
        private const val KEY_REGISTRATION_ENABLED = "platform_registration_enabled"
        private const val KEY_MAINTENANCE_MODE = "platform_maintenance_mode"
        private const val KEY_MAINTENANCE_MESSAGE = "platform_maintenance_message"
        private const val KEY_ANNOUNCEMENT_BANNER_ENABLED = "platform_announcement_banner_enabled"
        private const val KEY_ANNOUNCEMENT_BANNER_TEXT = "platform_announcement_banner_text"

        @Volatile
        private var instance: AppSettingsRepository? = null

        fun getInstance(context: Context): AppSettingsRepository {
            return instance ?: synchronized(this) {
                instance ?: AppSettingsRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
