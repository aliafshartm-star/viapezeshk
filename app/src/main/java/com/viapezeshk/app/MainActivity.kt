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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.viapezeshk.app.data.Disease
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
                    composable("lab_calculator") { LabCalculatorScreen(navController) }
                    // صفحه پویا برای همه بیماری‌ها
                    composable("disease_detail/{diseaseId}") { backStackEntry ->
                        val diseaseId = backStackEntry.arguments?.getString("diseaseId") ?: ""
                        DiseaseDetailScreen(navController, diseaseId)
                    }
                }
            }
        }
    }
}

// ======== صفحه اصلی ========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: DiseaseViewModel = viewModel()) {
    val diseases by viewModel.diseases.collectAsState()

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
            DiseaseListSection(navController, diseases)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

@Composable
fun BannerSection() {
    Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(20.dp)).background(brush = Brush.linearGradient(colors = listOf(Color(0xFF1E2A38), Color(0xFF2C3E50))))) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
            Text(text = "سیستم سلامت هوشمند", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = "دستیار سلامت آفلاین شما", color = Color(0xFFA0AAB5), fontSize = 14.sp)
        }
        Row(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF6C7A89)))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryTeal))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF6C7A89)))
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickActionItem(icon = Icons.Outlined.Science, title = "تفسیر بالینی", onClick = { navController.navigate("lab_calculator") })
        QuickActionItem(icon = Icons.Outlined.ChildCare, title = "دفترچه کودک")
        QuickActionItem(icon = Icons.Outlined.Coronavirus, title = "بیماری‌ها")
        QuickActionItem(icon = Icons.Outlined.Accessibility, title = "آناتومی")
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
fun DiseaseListSection(navController: NavController, diseases: List<Disease>) {
    Text(text = "دسته‌بندی بیماری‌ها", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
    
    if (diseases.isEmpty()) {
        Text("در حال بارگذاری دیتابیس...", color = TextSecondary)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            diseases.forEach { disease ->
                DiseaseListItem(
                    disease = disease,
                    onClick = { navController.navigate("disease_detail/${disease.id}") }
                )
            }
        }
    }
}

// تابع کمکی برای تبدیل اسم آیکون به آیکون واقعی
fun getIconFromName(name: String): ImageVector {
    return when (name) {
        "Coronavirus" -> Icons.Default.Coronavirus
        "Face" -> Icons.Default.Face
        "Psychology" -> Icons.Default.Psychology
        "Fastfood" -> Icons.Default.Fastfood
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
fun BottomNavBar() {
    NavigationBar(containerColor = SurfaceWhite, tonalElevation = 8.dp) {
        NavigationBarItem(icon = { Icon(Icons.Filled.Home, contentDescription = "خانه") }, label = { Text("خانه", fontSize = 11.sp) }, selected = true, onClick = { }, colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryTeal, selectedTextColor = PrimaryTeal, indicatorColor = PrimaryTeal.copy(alpha = 0.1f), unselectedIconColor = TextSecondary, unselectedTextColor = TextSecondary))
        NavigationBarItem(icon = { Icon(Icons.Filled.Search, contentDescription = "جستجو") }, label = { Text("جستجو", fontSize = 11.sp) }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Filled.Star, contentDescription = "نشان‌ها") }, label = { Text("نشان‌ها", fontSize = 11.sp) }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Filled.Phone, contentDescription = "ارتباط") }, label = { Text("ارتباط", fontSize = 11.sp) }, selected = false, onClick = { })
    }
}

// ======== صفحه تنظیمات ========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text("تنظیمات", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) }, containerColor = BackgroundLight) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("شهر شما:", fontWeight = FontWeight.Bold)
            Text("تهران", color = TextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("نسخه اپ: ۱.۰.۰", color = TextSecondary)
        }
    }
}

// ======== صفحه پویای جزئیات بیماری ========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseDetailScreen(navController: NavController, diseaseId: String, viewModel: DiseaseViewModel = viewModel()) {
    val diseases by viewModel.diseases.collectAsState()
    val disease = diseases.find { it.id == diseaseId }

    Scaffold(
        topBar = { TopAppBar(title = { Text(disease?.title ?: "جزئیات", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) },
        containerColor = BackgroundLight
    ) { padding ->
        if (disease == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryTeal)
            }
        } else {
            Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(PrimaryTeal.copy(alpha = 0.1f)).padding(16.dp)) {
                    Column {
                        Text(disease.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryTeal)
                        Text(disease.subtitle, color = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // نمایش محتوای دیتابیس
                Text(
                    text = disease.content,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

// ======== صفحه ماشین حساب (تفسیر بالینی) ========
data class LabTest(val name: String, val unit: String, val min: Float, val max: Float, val lowMsg: String, val highMsg: String, val borderlineMax: Float = 0f, val borderlineMsg: String = "")

val labTests = listOf(
    LabTest("هموگلوبین (Hb)", "g/dL", 12f, 16f, "احتمال کم‌خونی (فقر آهن).", "ممکن است کم‌آبی باشد."),
    LabTest("قند خون ناشتا (FBS)", "mg/dL", 70f, 99f, "افت قند خون.", "احتمال دیابت.", 125f, "شما در مرحله پیش‌دیابت هستید."),
    LabTest("کلسترول تام", "mg/dL", 0f, 200f, "معمولاً مشکلی نیست.", "کلسترول بالا، ریسک بیماری قلبی.", 239f, "کلسترول مرزی.")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabCalculatorScreen(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTest by remember { mutableStateOf(labTests[0]) }
    var inputValue by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var resultColor by remember { mutableStateOf(Color.Transparent) }
    var resultTextColor by remember { mutableStateOf(Color.Transparent) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("تفسیر بالینی", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت") } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceWhite)) },
        containerColor = BackgroundLight
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedTest.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("نوع آزمایش") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    labTests.forEach { test ->
                        DropdownMenuItem(text = { Text(test.name) }, onClick = { selectedTest = test; expanded = false; resultText = "" })
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("مقدار (${selectedTest.unit})") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = SurfaceWhite, unfocusedContainerColor = SurfaceWhite)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val valFloat = inputValue.toFloatOrNull()
                    if (valFloat != null) {
                        when {
                            valFloat < selectedTest.min -> {
                                resultText = "پایین‌تر از حد نرمال:\n${selectedTest.lowMsg}"
                                resultColor = Color(0xFFFDECEA)
                                resultTextColor = Color(0xFFC0392B)
                            }
                            valFloat > selectedTest.max -> {
                                if (selectedTest.borderlineMax > 0f && valFloat <= selectedTest.borderlineMax) {
                                    resultText = "مرزی (نیاز به توجه):\n${selectedTest.borderlineMsg}"
                                    resultColor = Color(0xFFFEF9E7)
                                    resultTextColor = Color(0xFFF39C12)
                                } else {
                                    resultText = "بالاتر از حد نرمال:\n${selectedTest.highMsg}"
                                    resultColor = Color(0xFFFDECEA)
                                    resultTextColor = Color(0xFFC0392B)
                                }
                            }
                            else -> {
                                resultText = "طبیعی (نرمال):\nمقدار شما در محدوده طبیعی است."
                                resultColor = Color(0xFFE8F8F5)
                                resultTextColor = Color(0xFF27AE60)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("تفسیر کن", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (resultText.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(resultColor).padding(16.dp)) {
                    Text(text = resultText, color = resultTextColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}