package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CartItem
import com.example.data.model.DelegatesData
import com.example.data.model.OrderSubmission
import com.example.data.model.SalesDelegate
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import java.io.File
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartScreen(
    viewModel: MainViewModel,
    onNavigateToBrochure: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val pharmacyName by viewModel.pharmacyName.collectAsStateWithLifecycle()
    val pharmacistPhone by viewModel.pharmacistPhone.collectAsStateWithLifecycle()
    val selectedDelegate by viewModel.selectedDelegate.collectAsStateWithLifecycle()
    val orderNotes by viewModel.orderNotes.collectAsStateWithLifecycle()

    var showDelegateDropdown by remember { mutableStateOf(false) }
    var generatedInvoiceFile by remember { mutableStateOf<File?>(null) }
    var submittedOrderSuccess by remember { mutableStateOf<OrderSubmission?>(null) }

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("ar-SY")) }

    val totalAmount = cartItems.sumOf { it.itemTotal }
    val totalPaidUnits = cartItems.sumOf { it.quantity }
    val totalBonusUnits = cartItems.sumOf { it.bonusCount }

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "سلة المشتريات فارغة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تصفح بروشور الأدوية وأضف احتياجات صيدليتك مع الاستفادة من بونص المستودع.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToBrochure,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_browse_medicines")
                ) {
                    Text("تصفح قائمة الأدوية الآن")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Order Items Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأصناف المطلوبة (${cartItems.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
                OutlinedButton(
                    onClick = { viewModel.clearCart() },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("إفراغ السلة", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        }

        items(cartItems, key = { it.medicine.id }) { item ->
            CartItemRow(
                item = item,
                onUpdateQuantity = { newQty -> viewModel.updateCartQuantity(item.medicine.id, newQty) },
                onRemove = { viewModel.removeFromCart(item.medicine.id) }
            )
        }

        // 2. Pharmacy and Delegate Configuration Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "بيانات الصيدلية والمندوب المسؤول",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pharmacyName,
                        onValueChange = { viewModel.setPharmacyDetails(it, pharmacistPhone, orderNotes) },
                        label = { Text("اسم الصيدلية / المركز الطبي") },
                        leadingIcon = { Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = EmeraldPrimary) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pharmacy_name")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pharmacistPhone,
                        onValueChange = { viewModel.setPharmacyDetails(pharmacyName, it, orderNotes) },
                        label = { Text("رقم هاتف الصيدلي للتواصل") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EmeraldPrimary) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pharmacist_phone")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sales Delegate Selector
                    Text(
                        text = "المندوب المسؤول عن استلام الطلبية:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDelegateDropdown = true }
                                .padding(vertical = 4.dp)
                                .testTag("delegate_selector_card")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = selectedDelegate.name,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "المنطقة: ${selectedDelegate.territory} • هاتف: ${selectedDelegate.phone}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary)
                            }
                        }

                        DropdownMenu(
                            expanded = showDelegateDropdown,
                            onDismissRequest = { showDelegateDropdown = false }
                        ) {
                            DelegatesData.delegatesList.forEach { delegate ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(delegate.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${delegate.territory} (${delegate.phone})",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setSelectedDelegate(delegate)
                                        showDelegateDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = orderNotes,
                        onValueChange = { viewModel.setPharmacyDetails(pharmacyName, pharmacistPhone, it) },
                        label = { Text("ملاحظات إضافية للطلبية (اختياري)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 3. Totals and Actions Section
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFFF1F8F5)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ملخص الفاتورة والحساب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مجموع القطع المطلوبة:")
                        Text("$totalPaidUnits قطعة", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("مجموع البونص المجاني 🎁:", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        Text("+$totalBonusUnits قطعة مجانية", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFD0DCD5))
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "صافي الفاتورة الإجمالي:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Text(
                            text = "${numberFormat.format(totalAmount)} ل.س",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // MANDATORY REQUIREMENT: 'Print/Save Invoice' Button
                    Button(
                        onClick = {
                            val pdfFile = viewModel.generateInvoicePdf(context)
                            generatedInvoiceFile = pdfFile
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_print_save_invoice"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "طباعة / حفظ الفاتورة PDF 📄",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Submit Order Button (Saves to Room last 5 orders)
                    Button(
                        onClick = {
                            val order = viewModel.submitOrder()
                            if (order != null) {
                                submittedOrderSuccess = order
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_submit_order"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تأكيد وإرسال الطلبية", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    // PDF Generated Dialog (Print & Share Options)
    generatedInvoiceFile?.let { pdfFile ->
        AlertDialog(
            onDismissRequest = { generatedInvoiceFile = null },
            icon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = AmberGold, modifier = Modifier.size(36.dp)) },
            title = { Text("تم إنشاء الفاتورة الاحترافية PDF بنجاح") },
            text = {
                Text(
                    text = "تم تجهيز ملف الفاتورة الرسمية متضمناً تفاصيل الصيدلية (${pharmacyName})، الأدوية المطلوبة، البونص، والمندوب المسؤول.\n\nاختر الإجراء المطلوب:"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.printInvoice(context, pdfFile)
                        generatedInvoiceFile = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("طباعة مباشرة")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.shareInvoice(context, pdfFile)
                        generatedInvoiceFile = null
                    }
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("مشاركة / حفظ")
                }
            }
        )
    }

    // Order Submitted Dialog (Saved to Room + WhatsApp options)
    submittedOrderSuccess?.let { order ->
        AlertDialog(
            onDismissRequest = { submittedOrderSuccess = null },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(40.dp)) },
            title = { Text("تم حفظ الطلبية #${order.id} بنجاح!") },
            text = {
                Column {
                    Text(
                        text = "تم حفظ الطلبية في قاعدة البيانات المحلية (سجل آخر 5 طلبيات) لسهولة إعادة الطلب لاحقاً.\n\nالقيمة الإجمالية: ${numberFormat.format(order.totalAmount)} ل.س\nالمندوب المسؤول: ${order.delegateName} (${order.delegatePhone})"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "يمكنك إرسال تفاصيل الطلبية مباشرة عبر واتساب للمندوب:",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDark
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val whatsappText = buildString {
                            append("طلب جديد من ${order.pharmacyName}\n")
                            append("رقم الطلبية: #${order.id}\n")
                            append("هاتف الصيدلي: ${order.pharmacistPhone}\n\n")
                            append("الأصناف المطلوبة:\n")
                            order.items.forEach {
                                append("• ${it.medicine.name} (الكمية: ${it.quantity} ${if (it.bonusCount > 0) "+ ${it.bonusCount} بونص مجاني" else ""})\n")
                            }
                            append("\nصافي الفاتورة: ${numberFormat.format(order.totalAmount)} ل.س\n")
                            if (order.notes.isNotBlank()) append("ملاحظات: ${order.notes}\n")
                        }
                        val cleanPhone = order.delegatePhone.replace(" ", "").replace("-", "")
                        val uri = Uri.parse("https://api.whatsapp.com/send?phone=+963${cleanPhone.removePrefix("0")}&text=${Uri.encode(whatsappText)}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "واتساب غير مثبت", Toast.LENGTH_SHORT).show()
                        }
                        submittedOrderSuccess = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إرسال عبر واتساب للمندوب", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { submittedOrderSuccess = null }) {
                    Text("إغلاق")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.forLanguageTag("ar-SY")) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.medicine.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.medicine.company} • ${numberFormat.format(item.medicine.price)} ل.س للقطعة",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف الصنف",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bonus Pill and Quantity Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bonus Info
                if (item.bonusCount > 0) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(1.dp, Color(0xFF81C784), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "🎁 بونص: +${item.bonusCount} علبة مجاناً",
                            color = Color(0xFF1B5E20),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "الكمية: ${item.quantity} قطعة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity - 1) },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "تقليل", modifier = Modifier.size(14.dp))
                    }

                    Text(
                        text = "${item.quantity}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "زيادة", modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "الإجمالي: ${item.quantity} علبة مدفوعة ${if (item.bonusCount > 0) "+ ${item.bonusCount} مجاناً" else ""}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${numberFormat.format(item.itemTotal)} ل.س",
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
