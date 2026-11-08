@file:OptIn(ExperimentalMaterial3Api::class)

package com.viapezeshk.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.viapezeshk.app.data.Banner
import com.viapezeshk.app.data.ContactItem
import com.viapezeshk.app.data.Disease
import com.viapezeshk.app.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViaPezeshkTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("search") { SearchScreen(navController) }
                    composable("bookmarks") { PlaceholderScreen(navController, "نشان‌شده‌ها") }
                    composable("contact") { ContactScreen(navController) }
                    composable("settings") { SettingsScreen(navController) }
                    composable("lab_calculator") { LabCalculatorScreen(navController) }
                    composable("pregnancy") { PregnancyScreen(navController) }
                    
                    // صفحه جدید برای نمایش بیماری‌های یک دسته خاص
                    composable("category/{categoryName}") { backStackEntry ->
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                        CategoryDiseasesScreen(navController, categoryName)
                    }
                    
                    composable("disease_detail/{diseaseId}") { backStackEntry ->
                        val diseaseId = backStackEntry.arguments?.getString("diseaseId") ?: ""
                        DiseaseDetailScreen(navController, diseaseId)
                    }
                }
            }
        }
    }
}

fun getUserCity(context: Context): String {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return prefs.getString("user_city", "همهٔ ایران") ?: "همهٔ ایران"
}

fun saveUserCity(context: Context, city: String) {
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("user_city", city).apply()
}

data class CategoryItem(val name: String, val icon: String)

val diseaseCategories = listOf(
    CategoryItem("مغز و اعصاب", "🧠"),
    CategoryItem("چشم و بینایی", "👁️"),
    CategoryItem("گوش، حلق و بینی", "👂"),
    CategoryItem("دهان و دندان", "🦷"),
    CategoryItem("قلب و ریه", "❤️"),
    CategoryItem("معده و گوارش", "🫃"),
    CategoryItem("کلیه و مجاری ادراری", "🫘"),
    CategoryItem("دستگاه تناسلی", "🧬"),
    CategoryItem("استخوان و مفاصل", "🦴"),
    CategoryItem("پوست و مو", "💇"),
    CategoryItem("غدد و خون", "🩸"),
    CategoryItem("عوامل بیماری‌زا", "🦠")
)

@Composable
fun HomeScreen(
    navController: NavController, 
    viewModel: DiseaseViewModel = viewModel(),
    bannerViewModel: BannerViewModel = viewModel()
) {
    val context = LocalContext.current
    val banners by bannerViewModel.banners.collectAsState()

    LaunchedEffect(Unit) {
        val userCity = getUserCity(context)
        bannerViewModel.loadBanners("home_main", userCity)
    }

    Scaffold(
        topBar = { TopHeader(onSettingsClick = { navController.navigate("settings") }) },
        bottomBar = { BottomNavBar(navController) },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            BannerCarousel(banners = banners)
            Spacer(modifier = Modifier.height(20.dp))
            QuickActionsSection(navController)
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("دسته‌بندی‌های آناتومی", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
            
            // گرید دسته‌بندی‌ها
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                diseaseCategories.chunked(2).forEach { rowCategories ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowCategories.forEach { category ->
                            CategoryCard(category, navController)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, navController: NavController) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .clickable { navController.navigate("category/${category.name}") }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = category.icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = category.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun TopHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(SurfaceWhite).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "ویاپزشک", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryTeal)
        IconButton(onClick = onSettingsClick, modifier = Modifier.clip(CircleShape).background(BackgroundLight)) {
            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "تنظیمات", tint = TextPrimary)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BannerCarousel(banners: List<Banner>) {
    val context = LocalContext.current
    if (banners.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(20.dp)).background(brush = Brush.linearGradient(colors = listOf(Color(0xFF1E2A38), Color(0xFF2C3E50))))) {
            Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
                Text(text = "سیستم سلامت هوشمند", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "دستیار سلامت آفلاین شما", color = Color(0xFFA0AAB5), fontSize = 14.sp)
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { banners.size })
    LaunchedEffect(pagerState) {
        while (true) { delay(4000); val nextPage = (pagerState.currentPage + 1) % banners.size; pagerState.animateScrollToPage(nextPage) }
    }

    Column {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(160.dp)) { page ->
            val banner = banners[page]
            val bgColor = try { Color(android.graphics.Color.parseColor(banner.content.bgColor ?: "#1E2A38")) } catch (e: Exception) { Color(0xFF1E2A38) }
            
            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)).background(bgColor).clickable {
                banner.action?.let { action ->
                    when (action.type) {
                        "url" -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(action.value)))
                        "tel" -> context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${action.value}")))
                    }
                }
            }) {
                if (!banner.content.image.isNullOrEmpty()) {
                    AsyncImage(model = banner.content.image, contentDescription = banner.content.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
                Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Bottom) {
                    Text(text = banner.content.title ?: "", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = banner.content.subtitle ?: "", color = Color(0xFFD1D1D6), fontSize = 14.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width = animateDpAsState(if (isSelected) 24.dp else 8.dp, label = "")
                Box(modifier = Modifier.padding(horizontal = 4.dp).height(8.dp).width(width.value).clip(CircleShape).background(if (isSelected) PrimaryTeal else Color(0xFFD1D1D6)))
            }
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickActionItem(icon = Icons.Outlined.Science, title = "تفسیر بالینی", onClick = { navController.navigate("lab_calculator") })
        QuickActionItem(icon = Icons.Outlined.ChildCare, title = "بارداری", onClick = { navController.navigate("pregnancy") })
        QuickActionItem(icon = Icons.Outlined.Search, title = "جستجو", onClick = { navController.navigate("search") })
        QuickActionItem(icon = Icons.Outlined.Phone, title = "ارتباط", onClick = { navController.navigate("contact") })
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable { onClick() }) {
        Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(SurfaceWhite), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title, tint = PrimaryTeal, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

// ======== صفحه نمایش بیماری‌های یک دسته ========
@Composable
fun CategoryDiseasesScreen(navController: NavController, categoryName: String, viewModel: DiseaseViewModel = viewModel()) {
    val diseases by viewModel.diseases.collectAsState()
    // فیلتر کردن بیماری‌ها بر اساس دسته‌بندی
    val filteredDiseases = diseases.filter { it.category == categoryName }

    Scaffold(
        topBar = { TopAppBar(title = { Text(categoryName, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16