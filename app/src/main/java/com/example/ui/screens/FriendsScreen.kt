package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.FinancialAccount
import com.example.data.model.Friend
import com.example.domain.AccountingEngine
import com.example.ui.DashboardUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FriendsScreen(
    state: DashboardUiState,
    onAddFriend: (name: String, nickname: String?, contact: String?, notes: String?) -> Unit,
    onRecordRepaymentFromFriend: (date: Long, amountPaise: Long, friendId: Long, accountId: Long, notes: String?) -> Unit,
    onRecordRepaymentToFriend: (date: Long, amountPaise: Long, friendId: Long, accountId: Long, notes: String?) -> Unit,
    onOpenOpeningBalances: () -> Unit
) {
    var showAddFriendDialog by remember { mutableStateOf(false) }
    var selectedFriendForRepayment by remember { mutableStateOf<Friend?>(null) }
    var repaymentType by remember { mutableStateOf("RECEIVED") } // RECEIVED (friend pays arpita) or PAID (arpita pays friend)
    var repaymentAmountText by remember { mutableStateOf("") }
    var repaymentAccountId by remember { mutableStateOf<Long?>(state.accounts.firstOrNull()?.id) }
    var repaymentDateMillis by remember { androidx.compose.runtime.mutableLongStateOf(System.currentTimeMillis()) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val sdf = remember { java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.US) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("friends_screen"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Friends & Shared Splits",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Track who owes what without awkward conversations",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "NET SPLIT POSITION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = AccountingEngine.formatPaise(state.netFriendsPositionPaise),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Receivables (Owed to you): ${AccountingEngine.formatPaise(state.totalReceivablesPaise)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Payables (You owe): ${AccountingEngine.formatPaise(state.totalPayablesPaise)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Opening Balances Quick Action
            item {
                OutlinedButton(
                    onClick = onOpenOpeningBalances,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("btn_friends_opening_balances")
                ) {
                    Icon(androidx.compose.material.icons.Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enter Opening Balances / Initial Friend Dues")
                }
            }

            // Friends List
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Friends Register (${state.friends.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (state.friends.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text("👥", style = MaterialTheme.typography.displaySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No friends in register yet", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text("Tap the '+' button to add friends and split shared purchases easily", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                items(state.friends) { friend ->
                    FriendCardItem(
                        friend = friend,
                        onSettleUpClick = {
                            selectedFriendForRepayment = friend
                            if (friend.totalReceivablePaise > 0L) {
                                repaymentType = "RECEIVED"
                                repaymentAmountText = (friend.totalReceivablePaise / 100.0).toString()
                            } else if (friend.totalPayablePaise > 0L) {
                                repaymentType = "PAID"
                                repaymentAmountText = (friend.totalPayablePaise / 100.0).toString()
                            } else {
                                repaymentType = "RECEIVED"
                                repaymentAmountText = ""
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { showAddFriendDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_friend")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Friend")
        }
    }

    // Add Friend Dialog
    if (showAddFriendDialog) {
        var friendName by remember { mutableStateOf("") }
        var friendNickname by remember { mutableStateOf("") }
        var friendContact by remember { mutableStateOf("") }
        var friendNotes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddFriendDialog = false },
            title = { Text("Add Friend to Register") },
            text = {
                Column {
                    OutlinedTextField(
                        value = friendName,
                        onValueChange = { friendName = it },
                        label = { Text("Full Name *") },
                        placeholder = { Text("e.g. Alex Sharma") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_friend_name")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = friendNickname,
                        onValueChange = { friendNickname = it },
                        label = { Text("Nickname (Optional)") },
                        placeholder = { Text("e.g. Lex") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = friendNotes,
                        onValueChange = { friendNotes = it },
                        label = { Text("Notes (Optional)") },
                        placeholder = { Text("e.g. School friend") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (friendName.isNotBlank()) {
                            onAddFriend(
                                friendName.trim(),
                                friendNickname.ifBlank { null },
                                friendContact.ifBlank { null },
                                friendNotes.ifBlank { null }
                            )
                            showAddFriendDialog = false
                        }
                    },
                    enabled = friendName.isNotBlank()
                ) {
                    Text("Add Friend")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFriendDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Settle Up / Record Repayment Dialog
    if (selectedFriendForRepayment != null) {
        val friend = selectedFriendForRepayment!!

        AlertDialog(
            onDismissRequest = { selectedFriendForRepayment = null },
            title = { Text("Record Settlement / Repayment") },
            text = {
                Column {
                    Text(
                        text = "Friend: ${friend.name}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = repaymentType == "RECEIVED",
                            onClick = { repaymentType = "RECEIVED" },
                            label = { Text("${friend.name} pays Arpita") }
                        )
                        FilterChip(
                            selected = repaymentType == "PAID",
                            onClick = { repaymentType = "PAID" },
                            label = { Text("Arpita pays ${friend.name}") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Date Picker
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Date: ${sdf.format(Date(repaymentDateMillis))}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            TextButton(onClick = {
                                val cal = java.util.Calendar.getInstance().apply { timeInMillis = repaymentDateMillis }
                                android.app.DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val newCal = java.util.Calendar.getInstance().apply {
                                            set(java.util.Calendar.YEAR, year)
                                            set(java.util.Calendar.MONTH, month)
                                            set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth)
                                        }
                                        repaymentDateMillis = newCal.timeInMillis
                                    },
                                    cal.get(java.util.Calendar.YEAR),
                                    cal.get(java.util.Calendar.MONTH),
                                    cal.get(java.util.Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Text("Change Date")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = repaymentAmountText,
                        onValueChange = { repaymentAmountText = it },
                        label = { Text("Amount (₹)") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Through Account:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.accounts.forEach { acc ->
                            FilterChip(
                                selected = repaymentAccountId == acc.id,
                                onClick = { repaymentAccountId = acc.id },
                                label = { Text(acc.name) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Double-entry note: Repayments reduce receivables/payables directly without creating artificial income or expenses.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountPaise = AccountingEngine.parseRupeesToPaise(repaymentAmountText)
                        val accId = repaymentAccountId ?: state.accounts.firstOrNull()?.id ?: 1L
                        if (amountPaise > 0L) {
                            if (repaymentType == "RECEIVED") {
                                onRecordRepaymentFromFriend(repaymentDateMillis, amountPaise, friend.id, accId, null)
                            } else {
                                onRecordRepaymentToFriend(repaymentDateMillis, amountPaise, friend.id, accId, null)
                            }
                            selectedFriendForRepayment = null
                        }
                    },
                    enabled = repaymentAmountText.isNotBlank()
                ) {
                    Text("Post Settlement")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedFriendForRepayment = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FriendCardItem(
    friend: Friend,
    onSettleUpClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("friend_card_${friend.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = friend.name.take(1).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = friend.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (friend.nickname != null) {
                            Text(
                                text = "\"${friend.nickname}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Settle Up Action Button
                OutlinedButton(
                    onClick = onSettleUpClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_settle_up_${friend.id}")
                ) {
                    Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Settle Up")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clear Human-Readable Statement
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when {
                    friend.totalReceivablePaise > 0L -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    friend.totalPayablePaise > 0L -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    val statusText = when {
                        friend.totalReceivablePaise > 0L && friend.totalPayablePaise == 0L ->
                            "${friend.name} owes you ${AccountingEngine.formatPaise(friend.totalReceivablePaise)}"
                        friend.totalPayablePaise > 0L && friend.totalReceivablePaise == 0L ->
                            "You owe ${friend.name} ${AccountingEngine.formatPaise(friend.totalPayablePaise)}"
                        friend.totalReceivablePaise > 0L && friend.totalPayablePaise > 0L ->
                            "${friend.name} owes you ${AccountingEngine.formatPaise(friend.totalReceivablePaise)}, you owe ${AccountingEngine.formatPaise(friend.totalPayablePaise)}"
                        else ->
                            "You and ${friend.name} are settled up! ✨"
                    }

                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (friend.netPositionPaise != 0L) {
                        Text(
                            text = "Net: ${AccountingEngine.formatPaise(friend.netPositionPaise)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (friend.netPositionPaise > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
