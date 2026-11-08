package com.example.data.model

data class PriceAlert(
    val id: Long = 0,
    val medicineId: String,
    val medicineName: String,
    val company: String,
    val targetThresholdPrice: Double,
    val initialPrice: Double,
    val currentPrice: Double,
    val isTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType = NotificationType.PRICE_DROP,
    val medicineId: String? = null,
    val isRead: Boolean = false
)

enum class NotificationType {
    PRICE_DROP,
    PROMOTION_BONUS,
    ORDER_CONFIRMED,
    EXPIRY_WARNING
}
