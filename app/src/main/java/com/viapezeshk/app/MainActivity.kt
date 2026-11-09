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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
import com.viapezeshk.app.data.Drug
import com.viapezeshk.app.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val fontSize by settingsViewModel.fontSize.collectAsState()
            
            val fontScale = when (fontSize) {
                0 -> 0.85f
                2 -> 1.2f
                else -> 1.0f
            }

            ViaPezeshkTheme(darkTheme = isDarkMode, fontScale = fontScale) {
                val navController = rememberNavController()
                
                // تعیین نقطه شروع بر اساس دیدن یا ندیدن سلب مسئولیت
                val startDestination = if (settingsViewModel.hasSeenDisclaimer()) "home" else "welcome"
                
                NavHost(navController = navController, startDestination = startDestination) {
                    
                    // صفحه خوش‌آمدگویی
                    composable("welcome") {
                        WelcomeScreen(onAccept = {
                            settingsViewModel.setHasSeenDisclaimer()
                            navController.navigate("home") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        })
                    }

                    composable("home") { HomeScreen(navController) }
                    composable("search") { SearchScreen(navController) }
                    composable("bookmarks") { BookmarksScreen(navController) }
                    composable("contact") { ContactScreen(navController) }
                    composable("settings") { SettingsScreen(navController, settingsViewModel) }
                    composable("lab_calculator") { LabCalculatorScreen(navController) }
                    
                    composable("pregnancy") { PregnancyScreen(navController) }
                    composable("pediatrics") { PediatricScreen(navController) }
                    composable("first_aid") { FirstAidListScreen(navController) }
                    composable("first_aid_detail/{topicId}") { backStackEntry ->
                        val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                        FirstAidDetailScreen(navController, topicId)
                    }
                    
                    composable("category/{categoryName}") { backStackEntry ->
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                        CategoryDiseasesScreen(navController, categoryName)
                    }
                    
                    composable("disease_detail/{diseaseId}") { backStackEntry ->
                        val diseaseId = backStackEntry.arguments?.getString("diseaseId") ?: ""
                        DiseaseDetailScreen(navController, diseaseId)
                    }

                    composable("drugs") { DrugListScreen(navController) }
                    composable("drug_detail/{drugId}") { backStackEntry ->
                        val drugId = backStackEntry.arguments?.getString("drugId") ?: ""
                        DrugDetailScreen(navController, drugId)
                    }
                }
            }
        }
    }
}

data class CategoryItem(val name: String, val icon: String)

val diseaseCategories = listOf(
    CategoryItem("مغز و اعصاب", "🧠"), CategoryItem("چشم و بینایی", "👁️"),
    CategoryItem("گوش، حلق و بینی", "👂"), CategoryItem("دهان و دندان", "🦷"),
    CategoryItem("قلب و ریه", "❤️"), CategoryItem("معده و گوارش", "🫃"),
    CategoryItem("کلیه و مجاری ادراری", "🫘"), CategoryItem("دستگاه تناسلی", "🧬"),
    CategoryItem("استخوان و مفاصل", "🦴"), CategoryItem("پوست و مو", "💇"),
    CategoryItem("غدد و خون", "🩸"), CategoryItem("عوامل بیماری‌زا", "🦠")
)

@Composable
fun HomeScreen(
    navController: NavController, 
    viewModel: DiseaseViewModel = viewModel(),
    bannerViewModel: BannerViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val banners by bannerViewModel.banners.collectAsState()
    val userCity by settingsViewModel.userCity.collectAsState()

    LaunchedEffect(userCity) {
        bannerViewModel.loadBanners("home_main", userCity)
    }

    Scaffold(
        topBar = { TopHeader(onSettingsClick = { navController.navigate("settings") }) },
        bottomBar = { BottomNavBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            BannerCarousel(banners = banners)
            Spacer(modifier = Modifier.height(20.dp))
            QuickActionsSection(navController)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HomeFeatureCard("داروها", Icons.Default.Medication, Color(0xFF2E7D32), Modifier.weight(1f)) { navController.navigate("drugs") }
                HomeFeatureCard("کمک‌های اولیه", Icons.Default.MedicalServices, Color(0xFFC62828), Modifier.weight(1f)) { navController.navigate("first_aid") }
                HomeFeatureCard("کودکان", Icons.Default.ChildCare, Color(0xFF1565C0), Modifier.weight(1f)) { navController.navigate("pediatrics") }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("دسته‌بندی‌های آناتومی", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(bottom = 12.dp))
            
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
fun HomeFeatureCard(title: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = modifier.clickable { onClick() }) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun RowScope.CategoryCard(category: CategoryItem, navController: NavController) {
    Row(
        modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).clickable { navController.navigate("category/${category.name}") }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = category.icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = category.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun TopHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "ویاپزشک", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        IconButton(onClick = onSettingsClick, modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.background)) {
            Icon(imageVector = Icons.Outlined.Settings, contentDescription = "تنظیمات", tint = MaterialTheme.colorScheme.onSurface)
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
                Box(modifier = Modifier.padding(horizontal = 4.dp).height(8.dp).width(width.value).clip(CircleShape).background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant))
            }
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickActionItem(icon = Icons.Outlined.Science, title = "تفسیر بالینی", onClick = { navController.navigate("lab_calculator") })
        QuickActionItem(icon = Icons.Outlined.PregnantWoman, title = "بارداری", onClick = { navController.navigate("pregnancy") })
        QuickActionItem(icon = Icons.Outlined.Search, title = "جستجو", onClick = { navController.navigate("search") })
        QuickActionItem(icon = Icons.Outlined.Phone, title = "ارتباط", onClick = { navController.navigate("contact") })
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, title: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable { onClick() }) {
        Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
fun CategoryDiseasesScreen(navController: NavController, categoryName: String, viewModel: DiseaseViewModel = viewModel()) {
    val diseases by viewModel.diseases.collectAsState()
    val filteredDiseases = diseases.filter { it.category == categoryName }

    Scaffold(
        topBar = { TopAppBar(title = { Text(categoryName, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (filteredDiseases.isEmpty()) {
                Text("در حال بارگذاری یا بیماری‌ای در این دسته یافت نشد.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        "Visibility" -> Icons.Default.Visibility
        "Hearing" -> Icons.Default.Hearing
        "WaterDrop" -> Icons.Default.WaterDrop
        "Accessibility" -> Icons.Default.Accessibility
        "Bloodtype" -> Icons.Default.Bloodtype
        else -> Icons.Default.Coronavirus
    }
}

@Composable
fun DiseaseListItem(disease: Disease, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(imageVector = getIconFromName(disease.iconName), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = disease.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = disease.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        NavigationBarItem(icon = { Icon(Icons.Filled.Home, contentDescription = "خانه") }, label = { Text("خانه", fontSize = 11.sp) }, selected = currentRoute == "home", onClick = { if (currentRoute != "home") navController.navigate("home") { popUpTo("home") { inclusive = true } } }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant))
        NavigationBarItem(icon = { Icon(Icons.Filled.Search, contentDescription = "جستجو") }, label = { Text("جستجو", fontSize = 11.sp) }, selected = currentRoute == "search", onClick = { if (currentRoute != "search") navController.navigate("search") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant))
        NavigationBarItem(icon = { Icon(Icons.Filled.Star, contentDescription = "نشان‌ها") }, label = { Text("نشان‌ها", fontSize = 11.sp) }, selected = currentRoute == "bookmarks", onClick = { if (currentRoute != "bookmarks") navController.navigate("bookmarks") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant))
        NavigationBarItem(icon = { Icon(Icons.Filled.Phone, contentDescription = "ارتباط") }, label = { Text("ارتباط", fontSize = 11.sp) }, selected = currentRoute == "contact", onClick = { if (currentRoute != "contact") navController.navigate("contact") }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MaterialTheme.colorScheme.primary, selectedTextColor = MaterialTheme.colorScheme.primary, indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant))
    }
}

@Composable
fun SearchScreen(navController: NavController, viewModel: DiseaseViewModel = viewModel()) {
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val isOnlineResult by viewModel.isOnlineResult.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val view = LocalView.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("دستیار هوشمند", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, 
        bottomBar = { BottomNavBar(navController) }, 
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())) {
                if (isSearching) {
                    Row(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("در حال فکر کردن و جستجو...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else if (searchResult != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = if (isOnlineResult) "پاسخ از منابع آنلاین (ویکی‌پدیا)" else "پاسخ از دیتابیس آفلاین", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = searchResult!!, fontSize = 15.sp, lineHeight = 28.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Justify)
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
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), modifier = Modifier.size(80.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("هر بیماری، علامت یا دارویی رو می‌خوای بپرس...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text("مثلا: دیابت، میگرن، آکنه...") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedBorderColor = MaterialTheme.colorScheme.primary))
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { 
                    viewModel.smartSearch(searchQuery)
                    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }, modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)) {
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
    LaunchedEffect(Unit) { viewModel.loadContacts() }

    Scaffold(topBar = { TopAppBar(title = { Text("ارتباط با ما", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, bottomBar = { BottomNavBar(navController) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(50.dp)) }
            Spacer(modifier = Modifier.height(16.dp))
            Text("پشتیبانی و ارتباط", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            Text("برای ارتباط با ما یا سفارش تبلیغات، از راه‌های زیر استفاده کنید:", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            if (contacts.isEmpty()) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } 
            else {
                contacts.forEach { contact ->
                    Button(onClick = {
                        when (contact.type) {
                            "url" -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(contact.value)))
                            "tel" -> context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.value}")))
                        }
                    }, modifier = Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
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
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val fontSize by settingsViewModel.fontSize.collectAsState()
    val currentCity by settingsViewModel.userCity.collectAsState()
    
    var expanded by remember { mutableStateOf(false) }
    val cities = listOf("همهٔ ایران", "تهران", "مشهد", "اصفهان", "کرج", "شیراز", "تبریز", "قم", "اهواز", "کرمانشاه", "ارومیه", "رشت", "زاهدان", "همدان", "کرمان", "یزد", "اردبیل", "بندرعباس", "اراک", "زنجان", "سنندج", "قزوین", "خرم‌آباد", "گرگان", "ساری", "بجنورد", "بیرجند", "شهرکرد", "یاسوج", "ایلام", "سمنان")

    Scaffold(topBar = { TopAppBar(title = { Text("تنظیمات", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("ظاهر برنامه", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("حالت تاریک (Dark Mode)", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Switch(checked = isDarkMode, onCheckedChange = { settingsViewModel.toggleDarkMode(it) }, colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)))
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    Text("اندازه فونت", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val sizes = listOf("کوچک", "متوسط", "بزرگ")
                        sizes.forEachIndexed { index, label ->
                            Button(onClick = { settingsViewModel.setFontSize(index) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (fontSize == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, contentColor = if (fontSize == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant), shape = RoundedCornerShape(8.dp)) {
                                Text(label, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("موقعیت مکانی", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("انتخاب شهر (برای تبلیغات هدفمند)", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(value = currentCity, onValueChange = {}, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.background, unfocusedContainerColor = MaterialTheme.colorScheme.background))
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            cities.forEach { city -> DropdownMenuItem(text = { Text(city) }, onClick = { settingsViewModel.setCity(city); expanded = false }) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("درباره ما", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("نسخه اپ: ۱.۰.۰", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("این اپلیکیشن صرفاً جنبه آموزشی دارد و جایگزین معاینه پزشک نیست.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DiseaseDetailScreen(
    navController: NavController, 
    diseaseId: String, 
    viewModel: DiseaseViewModel = viewModel(),
    bannerViewModel: BannerViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel()
) {
    val context = LocalContext.current
    val diseases by viewModel.diseases.collectAsState()
    val disease = diseases.find { it.id == diseaseId }
    
    val banners by bannerViewModel.banners.collectAsState()
    val userCity by settingsViewModel.userCity.collectAsState()
    
    LaunchedEffect(diseaseId, userCity) {
        bannerViewModel.loadBanners("page_$diseaseId", userCity)
    }

    Scaffold(topBar = { TopAppBar(title = { Text(disease?.title ?: "جزئیات", fontWeight = FontWeight.Bold) }, 
        actions = {
            if (disease != null) {
                IconButton(onClick = { 
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "بیماری ${disease.title}:\n\n${disease.content}\n\n(از اپلیکیشن ویاپزشک)")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "اشتراک‌گذاری", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = { bookmarkViewModel.toggleDiseaseBookmark(disease.id, disease.isBookmarked) }) {
                    Icon(
                        imageVector = if (disease.isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "نشان کردن",
                        tint = if (disease.isBookmarked) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        if (disease == null) { Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } }
        else {
            Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)).padding(16.dp)) { 
                    Column { 
                        Text(disease.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Text(disease.subtitle, color = MaterialTheme.colorScheme.onBackground) 
                    } 
                }
                Spacer(modifier = Modifier.height(16.dp))
                BannerCarousel(banners = banners)
                Spacer(modifier = Modifier.height(16.dp))
                val sections = disease.content.split("\n\n")
                sections.forEach { section ->
                    if (section.trim().startsWith("🚨")) {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(text = section.trim(), color = Color(0xFFC62828), fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp), lineHeight = 24.sp)
                        }
                    } else if (section.trim().startsWith("🔹")) {
                        val title = section.substringBefore("\n").replace("🔹", "").trim()
                        val body = section.substringAfter("\n").trim()
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(text = title, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = body, fontSize = 15.sp, lineHeight = 26.sp, color = MaterialTheme.colorScheme.onBackground)
                        }
                    } else {
                        Text(text = section.trim(), fontSize = 15.sp, lineHeight = 26.sp, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ======== صفحه داروها ========
@Composable
fun DrugListScreen(navController: NavController, viewModel: DrugViewModel = viewModel()) {
    val drugs by viewModel.drugs.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("دانشنامه داروها", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFF3E0)).padding(16.dp).padding(bottom = 8.dp)) {
                Text("⚠️ اطلاعات این بخش صرفاً جنبه آموزشی دارد. مصرف هر دارو باید با تجویز و تحت نظر پزشک باشد.", color = Color(0xFFE65100), fontSize = 13.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (drugs.isEmpty()) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    drugs.forEach { drug -> DrugListItem(drug = drug, onClick = { navController.navigate("drug_detail/${drug.id}") }) }
                }
            }
        }
    }
}

@Composable
fun DrugListItem(drug: Drug, onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Medication, contentDescription = null, tint = Color(0xFF2E7D32)) }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = drug.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = drug.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DrugDetailScreen(
    navController: NavController, 
    drugId: String, 
    viewModel: DrugViewModel = viewModel(),
    bookmarkViewModel: BookmarkViewModel = viewModel()
) {
    val context = LocalContext.current
    val drugs by viewModel.drugs.collectAsState()
    val drug = drugs.find { it.id == drugId }

    Scaffold(topBar = { TopAppBar(title = { Text(drug?.name ?: "جزئیات دارو", fontWeight = FontWeight.Bold) }, 
        actions = {
            if (drug != null) {
                IconButton(onClick = { 
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "داروی ${drug.name}:\n\nکاربرد: ${drug.usage}\n\nعوارض: ${drug.sideEffects}\n\n(از اپلیکیشن ویاپزشک)")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری"))
                }) {
                    Icon(Icons.Default.Share, contentDescription = "اشتراک‌گذاری", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = { bookmarkViewModel.toggleDrugBookmark(drug.id, drug.isBookmarked) }) {
                    Icon(
                        imageVector = if (drug.isBookmarked) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "نشان کردن",
                        tint = if (drug.isBookmarked) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        if (drug == null) { Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) } }
        else {
            Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color(0xFFE8F5E9)).padding(16.dp)) { 
                    Column { 
                        Text(drug.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2E7D32))
                        Text("نام ژنریک: ${drug.genericName}", color = Color.Black, fontSize = 14.sp)
                        Text("دسته: ${drug.category}", color = Color.DarkGray, fontSize = 13.sp)
                    } 
                }
                Spacer(modifier = Modifier.height(24.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), border = BorderStroke(1.dp, Color(0xFFC62828))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("⚠️ هشدار مهم", color = Color(0xFFC62828), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(drug.warnings, color = Color(0xFFC62828), fontSize = 14.sp, lineHeight = 22.sp)
                    }
                }
                Text("🔹 کاربرد عمومی:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(drug.usage, fontSize = 15.sp, lineHeight = 26.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))
                Text("🔹 عوارض جانبی شایع:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(drug.sideEffects, fontSize = 15.sp, lineHeight = 26.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant).padding(16.dp)) {
                    Text("این اطلاعات صرفاً جنبه آموزشی دارد و جایگزین نسخه پزشک نیست.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ======== ماشین حساب آزمایش ========
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
    Scaffold(topBar = { TopAppBar(title = { Text("تفسیر بالینی", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) }, containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = selectedTest.name, onValueChange = {}, readOnly = true, label = { Text("نوع آزمایش") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface))
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { labTests.forEach { test -> DropdownMenuItem(text = { Text(test.name) }, onClick = { selectedTest = test; expanded = false; resultText = "" }) } }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = inputValue, onValueChange = { inputValue = it }, label = { Text("مقدار (${selectedTest.unit})") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface, unfocusedContainerColor = MaterialTheme.colorScheme.surface))
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
            }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(12.dp)) { Text("تفسیر کن", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.height(24.dp))
            if (resultText.isNotEmpty()) { Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(resultColor).padding(16.dp)) { Text(text = resultText, color = resultTextColor, fontWeight = FontWeight.Bold, fontSize = 15.sp) } }
        }
    }
}

@Composable
fun PlaceholderScreen(navController: NavController, title: String) {
    Scaffold(topBar = { TopAppBar(title = { Text(title) }) }, bottomBar = { BottomNavBar(navController) }) { padding -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("به‌زودی...") } }
}