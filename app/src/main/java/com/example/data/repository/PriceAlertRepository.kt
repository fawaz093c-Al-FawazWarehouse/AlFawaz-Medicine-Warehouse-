package com.example.data.repository

import com.example.data.local.PriceAlertDao
import com.example.data.local.PriceAlertEntity
import com.example.data.model.Medicine
import com.example.data.model.PriceAlert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PriceAlertRepository(
    private val priceAlertDao: PriceAlertDao
) {
    val allAlerts: Flow<List<PriceAlert>> = priceAlertDao.getAllPriceAlerts().map { entities ->
        entities.map {
            PriceAlert(
                id = it.id,
                medicineId = it.medicineId,
                medicineName = it.medicineName,
                company = it.company,
                targetThresholdPrice = it.targetThresholdPrice,
                initialPrice = it.initialPrice,
                currentPrice = it.currentPrice,
                isTriggered = it.isTriggered,
                createdAt = it.createdAt
            )
        }
    }

    suspend fun setPriceThreshold(medicine: Medicine, targetPrice: Double): Long {
        val entity = PriceAlertEntity(
            medicineId = medicine.id,
            medicineName = medicine.name,
            company = medicine.company,
            targetThresholdPrice = targetPrice,
            initialPrice = medicine.price,
            currentPrice = medicine.price,
            isTriggered = medicine.price <= targetPrice
        )
        return priceAlertDao.insertPriceAlert(entity)
    }

    // Check all active alerts against updated medicines
    suspend fun checkPriceDrops(updatedMedicines: List<Medicine>): List<Pair<PriceAlert, Medicine>> {
        val activeEntities = priceAlertDao.getActivePriceAlerts()
        val triggered = mutableListOf<Pair<PriceAlert, Medicine>>()

        for (alert in activeEntities) {
            val currentMed = updatedMedicines.find { it.id == alert.medicineId }
            if (currentMed != null) {
                if (currentMed.price <= alert.targetThresholdPrice) {
                    // Trigger alert!
                    val updatedEntity = alert.copy(
                        currentPrice = currentMed.price,
                        isTriggered = true
                    )
                    priceAlertDao.updatePriceAlert(updatedEntity)
                    triggered.add(
                        PriceAlert(
                            id = alert.id,
                            medicineId = alert.medicineId,
                            medicineName = alert.medicineName,
                            company = alert.company,
                            targetThresholdPrice = alert.targetThresholdPrice,
                            initialPrice = alert.initialPrice,
                            currentPrice = currentMed.price,
                            isTriggered = true,
                            createdAt = alert.createdAt
                        ) to currentMed
                    )
                } else if (currentMed.price != alert.currentPrice) {
                    priceAlertDao.updatePriceAlert(alert.copy(currentPrice = currentMed.price))
                }
            }
        }
        return triggered
    }

    suspend fun deleteAlert(id: Long) {
        priceAlertDao.deletePriceAlert(id)
    }
}
