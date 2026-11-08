package com.viapezeshk.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.viapezeshk.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PregnancyScreen(navController: NavController) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var resultText by remember { mutableStateOf("") }
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
                title = { Text("بارداری", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // کارت ماشین حساب
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceWhite)
                    .padding(20.dp)
            ) {
                Column {
                    Text("ماشین حساب بارداری", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("تاریخ اولین روز آخرین پریود خود را انتخاب کنید:", fontSize = 14.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryTeal.copy(alpha = 0.1f),
                            contentColor = PrimaryTeal
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("انتخاب تاریخ", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
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
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("محاسبه تاریخ زایمان", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // نمایش نتیجه محاسبه
            if (resultText.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8F8F5))
                        .padding(16.dp)
                ) {
                    Text(
                        text = resultText,
                        color = Color(0xFF0E6655),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // بخش راهنمای سه‌ماهه
            Text("راهنمای سه‌ماهه‌ها", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("سه‌ماهه اول (هفته ۱ تا ۱۳)", fontWeight = FontWeight.Bold, color = PrimaryTeal, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("علائم: تهوع، خستگی مفرط.\nتغذیه: اسید فولیک (بسیار مهم).\nپرهیز: گوشت خام و پنیر غیرپاستوریزه.", fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("سه‌ماهه دوم (هفته ۱۴ تا ۲۷)", fontWeight = FontWeight.Bold, color = PrimaryTeal, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("کاهش تهوع، شروع حرکت جنین (هفته ۱۸-۲۰).\nآزمایش مهم: غربالگری و آزمایش قند بارداری (هفته ۲۴-۲۸).\nتغذیه: مصرف آهن و کلسیم.", fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("سه‌ماهه سوم (هفته ۲۸ تا زایمان)", fontWeight = FontWeight.Bold, color = PrimaryTeal, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("علائم: تنگی نفس، تکرر ادرار، انقباضات.\nمراقبت: شمارش روزانه حرکات جنین.\nعلائم خطر: خونریزی، درد شدید، کاهش حرکت جنین.", fontSize = 14.sp, color = TextSecondary, lineHeight = 22.sp)
                }
            }
        }
    }
}