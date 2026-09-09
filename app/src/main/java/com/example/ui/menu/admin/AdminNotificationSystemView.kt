package com.example.ui.menu.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserProfile
import com.example.data.repository.NotificationRepository
import com.example.data.repository.UserRepository
import com.example.ui.theme.LocalIsDarkMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationSystemView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkMode = LocalIsDarkMode.current
    val notifRepo = remember { NotificationRepository(context) }
    val userRepo = remember { UserRepository(context) }

    val allUsers by userRepo.getAllUsersFlow().collectAsState(initial = emptyList())
    val engagementEnabled by notifRepo.engagementNotificationsEnabledFlow.collectAsState()

    val bgScreen = if (isDarkMode) Color(0xFF18191A) else Color(0xFFF0F2F5)
    val bgCard = if (isDarkMode) Color(0xFF242526) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFE4E6EB) else Color(0xFF050505)
    val textSecondary = if (isDarkMode) Color(0xFFB0B3B8) else Color(0xFF65676B)
    val dividerColor = if (isDarkMode) Color(0xFF3E4042) else Color(0xFFE4E6EB)

    // Form states
    var targetMode by remember { mutableStateOf("ALL") } // "ALL" or "SINGLE"
    var selectedUser by remember { mutableStateOf<UserProfile?>(null) }
    var showUserPicker by remember { mutableStateOf(false) }
    var userSearchQuery by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgScreen,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notification System",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_notif_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgCard,
                    titleContentColor = textPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. User Engagement Notifications Toggle Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (engagementEnabled) Color(0xFF1877F2).copy(alpha = 0.15f) else Color(0xFFE53935).copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (engagementEnabled) Icons.Default.ThumbUp else Icons.Default.NotificationsOff,
                                        contentDescription = null,
                                        tint = if (engagementEnabled) Color(0xFF1877F2) else Color(0xFFE53935),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Like & Comment Notifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = if (engagementEnabled) "Currently Active (চালু আছে)" else "Disabled globally (বন্ধ আছে)",
                                    fontSize = 12.sp,
                                    color = if (engagementEnabled) Color(0xFF00C853) else Color(0xFFE53935),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Switch(
                            checked = engagementEnabled,
                            onCheckedChange = { isChecked ->
                                notifRepo.setEngagementNotificationsEnabled(isChecked)
                                Toast.makeText(
                                    context,
                                    if (isChecked) "Like & Comment notifications turned ON" else "Like & Comment notifications turned OFF",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF1877F2)
                            ),
                            modifier = Modifier.testTag("engagement_notif_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "লাইক ও কমেন্টের নোটিফিকেশন বন্ধ রাখলে ব্যবহারকারীরা লাইক ও কমেন্টের জন্য নতুন নোটিফিকেশন পাবেন না।",
                        fontSize = 12.sp,
                        color = textSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            // 2. Broadcast / Direct Notification Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = bgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Color(0xFF1877F2),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Send Push Announcement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Select Recipient (কার কাছে পাঠাবেন):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    // Recipient Mode Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (targetMode == "ALL") Color(0xFF1877F2) else if (isDarkMode) Color(0xFF3A3B3C) else Color(0xFFE4E6EB),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { targetMode = "ALL" }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.People,
                                    contentDescription = null,
                                    tint = if (targetMode == "ALL") Color.White else textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "All Users (${allUsers.size})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (targetMode == "ALL") Color.White else textPrimary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (targetMode == "SINGLE") Color(0xFF1877F2) else if (isDarkMode) Color(0xFF3A3B3C) else Color(0xFFE4E6EB),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    targetMode = "SINGLE"
                                    if (selectedUser == null && allUsers.isNotEmpty()) {
                                        showUserPicker = true
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (targetMode == "SINGLE") Color.White else textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Specific User",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (targetMode == "SINGLE") Color.White else textPrimary
                                )
                            }
                        }
                    }

                    // If Single User Selected
                    if (targetMode == "SINGLE") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDarkMode) Color(0xFF3A3B3C) else Color(0xFFF0F2F5),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showUserPicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (selectedUser != null) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFF1877F2).copy(alpha = 0.2f),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = selectedUser?.fullName?.firstOrNull()?.uppercase() ?: "U",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF1877F2)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = selectedUser?.fullName ?: "Unknown",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = textPrimary
                                            )
                                            Text(
                                                text = "UID: ${selectedUser?.uid?.take(10)}...",
                                                fontSize = 11.sp,
                                                color = textSecondary
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Click to select a target user...",
                                            fontSize = 13.sp,
                                            color = Color(0xFF1877F2),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Text(
                                    text = if (selectedUser == null) "Select" else "Change",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1877F2),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = dividerColor, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Title Field
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Notification Title (টাইটেল)") },
                        placeholder = { Text("e.g. নতুন আপডেট এসেছে! / গুরুত্বপূর্ণ নোটিশ") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_notif_title_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description Field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Notification Content (ডেসক্রিপশন)") },
                        placeholder = { Text("বিস্তারিত মেসেজ লিখুন...") },
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_notif_desc_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Image URL Field
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL (ছবির লিংক - ঐচ্ছিক)") },
                        placeholder = { Text("https://example.com/banner.jpg") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_notif_image_input")
                    )

                    // Image Preview
                    if (imageUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Preview",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Send Button
                    val canSend = title.isNotBlank() && description.isNotBlank() && (targetMode == "ALL" || selectedUser != null)
                    Button(
                        onClick = {
                            isSending = true
                            val targetRecipientId = if (targetMode == "ALL") "global" else selectedUser!!.uid
                            notifRepo.sendAdminNotification(
                                title = title,
                                content = description,
                                imageUrl = imageUrl,
                                recipientId = targetRecipientId,
                                senderName = "Admin Announcement"
                            )
                            isSending = false
                            Toast.makeText(
                                context,
                                if (targetMode == "ALL") "Broadcast notification sent to all users!" else "Notification sent to ${selectedUser?.fullName}!",
                                Toast.LENGTH_LONG
                            ).show()
                            title = ""
                            description = ""
                            imageUrl = ""
                        },
                        enabled = canSend && !isSending,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1877F2),
                            disabledContainerColor = Color(0xFF1877F2).copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("send_admin_notif_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (targetMode == "ALL") "Send to All Users (সকলকে পাঠান)" else "Send to ${selectedUser?.fullName ?: "User"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // User Picker Dialog
    if (showUserPicker) {
        AlertDialog(
            onDismissRequest = { showUserPicker = false },
            title = { Text("Select User", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.height(400.dp)) {
                    OutlinedTextField(
                        value = userSearchQuery,
                        onValueChange = { userSearchQuery = it },
                        placeholder = { Text("Search by name or email...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredUsers = allUsers.filter {
                        it.fullName.contains(userSearchQuery, ignoreCase = true) ||
                        it.email.contains(userSearchQuery, ignoreCase = true) ||
                        it.uid.contains(userSearchQuery, ignoreCase = true)
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredUsers, key = { it.uid }) { user ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedUser?.uid == user.uid) Color(0xFF1877F2).copy(alpha = 0.15f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedUser = user
                                        showUserPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF1877F2).copy(alpha = 0.2f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = user.fullName.firstOrNull()?.uppercase() ?: "U",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1877F2)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = user.fullName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        Text(text = user.email.ifBlank { user.uid }, fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showUserPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
}
