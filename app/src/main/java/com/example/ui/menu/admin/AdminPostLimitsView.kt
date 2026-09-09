package com.example.ui.menu.admin

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PageItem
import com.example.data.model.UserProfile
import com.example.data.repository.DailyPostLimits
import com.example.data.repository.GroupPageRepository
import com.example.data.repository.PostLimitRepository
import com.example.data.repository.UserRepository
import com.example.ui.theme.LocalIsDarkMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPostLimitsView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkMode = LocalIsDarkMode.current
    val postLimitRepo = remember { PostLimitRepository.getInstance(context) }
    val userRepo = remember { UserRepository(context) }
    val groupPageRepo = remember { GroupPageRepository(context) }

    val globalLimits by postLimitRepo.globalLimitsFlow.collectAsState()
    val allUsers by userRepo.getAllUsersFlow().collectAsState(initial = emptyList())
    val allPages by groupPageRepo.pagesFlow.collectAsState()

    val bgScreen = if (isDarkMode) Color(0xFF18191A) else Color(0xFFF0F2F5)
    val bgCard = if (isDarkMode) Color(0xFF242526) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFE4E6EB) else Color(0xFF050505)
    val textSecondary = if (isDarkMode) Color(0xFFB0B3B8) else Color(0xFF65676B)
    val dividerColor = if (isDarkMode) Color(0xFF3E4042) else Color(0xFFE4E6EB)

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Global Limits, 1: User Limits, 2: Page Limits

    // Global limits state
    var isEnabled by remember { mutableStateOf(globalLimits.isEnabled) }
    var textLimit by remember { mutableStateOf(globalLimits.maxTextPosts.toString()) }
    var photoLimit by remember { mutableStateOf(globalLimits.maxPhotoPosts.toString()) }
    var videoLimit by remember { mutableStateOf(globalLimits.maxVideoPosts.toString()) }
    var storyLimit by remember { mutableStateOf(globalLimits.maxStories.toString()) }

    // Dialogs
    var editingUserLimits by remember { mutableStateOf<UserProfile?>(null) }
    var editingPageLimits by remember { mutableStateOf<PageItem?>(null) }

    // User/Page Search
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgScreen,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily Post & Story Limits",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_post_limits_back")) {
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
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = bgCard,
                contentColor = Color(0xFF1877F2),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF1877F2)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Global Limits", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("User Limits (${allUsers.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Page Limits (${allPages.size})", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Global Limits Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Master Toggle
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = bgCard),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isEnabled) Color(0xFF1877F2).copy(alpha = 0.15f) else Color(0xFFE53935).copy(alpha = 0.15f),
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Speed,
                                                contentDescription = null,
                                                tint = if (isEnabled) Color(0xFF1877F2) else Color(0xFFE53935),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Enable Daily Limits",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = textPrimary
                                        )
                                        Text(
                                            text = if (isEnabled) "Restrictions are ACTIVE" else "Post limits DISABLED (Unlimited)",
                                            fontSize = 12.sp,
                                            color = if (isEnabled) Color(0xFF00C853) else Color(0xFFE53935),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Switch(
                                    checked = isEnabled,
                                    onCheckedChange = { isEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF1877F2)
                                    )
                                )
                            }
                        }

                        // Limits Inputs
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = bgCard),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Standard Daily Quotas (প্রতিদিনের লিমিট)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = "প্রতিটি ইউজার ও পেজের জন্য ডিফল্টভাবে দিনে সর্বোচ্চ কয়টি পোস্ট অনুমোদন করবেন তা সেট করুন।",
                                    fontSize = 12.sp,
                                    color = textSecondary,
                                    lineHeight = 16.sp
                                )

                                Divider(color = dividerColor, thickness = 0.5.dp)

                                // 1. Text Posts
                                LimitInputField(
                                    icon = Icons.Default.Article,
                                    iconTint = Color(0xFF1877F2),
                                    title = "Text Posts per Day (টেক্সট পোস্ট)",
                                    value = textLimit,
                                    onValueChange = { textLimit = it }
                                )

                                // 2. Photo Posts
                                LimitInputField(
                                    icon = Icons.Default.Image,
                                    iconTint = Color(0xFF00C853),
                                    title = "Photo Posts per Day (ছবি পোস্ট)",
                                    value = photoLimit,
                                    onValueChange = { photoLimit = it }
                                )

                                // 3. Video Posts
                                LimitInputField(
                                    icon = Icons.Default.VideoLibrary,
                                    iconTint = Color(0xFFE91E63),
                                    title = "Video & Reel Posts per Day (ভিডিও পোস্ট)",
                                    value = videoLimit,
                                    onValueChange = { videoLimit = it }
                                )

                                // 4. Story Posts
                                LimitInputField(
                                    icon = Icons.Default.ViewCarousel,
                                    iconTint = Color(0xFFFF9800),
                                    title = "Stories per Day (স্টোরি লিমিট)",
                                    value = storyLimit,
                                    onValueChange = { storyLimit = it }
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Button(
                                    onClick = {
                                        val newLimits = DailyPostLimits(
                                            maxTextPosts = textLimit.toIntOrNull() ?: 10,
                                            maxPhotoPosts = photoLimit.toIntOrNull() ?: 10,
                                            maxVideoPosts = videoLimit.toIntOrNull() ?: 5,
                                            maxStories = storyLimit.toIntOrNull() ?: 10,
                                            isEnabled = isEnabled
                                        )
                                        postLimitRepo.saveGlobalLimits(newLimits)
                                        Toast.makeText(context, "Global post limits updated successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("save_global_limits_button")
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save Global Settings", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Per-User Limits Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search users by name or email...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val filtered = allUsers.filter {
                            it.fullName.contains(searchQuery, ignoreCase = true) ||
                            it.email.contains(searchQuery, ignoreCase = true)
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtered, key = { it.uid }) { user ->
                                val custom = postLimitRepo.getUserCustomLimits(user.uid)
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = bgCard),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFF1877F2).copy(alpha = 0.15f),
                                                modifier = Modifier.size(40.dp)
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
                                                Text(
                                                    text = user.fullName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = textPrimary
                                                )
                                                Text(
                                                    text = if (custom != null) {
                                                        "Custom: T:${custom.maxTextPosts} P:${custom.maxPhotoPosts} V:${custom.maxVideoPosts} S:${custom.maxStories}"
                                                    } else {
                                                        "Using Global Limits"
                                                    },
                                                    fontSize = 11.sp,
                                                    color = if (custom != null) Color(0xFF00C853) else textSecondary,
                                                    fontWeight = if (custom != null) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { editingUserLimits = user },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(
                                                text = if (custom != null) "Edit" else "Set Limit",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Per-Page Limits Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search pages by name...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val filtered = allPages.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filtered, key = { it.id }) { page ->
                                val custom = postLimitRepo.getPageCustomLimits(page.id)
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = bgCard),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color(0xFFE91E63).copy(alpha = 0.15f),
                                                modifier = Modifier.size(40.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        Icons.Default.Flag,
                                                        contentDescription = null,
                                                        tint = Color(0xFFE91E63),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = page.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = textPrimary
                                                )
                                                Text(
                                                    text = if (custom != null) {
                                                        "Custom: T:${custom.maxTextPosts} P:${custom.maxPhotoPosts} V:${custom.maxVideoPosts} S:${custom.maxStories}"
                                                    } else {
                                                        "Using Global Limits"
                                                    },
                                                    fontSize = 11.sp,
                                                    color = if (custom != null) Color(0xFF00C853) else textSecondary,
                                                    fontWeight = if (custom != null) FontWeight.Bold else FontWeight.Normal
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = { editingPageLimits = page },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(
                                                text = if (custom != null) "Edit" else "Set Limit",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // User Custom Limit Dialog
    if (editingUserLimits != null) {
        val targetUser = editingUserLimits!!
        val currentCustom = postLimitRepo.getUserCustomLimits(targetUser.uid) ?: globalLimits
        CustomLimitEditorDialog(
            title = "Set Limits for ${targetUser.fullName}",
            initialLimits = currentCustom,
            hasCustom = postLimitRepo.getUserCustomLimits(targetUser.uid) != null,
            onDismiss = { editingUserLimits = null },
            onSave = { limits ->
                postLimitRepo.setUserCustomLimits(targetUser.uid, limits)
                editingUserLimits = null
                Toast.makeText(context, "Custom limit updated for ${targetUser.fullName}", Toast.LENGTH_SHORT).show()
            },
            onResetToGlobal = {
                postLimitRepo.setUserCustomLimits(targetUser.uid, null)
                editingUserLimits = null
                Toast.makeText(context, "Reset to global limits", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Page Custom Limit Dialog
    if (editingPageLimits != null) {
        val targetPage = editingPageLimits!!
        val currentCustom = postLimitRepo.getPageCustomLimits(targetPage.id) ?: globalLimits
        CustomLimitEditorDialog(
            title = "Set Limits for Page: ${targetPage.name}",
            initialLimits = currentCustom,
            hasCustom = postLimitRepo.getPageCustomLimits(targetPage.id) != null,
            onDismiss = { editingPageLimits = null },
            onSave = { limits ->
                postLimitRepo.setPageCustomLimits(targetPage.id, limits)
                editingPageLimits = null
                Toast.makeText(context, "Custom limit updated for ${targetPage.name}", Toast.LENGTH_SHORT).show()
            },
            onResetToGlobal = {
                postLimitRepo.setPageCustomLimits(targetPage.id, null)
                editingPageLimits = null
                Toast.makeText(context, "Reset to global limits", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun LimitInputField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.width(90.dp)
        )
    }
}

@Composable
private fun CustomLimitEditorDialog(
    title: String,
    initialLimits: DailyPostLimits,
    hasCustom: Boolean,
    onDismiss: () -> Unit,
    onSave: (DailyPostLimits) -> Unit,
    onResetToGlobal: () -> Unit
) {
    var tLimit by remember { mutableStateOf(initialLimits.maxTextPosts.toString()) }
    var pLimit by remember { mutableStateOf(initialLimits.maxPhotoPosts.toString()) }
    var vLimit by remember { mutableStateOf(initialLimits.maxVideoPosts.toString()) }
    var sLimit by remember { mutableStateOf(initialLimits.maxStories.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("প্রতিদিনের সর্বোচ্চ পোস্ট সংখ্যা দিন:", fontSize = 13.sp)
                LimitInputField(Icons.Default.Article, Color(0xFF1877F2), "Text Posts/day", tLimit) { tLimit = it }
                LimitInputField(Icons.Default.Image, Color(0xFF00C853), "Photo Posts/day", pLimit) { pLimit = it }
                LimitInputField(Icons.Default.VideoLibrary, Color(0xFFE91E63), "Video Posts/day", vLimit) { vLimit = it }
                LimitInputField(Icons.Default.ViewCarousel, Color(0xFFFF9800), "Stories/day", sLimit) { sLimit = it }

                if (hasCustom) {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = onResetToGlobal,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset to Global Limits")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val custom = DailyPostLimits(
                        maxTextPosts = tLimit.toIntOrNull() ?: 10,
                        maxPhotoPosts = pLimit.toIntOrNull() ?: 10,
                        maxVideoPosts = vLimit.toIntOrNull() ?: 5,
                        maxStories = sLimit.toIntOrNull() ?: 10,
                        isEnabled = true
                    )
                    onSave(custom)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
            ) {
                Text("Save Limit", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
