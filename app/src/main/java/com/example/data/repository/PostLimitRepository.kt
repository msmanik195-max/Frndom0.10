package com.example.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyPostLimits(
    val maxTextPosts: Int = 10,
    val maxPhotoPosts: Int = 10,
    val maxVideoPosts: Int = 5,
    val maxStories: Int = 10,
    val isEnabled: Boolean = true
) {
    fun toMap(): Map<String, Any> = mapOf(
        "maxTextPosts" to maxTextPosts,
        "maxPhotoPosts" to maxPhotoPosts,
        "maxVideoPosts" to maxVideoPosts,
        "maxStories" to maxStories,
        "isEnabled" to isEnabled
    )
}

class PostLimitRepository private constructor(private val context: Context) {

    private val prefs = context.getSharedPreferences("frndom_post_limits_prefs", Context.MODE_PRIVATE)

    private val dbRef: DatabaseReference? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseDatabase.getInstance().reference
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("PostLimitRepo", "FirebaseDatabase not initialized: ${e.message}")
            null
        }
    }

    private val _globalLimitsFlow = MutableStateFlow(loadGlobalLimitsLocal())
    val globalLimitsFlow: StateFlow<DailyPostLimits> = _globalLimitsFlow.asStateFlow()

    init {
        listenToFirebaseGlobalLimits()
    }

    private fun loadGlobalLimitsLocal(): DailyPostLimits {
        val enabled = prefs.getBoolean("global_limits_enabled", true)
        val textLimit = prefs.getInt("global_limit_text", 10)
        val photoLimit = prefs.getInt("global_limit_photo", 10)
        val videoLimit = prefs.getInt("global_limit_video", 5)
        val storyLimit = prefs.getInt("global_limit_story", 10)
        return DailyPostLimits(
            maxTextPosts = textLimit,
            maxPhotoPosts = photoLimit,
            maxVideoPosts = videoLimit,
            maxStories = storyLimit,
            isEnabled = enabled
        )
    }

    private fun saveGlobalLimitsLocal(limits: DailyPostLimits) {
        prefs.edit()
            .putBoolean("global_limits_enabled", limits.isEnabled)
            .putInt("global_limit_text", limits.maxTextPosts)
            .putInt("global_limit_photo", limits.maxPhotoPosts)
            .putInt("global_limit_video", limits.maxVideoPosts)
            .putInt("global_limit_story", limits.maxStories)
            .apply()
    }

    private fun listenToFirebaseGlobalLimits() {
        try {
            dbRef?.child("system_settings")?.child("post_limits")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val enabled = snapshot.child("isEnabled").getValue(Boolean::class.java) ?: true
                        val text = (snapshot.child("maxTextPosts").getValue(Long::class.java) ?: 10L).toInt()
                        val photo = (snapshot.child("maxPhotoPosts").getValue(Long::class.java) ?: 10L).toInt()
                        val video = (snapshot.child("maxVideoPosts").getValue(Long::class.java) ?: 5L).toInt()
                        val story = (snapshot.child("maxStories").getValue(Long::class.java) ?: 10L).toInt()
                        val limits = DailyPostLimits(
                            maxTextPosts = text,
                            maxPhotoPosts = photo,
                            maxVideoPosts = video,
                            maxStories = story,
                            isEnabled = enabled
                        )
                        _globalLimitsFlow.value = limits
                        saveGlobalLimitsLocal(limits)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } catch (e: Exception) {
            Log.w("PostLimitRepo", "Error listening to global limits: ${e.message}")
        }
    }

    fun saveGlobalLimits(limits: DailyPostLimits) {
        _globalLimitsFlow.value = limits
        saveGlobalLimitsLocal(limits)
        try {
            dbRef?.child("system_settings")?.child("post_limits")?.setValue(limits.toMap())
        } catch (e: Exception) {
            Log.e("PostLimitRepo", "Error saving global limits to Firebase: ${e.message}")
        }
    }

    // --- User Custom Limits ---
    fun getUserCustomLimits(userId: String): DailyPostLimits? {
        if (userId.isBlank()) return null
        val json = prefs.getString("user_limit_$userId", null) ?: return null
        return try {
            val obj = JSONObject(json)
            DailyPostLimits(
                maxTextPosts = obj.optInt("maxTextPosts", 10),
                maxPhotoPosts = obj.optInt("maxPhotoPosts", 10),
                maxVideoPosts = obj.optInt("maxVideoPosts", 5),
                maxStories = obj.optInt("maxStories", 10),
                isEnabled = obj.optBoolean("isEnabled", true)
            )
        } catch (_: Exception) {
            null
        }
    }

    fun setUserCustomLimits(userId: String, limits: DailyPostLimits?) {
        if (userId.isBlank()) return
        if (limits == null) {
            prefs.edit().remove("user_limit_$userId").apply()
            try { dbRef?.child("user_post_limits")?.child(userId)?.removeValue() } catch (_: Exception) {}
        } else {
            val obj = JSONObject().apply {
                put("maxTextPosts", limits.maxTextPosts)
                put("maxPhotoPosts", limits.maxPhotoPosts)
                put("maxVideoPosts", limits.maxVideoPosts)
                put("maxStories", limits.maxStories)
                put("isEnabled", limits.isEnabled)
            }
            prefs.edit().putString("user_limit_$userId", obj.toString()).apply()
            try { dbRef?.child("user_post_limits")?.child(userId)?.setValue(limits.toMap()) } catch (_: Exception) {}
        }
    }

    // --- Page Custom Limits ---
    fun getPageCustomLimits(pageId: String): DailyPostLimits? {
        if (pageId.isBlank()) return null
        val json = prefs.getString("page_limit_$pageId", null) ?: return null
        return try {
            val obj = JSONObject(json)
            DailyPostLimits(
                maxTextPosts = obj.optInt("maxTextPosts", 10),
                maxPhotoPosts = obj.optInt("maxPhotoPosts", 10),
                maxVideoPosts = obj.optInt("maxVideoPosts", 5),
                maxStories = obj.optInt("maxStories", 10),
                isEnabled = obj.optBoolean("isEnabled", true)
            )
        } catch (_: Exception) {
            null
        }
    }

    fun setPageCustomLimits(pageId: String, limits: DailyPostLimits?) {
        if (pageId.isBlank()) return
        if (limits == null) {
            prefs.edit().remove("page_limit_$pageId").apply()
            try { dbRef?.child("page_post_limits")?.child(pageId)?.removeValue() } catch (_: Exception) {}
        } else {
            val obj = JSONObject().apply {
                put("maxTextPosts", limits.maxTextPosts)
                put("maxPhotoPosts", limits.maxPhotoPosts)
                put("maxVideoPosts", limits.maxVideoPosts)
                put("maxStories", limits.maxStories)
                put("isEnabled", limits.isEnabled)
            }
            prefs.edit().putString("page_limit_$pageId", obj.toString()).apply()
            try { dbRef?.child("page_post_limits")?.child(pageId)?.setValue(limits.toMap()) } catch (_: Exception) {}
        }
    }

    fun getEffectiveLimits(targetId: String, isPage: Boolean): DailyPostLimits {
        if (isPage) {
            val custom = getPageCustomLimits(targetId)
            if (custom != null) return custom
        } else {
            val custom = getUserCustomLimits(targetId)
            if (custom != null) return custom
        }
        return _globalLimitsFlow.value
    }

    private fun getTodayDateKey(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    }

    private fun normalizeType(type: String): String {
        return when (type.lowercase(Locale.ROOT)) {
            "photo", "image", "media" -> "photo"
            "video", "reel" -> "video"
            "story" -> "story"
            else -> "text"
        }
    }

    fun getTodayPostCount(targetId: String, postType: String): Int {
        if (targetId.isBlank()) return 0
        val normType = normalizeType(postType)
        val key = "count_${targetId}_${normType}_${getTodayDateKey()}"
        return prefs.getInt(key, 0)
    }

    fun recordPostCreated(userId: String, isPage: Boolean, pageId: String, postType: String) {
        val targetId = if (isPage && pageId.isNotBlank()) pageId else userId
        if (targetId.isBlank()) return
        val normType = normalizeType(postType)
        val key = "count_${targetId}_${normType}_${getTodayDateKey()}"
        val current = prefs.getInt(key, 0)
        prefs.edit().putInt(key, current + 1).apply()
    }

    /**
     * Checks if the user or page is allowed to post today based on post limits.
     * Returns Pair(canPost, errorMessage)
     */
    fun canCreatePost(userId: String, isPage: Boolean, pageId: String, postType: String): Pair<Boolean, String> {
        val appSettings = AppSettingsRepository.getInstance(context)
        val normType = normalizeType(postType)

        // 1. Global Admin Platform Feature Checks
        if (normType == "story" && !appSettings.isStoryCreationEnabled.value) {
            return Pair(false, "অ্যাডমিন সাময়িকভাবে স্টোরি আপলোড বন্ধ রেখেছেন। (Story creation is currently disabled by administrator)")
        }
        if (normType == "video" && !appSettings.isReelsUploadEnabled.value) {
            return Pair(false, "অ্যাডমিন সাময়িকভাবে ভিডিও ও রিলস আপলোড বন্ধ রেখেছেন। (Video/Reel upload is currently disabled by administrator)")
        }
        if (normType != "story" && !appSettings.isPostingEnabled.value) {
            return Pair(false, "অ্যাডমিন সাময়িকভাবে নতুন পোস্ট তৈরি বন্ধ রেখেছেন। (Feed posting is currently disabled by administrator)")
        }

        // 2. Daily Limits Check
        val targetId = if (isPage && pageId.isNotBlank()) pageId else userId
        val limits = getEffectiveLimits(targetId, isPage)

        if (!limits.isEnabled) {
            return Pair(true, "")
        }
        val currentCount = getTodayPostCount(targetId, normType)
        val maxAllowed = when (normType) {
            "photo" -> limits.maxPhotoPosts
            "video" -> limits.maxVideoPosts
            "story" -> limits.maxStories
            else -> limits.maxTextPosts
        }

        if (maxAllowed in 1..currentCount) {
            val typeTitle = when (normType) {
                "photo" -> "ফটো পোস্ট (Photo Post)"
                "video" -> "ভিডিও/রিল পোস্ট (Video/Reel Post)"
                "story" -> "স্টোরি (Story)"
                else -> "টেক্সট পোস্ট (Text Post)"
            }
            val targetTitle = if (isPage) "এই পেজের" else "আপনার"
            val msg = "আজকের জন্য $targetTitle $typeTitle করার লিমিট ($maxAllowed টি) শেষ হয়ে গেছে! অনুগ্রহ করে আগামীকাল আবার চেষ্টা করুন।"
            return Pair(false, msg)
        }

        return Pair(true, "")
    }

    companion object {
        @Volatile
        private var INSTANCE: PostLimitRepository? = null

        fun getInstance(context: Context): PostLimitRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PostLimitRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
