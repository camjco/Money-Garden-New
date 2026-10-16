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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.data.model.FinancialTransaction
import com.example.data.model.TransactionType
import com.example.domain.AccountingEngine
import com.example.ui.DashboardUiState
import com.example.ui.components.CategoryIconHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    state: DashboardUiState,
    onOpenAddTransaction: () -> Unit,
    onReverseTransaction: (txId: Long, reason: String) -> Unit,
    onDeleteTransaction: (txId: Long) -> Unit = {},
    onClearAllTransactions: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<TransactionType?>(null) }
    var showOnlyReversed by remember { mutableStateOf(false) }

    var selectedTxForDetails by remember { mutableStateOf<FinancialTransaction?>(null) }
    var showReversePromptForTx by remember { mutableStateOf<FinancialTransaction?>(null) }
    var showDeletePromptForTx by remember { mutableStateOf<FinancialTransaction?>(null) }
    var showClearAllPrompt by remember { mutableStateOf(false) }
    var reversalReasonText by remember { mutableStateOf("") }

    val filteredTransactions = remember(state.transactions, searchQuery, selectedTypeFilter, showOnlyReversed) {
        state.transactions.filter { tx ->
            val matchesSearch = searchQuery.isBlank() ||
                tx.description.contains(searchQuery, ignoreCase = true) ||
                (tx.notes?.contains(searchQuery, ignoreCase = true) == true)
            val matchesType = selectedTypeFilter == null || tx.type == selectedTypeFilter
            val matchesReversed = if (showOnlyReversed) tx.isReversed else true
            matchesSearch && matchesType && matchesReversed
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("transactions_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Transaction Ledger",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Double-entry accounting journal & audit trail",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (state.transactions.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearAllPrompt = true },
                            modifier = Modifier.testTag("btn_clear_all_transactions")
                        ) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Clear All Transactions",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by description or notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_transactions")
                )
            }

            // Filter Chips
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedTypeFilter == null && !showOnlyReversed,
                        onClick = {
                            selectedTypeFilter = null
                            showOnlyReversed = false
                        },
                        label = { Text("All (${state.transactions.size})") }
                    )
                    FilterChip(
                        selected = selectedTypeFilter == TransactionType.EXPENSE,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == TransactionType.EXPENSE) null else TransactionType.EXPENSE
                            showOnlyReversed = false
                        },
                        label = { Text("Expenses") }
                    )
                    FilterChip(
                        selected = selectedTypeFilter == TransactionType.INCOME,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == TransactionType.INCOME) null else TransactionType.INCOME
                            showOnlyReversed = false
                        },
                        label = { Text("Income") }
                    )
                    FilterChip(
                        selected = selectedTypeFilter == TransactionType.TRANSFER,
                        onClick = {
                            selectedTypeFilter = if (selectedTypeFilter == TransactionType.TRANSFER) null else TransactionType.TRANSFER
                            showOnlyReversed = false
                        },
                        label = { Text("Transfers") }
                    )
                    FilterChip(
                        selected = showOnlyReversed,
                        onClick = {
                            showOnlyReversed = !showOnlyReversed
                            selectedTypeFilter = null
                        },
                        label = { Text("Reversed") }
                    )
                }
            }

            if (filteredTransactions.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No transactions found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Try adjusting your filters or record a new transaction", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                items(filteredTransactions) { tx ->
                    val category = state.categories.find { it.id == tx.categoryId }
                    val account = state.accounts.find { it.id == tx.sourceAccountId ?: tx.destinationAccountId }
                    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US) }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTxForDetails = tx }
                            .testTag("tx_row_${tx.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = when (tx.type) {
                                    TransactionType.INCOME -> MaterialTheme.colorScheme.primaryContainer
                                    TransactionType.EXPENSE -> MaterialTheme.colorScheme.surfaceVariant
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                },
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = CategoryIconHelper.getIcon(category?.iconName ?: "receipt"),
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = when (tx.type) {
                                            TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                                            TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.secondary
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tx.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    if (tx.isReversed) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.errorContainer
                                        ) {
                                            Text(
                                                text = "REVERSED",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onErrorContainer,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = sdf.format(Date(tx.date)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (tx.receiptUri != null) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            Icons.Default.AttachFile,
                                            contentDescription = "Receipt attached",
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val prefix = when (tx.type) {
                                    TransactionType.INCOME -> "+"
                                    TransactionType.EXPENSE -> "-"
                                    else -> ""
                                }
                                val color = when (tx.type) {
                                    TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                                    TransactionType.EXPENSE -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.secondary
                                }

                                Text(
                                    text = "$prefix${AccountingEngine.formatPaise(tx.personalAmountPaise)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = color
                                )

                                if (tx.totalAmountPaise != tx.personalAmountPaise && tx.type == TransactionType.EXPENSE) {
                                    Text(
                                        text = "Total paid ${AccountingEngine.formatPaise(tx.totalAmountPaise)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to Add Transaction
        FloatingActionButton(
            onClick = onOpenAddTransaction,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_transaction")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction")
        }
    }

    // Modal Bottom Sheet: Transaction Details & Double-Entry view
    if (selectedTxForDetails != null) {
        val tx = selectedTxForDetails!!
        val category = state.categories.find { it.id == tx.categoryId }
        val sourceAccount = state.accounts.find { it.id == tx.sourceAccountId }
        val destAccount = state.accounts.find { it.id == tx.destinationAccountId }
        val friend = state.friends.find { it.id == tx.friendId }
        val sdf = SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a", Locale.US)

        ModalBottomSheet(
            onDismissRequest = { selectedTxForDetails = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Transaction #${tx.id}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { selectedTxForDetails = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Detail list
                Text("Description: ${tx.description}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text("Date: ${sdf.format(Date(tx.date))}", style = MaterialTheme.typography.bodyMedium)
                Text("Type: ${tx.type.name}", style = MaterialTheme.typography.bodyMedium)
                if (category != null) {
                    Text("Category: ${category.name}", style = MaterialTheme.typography.bodyMedium)
                }
                if (sourceAccount != null) {
                    Text("Source Account: ${sourceAccount.name}", style = MaterialTheme.typography.bodyMedium)
                }
                if (destAccount != null) {
                    Text("Destination Account: ${destAccount.name}", style = MaterialTheme.typography.bodyMedium)
                }
                if (friend != null) {
                    Text("Friend: ${friend.name}", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Amount breakdown
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Total Transaction Amount:", style = MaterialTheme.typography.bodyMedium)
                            Text(AccountingEngine.formatPaise(tx.totalAmountPaise), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Arpita's Personal Share:", style = MaterialTheme.typography.bodyMedium)
                            Text(AccountingEngine.formatPaise(tx.personalAmountPaise), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        if (tx.totalAmountPaise != tx.personalAmountPaise) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Friend Share (Receivable):", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    AccountingEngine.formatPaise(tx.totalAmountPaise - tx.personalAmountPaise),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Receipt photo preview if available
                if (tx.receiptUri != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Supporting Receipt Photo:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    AsyncImage(
                        model = tx.receiptUri,
                        contentDescription = "Receipt photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete & Reversal Actions
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            showDeletePromptForTx = tx
                            selectedTxForDetails = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_delete_transaction")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete")
                    }

                    if (!tx.isReversed) {
                        OutlinedButton(
                            onClick = {
                                showReversePromptForTx = tx
                                selectedTxForDetails = null
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("btn_reverse_transaction")
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reverse")
                        }
                    }
                }

                if (tx.isReversed) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "This transaction was safely reversed and preserved in the audit log.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Delete confirmation dialog
    if (showDeletePromptForTx != null) {
        val tx = showDeletePromptForTx!!
        AlertDialog(
            onDismissRequest = { showDeletePromptForTx = null },
            title = { Text("Delete Transaction #${tx.id}?") },
            text = {
                Text("Are you sure you want to delete this transaction (\"${tx.description}\")? Your cash/bank and friend balances will be restored to what they were before.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTransaction(tx.id)
                        showDeletePromptForTx = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("btn_confirm_delete_transaction")
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePromptForTx = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear All confirmation dialog
    if (showClearAllPrompt) {
        AlertDialog(
            onDismissRequest = { showClearAllPrompt = false },
            title = { Text("Clear All Transactions?") },
            text = {
                Text("This will delete all transactions and reset your cash and bank accounts back to their original opening balances. Friends' shared balances will also be reset to ₹0.00.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllTransactions()
                        showClearAllPrompt = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("btn_confirm_clear_all")
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllPrompt = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reversal confirmation dialog
    if (showReversePromptForTx != null) {
        val tx = showReversePromptForTx!!
        AlertDialog(
            onDismissRequest = { showReversePromptForTx = null },
            title = { Text("Confirm Reversal") },
            text = {
                Column {
                    Text("In double-entry accounting, transactions are never deleted silently. A balanced reversal journal entry will be posted to restore accounts.")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reversalReasonText,
                        onValueChange = { reversalReasonText = it },
                        label = { Text("Reason for reversal") },
                        placeholder = { Text("e.g. Wrong amount entered, duplicate") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReverseTransaction(tx.id, reversalReasonText.ifBlank { "User requested reversal" })
                        showReversePromptForTx = null
                        reversalReasonText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Post Reversal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReversePromptForTx = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
