package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIcon(name: String): ImageVector {
        return when (name) {
            "restaurant" -> Icons.Default.Restaurant
            "directions_bus" -> Icons.Default.DirectionsBus
            "school" -> Icons.Default.School
            "shopping_bag" -> Icons.Default.ShoppingBag
            "sports_esports" -> Icons.Default.SportsEsports
            "spa" -> Icons.Default.Spa
            "card_giftcard" -> Icons.Default.CardGiftcard
            "savings" -> Icons.Default.Savings
            "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
            "account_balance" -> Icons.Default.AccountBalance
            "redeem" -> Icons.Default.Redeem
            "work" -> Icons.Default.Work
            "replay" -> Icons.Default.Replay
            "paid" -> Icons.Default.Paid
            "payments" -> Icons.Default.Payments
            else -> Icons.Default.Category
        }
    }
}
