package com.example.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class ExpiryStatus {
    FRESH,         // > 12 months
    NEAR_EXPIRY,   // 3 - 12 months
    CRITICAL       // < 3 months
}

data class Medicine(
    val id: String,
    val name: String,
    val company: String,
    val price: Double,
    val bonus: String = "", // e.g. "2+10", "1+10", "5+20", "10+10"
    val bonusPaid: Int = 10,
    val bonusFree: Int = 0,
    val form: String = "حبوب", // حبوب, شراب, مرهم, كريم, قطرة, تحاميل, بخاخ, كبسول, فوار, غسول, امبول, جل
    val dosage: String = "",
    val expirationDate: String = "2027-06", // YYYY-MM
    val batchNumber: String = "BT-2024",
    val description: String = "",
    val activeIngredient: String = "",
    val isPromoted: Boolean = false,
    val oldPrice: Double? = null
) {
    // Calculate bonus pieces based on purchased quantity
    fun calculateBonusCount(quantity: Int): Int {
        if (bonusPaid <= 0 || bonusFree <= 0) return 0
        val multiplier = quantity / bonusPaid
        return multiplier * bonusFree
    }

    // Effective unit price taking bonus into account
    fun getEffectivePricePerUnit(quantity: Int): Double {
        val freeCount = calculateBonusCount(quantity)
        val totalPieces = quantity + freeCount
        if (totalPieces == 0) return price
        val totalCost = quantity * price
        return totalCost / totalPieces
    }

    // Expiry status analysis for inventory rotation (FEFO)
    fun getExpiryStatus(): ExpiryStatus {
        return try {
            val parts = expirationDate.split("-")
            if (parts.size >= 2) {
                val year = parts[0].toIntOrNull() ?: 2027
                val month = parts[1].toIntOrNull() ?: 1
                val target = LocalDate.of(year, month, 1)
                val now = LocalDate.now()
                val monthsDiff = ChronoUnit.MONTHS.between(now, target)
                when {
                    monthsDiff <= 3 -> ExpiryStatus.CRITICAL
                    monthsDiff <= 9 -> ExpiryStatus.NEAR_EXPIRY
                    else -> ExpiryStatus.FRESH
                }
            } else {
                ExpiryStatus.FRESH
            }
        } catch (e: Exception) {
            ExpiryStatus.FRESH
        }
    }
}

enum class BrochureSortOption(val titleAr: String) {
    NAME_ASC("الاسم الأبجدي (أ - ي)"),
    BONUS_DESC("أعلى نسبة بونص مجاني 🎁"),
    PRICE_ASC("السعر: من الأقل للأعلى"),
    PRICE_DESC("السعر: من الأعلى للأقل"),
    COMPANY_ASC("اسم الشركة المصنعة")
}
