package com.example.data.repository

import com.example.data.local.OrderDao
import com.example.data.local.OrderEntity
import com.example.data.model.CartItem
import com.example.data.model.Medicine
import com.example.data.model.OrderSubmission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class OrderRepository(
    private val orderDao: OrderDao,
    private val medicineRepository: MedicineRepository
) {
    // Exposes the last 5 submitted orders reactively
    val last5Orders: Flow<List<OrderSubmission>> = orderDao.getLast5Orders().map { entities ->
        entities.map { entity ->
            val items = parseItemsJson(entity.itemsJson)
            OrderSubmission(
                id = entity.orderUid,
                pharmacyName = entity.pharmacyName,
                pharmacistPhone = entity.pharmacistPhone,
                delegateName = entity.delegateName,
                delegatePhone = entity.delegatePhone,
                items = items,
                totalAmount = entity.totalAmount,
                totalBonusPieces = entity.totalBonusPieces,
                totalPaidPieces = entity.totalPaidPieces,
                notes = entity.notes,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun saveOrder(order: OrderSubmission): Long {
        val itemsJson = serializeItemsToJson(order.items)
        val entity = OrderEntity(
            orderUid = order.id,
            pharmacyName = order.pharmacyName,
            pharmacistPhone = order.pharmacistPhone,
            delegateName = order.delegateName,
            delegatePhone = order.delegatePhone,
            totalAmount = order.totalAmount,
            totalBonusPieces = order.totalBonusPieces,
            totalPaidPieces = order.totalPaidPieces,
            itemsJson = itemsJson,
            notes = order.notes,
            timestamp = order.timestamp
        )
        val insertedId = orderDao.insertOrder(entity)
        // Ensure strictly only the last 5 orders are maintained in the database
        orderDao.trimToLast5Orders()
        return insertedId
    }

    private fun serializeItemsToJson(items: List<CartItem>): String {
        val jsonArray = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("medId", item.medicine.id)
                put("medName", item.medicine.name)
                put("company", item.medicine.company)
                put("price", item.medicine.price)
                put("bonus", item.medicine.bonus)
                put("bonusPaid", item.medicine.bonusPaid)
                put("bonusFree", item.medicine.bonusFree)
                put("form", item.medicine.form)
                put("dosage", item.medicine.dosage)
                put("expDate", item.medicine.expirationDate)
                put("batch", item.medicine.batchNumber)
                put("quantity", item.quantity)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    private fun parseItemsJson(jsonStr: String): List<CartItem> {
        val list = mutableListOf<CartItem>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val medId = obj.optString("medId", "")
                val knownMed = medicineRepository.getMedicineById(medId)
                val med = knownMed ?: Medicine(
                    id = medId,
                    name = obj.optString("medName", ""),
                    company = obj.optString("company", ""),
                    price = obj.optDouble("price", 0.0),
                    bonus = obj.optString("bonus", ""),
                    bonusPaid = obj.optInt("bonusPaid", 10),
                    bonusFree = obj.optInt("bonusFree", 0),
                    form = obj.optString("form", "حبوب"),
                    dosage = obj.optString("dosage", ""),
                    expirationDate = obj.optString("expDate", "2027-06"),
                    batchNumber = obj.optString("batch", "")
                )
                val qty = obj.optInt("quantity", 1)
                list.add(CartItem(medicine = med, quantity = qty))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
