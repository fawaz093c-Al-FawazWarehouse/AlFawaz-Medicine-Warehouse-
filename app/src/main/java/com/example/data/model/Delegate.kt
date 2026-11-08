package com.example.data.model

data class SalesDelegate(
    val id: String,
    val name: String,
    val title: String,
    val region: String,
    val phone: String,
    val isDefault: Boolean = false
) {
    val territory: String get() = region
}

object DelegatesData {
    val delegatesList = listOf(
        SalesDelegate(
            id = "del_1",
            name = "فواز البشير",
            title = "مندوب المدينة",
            region = "المدينة (المركز)",
            phone = "0995711536",
            isDefault = true
        ),
        SalesDelegate(
            id = "del_2",
            name = "ابو عبيدة",
            title = "مندوب أطراف المدينة",
            region = "أطراف المدينة وضواحيها",
            phone = "0933907943"
        ),
        SalesDelegate(
            id = "del_3",
            name = "ابو احمد",
            title = "مندوب خط المنصورة + الطبقة",
            region = "خط المنصورة والطبقة",
            phone = "0940734543"
        ),
        SalesDelegate(
            id = "del_4",
            name = "اسماعيل",
            title = "مندوب الخط الشمالي",
            region = "الخط الشمالي وريف الرقة",
            phone = "0936577184"
        ),
        SalesDelegate(
            id = "del_5",
            name = "رائد",
            title = "مندوب الخط الشرقي",
            region = "الخط الشرقي والقرى المجاورة",
            phone = "0939329718"
        )
    )
}
