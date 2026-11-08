package com.example.data.model

data class CartItem(
    val medicine: Medicine,
    val quantity: Int
) {
    val bonusCount: Int
        get() = medicine.calculateBonusCount(quantity)

    val totalPieces: Int
        get() = quantity + bonusCount

    val itemTotal: Double
        get() = quantity * medicine.price

    val effectiveUnitPrice: Double
        get() = medicine.getEffectivePricePerUnit(quantity)
}

data class OrderSubmission(
    val id: String,
    val pharmacyName: String,
    val pharmacistPhone: String,
    val delegateName: String,
    val delegatePhone: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val totalBonusPieces: Int,
    val totalPaidPieces: Int,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
