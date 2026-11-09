@file:OptIn(ExperimentalMaterial3Api::class)

package com.viapezeshk.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.viapezeshk.app.data.HealthRecord
import com.viapezeshk.app.data.Medication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HealthScreen(navController: NavController, viewModel: HealthViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(0) }
    val medications by viewModel.medications.collectAsState()
    val allRecords by viewModel.allHealthRecords.collectAsState()

    // --- درخواست مجوز اعلان (مخصوص اندروید ۱۳ به بالا) ---
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* نتیجه مهم نیست، ما فقط درخواست می‌دیم */ }
    )
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("پنل سلامت من", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // تب‌ها
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("💊 داروها", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("🩺 علائم", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("🎭 حال روحی", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> MedicationTab(viewModel, medications)
                1 -> VitalsTab(viewModel, allRecords)
                2 -> MoodTab(viewModel, allRecords)
            }
        }
    }
}

// ======== تب داروها ========
@Composable
fun MedicationTab(viewModel: HealthViewModel, medications: List<Medication>) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("افزودن داروی جدید", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("اسم دارو") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = dosage, onValueChange = { dosage = it }, label = { Text("مقدار") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("ساعت (مثلاً 08:00)") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { if (name.isNotBlank()) { viewModel.addMedication(name, dosage, time); name = ""; dosage = ""; time = "" } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.width(8.dp)); Text("افزودن", fontWeight = FontWeight.Bold)
                }
            }
        }
        Text("لیست داروها", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        if (medications.isEmpty()) { Text("دارویی ثبت نشده.", color = MaterialTheme.colorScheme.onSurfaceVariant) } 
        else {
            medications.forEach { med ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(med.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("${med.dosage} - ${med.time}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = med.isActive, onCheckedChange = { viewModel.toggleMedicationActive(med) })
                        IconButton(onClick = { viewModel.deleteMedication(med) }) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red) }
                    }
                }
            }
        }
    }
}

// ======== تب علائم حیاتی ========
@Composable
fun VitalsTab(viewModel: HealthViewModel, allRecords: List<HealthRecord>) {
    var selectedType by remember { mutableStateOf("glucose") }
    var expanded by remember { mutableStateOf(false) }
    var inputValue by remember { mutableStateOf("") }
    val types = listOf("glucose" to "قند خون", "bp_sys" to "فشار (سیستول)", "bp_dia" to "فشار (دیاستول)", "weight" to "وزن")
    val records = allRecords.filter { it.type != "mood" }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ثبت علائم", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(value = types.find { it.first == selectedType }?.second ?: "", onValueChange = {}, readOnly = true, label = { Text("نوع") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { types.forEach { type -> DropdownMenuItem(text = { Text(type.second) }, onClick = { selectedType = type.first; expanded = false }) } }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = inputValue, onValueChange = { inputValue = it }, label = { Text("مقدار") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { val value = inputValue.toFloatOrNull(); if (value != null) { viewModel.addHealthRecord(selectedType, value); inputValue = "" } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(12.dp)) { Text("ثبت", fontWeight = FontWeight.Bold) }
            }
        }
        Text("تاریخچه", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        if (records.isEmpty()) { Text("چیزی ثبت نشده.", color = MaterialTheme.colorScheme.onSurfaceVariant) } 
        else {
            records.forEach { record ->
                val typeName = types.find { it.first == record.type }?.second ?: record.type
                val date = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("fa")).format(Date(record.timestamp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) { Text(typeName, fontWeight = FontWeight.Bold, fontSize = 14.sp); Text(date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Text("${record.value}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        IconButton(onClick = { viewModel.deleteHealthRecord(record) }) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}

// ======== تب حال روحی (Mood Tracker) ========
@Composable
fun MoodTab(viewModel: HealthViewModel, allRecords: List<HealthRecord>) {
    val moodRecords = allRecords.filter { it.type == "mood" }
    val moods = listOf(
        5.0f to "😄",
        4.0f to "🙂",
        3.0f to "😐",
        2.0f to "😔",
        1.0f to "😢"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("امروز چه حالی داری؟", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    moods.forEach { (value, emoji) ->
                        Text(
                            text = emoji, 
                            fontSize = 32.sp, 
                            modifier = Modifier.clickable { 
                                viewModel.addHealthRecord("mood", value) 
                            }.padding(8.dp)
                        )
                    }
                }
            }
        }
        Text("تاریخچه حال روحی", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        if (moodRecords.isEmpty()) { Text("هنوز حالی ثبت نکردی.", color = MaterialTheme.colorScheme.onSurfaceVariant) } 
        else {
            moodRecords.forEach { record ->
                val emoji = when(record.value.toInt()) { 5 -> "😄"; 4 -> "🙂"; 3 -> "😐"; 2 -> "😔"; 1 -> "😢"; else -> "😐" }
                val date = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale("fa")).format(Date(record.timestamp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) { Text("حال روحی ثبت شده", fontWeight = FontWeight.Bold, fontSize = 14.sp); Text(date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        IconButton(onClick = { viewModel.deleteHealthRecord(record) }) { Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}