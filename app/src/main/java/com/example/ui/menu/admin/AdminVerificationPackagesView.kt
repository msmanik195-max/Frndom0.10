package com.example.ui.menu.admin

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.VerificationPlanRepository
import com.example.ui.theme.LocalIsDarkMode
import com.example.ui.verification.VerificationPlan
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVerificationPackagesView(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDarkMode = LocalIsDarkMode.current
    val repo = remember { VerificationPlanRepository.getInstance(context) }
    val plans by repo.plansFlow.collectAsState()

    val bgScreen = if (isDarkMode) Color(0xFF18191A) else Color(0xFFF0F2F5)
    val bgCard = if (isDarkMode) Color(0xFF242526) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFE4E6EB) else Color(0xFF050505)
    val textSecondary = if (isDarkMode) Color(0xFFB0B3B8) else Color(0xFF65676B)
    val dividerColor = if (isDarkMode) Color(0xFF3E4042) else Color(0xFFE4E6EB)

    var editingPlan by remember { mutableStateOf<VerificationPlan?>(null) }
    var isAddingNew by remember { mutableStateOf(false) }
    var planToDelete by remember { mutableStateOf<VerificationPlan?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = bgScreen,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Verification Packages",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textPrimary
                        )
                        Text(
                            text = "${plans.size} Active Package${if (plans.size != 1) "s" else ""}",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_packages_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Defaults",
                            tint = textSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgCard,
                    titleContentColor = textPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isAddingNew = true
                    editingPlan = VerificationPlan(
                        id = "plan_${UUID.randomUUID().toString().take(8)}",
                        title = "1 Month Pro",
                        durationDays = 30,
                        durationText = "30 Days Validity",
                        price = 199.0,
                        tag = "Popular",
                        description = "Verified green badge, trust profile & priority features",
                        isFree = false
                    )
                },
                containerColor = Color(0xFF00C853),
                contentColor = Color.White,
                modifier = Modifier.testTag("add_package_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Package")
            }
        }
    ) { padding ->
        if (plans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF00C853).copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Packages Created Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Click the '+' button below to add your first verification package.",
                        fontSize = 14.sp,
                        color = textSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Header Banner
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = bgCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF00C853).copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = Color(0xFF00C853),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Unlimited Verification Plans",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textPrimary
                                )
                                Text(
                                    text = "Manage flexible durations (7d, 15d, 1m, 3m, 6m, 1y, 2y) & prices for user badge subscriptions.",
                                    fontSize = 12.sp,
                                    color = textSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                items(plans, key = { it.id }) { plan ->
                    AdminPlanCard(
                        plan = plan,
                        isDarkMode = isDarkMode,
                        onEdit = {
                            isAddingNew = false
                            editingPlan = plan
                        },
                        onDelete = {
                            planToDelete = plan
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    // Edit/Add Package Dialog
    if (editingPlan != null) {
        PackageEditorDialog(
            initialPlan = editingPlan!!,
            isNew = isAddingNew,
            isDarkMode = isDarkMode,
            onDismiss = { editingPlan = null },
            onSave = { savedPlan ->
                repo.addOrUpdatePlan(savedPlan)
                editingPlan = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (planToDelete != null) {
        AlertDialog(
            onDismissRequest = { planToDelete = null },
            title = { Text("Delete Verification Package") },
            text = { Text("Are you sure you want to delete '${planToDelete?.title}'? Users will no longer be able to purchase or view this package.") },
            confirmButton = {
                Button(
                    onClick = {
                        planToDelete?.let { repo.deletePlan(it.id) }
                        planToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { planToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Defaults Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Default Packages") },
            text = { Text("Are you sure you want to reset all verification packages to default tiers (Free, 1 Month, 6 Months, 1 Year)? Custom packages will be replaced.") },
            confirmButton = {
                Button(
                    onClick = {
                        repo.resetToDefaults()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                ) {
                    Text("Reset Defaults", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AdminPlanCard(
    plan: VerificationPlan,
    isDarkMode: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val bgCard = if (isDarkMode) Color(0xFF242526) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFE4E6EB) else Color(0xFF050505)
    val textSecondary = if (isDarkMode) Color(0xFFB0B3B8) else Color(0xFF65676B)
    val dividerColor = if (isDarkMode) Color(0xFF3E4042) else Color(0xFFE4E6EB)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("admin_plan_card_${plan.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Title, Tag & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!plan.tag.isNullOrBlank()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (plan.isFree) Color(0xFF00C853).copy(alpha = 0.15f) else Color(0xFF1877F2).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = plan.tag,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (plan.isFree) Color(0xFF00C853) else Color(0xFF1877F2),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plan.durationText.ifBlank { "${plan.durationDays} Days" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF00C853)
                    )
                }

                // Price display
                Column(horizontalAlignment = Alignment.End) {
                    if (plan.isFree || plan.price <= 0.0) {
                        Text(
                            text = "FREE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00C853)
                        )
                    } else {
                        Text(
                            text = "৳${plan.price.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                        Text(
                            text = "BDT",
                            fontSize = 11.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (plan.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = plan.description,
                    fontSize = 13.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = dividerColor, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                    modifier = Modifier.height(36.dp).testTag("delete_plan_${plan.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    modifier = Modifier.height(36.dp).testTag("edit_plan_${plan.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Plan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun PackageEditorDialog(
    initialPlan: VerificationPlan,
    isNew: Boolean,
    isDarkMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (VerificationPlan) -> Unit
) {
    var title by remember { mutableStateOf(initialPlan.title) }
    var durationDays by remember { mutableStateOf(initialPlan.durationDays) }
    var priceText by remember { mutableStateOf(if (initialPlan.price <= 0.0) "0" else initialPlan.price.toInt().toString()) }
    var tag by remember { mutableStateOf(initialPlan.tag ?: "") }
    var description by remember { mutableStateOf(initialPlan.description) }
    var isCustomDuration by remember { mutableStateOf(false) }
    var customDurationText by remember { mutableStateOf(initialPlan.durationDays.toString()) }

    // Pre-set duration options as requested: 7 days, 15 days, 1 month (30), 3 months (90), 6 months (180), 1 year (365), 2 years (730)
    val durationPresets = listOf(
        Pair("7 Days", 7),
        Pair("15 Days", 15),
        Pair("1 Month", 30),
        Pair("3 Months", 90),
        Pair("6 Months", 180),
        Pair("1 Year", 365),
        Pair("2 Years", 730)
    )

    val bgCard = if (isDarkMode) Color(0xFF242526) else Color.White
    val textPrimary = if (isDarkMode) Color(0xFFE4E6EB) else Color(0xFF050505)
    val textSecondary = if (isDarkMode) Color(0xFFB0B3B8) else Color(0xFF65676B)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "Add Verification Package" else "Edit Verification Package",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Package Name / Title") },
                    placeholder = { Text("e.g. 1 Month Pro, Annual VIP") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_plan_title")
                )

                // Duration Selector
                Text(
                    text = "Select Package Duration:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Grid/Row of Preset Chips
                    val rows = durationPresets.chunked(3)
                    rows.forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowItems.forEach { preset ->
                                val isSelected = !isCustomDuration && durationDays == preset.second
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) Color(0xFF00C853) else if (isDarkMode) Color(0xFF3A3B3C) else Color(0xFFE4E6EB),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            isCustomDuration = false
                                            durationDays = preset.second
                                            if (title.isBlank() || title.contains("Month") || title.contains("Days") || title.contains("Year")) {
                                                title = preset.first
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = preset.first,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else textPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Custom Days Chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCustomDuration) Color(0xFF00C853) else if (isDarkMode) Color(0xFF3A3B3C) else Color(0xFFE4E6EB),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isCustomDuration = true }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Custom Days (কাস্টম দিন)",
                                fontSize = 12.sp,
                                fontWeight = if (isCustomDuration) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCustomDuration) Color.White else textPrimary
                            )
                        }
                    }
                }

                if (isCustomDuration) {
                    OutlinedTextField(
                        value = customDurationText,
                        onValueChange = {
                            customDurationText = it.filter { char -> char.isDigit() }
                            val parsed = customDurationText.toIntOrNull()
                            if (parsed != null && parsed > 0) {
                                durationDays = parsed
                            }
                        },
                        label = { Text("Number of Days (দিন)") },
                        placeholder = { Text("e.g. 45, 100") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_custom_days")
                    )
                }

                // Price
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Price in BDT (৳) - Enter 0 for Free") },
                    placeholder = { Text("e.g. 199, 499, 0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_plan_price")
                )

                // Tag / Badge
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Promo Tag (Optional)") },
                    placeholder = { Text("e.g. Popular, Best Value, 20% Off") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_plan_tag")
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Perks") },
                    placeholder = { Text("e.g. Instant green checkmark badge & verified status") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("input_plan_desc")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalDays = if (isCustomDuration) {
                        customDurationText.toIntOrNull() ?: durationDays
                    } else durationDays

                    val priceVal = priceText.toDoubleOrNull() ?: 0.0
                    val durationLabel = when (finalDays) {
                        7 -> "7 Days Validity"
                        15 -> "15 Days Validity"
                        30 -> "30 Days (1 Month)"
                        90 -> "90 Days (3 Months)"
                        180 -> "180 Days (6 Months)"
                        365 -> "365 Days (1 Year)"
                        730 -> "730 Days (2 Years)"
                        else -> "$finalDays Days Validity"
                    }

                    val updated = initialPlan.copy(
                        title = title.ifBlank { "Verification Package" },
                        durationDays = finalDays,
                        durationText = durationLabel,
                        price = priceVal,
                        tag = tag.ifBlank { null },
                        description = description.ifBlank { "Green verification badge & verified profile features" },
                        isFree = priceVal <= 0.0
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                modifier = Modifier.testTag("save_package_button")
            ) {
                Text("Save Package", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
