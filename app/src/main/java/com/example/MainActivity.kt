package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.screens.AiPharmacistScreen
import com.example.ui.screens.BrochureScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.DelegatesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.PriceAlertsScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AmberGold
import com.example.ui.theme.EmeraldDark
import com.example.ui.AppScreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    NotificationPermissionHandler()
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NotificationPermissionHandler() {
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { _ -> }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: MainViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val uiFeedback by viewModel.uiFeedback.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiFeedback) {
        uiFeedback?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    val cartTotalCount = cartItems.sumOf { it.quantity }
    val unreadNotifsCount = notifications.size

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                shadowElevation = 2.dp,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_alfawaz),
                                    contentDescription = "شعار مستودع الفواز",
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "مستودع الفواز للأدوية",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = currentScreen.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldLight
                                )
                            }
                        }
                    },
                    actions = {
                        // AI Pharmacist Shortcut
                        IconButton(
                            onClick = { currentScreen = AppScreen.AI_PHARMACIST },
                            modifier = Modifier.testTag("topbar_btn_ai")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "المساعد الذكي",
                                tint = if (currentScreen == AppScreen.AI_PHARMACIST) AmberGold else Color.White
                            )
                        }

                        // Notifications bell button with badge
                        IconButton(
                            onClick = { currentScreen = AppScreen.NOTIFICATIONS },
                            modifier = Modifier.testTag("topbar_btn_notifications")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifsCount > 0) {
                                        Badge(containerColor = AmberGold, contentColor = Color.Black) {
                                            Text("$unreadNotifsCount", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "الإشعارات",
                                    tint = if (currentScreen == AppScreen.NOTIFICATIONS) AmberGold else Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = EmeraldDark
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    // 1. Home (الرئيسية)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { currentScreen = AppScreen.HOME },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            indicatorColor = EmeraldLight
                        ),
                        modifier = Modifier.testTag("tab_home")
                    )

                    // 2. Pharmacist Orders (طلب أدوية)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.BROCHURE,
                        onClick = { currentScreen = AppScreen.BROCHURE },
                        icon = { Icon(Icons.Default.AddShoppingCart, contentDescription = "طلب أدوية") },
                        label = { Text("طلب أدوية", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            indicatorColor = EmeraldLight
                        ),
                        modifier = Modifier.testTag("tab_brochure")
                    )

                    // 3. Cart & Invoice (الفاتورة والسلة)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.CART || currentScreen == AppScreen.ORDERS_HISTORY,
                        onClick = { currentScreen = AppScreen.CART },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartTotalCount > 0) {
                                        Badge(containerColor = EmeraldPrimary, contentColor = Color.White) {
                                            Text("$cartTotalCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = "الفاتورة والسلة")
                            }
                        },
                        label = { Text("الفاتورة والسلة", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            indicatorColor = EmeraldLight
                        ),
                        modifier = Modifier.testTag("tab_cart")
                    )

                    // 4. Exclusive Agencies & Reps (الوكلاء الحصريين)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DELEGATES,
                        onClick = { currentScreen = AppScreen.DELEGATES },
                        icon = { Icon(Icons.Default.Business, contentDescription = "الوكلاء الحصريين") },
                        label = { Text("الوكلاء الحصريين", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            indicatorColor = EmeraldLight
                        ),
                        modifier = Modifier.testTag("tab_delegates")
                    )

                    // 5. Profile & Settings (الملف الشخصي / الإعدادات)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROFILE_SETTINGS,
                        onClick = { currentScreen = AppScreen.PROFILE_SETTINGS },
                        icon = { Icon(Icons.Default.Person, contentDescription = "الملف الشخصي") },
                        label = { Text("الملف الشخصي", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            indicatorColor = EmeraldLight
                        ),
                        modifier = Modifier.testTag("tab_profile_settings")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.HOME -> HomeScreen(
                        viewModel = viewModel,
                        onNavigate = { screen -> currentScreen = screen }
                    )
                    AppScreen.BROCHURE -> BrochureScreen(
                        viewModel = viewModel,
                        onNavigateToCart = { currentScreen = AppScreen.CART }
                    )
                    AppScreen.CART -> CartScreen(
                        viewModel = viewModel,
                        onNavigateToBrochure = { currentScreen = AppScreen.BROCHURE }
                    )
                    AppScreen.DELEGATES -> DelegatesScreen(
                        onNavigateToBrochure = { company ->
                            viewModel.setSelectedCompany(company)
                            currentScreen = AppScreen.BROCHURE
                        }
                    )
                    AppScreen.PROFILE_SETTINGS -> ProfileSettingsScreen(
                        viewModel = viewModel
                    )
                    AppScreen.ORDERS_HISTORY -> OrderHistoryScreen(
                        viewModel = viewModel,
                        onNavigateToCart = { currentScreen = AppScreen.CART }
                    )
                    AppScreen.PRICE_ALERTS -> PriceAlertsScreen(
                        viewModel = viewModel,
                        onNavigateToBrochure = { currentScreen = AppScreen.BROCHURE }
                    )
                    AppScreen.AI_PHARMACIST -> AiPharmacistScreen(
                        viewModel = viewModel
                    )
                    AppScreen.NOTIFICATIONS -> NotificationsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }

        // Smooth Splash / Loading Overlay
        AnimatedVisibility(
            visible = showSplash,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SplashScreen(onDismiss = { showSplash = false })
        }
    }
}
