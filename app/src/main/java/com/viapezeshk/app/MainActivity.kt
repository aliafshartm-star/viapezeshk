package com.viapezeshk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.viapezeshk.app.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViaPezeshkTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = { TopHeader() },
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
            QuickActionsSection()
            Spacer(modifier = Modifier.height(24.dp))
            DiseaseListSection()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // لوگو
        Text(
            text = "ویاپزشک",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryTeal
        )
        
        // دکمه تنظیمات (به جای آدمک)
        IconButton(
            onClick = { /* تنظیمات */ },
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
    // کادر تبلیغات شبیه عکس
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1E2A38), Color(0xFF2C3E50)) // رنگ تیره شبیه بنر Via
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
        
        // نقطه‌های پایین بنر (شبیه عکس)
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
fun QuickActionsSection() {
    // آیکون‌های ۴ تایی مثل عکس
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        QuickActionItem(icon = Icons.Outlined.Science, title = "تفسیر بالینی")
        QuickActionItem(icon = Icons.Outlined.ChildCare, title = "دفترچه کودک")
        QuickActionItem(icon = Icons.Outlined.Coronavirus, title = "بیماری‌ها")
        QuickActionItem(icon = Icons.Outlined.Accessibility, title = "آناتومی")
    }
}

@Composable
fun QuickActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
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
fun DiseaseListSection() {
    Text(
        text = "دسته‌بندی بیماری‌ها",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    // لیست بیماری‌ها (به جای لیست ارزها)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DiseaseListItem(title = "HPV (پاپیلوما)", subtitle = "ویروس شایع، انواع و پیشگیری")
        DiseaseListItem(title = "ریزش مو", subtitle = "علل، انواع و درمان‌های ریزش مو")
        DiseaseListItem(title = "میگرن", subtitle = "سردردهای شدید و ضربان‌دار")
        DiseaseListItem(title = "ریفلاکس معده", subtitle = "سوزش سر دل و برگشت اسید")
    }
}

@Composable
fun DiseaseListItem(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // آیکون سمت راست
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
        
        // متن‌ها
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        
        // فلش سمت چپ
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}

@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = SurfaceWhite,
        tonalElevation = 8.dp
    ) {
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
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "جستجو") },
            label = { Text("جستجو", fontSize = 11.sp) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "نشان‌ها") },
            label = { Text("نشان‌ها", fontSize = 11.sp) },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Phone, contentDescription = "ارتباط") },
            label = { Text("ارتباط", fontSize = 11.sp) },
            selected = false,
            onClick = { }
        )
    }
}