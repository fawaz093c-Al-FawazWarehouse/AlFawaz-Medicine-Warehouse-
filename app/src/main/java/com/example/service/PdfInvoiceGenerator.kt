package com.example.service

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.data.model.OrderSubmission
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfInvoiceGenerator {

    private const val PAGE_WIDTH = 595 // A4 standard width
    private const val PAGE_HEIGHT = 842 // A4 standard height

    fun generateInvoicePdf(context: Context, order: OrderSubmission): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawInvoice(canvas, order)

        pdfDocument.finishPage(page)

        // Save to cache directory
        val invoiceDir = File(context.cacheDir, "invoices")
        if (!invoiceDir.exists()) invoiceDir.mkdirs()
        val fileName = "فاتورة_مستودع_الفواز_${order.id}.pdf"
        val pdfFile = File(invoiceDir, fileName)

        FileOutputStream(pdfFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return pdfFile
    }

    private fun drawInvoice(canvas: Canvas, order: OrderSubmission) {
        val numberFormat = NumberFormat.getNumberInstance(Locale("ar", "SY"))
        val dateFormat = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale("ar"))
        val formattedDate = dateFormat.format(Date(order.timestamp))

        val bgPaint = Paint().apply { color = Color.WHITE }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // Top Header Banner
        val headerPaint = Paint().apply { color = Color.parseColor("#0F6E56") }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 95f, headerPaint)

        // Gold Accent Line
        val goldLinePaint = Paint().apply { color = Color.parseColor("#D48806") }
        canvas.drawRect(0f, 95f, PAGE_WIDTH.toFloat(), 98f, goldLinePaint)

        // Header Titles (Arabic RTL aligned)
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("مستودع الفواز للأدوية والمستلزمات الطبية", PAGE_WIDTH - 28f, 40f, titlePaint)

        val subTitlePaint = Paint().apply {
            color = Color.parseColor("#C8E6C9")
            textSize = 11f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("فواتير وطلبيات الصيادلة - خدمة التوزيع الدوائي السريع", PAGE_WIDTH - 28f, 60f, subTitlePaint)
        canvas.drawText("هاتف: 0995711536  •  البريد: alfawazwarehouse@gmail.com", PAGE_WIDTH - 28f, 78f, subTitlePaint)

        // Left Header: Invoice # & Date
        val invNoPaint = Paint().apply {
            color = Color.WHITE
            textSize = 12f
            isFakeBoldText = true
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText("فاتورة طلبية: #${order.id}", 28f, 45f, invNoPaint)

        val invDatePaint = Paint().apply {
            color = Color.parseColor("#E8F5E9")
            textSize = 10f
            textAlign = Paint.Align.LEFT
        }
        canvas.drawText(formattedDate, 28f, 68f, invDatePaint)

        // Order & Pharmacy Info Box
        var currentY = 115f
        val boxBorderPaint = Paint().apply {
            color = Color.parseColor("#D0DCD5")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val boxFillPaint = Paint().apply {
            color = Color.parseColor("#F7FBF9")
            style = Paint.Style.FILL
        }
        canvas.drawRect(28f, currentY, PAGE_WIDTH - 28f, currentY + 75f, boxFillPaint)
        canvas.drawRect(28f, currentY, PAGE_WIDTH - 28f, currentY + 75f, boxBorderPaint)

        val labelPaint = Paint().apply {
            color = Color.parseColor("#52695E")
            textSize = 10f
            textAlign = Paint.Align.RIGHT
        }
        val valuePaint = Paint().apply {
            color = Color.parseColor("#14241E")
            textSize = 11f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }

        // Row 1
        canvas.drawText("اسم الصيدلية / العميل:", PAGE_WIDTH - 38f, currentY + 22f, labelPaint)
        canvas.drawText(order.pharmacyName.ifBlank { "صيدلية معتمدة" }, PAGE_WIDTH - 150f, currentY + 22f, valuePaint)

        canvas.drawText("رقم هاتف الصيدلي:", PAGE_WIDTH - 320f, currentY + 22f, labelPaint)
        canvas.drawText(order.pharmacistPhone.ifBlank { "غير محدد" }, PAGE_WIDTH - 415f, currentY + 22f, valuePaint)

        // Row 2
        canvas.drawText("المندوب المسؤول:", PAGE_WIDTH - 38f, currentY + 48f, labelPaint)
        canvas.drawText("${order.delegateName} (${order.delegatePhone})", PAGE_WIDTH - 150f, currentY + 48f, valuePaint)

        canvas.drawText("طريقة الاستلام:", PAGE_WIDTH - 320f, currentY + 48f, labelPaint)
        canvas.drawText("تسليم يدوي عبر المندوب", PAGE_WIDTH - 415f, currentY + 48f, valuePaint)

        currentY += 92f

        // Table Header
        val tableHeaderBg = Paint().apply { color = Color.parseColor("#094636") }
        canvas.drawRect(28f, currentY, PAGE_WIDTH - 28f, currentY + 26f, tableHeaderBg)

        val tableHeaderText = Paint().apply {
            color = Color.WHITE
            textSize = 10f
            isFakeBoldText = true
        }

        // Columns:
        // 1. المجموع (Total) 28..105 (Left)
        // 2. البونص (Bonus) 105..165
        // 3. الكمية (Qty) 165..215
        // 4. الإفرادي (Unit Price) 215..285
        // 5. الصلاحية (Expiry) 285..345
        // 6. الشركة (Lab) 345..415
        // 7. المادة / الدواء (Medicine) 415..567 (Right)

        tableHeaderText.textAlign = Paint.Align.CENTER
        canvas.drawText("المجموع (ل.س)", 66f, currentY + 17f, tableHeaderText)
        canvas.drawText("البونص 🎁", 135f, currentY + 17f, tableHeaderText)
        canvas.drawText("الكمية", 190f, currentY + 17f, tableHeaderText)
        canvas.drawText("الإفرادي", 250f, currentY + 17f, tableHeaderText)
        canvas.drawText("الصلاحية", 315f, currentY + 17f, tableHeaderText)
        canvas.drawText("الشركة", 380f, currentY + 17f, tableHeaderText)
        tableHeaderText.textAlign = Paint.Align.RIGHT
        canvas.drawText("اسم الصنف والدواء", PAGE_WIDTH - 38f, currentY + 17f, tableHeaderText)

        currentY += 26f

        // Table Rows
        val rowBgEven = Paint().apply { color = Color.WHITE }
        val rowBgOdd = Paint().apply { color = Color.parseColor("#F9FCFB") }
        val cellText = Paint().apply {
            color = Color.parseColor("#14241E")
            textSize = 9.5f
        }
        val bonusText = Paint().apply {
            color = Color.parseColor("#2E7D32")
            textSize = 9.5f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val rowBorderPaint = Paint().apply {
            color = Color.parseColor("#EAEFEB")
            strokeWidth = 1f
        }

        for ((index, item) in order.items.take(15).withIndex()) {
            val isEven = index % 2 == 0
            canvas.drawRect(28f, currentY, PAGE_WIDTH - 28f, currentY + 24f, if (isEven) rowBgEven else rowBgOdd)
            canvas.drawLine(28f, currentY + 24f, PAGE_WIDTH - 28f, currentY + 24f, rowBorderPaint)

            // 1. Subtotal
            cellText.textAlign = Paint.Align.CENTER
            cellText.isFakeBoldText = true
            canvas.drawText(numberFormat.format(item.itemTotal), 66f, currentY + 16f, cellText)

            // 2. Bonus
            if (item.bonusCount > 0) {
                canvas.drawText("+${item.bonusCount}", 135f, currentY + 16f, bonusText)
            } else {
                canvas.drawText("-", 135f, currentY + 16f, cellText)
            }

            // 3. Quantity
            cellText.isFakeBoldText = false
            canvas.drawText("${item.quantity}", 190f, currentY + 16f, cellText)

            // 4. Unit Price
            canvas.drawText(numberFormat.format(item.medicine.price), 250f, currentY + 16f, cellText)

            // 5. Expiry
            canvas.drawText(item.medicine.expirationDate, 315f, currentY + 16f, cellText)

            // 6. Company
            canvas.drawText(item.medicine.company, 380f, currentY + 16f, cellText)

            // 7. Medicine Name
            cellText.textAlign = Paint.Align.RIGHT
            cellText.isFakeBoldText = true
            val medName = if (item.medicine.name.length > 25) item.medicine.name.take(23) + ".." else item.medicine.name
            canvas.drawText(medName, PAGE_WIDTH - 38f, currentY + 16f, cellText)

            currentY += 24f
        }

        // Summary Card at bottom
        currentY = Math.max(currentY + 15f, 620f)
        val summaryBg = Paint().apply { color = Color.parseColor("#E8F5E9") }
        canvas.drawRect(PAGE_WIDTH - 260f, currentY, PAGE_WIDTH - 28f, currentY + 110f, summaryBg)
        canvas.drawRect(PAGE_WIDTH - 260f, currentY, PAGE_WIDTH - 28f, currentY + 110f, boxBorderPaint)

        val sumLabel = Paint().apply {
            color = Color.parseColor("#094636")
            textSize = 10.5f
            textAlign = Paint.Align.RIGHT
        }
        val sumVal = Paint().apply {
            color = Color.parseColor("#14241E")
            textSize = 11f
            isFakeBoldText = true
            textAlign = Paint.Align.LEFT
        }

        canvas.drawText("مجموع القطع المشتراة:", PAGE_WIDTH - 38f, currentY + 25f, sumLabel)
        canvas.drawText("${order.totalPaidPieces} قطعة", PAGE_WIDTH - 248f, currentY + 25f, sumVal)

        canvas.drawText("مجموع بونص مجاني 🎁:", PAGE_WIDTH - 38f, currentY + 48f, sumLabel)
        val greenBonusVal = Paint(sumVal).apply { color = Color.parseColor("#2E7D32") }
        canvas.drawText("+${order.totalBonusPieces} قطعة مجانية", PAGE_WIDTH - 248f, currentY + 48f, greenBonusVal)

        val goldDivider = Paint().apply { color = Color.parseColor("#D48806"); strokeWidth = 1.5f }
        canvas.drawLine(PAGE_WIDTH - 250f, currentY + 60f, PAGE_WIDTH - 38f, currentY + 60f, goldDivider)

        val grandTotalLabel = Paint().apply {
            color = Color.parseColor("#0F6E56")
            textSize = 13f
            isFakeBoldText = true
            textAlign = Paint.Align.RIGHT
        }
        val grandTotalVal = Paint().apply {
            color = Color.parseColor("#0F6E56")
            textSize = 14f
            isFakeBoldText = true
            textAlign = Paint.Align.LEFT
        }

        canvas.drawText("صافي الفاتورة الإجمالي:", PAGE_WIDTH - 38f, currentY + 88f, grandTotalLabel)
        canvas.drawText("${numberFormat.format(order.totalAmount)} ل.س", PAGE_WIDTH - 248f, currentY + 88f, grandTotalVal)

        // Important Notice & Delegates Contacts on Left
        val notesPaint = Paint().apply {
            color = Color.parseColor("#52695E")
            textSize = 9.5f
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("• ملاحظة هامة: الطلب حصراً عن طريق المندوب المختص بمنطقتك.", PAGE_WIDTH - 280f, currentY + 30f, notesPaint)
        canvas.drawText("• فواز البشير (المدينة): 0995711536", PAGE_WIDTH - 280f, currentY + 48f, notesPaint)
        canvas.drawText("• ابو عبيدة (أطراف المدينة): 0933907943", PAGE_WIDTH - 280f, currentY + 66f, notesPaint)
        canvas.drawText("• ابو احمد (المنصورة + الطبقة): 0940734543", PAGE_WIDTH - 280f, currentY + 84f, notesPaint)
        canvas.drawText("• اسماعيل (الخط الشمالي): 0936577184  • رائد (الخط الشرقي): 0939329718", PAGE_WIDTH - 280f, currentY + 102f, notesPaint)

        // Footer Bar
        val footerPaint = Paint().apply { color = Color.parseColor("#094636") }
        canvas.drawRect(0f, PAGE_HEIGHT - 35f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), footerPaint)

        val footerText = Paint().apply {
            color = Color.WHITE
            textSize = 9.5f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("مستودع الفواز للأدوية - نعتز بثقتكم وخدمتكم على مدار الساعة", PAGE_WIDTH / 2f, PAGE_HEIGHT - 14f, footerText)
    }

    // Direct Android Printing Functionality
    fun printInvoice(context: Context, pdfFile: File) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder(pdfFile.name)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    val input = FileInputStream(pdfFile)
                    val output = FileOutputStream(destination?.fileDescriptor)
                    input.copyTo(output)
                    input.close()
                    output.close()
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }

        printManager.print("فاتورة_مستودع_الفواز", printAdapter, PrintAttributes.Builder().build())
    }

    // Share / Open PDF Intent
    fun shareInvoice(context: Context, pdfFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "فاتورة طلبية أدوية - مستودع الفواز")
                putExtra(Intent.EXTRA_TEXT, "مرفق فاتورة طلبية الأدوية من مستودع الفواز.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "مشاركة / حفظ الفاتورة PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
