@file:OptIn(ExperimentalMaterial3Api::class)

package com.viapezeshk.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
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
import androidx.compose.ui.platform.LocalView
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
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                diseaseCategories.chunked(2).forEach { rowCategories ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowCategories.forEach { category ->
                            CategoryCard(category = category, navController = navController)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RowScope.CategoryCard(category: CategoryItem, navController: NavController) {
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

@Composable
fun CategoryDiseasesScreen(navController: NavController, categoryName: String, viewModel: DiseaseViewModel = viewModel()) {
    val diseases by viewModel.diseases.collectAsState()
    val filteredDiseases = diseases.filter { it.category == categoryName }

    Scaffold(
        topBar = { TopAppBar(title = { Text(categoryName, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (filteredDiseases.isEmpty()) {
                Text("در حال بارگذاری یا بیماری‌ای در این دسته یافت نشد.", color = TextSecondary)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    filteredDiseases.forEach { disease -> 
                        DiseaseListItem(disease = disease, onClick = { navController.navigate("disease_detail/${disease.id}") }) 
                    }
                }
            }
        }
    }
}

fun getIconFromName(name: String): ImageVector {
    return when (name) {
        "Coronavirus" -> Icons.Default.Coronavirus
        "Face" -> Icons.Default.Face
        "Psychology" -> Icons.Default.Psychology
        "Fastfood" -> Icons.Default.Fastfood
        "Favorite" -> Icons.Default.Favorite
        else -> Icons.Default.Coronavirus
    }
}

@Composable
fun DiseaseListItem(disease: Disease, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceWhite).clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(PrimaryTeal.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(imageVector = getIconFromName(disease.iconName), contentDescription = null, tint = PrimaryTeal)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = disease.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = disease.subtitle, fontSize = 12.sp, color = TextSecondary)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = TextSecondary)
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = SurfaceWhite, tonalElevation = 8.dp) {
        NavigationBarItem(icon = { Icon(Icons.Filled.Home, contentDescription = "خانه") }, label = { Text("خانه", fontSize = 11.sp) }, selected = currentRoute == "home", onClick = { if (currentRoute != "home") navController.navigate("home") { popUpTo("home") { inclusive = true } } }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryTeal, selectedTextColor = PrimaryTeal, indicatorColor = PrimaryTeal.copy(alpha = 0.1f), unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary))
        NavigationBarItem(icon = { Icon(Icons.Filled.Search, contentDescription = "جستجو") }, label = { Text("جستجو", fontSize = 11.sp) }, selected = currentRoute == "search", onClick = { if (currentRoute != "search") navController.navigate("search") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryTeal, selectedTextColor = PrimaryTeal, indicatorColor = PrimaryTeal.copy(alpha = 0.1f), unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary))
        NavigationBarItem(icon = { Icon(Icons.Filled.Star, contentDescription = "نشان‌ها") }, label = { Text("نشان‌ها", fontSize = 11.sp) }, selected = currentRoute == "bookmarks", onClick = { if (currentRoute != "bookmarks") navController.navigate("bookmarks") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryTeal, selectedTextColor = PrimaryTeal, indicatorColor = PrimaryTeal.copy(alpha = 0.1f), unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary))
        NavigationBarItem(icon = { Icon(Icons.Filled.Phone, contentDescription = "ارتباط") }, label = { Text("ارتباط", fontSize = 11.sp) }, selected = currentRoute == "contact", onClick = { if (currentRoute != "contact") navController.navigate("contact") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryTeal, selectedTextColor = PrimaryTeal, indicatorColor = PrimaryTeal.copy(alpha = 0.1f), unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary))
    }
}

@Composable
fun PregnancyScreen(navController: NavController) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var resultText by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { selectedDateMillis = datePickerState.selectedDateMillis; showDatePicker = false }) { Text("تایید") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("لغو") } }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("بارداری", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) }, containerColor = BackgroundLight) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(SurfaceWhite).padding(20.dp)) {
                Column {
                    Text("ماشین حساب بارداری", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("تاریخ اولین روز آخرین پریود خود را انتخاب کنید:", fontSize = 14.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal.copy(alpha = 0.1f), contentColor = PrimaryTeal), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.DateRange, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("انتخاب تاریخ", fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        selectedDateMillis?.let { millis ->
                            val lmpDate = Date(millis)
                            val calendar = Calendar.getInstance()
                            calendar.time = lmpDate
                            calendar.add(Calendar.DAY_OF_YEAR, 280)
                            val eddDate = calendar.time
                            val format = SimpleDateFormat("yyyy/MM/dd", Locale("fa"))
                            val lmpStr = format.format(lmpDate)
                            val eddStr = format.format(eddDate)
                            val diff = System.currentTimeMillis() - millis
                            val days = TimeUnit.MILLISECONDS.toDays(diff).toInt()
                            val weeks = days / 7
                            resultText = "تاریخ آخرین پریود: $lmpStr\nتاریخ تقریبی زایمان: $eddStr\nشما در هفته $weeks بارداری هستید."
                        }
                    }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal), shape = RoundedCornerShape(12.dp)) { Text("محاسبه تاریخ زایمان", fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (resultText.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFE8F8F5)).padding(16.dp)) { Text(text = resultText, color = Color(0xFF0E6655), fontWeight = FontWeight.Bold, lineHeight = 24.sp) }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("راهنمای سه‌ماهه‌ها", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(16.dp)) { Text("سه‌ماهه اول (هفته ۱ تا ۱۳)", fontWeight = FontWeight.Bold, color = PrimaryTeal); Text("علائم: تهوع، خستگی. تغذیه: اسید فولیک. پرهیز از گوشت خام.", fontSize = 13.sp, color = TextSecondary) } }
            Spacer(modifier = Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(16.dp)) { Text("سه‌ماهه دوم (هفته ۱۴ تا ۲۷)", fontWeight = FontWeight.Bold, color = PrimaryTeal); Text("کاهش تهوع، شروع حرکت جنین. آزمایش قند بارداری در هفته ۲۴-۲۸.", fontSize = 13.sp, color = TextSecondary) } }
            Spacer(modifier = Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = SurfaceWhite), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(16.dp)) { Text("سه‌ماهه سوم (هفته ۲۸ تا زایمان)", fontWeight = FontWeight.Bold, color = PrimaryTeal); Text("شمارش حرکات جنین، آمادگی زایمان. علائم خطر: خونریزی، درد شدید.", fontSize = 13.sp, color = TextSecondary) } }
        }
    }
}

// ======== صفحه جستجوی هوشمند (چت‌مانند) ========
@Composable
fun SearchScreen(navController: NavController, viewModel: DiseaseViewModel = viewModel()) {
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val isOnlineResult by viewModel.isOnlineResult.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val view = LocalView.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("دستیار هوشمند", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) }, 
        bottomBar = { BottomNavBar(navController) }, 
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            
            Box(modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
                if (isSearching) {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = PrimaryTeal, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("در حال فکر کردن و جستجو...", color = TextSecondary)
                    }
                } else if (searchResult != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = PrimaryTeal)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isOnlineResult) "پاسخ از منابع آنلاین (ویکی‌پدیا)" else "پاسخ از دیتابیس آفلاین",
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryTeal,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = searchResult!!,
                                fontSize = 15.sp,
                                lineHeight = 28.sp,
                                color = TextPrimary,
                                textAlign = TextAlign.Justify
                            )
                            
                            if (isOnlineResult) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFF3E0)).padding(8.dp)) {
                                    Text("⚠️ این اطلاعات از اینترنت دریافت شده و جایگزین نظر پزشک نیست.", color = Color(0xFFE65100), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = PrimaryTeal.copy(alpha = 0.5f), modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("هر بیماری، علامت یا دارویی رو می‌خوای بپرس...", color = TextSecondary)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("مثلا: دیابت، میگرن، آکنه...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        focusedBorderColor = PrimaryTeal
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { 
                        viewModel.smartSearch(searchQuery)
                        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    },
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(PrimaryTeal)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "جستجو", tint = Color.White)
                }
            }
        }
    }
}

fun getContactIcon(iconName: String): ImageVector {
    return when(iconName) {
        "Send" -> Icons.Default.Send
        "Phone" -> Icons.Default.Phone
        "PlayArrow" -> Icons.Default.PlayArrow
        "CameraAlt" -> Icons.Default.CameraAlt
        "Language" -> Icons.Default.Language
        else -> Icons.Default.Star
    }
}

@Composable
fun ContactScreen(navController: NavController, viewModel: ContactViewModel = viewModel()) {
    val context = LocalContext.current
    val contacts by viewModel.contacts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadContacts()
    }

    Scaffold(topBar = { TopAppBar(title = { Text("ارتباط با ما", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) }, bottomBar = { BottomNavBar(navController) }, containerColor = BackgroundLight) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(PrimaryTeal.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.SupportAgent, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(50.dp)) }
            Spacer(modifier = Modifier.height(16.dp))
            Text("پشتیبانی و ارتباط", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text("برای ارتباط با ما یا سفارش تبلیغات، از راه‌های زیر استفاده کنید:", color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))

            if (contacts.isEmpty()) {
                CircularProgressIndicator(color = PrimaryTeal)
            } else {
                contacts.forEach { contact ->
                    Button(
                        onClick = {
                            when (contact.type) {
                                "url" -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(contact.value)))
                                "tel" -> context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.value}")))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceWhite, contentColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryTeal)
                    ) {
                        Icon(getContactIcon(contact.icon), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(contact.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    var currentCity by remember { mutableStateOf(getUserCity(context)) }
    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("همهٔ ایران", "تهران", "مشهد", "اصفهان", "کرج", "شیراز", "تبریز", "قم", "اهواز", "کرمانشاه", "ارومیه", "رشت", "زاهدان", "همدان", "کرمان", "یزد", "اردبیل", "بندرعباس", "اراک", "زنجان", "سنندج", "قزوین", "خرم‌آباد", "گرگان", "ساری", "بجنورد", "بیرجند", "شهرکرد", "یاسوج", "ایلام", "سمنان")

    Scaffold(topBar = { TopAppBar(title = { Text("تنظیمات", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) }, containerColor = BackgroundLight) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("انتخاب شهر (برای تبلیغات هدفمند):", fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = currentCity, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite))
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    cities.forEach { city -> DropdownMenuItem(text = { Text(city) }, onClick = { currentCity = city; saveUserCity(context, city); expanded = false }) }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("نسخه اپ: ۱.۰.۰", color = TextSecondary)
        }
    }
}

@Composable
fun DiseaseDetailScreen(
    navController: NavController, 
    diseaseId: String, 
    viewModel: DiseaseViewModel = viewModel(),
    bannerViewModel: BannerViewModel = viewModel()
) {
    val context = LocalContext.current
    val diseases by viewModel.diseases.collectAsState()
    val disease = diseases.find { it.id == diseaseId }
    
    val banners by bannerViewModel.banners.collectAsState()
    LaunchedEffect(diseaseId) {
        val userCity = getUserCity(context)
        bannerViewModel.loadBanners("page_$diseaseId", userCity)
    }

    Scaffold(topBar = { TopAppBar(title = { Text(disease?.title ?: "جزئیات", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) }, containerColor = BackgroundLight) { padding ->
        if (disease == null) { Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryTeal) } }
        else {
            Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
                
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(PrimaryTeal.copy(alpha = 0.1f)).padding(16.dp)) { 
                    Column { 
                        Text(disease.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryTeal)
                        Text(disease.subtitle, color = TextPrimary) 
                    } 
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                BannerCarousel(banners = banners)
                Spacer(modifier = Modifier.height(16.dp))
                
                val sections = disease.content.split("\n\n")
                sections.forEach { section ->
                    if (section.trim().startsWith("🚨")) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = section.trim(),
                                color = Color(0xFFC62828),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp),
                                lineHeight = 24.sp
                            )
                        }
                    } else if (section.trim().startsWith("🔹")) {
                        val title = section.substringBefore("\n").replace("🔹", "").trim()
                        val body = section.substringAfter("\n").trim()
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(text = title, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = body, fontSize = 15.sp, lineHeight = 26.sp, color = TextPrimary)
                        }
                    } else {
                        Text(text = section.trim(), fontSize = 15.sp, lineHeight = 26.sp, color = TextPrimary, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

data class LabTest(val name: String, val unit: String, val min: Float, val max: Float, val lowMsg: String, val highMsg: String, val borderlineMax: Float = 0f, val borderlineMsg: String = "")

val labTests = listOf(
    LabTest("هموگلوبین (Hb)", "g/dL", 12f, 16f, "احتمال کم‌خونی (فقر آهن).", "ممکن است کم‌آبی باشد."),
    LabTest("قند خون ناشتا (FBS)", "mg/dL", 70f, 99f, "افت قند خون.", "احتمال دیابت.", 125f, "شما در مرحله پیش‌دیابت هستید."),
    LabTest("کلسترول تام", "mg/dL", 0f, 200f, "معمولاً مشکلی نیست.", "کلسترول بالا، ریسک بیماری قلبی.", 239f, "کلسترول مرزی.")
)

@Composable
fun LabCalculatorScreen(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTest by remember { mutableStateOf(labTests[0]) }
    var inputValue by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var resultColor by remember { mutableStateOf(Color.Transparent) }
    var resultTextColor by remember { mutableStateOf(Color.Transparent) }
    Scaffold(topBar = { TopAppBar(title = { Text("تفسیر بالینی", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) }, containerColor = BackgroundLight) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = selectedTest.name, onValueChange = {}, readOnly = true, label = { Text("نوع آزمایش") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite))
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { labTests.forEach { test -> DropdownMenuItem(text = { Text(test.name) }, onClick = { selectedTest = test; expanded = false; resultText = "" }) } }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = inputValue, onValueChange = { inputValue = it }, label = { Text("مقدار (${selectedTest.unit})") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val valFloat = inputValue.toFloatOrNull()
                if (valFloat != null) {
                    when {
                        valFloat < selectedTest.min -> { resultText = "پایین‌تر از حد نرمال:\n${selectedTest.lowMsg}"; resultColor = Color(0xFFFDECEA); resultTextColor = Color(0xFFC0392B) }
                        valFloat > selectedTest.max -> { if (selectedTest.borderlineMax > 0f && valFloat <= selectedTest.borderlineMax) { resultText = "مرزی (نیاز به توجه):\n${selectedTest.borderlineMsg}"; resultColor = Color(0xFFFEF9E7); resultTextColor = Color(0xFFF39C12) } else { resultText = "بالاتر از حد نرمال:\n${selectedTest.highMsg}"; resultColor = Color(0xFFFDECEA); resultTextColor = Color(0xFFC0392B) } }
                        else -> { resultText = "طبیعی (نرمال):\nمقدار شما در محدوده طبیعی است."; resultColor = Color(0xFFE8F8F5); resultTextColor = Color(0xFF27AE60) }
                    }
                }
            }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal), shape = RoundedCornerShape(12.dp)) { Text("تفسیر کن", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(24.dp))
            if (resultText.isNotEmpty()) { Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(resultColor).padding(16.dp)) { Text(text = resultText, color = resultTextColor, fontWeight = FontWeight.Bold, fontSize = 15.sp) } }
        }
    }
}

@Composable
fun PlaceholderScreen(navController: NavController, title: String) {
    Scaffold(topBar = { TopAppBar(title = { Text(title) }) }, bottomBar = { BottomNavBar(navController) }) { padding -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("به‌زودی...") } }
}