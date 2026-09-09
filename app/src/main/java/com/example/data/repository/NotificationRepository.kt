package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.NotificationItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class NotificationRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("frndom_notifications_prefs", Context.MODE_PRIVATE)

    private val dbRef: DatabaseReference? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseDatabase.getInstance().getReference("notifications")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w("NotificationRepo", "FirebaseDatabase not initialized: ${e.message}")
            null
        }
    }

    private val settingsRef: DatabaseReference? by lazy {
        try {
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseDatabase.getInstance().getReference("system_settings").child("notifications")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private val _engagementNotificationsEnabled = MutableStateFlow(
        prefs.getBoolean("engagement_notifications_enabled", true)
    )
    val engagementNotificationsEnabledFlow: StateFlow<Boolean> = _engagementNotificationsEnabled.asStateFlow()

    init {
        listenToEngagementSettings()
    }

    private fun listenToEngagementSettings() {
        try {
            settingsRef?.child("engagement_enabled")?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val enabled = snapshot.getValue(Boolean::class.java) ?: true
                    _engagementNotificationsEnabled.value = enabled
                    prefs.edit().putBoolean("engagement_notifications_enabled", enabled).apply()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        } catch (e: Exception) {
            Log.w("NotificationRepo", "Failed to listen to engagement settings: ${e.message}")
        }
    }

    fun isEngagementNotificationsEnabled(): Boolean {
        return _engagementNotificationsEnabled.value
    }

    fun setEngagementNotificationsEnabled(enabled: Boolean) {
        _engagementNotificationsEnabled.value = enabled
        prefs.edit().putBoolean("engagement_notifications_enabled", enabled).apply()
        try {
            settingsRef?.child("engagement_enabled")?.setValue(enabled)
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error setting engagement notifications: ${e.message}")
        }
    }

    fun getLocalNotifications(recipientId: String): List<NotificationItem> {
        if (recipientId.isBlank()) return emptyList()
        val json = prefs.getString("notifications_$recipientId", null) ?: return emptyList()
        val list = mutableListOf<NotificationItem>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    NotificationItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        recipientId = obj.optString("recipientId", recipientId),
                        senderId = obj.optString("senderId", ""),
                        senderName = obj.optString("senderName", "User"),
                        senderAvatarUrl = obj.optString("senderAvatarUrl", ""),
                        postId = obj.optString("postId", ""),
                        type = obj.optString("type", "like"),
                        title = obj.optString("title", ""),
                        content = obj.optString("content", ""),
                        imageUrl = obj.optString("imageUrl", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isRead = obj.optBoolean("isRead", false)
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error parsing cached notifications: ${e.message}")
        }
        return list.sortedByDescending { it.timestamp }
    }

    fun saveLocalNotifications(recipientId: String, notifications: List<NotificationItem>) {
        if (recipientId.isBlank()) return
        try {
            val arr = JSONArray()
            for (n in notifications) {
                val obj = JSONObject().apply {
                    put("id", n.id)
                    put("recipientId", n.recipientId)
                    put("senderId", n.senderId)
                    put("senderName", n.senderName)
                    put("senderAvatarUrl", n.senderAvatarUrl)
                    put("postId", n.postId)
                    put("type", n.type)
                    put("title", n.title)
                    put("content", n.content)
                    put("imageUrl", n.imageUrl)
                    put("timestamp", n.timestamp)
                    put("isRead", n.isRead)
                }
                arr.put(obj)
            }
            prefs.edit().putString("notifications_$recipientId", arr.toString()).apply()
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Error saving cached notifications: ${e.message}")
        }
    }

    fun addNotification(notification: NotificationItem) {
        // If engagement notifications are disabled and this is like/comment/follow/post, suppress it!
        val isEngagementType = notification.type in listOf("like", "comment", "follow", "post")
        if (isEngagementType && !isEngagementNotificationsEnabled()) {
            Log.d("NotificationRepo", "Engagement notification suppressed by Admin setting")
            return
        }

        val targetRecipient = notification.recipientId.ifBlank { "global" }
        val newNotif = if (notification.id.isBlank()) notification.copy(id = UUID.randomUUID().toString(), recipientId = targetRecipient) else notification.copy(recipientId = targetRecipient)

        // 1. Immediately update local storage
        val currentList = getLocalNotifications(targetRecipient).toMutableList()
        currentList.removeAll { it.id == newNotif.id }
        currentList.add(0, newNotif)
        saveLocalNotifications(targetRecipient, currentList)

        // 2. Persist to Firebase Realtime Database
        try {
            dbRef?.child(targetRecipient)?.child(newNotif.id)?.setValue(newNotif.toMap())
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Firebase addNotification error: ${e.message}")
        }
    }

    fun sendAdminNotification(
        title: String,
        content: String,
        imageUrl: String = "",
        recipientId: String = "global",
        senderName: String = "System Admin"
    ) {
        val item = NotificationItem(
            id = UUID.randomUUID().toString(),
            recipientId = recipientId.ifBlank { "global" },
            senderId = "admin_broadcast",
            senderName = senderName,
            senderAvatarUrl = "",
            postId = "",
            type = "admin_announcement",
            title = title.trim(),
            content = content.trim(),
            imageUrl = imageUrl.trim(),
            timestamp = System.currentTimeMillis(),
            isRead = false
        )
        addNotification(item)
    }

    fun getNotificationsFlow(recipientId: String): Flow<List<NotificationItem>> = callbackFlow {
        val target = recipientId.ifBlank { "global" }
        // 1. Send cached notifications immediately
        val initialUser = getLocalNotifications(target)
        val initialGlobal = if (target != "global") getLocalNotifications("global") else emptyList()
        val initialMerged = (initialUser + initialGlobal).distinctBy { it.id }.sortedByDescending { it.timestamp }
        trySend(initialMerged)

        val recipientRef = try {
            dbRef?.child(target)
        } catch (e: Exception) {
            null
        }

        val globalRef = if (target != "global") {
            try { dbRef?.child("global") } catch (e: Exception) { null }
        } else null

        var userList = initialUser
        var globalList = initialGlobal

        fun emitCombined() {
            val combined = (userList + globalList).distinctBy { it.id }.sortedByDescending { it.timestamp }
            trySend(combined)
        }

        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fbList = mutableListOf<NotificationItem>()
                for (child in snapshot.children) {
                    val notif = parseNotificationItem(child)
                    if (notif != null) {
                        fbList.add(notif)
                    }
                }

                val mergedMap = LinkedHashMap<String, NotificationItem>()
                getLocalNotifications(target).forEach { mergedMap[it.id] = it }
                fbList.forEach { mergedMap[it.id] = it }
                userList = mergedMap.values.toList().sortedByDescending { it.timestamp }
                saveLocalNotifications(target, userList)
                emitCombined()
            }

            override fun onCancelled(error: DatabaseError) {
                userList = getLocalNotifications(target)
                emitCombined()
            }
        }

        val globalListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fbList = mutableListOf<NotificationItem>()
                for (child in snapshot.children) {
                    val notif = parseNotificationItem(child)
                    if (notif != null) {
                        fbList.add(notif)
                    }
                }
                val mergedMap = LinkedHashMap<String, NotificationItem>()
                getLocalNotifications("global").forEach { mergedMap[it.id] = it }
                fbList.forEach { mergedMap[it.id] = it }
                globalList = mergedMap.values.toList().sortedByDescending { it.timestamp }
                saveLocalNotifications("global", globalList)
                emitCombined()
            }

            override fun onCancelled(error: DatabaseError) {
                globalList = getLocalNotifications("global")
                emitCombined()
            }
        }

        recipientRef?.addValueEventListener(userListener)
        globalRef?.addValueEventListener(globalListener)

        awaitClose {
            recipientRef?.removeEventListener(userListener)
            globalRef?.removeEventListener(globalListener)
        }
    }

    private fun parseNotificationItem(child: DataSnapshot): NotificationItem? {
        return try {
            val id = child.child("id").getValue(String::class.java) ?: child.key ?: ""
            if (id.isBlank()) return null
            val recipientId = child.child("recipientId").getValue(String::class.java) ?: ""
            val senderId = child.child("senderId").getValue(String::class.java) ?: ""
            val senderName = child.child("senderName").getValue(String::class.java) ?: "User"
            val senderAvatarUrl = child.child("senderAvatarUrl").getValue(String::class.java) ?: ""
            val postId = child.child("postId").getValue(String::class.java) ?: ""
            val type = child.child("type").getValue(String::class.java) ?: "like"
            val title = child.child("title").getValue(String::class.java) ?: ""
            val content = child.child("content").getValue(String::class.java) ?: ""
            val imageUrl = child.child("imageUrl").getValue(String::class.java) ?: ""
            val timestamp = child.child("timestamp").getValue(Long::class.java) ?: System.currentTimeMillis()
            val isRead = child.child("isRead").getValue(Boolean::class.java) ?: false

            NotificationItem(
                id = id,
                recipientId = recipientId,
                senderId = senderId,
                senderName = senderName,
                senderAvatarUrl = senderAvatarUrl,
                postId = postId,
                type = type,
                title = title,
                content = content,
                imageUrl = imageUrl,
                timestamp = timestamp,
                isRead = isRead
            )
        } catch (_: Exception) {
            null
        }
    }

    fun markAllAsRead(recipientId: String) {
        val target = recipientId.ifBlank { "global" }
        val list = getLocalNotifications(target).map { it.copy(isRead = true) }
        saveLocalNotifications(target, list)
        try {
            list.forEach { n ->
                dbRef?.child(target)?.child(n.id)?.child("isRead")?.setValue(true)
            }
        } catch (e: Exception) {
            Log.e("NotificationRepo", "Firebase markAllAsRead error: ${e.message}")
        }
    }

    fun removeNotification(recipientId: String, postId: String, type: String, senderId: String = "") {
        val target = recipientId.ifBlank { "global" }
        val current = getLocalNotifications(target).toMutableList()
        val toRemove = current.filter {
            it.postId == postId && it.type == type && (senderId.isBlank() || it.senderId == senderId)
        }
        if (toRemove.isNotEmpty()) {
            current.removeAll(toRemove)
            saveLocalNotifications(target, current)
            try {
                toRemove.forEach { n ->
                    dbRef?.child(target)?.child(n.id)?.removeValue()
                }
            } catch (e: Exception) {
                Log.e("NotificationRepo", "Firebase removeNotification error: ${e.message}")
            }
        }
    }
}
