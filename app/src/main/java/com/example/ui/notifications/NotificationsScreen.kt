package com.example.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.NotificationItem
import com.example.data.model.UserProfile
import com.example.data.repository.NotificationRepository
import com.example.data.repository.UserRepository
import com.example.ui.theme.LocalIsDarkMode

@Composable
fun NotificationsScreen(
    currentUserId: String,
    notificationRepository: NotificationRepository,
    userRepository: UserRepository,
    onUserClick: (UserProfile) -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDarkMode = LocalIsDarkMode.current
    val notifications by notificationRepository.getNotificationsFlow(currentUserId).collectAsState(initial = emptyList())
    val allUsers by userRepository.getAllUsersFlow().collectAsState(initial = emptyList())

    val bgColor = if (isDarkMode) Color(0xFF18191A) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFE4E6EB) else Color(0xFF050505)
    val textSecondary = if (isDarkMode) Color(0xFFB0B3B8) else Color(0xFF65676B)
    val dividerColor = if (isDarkMode) Color(0xFF3E4042) else Color(0xFFE4E6EB)
    val emptyIconBg = if (isDarkMode) Color(0xFF242526) else Color(0xFFF0F2F5)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("notifications_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Back button only
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("notifications_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.weight(1f)
                )

                if (notifications.isNotEmpty()) {
                    IconButton(
                        onClick = { notificationRepository.markAllAsRead(currentUserId) },
                        modifier = Modifier.testTag("mark_all_read_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Mark all as read",
                            tint = Color(0xFF1877F2),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            Divider(thickness = 0.5.dp, color = dividerColor)

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = emptyIconBg,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsNone,
                                    contentDescription = null,
                                    tint = textSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Notifications",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Likes, comments and follows on your posts will appear here.",
                            fontSize = 14.sp,
                            color = textSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(notifications, key = { it.id }) { item ->
                        val targetSender = allUsers.find { it.uid == item.senderId } ?: UserProfile(
                            uid = item.senderId,
                            fullName = item.senderName,
                            profilePictureUrl = item.senderAvatarUrl
                        )

                        NotificationRowItem(
                            notification = item,
                            isDarkMode = isDarkMode,
                            onClick = { onUserClick(targetSender) },
                            onAvatarClick = { onUserClick(targetSender) }
                        )
                        Divider(thickness = 0.5.dp, color = dividerColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationRowItem(
    notification: NotificationItem,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    val backgroundColor = if (!notification.isRead) {
        if (isDarkMode) Color(0xFF1E2A3A) else Color(0xFFEBF5FF)
    } else {
        if (isDarkMode) Color(0xFF18191A) else Color.White
    }
    val textPrimary = if (isDarkMode) Color(0xFFE4E6EB) else Color(0xFF050505)
    val textSecondary = if (isDarkMode) Color(0xFFB0B3B8) else Color(0xFF65676B)
    val timeAgo = remember(notification.timestamp) { formatTimeAgo(notification.timestamp) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("notification_item_${notification.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Type Icon Badge
        Box(modifier = Modifier.size(54.dp)) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAvatarClick),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                if (notification.senderAvatarUrl.isNotBlank()) {
                    AsyncImage(
                        model = notification.senderAvatarUrl,
                        contentDescription = notification.senderName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = notification.senderName.firstOrNull()?.uppercase() ?: "U",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Action Badge Icon
            val (badgeIcon, badgeBg) = when (notification.type) {
                "like" -> Pair(Icons.Filled.Favorite, Color(0xFFFA383E))
                "comment" -> Pair(Icons.Filled.ChatBubble, Color(0xFF1877F2))
                "follow" -> Pair(Icons.Filled.PersonAdd, Color(0xFF2E7D32))
                else -> Pair(Icons.Filled.Favorite, Color(0xFFFA383E))
            }

            Surface(
                shape = CircleShape,
                color = badgeBg,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Text & Timestamp
        Column(modifier = Modifier.weight(1f)) {
            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 14.sp)) {
                    append(notification.senderName)
                }
                append(" ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = textPrimary, fontSize = 14.sp)) {
                    append(notification.content)
                }
            }

            Text(
                text = annotatedString,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = timeAgo,
                fontSize = 12.sp,
                color = if (!notification.isRead) Color(0xFF1877F2) else textSecondary,
                fontWeight = if (!notification.isRead) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF1877F2), CircleShape)
            )
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    if (diff < 0) return "Just now"
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> "${days / 7}w ago"
    }
}
