package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppNotification
import com.example.data.model.BrochureSortOption
import com.example.data.model.CartItem
import com.example.data.model.DelegatesData
import com.example.data.model.Medicine
import com.example.data.model.OrderSubmission
import com.example.data.model.PriceAlert
import com.example.data.model.SalesDelegate
import com.example.data.repository.MedicineRepository
import com.example.data.repository.OrderRepository
import com.example.data.repository.PriceAlertRepository
import com.example.service.AppNotificationManager
import com.example.service.ChatMessage
import com.example.service.GeminiPharmacistService
import com.example.service.MessageSender
import com.example.service.PdfInvoiceGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val medicineRepository = MedicineRepository()
    private val database = AppDatabase.getDatabase(application)
    private val orderRepository = OrderRepository(database.orderDao(), medicineRepository)
    private val priceAlertRepository = PriceAlertRepository(database.priceAlertDao())
    private val notificationManager = AppNotificationManager(application)
    private val geminiService = GeminiPharmacistService(medicineRepository)

    // === Brochure Filter & Sort State ===
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCompany = MutableStateFlow("الكل")
    val selectedCompany: StateFlow<String> = _selectedCompany.asStateFlow()

    private val _selectedCategory = MutableStateFlow("الكل")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow(BrochureSortOption.NAME_ASC)
    val selectedSort: StateFlow<BrochureSortOption> = _selectedSort.asStateFlow()

    private val _onlyWithBonus = MutableStateFlow(false)
    val onlyWithBonus: StateFlow<Boolean> = _onlyWithBonus.asStateFlow()

    val companies: List<String> = medicineRepository.getCompanies()
    val categories: List<String> = medicineRepository.getCategories()

    // Filtered medicines Flow
    val filteredMedicines: StateFlow<List<Medicine>> = combine(
        medicineRepository.medicines,
        _searchQuery,
        _selectedCompany,
        _selectedCategory,
        combine(_onlyWithBonus, _selectedSort) { bonus, sort -> bonus to sort }
    ) { _, query, company, category, (onlyBonus, sort) ->
        medicineRepository.filterAndSort(
            query = query,
            company = company,
            form = category,
            onlyWithBonus = onlyBonus,
            sortOption = sort
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === Cart & Order Form State ===
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _pharmacyName = MutableStateFlow("صيدلية الأمل الحديثة")
    val pharmacyName: StateFlow<String> = _pharmacyName.asStateFlow()

    private val _pharmacistPhone = MutableStateFlow("09XXXXXXXX")
    val pharmacistPhone: StateFlow<String> = _pharmacistPhone.asStateFlow()

    private val _selectedDelegate = MutableStateFlow(DelegatesData.delegatesList.first())
    val selectedDelegate: StateFlow<SalesDelegate> = _selectedDelegate.asStateFlow()

    private val _orderNotes = MutableStateFlow("")
    val orderNotes: StateFlow<String> = _orderNotes.asStateFlow()

    // === Saved Last 5 Orders (Room) ===
    val last5Orders: StateFlow<List<OrderSubmission>> = orderRepository.last5Orders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === Price Alerts (Room) ===
    val priceAlerts: StateFlow<List<PriceAlert>> = priceAlertRepository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // === Push Notifications ===
    val notifications: StateFlow<List<AppNotification>> = notificationManager.notifications

    // === AI Pharmacist Chat ===
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_1",
                sender = MessageSender.AI_PHARMACIST,
                text = "مرحباً دكتور! 🩺 أنا المساعد الصيدلاني الذكي لمستودع الفواز للأدوية والمستلزمات الطبية. يمكنك سؤالي عن بدائل الأدوية، التداخلات الدوائية، عروض البونص المحدثة، أو البحث في المستودع."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Status / Toast feedback message
    private val _uiFeedback = MutableStateFlow<String?>(null)
    val uiFeedback: StateFlow<String?> = _uiFeedback.asStateFlow()

    fun clearFeedback() {
        _uiFeedback.value = null
    }

    // === Brochure Actions ===
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCompany(company: String) {
        _selectedCompany.value = company
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedSort(sort: BrochureSortOption) {
        _selectedSort.value = sort
    }

    fun setOnlyWithBonus(only: Boolean) {
        _onlyWithBonus.value = only
    }

    // === Cart Operations ===
    fun addToCart(medicine: Medicine, quantityToAdd: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.medicine.id == medicine.id }
        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + quantityToAdd)
        } else {
            current.add(CartItem(medicine = medicine, quantity = quantityToAdd))
        }
        _cartItems.value = current
        _uiFeedback.value = "تمت إضافة ${medicine.name} للسلة"
    }

    fun updateCartQuantity(medicineId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(medicineId)
            return
        }
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.medicine.id == medicineId }
        if (index != -1) {
            current[index] = current[index].copy(quantity = newQuantity)
            _cartItems.value = current
        }
    }

    fun removeFromCart(medicineId: String) {
        val current = _cartItems.value.toMutableList()
        current.removeAll { it.medicine.id == medicineId }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun setPharmacyDetails(name: String, phone: String, notes: String = "") {
        _pharmacyName.value = name
        _pharmacistPhone.value = phone
        _orderNotes.value = notes
    }

    fun setSelectedDelegate(delegate: SalesDelegate) {
        _selectedDelegate.value = delegate
    }

    // Reorder feature from past saved orders
    fun reorderFromHistory(order: OrderSubmission) {
        _cartItems.value = order.items
        _pharmacyName.value = order.pharmacyName
        _pharmacistPhone.value = order.pharmacistPhone
        val delegate = DelegatesData.delegatesList.find { it.name == order.delegateName }
            ?: DelegatesData.delegatesList.first()
        _selectedDelegate.value = delegate
        _uiFeedback.value = "تم استرجاع أصناف الطلبية #${order.id} إلى السلة بنجاح!"
    }

    // Submit Order & Save to Room (Last 5 orders)
    fun submitOrder(): OrderSubmission? {
        val items = _cartItems.value
        if (items.isEmpty()) return null

        val orderUid = (1000 + (Math.random() * 9000).toInt()).toString()
        val totalAmount = items.sumOf { it.itemTotal }
        val totalBonus = items.sumOf { it.bonusCount }
        val totalPaid = items.sumOf { it.quantity }

        val order = OrderSubmission(
            id = orderUid,
            pharmacyName = _pharmacyName.value.ifBlank { "صيدلية معتمدة" },
            pharmacistPhone = _pharmacistPhone.value.ifBlank { "0995711536" },
            delegateName = _selectedDelegate.value.name,
            delegatePhone = _selectedDelegate.value.phone,
            items = items,
            totalAmount = totalAmount,
            totalBonusPieces = totalBonus,
            totalPaidPieces = totalPaid,
            notes = _orderNotes.value,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            orderRepository.saveOrder(order)
            notificationManager.notifyOrderPlaced(order)
        }

        clearCart()
        _uiFeedback.value = "تم تأكيد إرسال الطلبية وحفظها في السجل بنجاح!"
        return order
    }

    // Generate Invoice PDF
    fun generateInvoicePdf(context: Context, customOrder: OrderSubmission? = null): File {
        val order = customOrder ?: run {
            val items = _cartItems.value
            val orderUid = "PREVIEW-${(1000 + (Math.random() * 9000).toInt())}"
            val totalAmount = items.sumOf { it.itemTotal }
            val totalBonus = items.sumOf { it.bonusCount }
            val totalPaid = items.sumOf { it.quantity }
            OrderSubmission(
                id = orderUid,
                pharmacyName = _pharmacyName.value.ifBlank { "صيدلية معتمدة" },
                pharmacistPhone = _pharmacistPhone.value.ifBlank { "0995711536" },
                delegateName = _selectedDelegate.value.name,
                delegatePhone = _selectedDelegate.value.phone,
                items = items,
                totalAmount = totalAmount,
                totalBonusPieces = totalBonus,
                totalPaidPieces = totalPaid,
                notes = _orderNotes.value
            )
        }
        return PdfInvoiceGenerator.generateInvoicePdf(context, order)
    }

    fun printInvoice(context: Context, pdfFile: File) {
        PdfInvoiceGenerator.printInvoice(context, pdfFile)
    }

    fun shareInvoice(context: Context, pdfFile: File) {
        PdfInvoiceGenerator.shareInvoice(context, pdfFile)
    }

    // === Price Alert Actions ===
    fun setPriceThreshold(medicine: Medicine, targetPrice: Double) {
        viewModelScope.launch {
            priceAlertRepository.setPriceThreshold(medicine, targetPrice)
            _uiFeedback.value = "تم تفعيل مراقبة سعر ${medicine.name} عند $targetPrice ل.س"
            // If already below target, trigger immediately
            if (medicine.price <= targetPrice) {
                checkPriceDropAlerts()
            }
        }
    }

    fun deletePriceAlert(id: Long) {
        viewModelScope.launch {
            priceAlertRepository.deleteAlert(id)
            _uiFeedback.value = "تم حذف تنبيه السعر"
        }
    }

    // Simulate price change (for price drop trigger test & notification system)
    fun simulatePriceDrop(medicineId: String, newPrice: Double) {
        viewModelScope.launch {
            val updatedMed = medicineRepository.updateMedicinePrice(medicineId, newPrice)
            if (updatedMed != null) {
                checkPriceDropAlerts()
                _uiFeedback.value = "تم تحديث سعر ${updatedMed.name} إلى $newPrice ل.س"
            }
        }
    }

    private suspend fun checkPriceDropAlerts() {
        val triggered = priceAlertRepository.checkPriceDrops(medicineRepository.getAllMedicines())
        for ((alert, med) in triggered) {
            notificationManager.notifyPriceDrop(alert, med)
        }
    }

    // Simulate new promotion push notification
    fun simulateNewPromotionPush(medicine: Medicine, newBonus: String, bonusPaid: Int, bonusFree: Int) {
        viewModelScope.launch {
            val updated = medicineRepository.applySpecialPromotion(
                medicineId = medicine.id,
                newBonus = newBonus,
                bonusPaid = bonusPaid,
                bonusFree = bonusFree
            )
            if (updated != null) {
                notificationManager.notifyPromotion(updated, "لفترة محدودة لدى مستودع الفواز")
                _uiFeedback.value = "تم إطلاق إشعار العرض الترويجي: بونص $newBonus"
            }
        }
    }

    fun clearNotifications() {
        notificationManager.clearAllNotifications()
    }

    // === AI Pharmacist Chat Actions ===
    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = userText
        )
        val current = _chatMessages.value.toMutableList()
        current.add(userMsg)
        _chatMessages.value = current

        _isAiThinking.value = true

        viewModelScope.launch {
            val aiResponse = geminiService.sendMessage(userText)
            val updated = _chatMessages.value.toMutableList()
            updated.add(aiResponse)
            _chatMessages.value = updated
            _isAiThinking.value = false
        }
    }
}
