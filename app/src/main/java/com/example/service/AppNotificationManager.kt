package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.AppNotification
import com.example.data.model.Medicine
import com.example.data.model.NotificationType
import com.example.data.model.OrderSubmission
import com.example.data.model.PriceAlert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.NumberFormat
import java.util.Locale

class AppNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val promoChannel = NotificationChannel(
                CHANNEL_PROMOTIONS,
                "عروض وبونصات مستودع الفواز",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات بالعروض الحصرية والبونصات الإضافية الجديدة على الأدوية"
                enableVibration(true)
            }

            val priceAlertChannel = NotificationChannel(
                CHANNEL_PRICE_ALERTS,
                "تنبيهات انخفاض أسعار الأدوية",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعار فوري عند انخفاض سعر دواء إلى الحد المحدد المطلوب"
                enableVibration(true)
            }

            val orderChannel = NotificationChannel(
                CHANNEL_ORDERS,
                "طلبيات وفواتير الصيدلية",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "تأكيدات إرسال الطلبيات وإصدار الفواتير"
            }

            notificationManager.createNotificationChannel(promoChannel)
            notificationManager.createNotificationChannel(priceAlertChannel)
            notificationManager.createNotificationChannel(orderChannel)
        }
    }

    private fun getLaunchPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    // Trigger Price Drop Notification
    fun notifyPriceDrop(alert: PriceAlert, medicine: Medicine) {
        val formatter = NumberFormat.getNumberInstance(Locale("ar", "SY"))
        val oldPriceStr = formatter.format(alert.initialPrice)
        val newPriceStr = formatter.format(medicine.price)
        val targetPriceStr = formatter.format(alert.targetThresholdPrice)

        val title = "📉 هبوط سعر: ${medicine.name}"
        val message = "انخفض سعر ${medicine.name} (${medicine.company}) إلى $newPriceStr ل.س (الحد المطلوب: $targetPriceStr ل.س - السعر السابق: $oldPriceStr ل.س)"

        recordNotification(
            title = title,
            message = message,
            type = NotificationType.PRICE_DROP,
            medicineId = medicine.id
        )

        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_PRICE_ALERTS)
                .setSmallIcon(R.drawable.alfawaz_logo_1787485756352)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getLaunchPendingIntent())
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(alert.id.toInt() + 1000, notification)
        } catch (e: SecurityException) {
            // Notifications permission not granted yet
        }
    }

    // Trigger Promotion / Bonus Announcement Notification
    fun notifyPromotion(medicine: Medicine, promoDescription: String) {
        val formatter = NumberFormat.getNumberInstance(Locale("ar", "SY"))
        val priceStr = formatter.format(medicine.price)

        val title = "🎁 عرض بونص حصري: ${medicine.name}"
        val message = "عرض مميز من شركة ${medicine.company} على ${medicine.name} - بونص ${medicine.bonus} بسعر $priceStr ل.س! $promoDescription"

        recordNotification(
            title = title,
            message = message,
            type = NotificationType.PROMOTION_BONUS,
            medicineId = medicine.id
        )

        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_PROMOTIONS)
                .setSmallIcon(R.drawable.alfawaz_logo_1787485756352)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getLaunchPendingIntent())
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            // Permission handling
        }
    }

    // Trigger Order Confirmation Notification
    fun notifyOrderPlaced(order: OrderSubmission) {
        val formatter = NumberFormat.getNumberInstance(Locale("ar", "SY"))
        val totalStr = formatter.format(order.totalAmount)

        val title = "✅ تم استلام طلبية ${order.pharmacyName}"
        val message = "تم تجهيز الفاتورة بقيمة $totalStr ل.س (${order.totalPaidPieces} قطعة + ${order.totalBonusPieces} بونص مجاني). تم توجيه الطلب للمندوب ${order.delegateName}."

        recordNotification(
            title = title,
            message = message,
            type = NotificationType.ORDER_CONFIRMED
        )

        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_ORDERS)
                .setSmallIcon(R.drawable.alfawaz_logo_1787485756352)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(getLaunchPendingIntent())
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(order.timestamp.toInt(), notification)
        } catch (e: SecurityException) {
            // Permission handling
        }
    }

    private fun recordNotification(
        title: String,
        message: String,
        type: NotificationType,
        medicineId: String? = null
    ) {
        val item = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = type,
            medicineId = medicineId
        )
        val current = _notifications.value.toMutableList()
        current.add(0, item)
        _notifications.value = current
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    companion object {
        const val CHANNEL_PROMOTIONS = "pharmacy_promotions_channel"
        const val CHANNEL_PRICE_ALERTS = "price_alerts_channel"
        const val CHANNEL_ORDERS = "orders_channel"
    }
}
