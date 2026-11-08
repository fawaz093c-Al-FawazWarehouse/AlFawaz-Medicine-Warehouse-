package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Medicine
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PharmacyBackground
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

data class PromoSlide(
    val drawableRes: Int,
    val tag: String,
    val title: String,
    val subtitle: String,
    val accentColor: Color = EmeraldPrimary
)

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val medicines by viewModel.filteredMedicines.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale("ar", "SY")) }

    // Featured medicines with high bonus
    val featuredBonusMeds = remember(medicines) {
        medicines.filter { it.bonusFree > 0 }.take(6)
    }

    val promoSlides = remember {
        listOf(
            PromoSlide(
                drawableRes = R.drawable.img_corporate_poster,
                tag = "البوستر المؤسسي الرسمي",
                title = "حيث تبدأ الثقة • Where trust begins",
                subtitle = "الوكلاء الحصريون لـ 10 شركات دوائية رائدة بروشور 32",
                accentColor = Color(0xFF00796B)
            ),
            PromoSlide(
                drawableRes = R.drawable.img_pharma_banner1_1789256772968,
                tag = "عروض البونص الإضافي الحصري",
                title = "بونص مجاني يصل حتى 5+20 على أهم الأصناف",
                subtitle = "وفّر أكثر لصيدليتك مع حسومات مستودع الفواز الحصرية",
                accentColor = EmeraldPrimary
            ),
            PromoSlide(
                drawableRes = R.drawable.img_pharma_banner2_1789256782549,
                tag = "توزيع دوائي سريع وموثوق",
                title = "تغطية شاملة وتوريد يومي لكافة الصيدليات",
                subtitle = "جاهزية فورية لطلبياتكم عبر مندوبي التوزيع المعتمدين",
                accentColor = AmberGold
            ),
            PromoSlide(
                drawableRes = R.drawable.img_pharma_banner3_1789256791068,
                tag = "وكلاء حصريون لأكبر الشركات",
                title = "أدوية بشرية ومستحضرات صيدلانية معتمدة",
                subtitle = "المتحدة، ميديكو، ميديوتيك، ابن حيان، ابن رشد، بركات، دومنا، لاما، هابي كيور، سيليا",
                accentColor = Color(0xFF1E88E5)
            )
        )
    }

    // Official 10 partner agencies for quick logo browsing
    val partnerAgencies = remember {
        listOf(
            Triple("المتحدة Allied", "Allied Pharma", R.drawable.logo_allied),
            Triple("مختبرات ميديكو", "Medico Labs", R.drawable.logo_medico),
            Triple("ميديوتيك", "Mediotech", R.drawable.logo_mediotech),
            Triple("ابن حيان", "Ibn Hayyan", R.drawable.logo_ibn_hayyan),
            Triple("ابن رشد", "Ibn Roshed", R.drawable.logo_ibn_rushd),
            Triple("بركات BPI", "Barakat", R.drawable.logo_barakat),
            Triple("دومنا Domina", "Domna Pharma", R.drawable.logo_domna),
            Triple("لاما فارما", "Lama Pharma", R.drawable.logo_lama),
            Triple("هابي كيور", "Happy Cure", R.drawable.logo_happy_cure),
            Triple("حليب سيليا", "Celia Milk", R.drawable.logo_celia)
        )
    }

    val pagerState = rememberPagerState(pageCount = { promoSlides.size })

    // Auto-advance carousel smoothly and quietly
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500)
            val nextPage = (pagerState.currentPage + 1) % promoSlides.size
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(durationMillis = 800)
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFF6FAF8),
                        Color(0xFFEFF6F3)
                    )
                )
            ),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ==========================================
        // 1. TOP HEADER: Warehouse Logo & Slogan
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Al-Fawwaz Clean Warehouse Authentic Logo (Enlarged prominent hero display)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.96f)
                        .padding(top = 2.dp)
                        .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_alfawaz),
                            contentDescription = "شعار مستودع الفواز للأدوية البشرية",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(145.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Official Warehouse Title & Slogan
                Text(
                    text = "« مستودع الفواز للأدوية البشرية »",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(2.dp)
                            .background(AmberGold, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "حيث تبدأ الثقة  •  Where trust begins",
                        style = MaterialTheme.typography.labelMedium,
                        color = AmberGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(2.dp)
                            .background(AmberGold, CircleShape)
                    )
                }
            }
        }

        // Active Pharmacist Order Alert Card (If items in cart)
        if (cartItems.isNotEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.5.dp, EmeraldPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "طلبية الصيدلية قيد التجهيز (${cartItems.size} أصناف)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "إجمالي السعر: ${numberFormat.format(cartItems.sumOf { it.itemTotal })} ل.س",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldPrimary
                                )
                            }
                        }

                        Button(
                            onClick = { onNavigate(AppScreen.BROCHURE) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("متابعة الطلب", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 2. ANIMATED PROMOTIONAL CAROUSEL / SLIDER
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = AmberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "العروض والحسومات الحصرية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }

                    Surface(
                        color = EmeraldLight,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "تحديث يومي",
                            color = EmeraldPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Pager for smooth slide transitions
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .testTag("home_promo_carousel"),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { pageIndex ->
                        val slide = promoSlides[pageIndex]
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onNavigate(AppScreen.BROCHURE) }
                        ) {
                            // Banner Photo
                            Image(
                                painter = painterResource(id = slide.drawableRes),
                                contentDescription = slide.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Gradient Overlay for High Readability
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.2f),
                                                Color.Black.copy(alpha = 0.75f)
                                            )
                                        )
                                    )
                            )

                            // Text Content on Banner
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Tag Chip
                                Surface(
                                    color = slide.accentColor,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = slide.tag,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = slide.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = slide.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFE0E0E0),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Carousel Dot Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(promoSlides.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(
                                    width = if (isSelected) 22.dp else 7.dp,
                                    height = 6.dp
                                )
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) EmeraldPrimary else Color.LightGray.copy(alpha = 0.6f)
                                )
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2.5 OFFICIAL PARTNER AGENCIES LOGO STRIP
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الوكالات الحصرية الرسمية (10 شركات)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                        }
                        Text(
                            text = "حيث تبدأ الثقة • شركاء التوزيع المعتمدون",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGold
                        )
                    }

                    OutlinedButton(
                        onClick = { onNavigate(AppScreen.DELEGATES) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("التفاصيل", fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(partnerAgencies) { (nameAr, nameEn, logoRes) ->
                        val companyFilter = when {
                            nameAr.contains("المتحدة") -> "المتحدة"
                            nameAr.contains("ميديكو") -> "ميديكو"
                            nameAr.contains("ميديوتيك") -> "ميديوتيك"
                            nameAr.contains("ابن حيان") -> "ابن حيان"
                            nameAr.contains("ابن رشد") -> "ابن رشد"
                            nameAr.contains("بركات") -> "بركات"
                            nameAr.contains("دومنا") -> "دومنا"
                            nameAr.contains("لاما") -> "لاما"
                            nameAr.contains("هابي كيور") -> "هابي كيور"
                            nameAr.contains("سيليا") -> "سيليا"
                            else -> nameAr
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .width(135.dp)
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.setSelectedCompany(companyFilter)
                                    onNavigate(AppScreen.BROCHURE)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFAFAFA),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = logoRes),
                                            contentDescription = nameAr,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = nameAr,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = nameEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF1976D2),
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. SMART QUICK NAVIGATION TABS / BUTTONS
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "التنقل السريع والخدمات الصيدلانية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 + Row Grid of Smart Navigation Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tab 1: Pharmacist Order Interface
                    QuickNavCard(
                        title = "طلب أدوية الصيدلية",
                        subtitle = "اختيار الأدوية مع السعر والبونص",
                        icon = Icons.Default.AddShoppingCart,
                        badge = "طلب فوري",
                        containerColor = Color(0xFFE8F5E9),
                        iconTint = EmeraldPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "home_nav_brochure",
                        onClick = { onNavigate(AppScreen.BROCHURE) }
                    )

                    // Tab 2: Orders & Cart
                    QuickNavCard(
                        title = "الفاتورة والسلة",
                        subtitle = if (cartItems.isEmpty()) "السلة فارغة" else "${cartItems.sumOf { it.quantity }} علبة • ${numberFormat.format(cartItems.sumOf { it.itemTotal })} ل.س",
                        icon = Icons.Default.ReceiptLong,
                        badge = if (cartItems.isNotEmpty()) "${cartItems.size} أصناف" else null,
                        containerColor = Color(0xFFE0F2F1),
                        iconTint = Color(0xFF00796B),
                        modifier = Modifier.weight(1f),
                        testTag = "home_nav_cart",
                        onClick = { onNavigate(AppScreen.CART) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tab 3: Exclusive Agencies & Delegates
                    QuickNavCard(
                        title = "الوكلاء الحصريين",
                        subtitle = "الشركات والمندوبين",
                        icon = Icons.Default.Business,
                        badge = "معتمد",
                        containerColor = Color(0xFFFFF8E1),
                        iconTint = AmberGold,
                        modifier = Modifier.weight(1f),
                        testTag = "home_nav_delegates",
                        onClick = { onNavigate(AppScreen.DELEGATES) }
                    )

                    // Tab 4: Profile & Settings
                    QuickNavCard(
                        title = "الملف الشخصي",
                        subtitle = "بيانات الصيدلية والإعدادات",
                        icon = Icons.Default.Person,
                        badge = null,
                        containerColor = Color(0xFFF3E5F5),
                        iconTint = Color(0xFF7B1FA2),
                        modifier = Modifier.weight(1f),
                        testTag = "home_nav_profile",
                        onClick = { onNavigate(AppScreen.PROFILE_SETTINGS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab 5 & 6 Secondary Row (AI Pharmacist & Price Alerts)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // AI Pharmacist
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFFFFDE7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(AppScreen.AI_PHARMACIST) }
                            .testTag("home_nav_ai_pharmacist")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "المساعد الذكي",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "بدائل وتداخلات",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Price Alerts
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE8EAF6),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(AppScreen.PRICE_ALERTS) }
                            .testTag("home_nav_price_alerts")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFF3949AB),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "تنبيهات الأسعار",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "متابعة التخفيضات",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // 4. TOP FEATURED BONUS OFFERS CAROUSEL ROW
        // ==========================================
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "أبرز عروض البونص المباشر 🎁",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Text(
                            text = "أصناف متوفرة بأعلى نسبة بونص مجاني لصيدليتك",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = { onNavigate(AppScreen.BROCHURE) },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("عرض الكل", fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featuredBonusMeds, key = { it.id }) { med ->
                        FeaturedMedicineCard(
                            medicine = med,
                            onAddToCart = { qty ->
                                viewModel.addToCart(med, qty)
                            }
                        )
                    }
                }
            }
        }

        // ==========================================
        // 5. WAREHOUSE TRUST & ADVANTAGES BANNER
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مميزات التعامل مع مستودع الفواز",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AdvantageItem(
                            icon = Icons.Default.Speed,
                            title = "توصيل فوري",
                            subtitle = "جاهزية وسرعة"
                        )
                        AdvantageItem(
                            icon = Icons.Default.LocalOffer,
                            title = "بونص مجزي",
                            subtitle = "حسم فوري"
                        )
                        AdvantageItem(
                            icon = Icons.Default.Security,
                            title = "ضمان الأصالة",
                            subtitle = "مصدر موثوق"
                        )
                        AdvantageItem(
                            icon = Icons.Default.ReceiptLong,
                            title = "فواتير PDF",
                            subtitle = "طباعة ومشاركة"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String?,
    containerColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        shadowElevation = 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                if (badge != null) {
                    Surface(
                        color = iconTint,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badge,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FeaturedMedicineCard(
    medicine: Medicine,
    onAddToCart: (Int) -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale("ar", "SY")) }

    Card(
        modifier = Modifier
            .width(200.dp)
            .testTag("featured_med_${medicine.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Company & Bonus Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldLight,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = medicine.company,
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (medicine.bonus.isNotBlank()) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF43A047))
                    ) {
                        Text(
                            text = "🎁 بونص ${medicine.bonus}",
                            color = Color(0xFF1B5E20),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Medicine Name
            Text(
                text = medicine.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(40.dp)
            )

            if (medicine.activeIngredient.isNotBlank()) {
                Text(
                    text = medicine.activeIngredient,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price without any expiry date display
            Text(
                text = "${numberFormat.format(medicine.price)} ل.س",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                color = EmeraldPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddToCart(medicine.bonusPaid.coerceAtLeast(1)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة للسلة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AdvantageItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(68.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = EmeraldLight,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = EmeraldDark,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
