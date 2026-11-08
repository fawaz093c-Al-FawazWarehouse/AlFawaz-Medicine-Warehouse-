package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.DelegatesData
import com.example.data.model.SalesDelegate
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary

/**
 * Model representing each official exclusive partner agency of Al-Fawwaz Warehouse
 */
data class OfficialAgency(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val filterKeyword: String,
    val logoRes: Int,
    val slogan: String,
    val description: String,
    val keyProducts: String,
    val badge: String = "وكيل حصري معتمد"
)

@Composable
fun DelegatesScreen(
    modifier: Modifier = Modifier,
    onNavigateToBrochure: ((companyFilter: String) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPosterDialog by remember { mutableStateOf(false) }

    // Official 10 exclusive agency partners matching brochure 32 and user logos
    val officialAgencies = remember {
        listOf(
            OfficialAgency(
                id = "agency_allied",
                nameAr = "الشركة المتحدة للصناعات الدوائية",
                nameEn = "Allied Pharmaceutical Industries",
                filterKeyword = "المتحدة",
                logoRes = R.drawable.logo_allied,
                slogan = "جودة صيدلانية متقدمة وحلول وريدية وتخصصية",
                description = "رائدة صناعة مضادات التقيؤ الحديثة لمرضى الأورام (ابريماند)، بخاخات الربو والتحسس، مستحضرات العناية الجلدية والمطهرات.",
                keyProducts = "ابريماند (125، 80، 40 ملغ)، بيكولايد، فلوكالايد، بانتينول جل/كريم، سالين بخاخ، بوفيدون، كولد كريم بلس"
            ),
            OfficialAgency(
                id = "agency_medico",
                nameAr = "مختبرات ميديكو للصناعات الدوائية",
                nameEn = "Medico Laboratories",
                filterKeyword = "ميديكو",
                logoRes = R.drawable.logo_medico,
                slogan = "صرح وطني عريق في إنتاج الصادات الحيوية والمسكنات",
                description = "خطوط إنتاجية ضخمة تغطي الصادات واسعة الطيف، مضادات الالتهاب، قطرات ومراهم العين، وأدوية الجهاز القلبي والتنفسي.",
                keyProducts = "ازيترومايسين (250/500)، اولترامول (حب، شراب، تحاميل)، سيفيبرول، اولتراديكس مرهم وكريم، فوسي كلير، توبراميد، برومكسين"
            ),
            OfficialAgency(
                id = "agency_mediotech",
                nameAr = "شركة ميديوتيك للصناعات الدوائية",
                nameEn = "Mediotech / Mediotic",
                filterKeyword = "ميديوتيك",
                logoRes = R.drawable.logo_mediotech,
                slogan = "ابتكار مستمر في العلاجات التنفسية والجلدية",
                description = "تميز في إنتاج مضادات التحسس، قطرات ومراهم العيون، أدوية الهضم، مضادات الفطور، ومستحضرات الرذاذ الأنفي الموضعي.",
                keyProducts = "اتاكس شراب، الكوميد (رذاذ، كريم، مرهم)، برومكسين، سوتال، سي سبراي، فلوكوناز، كولشيميد، موتالون تحاميل، ميديوفوريل"
            ),
            OfficialAgency(
                id = "agency_ibn_hayyan",
                nameAr = "شركة ابن حيان للصناعات الدوائية",
                nameEn = "Ibn Hayyan Pharmaceuticals",
                filterKeyword = "ابن حيان",
                logoRes = R.drawable.logo_ibn_hayyan,
                slogan = "رائدة الأدوية الإسعافية والأمبولات والمحاليل الوريدية",
                description = "تخصص عالي في الصادات القوية، مضادات التشنج، أدوية القلب والأوعية، مسكنات الألم ومضادات الالتهاب المركزية.",
                keyProducts = "اتروبين، ادرينالين أمبولات، اوغماسيل شراب، اموكسام، ديكلون (تحاميل وأمبولات)، تروكسيفين، فاسكودبين، فالبرون، كلوكسام"
            ),
            OfficialAgency(
                id = "agency_ibn_rushd",
                nameAr = "شركة ابن رشد للصناعات الدوائية",
                nameEn = "Ibn Roshed Pharmaceuticals",
                filterKeyword = "ابن رشد",
                logoRes = R.drawable.logo_ibn_rushd,
                slogan = "علاجات متطورة لصحة العظام والمفاصل والمناعة",
                description = "مجموعة نوعية تشمل مسكنات آلام المفاصل، المسكنات المركزية، الفيتامينات العلاجية ومضادات الالتهاب غير الستيرويدية.",
                keyProducts = "ارجين فورت، اوستاكال، باراسيتامول تحاميل، برونكورد، تابلوكسيب، ديكلوفيناك، سامروش، شيتوكان، لبلاب توس"
            ),
            OfficialAgency(
                id = "agency_barakat",
                nameAr = "شركة بركات للصناعات الدوائية",
                nameEn = "Barakat Pharmaceutical Industries (BPI)",
                filterKeyword = "بركات",
                logoRes = R.drawable.logo_barakat,
                slogan = "إحدى كبرى القلاع الدوائية في أدوية السكري والقلب والصادات",
                description = "تغطية علاجية واسعة لأدوية الأمراض المزمنة، خافضات السكر الحديثة، الصادات الفموية، وأشربة الأطفال والمسكنات.",
                keyProducts = "ايمبا غليف، اديزيم، اكزانتريك، اموكسيسللين، باراسيتامول، باركاسين، ديكلوبار، كلافينين، باركانيدازول"
            ),
            OfficialAgency(
                id = "agency_domna",
                nameAr = "شركة دومنا للصناعات الدوائية",
                nameEn = "Domina Pharmaceuticals (Since 1988)",
                filterKeyword = "دومنا",
                logoRes = R.drawable.logo_domna,
                slogan = "عراقة صيدلانية متخصصة في العلاجات الجلدية والعصبية والعيون",
                description = "أكثر من 35 عاماً من الخبرة في تصنيع الكريمات والمراهم التخصصية، مضادات الفطور، قطرات العين، وأدوية الجهاز العصبي.",
                keyProducts = "ادبالين جل، اريبزولمين، اريتروسول، بانتينول، تازاروتين، دوكريتول، دوميكول، فابو مرهم، بينوكسيد"
            ),
            OfficialAgency(
                id = "agency_lama",
                nameAr = "شركة لاما للصناعات الدوائية",
                nameEn = "Lama Pharma",
                filterKeyword = "لاما",
                logoRes = R.drawable.logo_lama,
                slogan = "ريادة وطنية في السيفالوسبورينات والمضادات الحيوية النوعية",
                description = "تركيز متخصص على الصادات الوريدية والفموية المتطورة، مركبات السولباكتام، والسيفالوسبورينات الحديثة للأطفال والبالغين.",
                keyProducts = "اكساسيف، اونودينير، روسيلاين فيال، زوناسول، سيفازولين، سيفيكسيم، كلاماتاكس، لاميديم"
            ),
            OfficialAgency(
                id = "agency_happy_cure",
                nameAr = "شركة هابي كيور للمتممات الغذائية",
                nameEn = "Happy Cure",
                filterKeyword = "هابي كيور",
                logoRes = R.drawable.logo_happy_cure,
                slogan = "أرقى تشكيلات الفوارات العلاجية ومتممات المناعة والصحة",
                description = "تركيبات سويسرية وألمانية متقدمة من المتممات الغذائية، فوارات الفيتامين سي والزنك، أوميغا 3، ودعم المفاصل والجمال.",
                keyProducts = "HSN ادفانسيد، اوميغا 3، غلوكوزامين ام اس ام، فوار سي بلس، فوار هابي غلوبين، فيتامين D3+ZN+C، مغنيزيوم بلس"
            ),
            OfficialAgency(
                id = "agency_celia",
                nameAr = "حليب أطفال سيليا الفرنسي",
                nameEn = "Celia Infant Nutrition",
                filterKeyword = "سيليا",
                logoRes = R.drawable.logo_celia,
                slogan = "التغذية الطبية الفرنسية الموثوقة لنمو وصحة الرضع",
                description = "حليب الرضع والنمو الطبي المعتمد دولياً، المدعم بالبريبايوتكس، أوميغا 3 والفيتامينات الأساسية لمناعة ونمو سليم للطفل.",
                keyProducts = "حليب سيليا رقم 1 (0-6 أشهر)، سيليا رقم 2 (6-12 شهراً)، سيليا رقم 3 للنمو، تركيبات خاصة للهضم والتحسس"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Corporate Header Card
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .size(68.dp)
                                .border(1.5.dp, EmeraldPrimary.copy(alpha = 0.25f), CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_alfawaz),
                                contentDescription = "شعار مستودع الفواز",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "مستودع الفواز للأدوية البشرية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "« حيث تبدأ الثقة »",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AmberGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Where trust begins",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // View Corporate Poster Button
                    OutlinedButton(
                        onClick = { showPosterDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_view_corporate_poster")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("البوستر الرسمي", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tab Row: Exclusive Agencies vs Sales Delegates
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EmeraldPrimary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("الوكالات الحصرية الرسمية (10)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("فريق المندوبين والتوزيع", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            // ==========================================
            // 1. EXCLUSIVE AGENCIES TAB (All 10 Companies)
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Corporate Poster Preview Banner
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPosterDialog = true }
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_corporate_poster),
                                    contentDescription = "بوستر مستودع الفواز الرسمي",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Surface(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(bottomEnd = 12.dp),
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fullscreen,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "انقر لتكبير البوستر المؤسسي الرسمي",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "مستودع الفواز – وكلاء حصريون معتمدون",
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldDark,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "الموزع الحصري المعتمد لـ 10 شركات دوائية رائدة بروشور 32",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32)
                                    )
                                }

                                Surface(
                                    color = EmeraldPrimary,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "10 وكالات",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Listing of all 10 Official Agencies with Authentic Logos
                items(officialAgencies, key = { it.id }) { agency ->
                    OfficialAgencyCard(
                        agency = agency,
                        onBrowseProducts = {
                            onNavigateToBrochure?.invoke(agency.filterKeyword)
                        }
                    )
                }
            }
        } else {
            // ==========================================
            // 2. SALES DELEGATES TAB (Matching Official Brochure Page 45)
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Official Brochure Notice Banner (page 45)
                item {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE65100),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "ملاحظة هامة (بروشور مستودع الفواز 32):",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100),
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "« الطلب حصراً عن طريق المندوب »",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBF360C),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "يرجى التواصل مع المندوب المعتمد لمنطقتك لتأكيد الطلب وتحديد مواعيد التوريد والفاتورة.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF4E342E)
                                )
                            }
                        }
                    }
                }

                // Official Delegates List
                items(DelegatesData.delegatesList) { delegate ->
                    DelegateCard(
                        delegate = delegate,
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${delegate.phone}"))
                            context.startActivity(intent)
                        },
                        onWhatsApp = {
                            val msg = "مرحباً يا ${delegate.name}، معك صيدلية في منطقة (${delegate.territory}). أود تثبيت طلبية دوائية من بروشور مستودع الفواز."
                            val url = "https://api.whatsapp.com/send?phone=963${delegate.phone.removePrefix("0")}&text=${Uri.encode(msg)}"
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "تعذر فتح تطبيق واتساب، يمكنك الاتصال هاتفياً", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    // ==========================================
    // Corporate Brand Poster Full-Screen Dialog
    // ==========================================
    if (showPosterDialog) {
        Dialog(
            onDismissRequest = { showPosterDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "البوستر المؤسسي الرسمي",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "مستودع الفواز للأدوية البشرية – حيث تبدأ الثقة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberGold
                                )
                            }
                        }

                        IconButton(
                            onClick = { showPosterDialog = false },
                            modifier = Modifier.testTag("btn_close_poster_dialog")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    // Full resolution poster
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F8F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_corporate_poster),
                            contentDescription = "البوستر الرسمي لمستودع الفواز",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "الشركاء والوكلاء المعتمدون:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "المتحدة Allied • مختبرات ميديكو • ميديوتيك • ابن حيان • ابن رشد • بركات BPI • دومنا • لاما فارما • هابي كيور • حليب سيليا",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showPosterDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تم والعودة للتطبيق", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Clean corporate card displaying each official exclusive agency
 */
@Composable
fun OfficialAgencyCard(
    agency: OfficialAgency,
    onBrowseProducts: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("agency_card_${agency.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Authentic Logo + Names + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official Logo Frame
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(68.dp)
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = agency.logoRes),
                            contentDescription = agency.nameAr,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = agency.nameAr,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )

                        Surface(
                            color = AmberGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = AmberGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = agency.badge,
                                    color = AmberGold,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = agency.nameEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = agency.slogan,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = agency.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Key Product Lines
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "أبرز الأصناف في البروشور:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Text(
                            text = agency.keyProducts,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button: Browse Agency Products in Brochure
            OutlinedButton(
                onClick = onBrowseProducts,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "استعراض أصناف ${agency.filterKeyword} في البروشور",
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DelegateCard(
    delegate: SalesDelegate,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("delegate_card_${delegate.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (delegate.isDefault) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (delegate.isDefault) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = delegate.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark
                            )
                            if (delegate.isDefault) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = EmeraldPrimary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "المسؤول العام",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = delegate.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "خط التوزيع: ${delegate.territory}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اتصال", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onWhatsApp,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("واتساب", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
