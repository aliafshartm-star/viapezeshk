package com.viapezeshk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.viapezeshk.app.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViaPezeshkTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("settings") { SettingsScreen(navController) }
                    composable("disease_hpv") { HpvScreen(navController) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopHeader(onSettingsClick = { navController.navigate("settings") }) },
        bottomBar = { BottomNavBar() },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            BannerSection()
            Spacer(modifier = Modifier.height(20.dp))
            QuickActionsSection(navController)
            Spacer(modifier = Modifier.height(24.dp))
            DiseaseListSection(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "ویاپزشک",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryTeal
        )
        
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(BackgroundLight)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "تنظیمات",
                tint = TextPrimary
            )
        }
    }
}

@Composable
fun BannerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1E2A38), Color(0xFF2C3E50))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "سیستم سلامت هوشمند",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "دستیار سلامت آفلاین شما",
                color = Color(0xFFA0AAB5),
                fontSize = 14.sp
            )
        }
        
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF6C7A89)))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryTeal))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF6C7A89)))
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem(icon = Icons.Outlined.Science, title = "تفسیر بالینی")
        QuickActionItem(icon = Icons.Outlined.ChildCare, title = "دفترچه کودک")
        QuickActionItem(icon = Icons.Outlined.Coronavirus, title = "بیماری‌ها", onClick = { navController.navigate("disease_hpv") })
        QuickActionItem(icon = Icons.Outlined.Accessibility, title = "آناتومی")
    }
}

@Composable
fun QuickActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = PrimaryTeal,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun DiseaseListSection(navController: NavController) {
    Text(
        text = "دسته‌بندی بیماری‌ها",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DiseaseListItem(title = "HPV (پاپیلوما)", subtitle = "ویروس شایع، انواع و پیشگیری", onClick = { navController.navigate("disease_hpv") })
        DiseaseListItem(title = "ریزش مو", subtitle = "علل، انواع و درمان‌های ریزش مو")
        DiseaseListItem(title = "میگرن", subtitle = "سردردهای شدید و ضربان‌دار")
        DiseaseListItem(title = "ریفلاکس معده", subtitle = "سوزش سر دل و برگشت اسید")
    }
}

@Composable
fun DiseaseListItem(title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PrimaryTeal.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Coronavirus,
                contentDescription = null,
                tint = PrimaryTeal
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(containerColor = SurfaceWhite, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "خانه") },
            label = { Text("خانه", fontSize = 11.sp) },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryTeal,
                selectedTextColor = PrimaryTeal,
                indicatorColor = PrimaryTeal.copy(alpha = 0.1f),
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            )
        )
        NavigationBarItem(icon = { Icon(Icons.Filled.Search, contentDescription = "جستجو") }, label = { Text("جستجو", fontSize = 11.sp) }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Filled.Star, contentDescription = "نشان‌ها") }, label = { Text("نشان‌ها", fontSize = 11.sp) }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Filled.Phone, contentDescription = "ارتباط") }, label = { Text("ارتباط", fontSize = 11.sp) }, selected = false, onClick = { })
    }
}

// ======== صفحه تنظیمات (دمو) ========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظیمات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("شهر شما:", fontWeight = FontWeight.Bold)
            Text("تهران", color = TextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("نسخه اپ: ۱.۰.۰", color = TextSecondary)
        }
    }
}

// ======== صفحه HPV (دمو) ========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HpvScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HPV", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryTeal.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Column {
                    Text("HPV (ویروس پاپیلومای انسانی)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryTeal)
                    Text("ویروس شایع، معمولاً بی‌علامت", color = TextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("علائم:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("اکثر موارد بدون علامت هستند. اما ممکن است شامل زگیل تناسلی باشد.", color = TextSecondary)
        }
    }
}