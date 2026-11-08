package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderUid: String,
    val pharmacyName: String,
    val pharmacistPhone: String,
    val delegateName: String,
    val delegatePhone: String,
    val totalAmount: Double,
    val totalBonusPieces: Int,
    val totalPaidPieces: Int,
    val itemsJson: String, // Stored as JSON string
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true)
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
