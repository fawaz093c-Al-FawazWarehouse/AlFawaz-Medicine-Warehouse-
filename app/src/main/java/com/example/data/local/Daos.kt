package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    // Return only the last 5 submitted orders as specifically requested
    @Query("SELECT * FROM orders ORDER BY timestamp DESC LIMIT 5")
    fun getLast5Orders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Query("DELETE FROM orders WHERE id NOT IN (SELECT id FROM orders ORDER BY timestamp DESC LIMIT 5)")
    suspend fun trimToLast5Orders()

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Long)
}

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllPriceAlerts(): Flow<List<PriceAlertEntity>>

    @Query("SELECT * FROM price_alerts WHERE isTriggered = 0")
    suspend fun getActivePriceAlerts(): List<PriceAlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceAlert(alert: PriceAlertEntity): Long

    @Update
    suspend fun updatePriceAlert(alert: PriceAlertEntity)

    @Query("DELETE FROM price_alerts WHERE id = :alertId")
    suspend fun deletePriceAlert(alertId: Long)

    @Query("DELETE FROM price_alerts WHERE medicineId = :medicineId")
    suspend fun deleteByMedicineId(medicineId: String)
}
