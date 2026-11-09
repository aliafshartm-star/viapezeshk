@file:OptIn(ExperimentalMaterial3Api::class)

package com.viapezeshk.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PeriodScreen(navController: NavController, viewModel: PeriodViewModel = viewModel()) {
    val records by viewModel.periodRecords.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("تایید") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("لغو") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تقویم پریود و بارداری", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // فرم ثبت
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ثبت پریود جدید", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("تاریخ شروع آخرین پریود خود را انتخاب کنید:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), contentColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("انتخاب تاریخ", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            selectedDateMillis?.let {
                                viewModel.addPeriod(it)
                                selectedDateMillis = null
                            }
                        },
                        enabled = selectedDateMillis != null,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("ثبت و پیش‌بینی", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // نمایش پیش‌بینی‌ها (بر اساس آخرین رکورد)
            if (records.isNotEmpty()) {
                val latestRecord = records.first()
                val predictions = viewModel.calculatePredictions(latestRecord)
                val format = SimpleDateFormat("yyyy/MM/dd", Locale("fa"))
                val daysLeft = viewModel.daysUntilNextPeriod(predictions["nextPeriod"] ?: 0L)

                Text("پیش‌بینی‌های شما", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(8.dp))

                // کارت پریود بعدی
                PredictionCard(
                    title = "پریود بعدی",
                    date = format.format(Date(predictions["nextPeriod"] ?: 0L)),
                    icon = Icons.Default.WaterDrop,
                    color = Color(0xFFE91E63),
                    extraText = "($daysLeft روز مانده)"
                )
                
                // کارت تخمک‌گذاری
                PredictionCard(
                    title = "روز تخمک‌گذاری",
                    date = format.format(Date(predictions["ovulation"] ?: 0L)),
                    icon = Icons.Default.Egg,
                    color = Color(0xFFFF9800)
                )
                
                // کارت پنجره باروری
                val fertileStart = format.format(Date(predictions["fertileStart"] ?: 0L))
                val fertileEnd = format.format(Date(predictions["fertileEnd"] ?: 0L))
                PredictionCard(
                    title = "پنجره باروری",
                    date = "از $fertileStart تا $fertileEnd",
                    icon = Icons.Default.Favorite,
                    color = Color(0xFF4CAF50)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            // تاریخچه
            Text("تاریخچه پریودها", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (records.isEmpty()) {
                Text("هنوز پریودی ثبت نکرده‌اید.", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 16.dp))
            } else {
                val format = SimpleDateFormat("yyyy/MM/dd", Locale("fa"))
                records.forEach { record ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("شروع: ${format.format(Date(record.startDate))}", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.deletePeriod(record) }) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionCard(title: String, date: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, extraText: String = "") {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(date, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (extraText.isNotEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(extraText, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}