@file:OptIn(ExperimentalMaterial3Api::class)

package com.viapezeshk.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun BmiScreen(navController: NavController) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isMale by remember { mutableStateOf(true) }

    var showResult by remember { mutableStateOf(false) }
    var bmiValue by remember { mutableStateOf(0.0) }
    var bmiCategory by remember { mutableStateOf("") }
    var resultColor by remember { mutableStateOf(Color.Transparent) }
    var calorieValue by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("محاسبه‌گر BMI و کالری", fontWeight = FontWeight.Bold) },
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
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            // فرم ورودی
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("اطلاعات خود را وارد کنید", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("قد (سانتی‌متر)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("وزن (کیلوگرم)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("سن (سال)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("جنسیت", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isMale, onClick = { isMale = true })
                        Text("مرد", modifier = Modifier.padding(end = 16.dp))
                        RadioButton(selected = !isMale, onClick = { isMale = false })
                        Text("زن")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val h = height.toFloatOrNull()
                            val w = weight.toFloatOrNull()
                            val a = age.toFloatOrNull()
                            if (h != null && w != null && a != null && h > 0 && w > 0 && a > 0) {
                                val heightM = h / 100f
                                bmiValue = (w / (heightM * heightM)).toDouble()
                                
                                when {
                                    bmiValue < 18.5 -> { bmiCategory = "کمبود وزن"; resultColor = Color(0xFF2196F3) }
                                    bmiValue < 24.9 -> { bmiCategory = "وزن نرمال"; resultColor = Color(0xFF4CAF50) }
                                    bmiValue < 29.9 -> { bmiCategory = "اضافه وزن"; resultColor = Color(0xFFFF9800) }
                                    else -> { bmiCategory = "چاقی"; resultColor = Color(0xFFF44336) }
                                }

                                // فرمول Mifflin-St Jeor برای متابولیسم پایه
                                val bmr = if (isMale) {
                                    (10 * w) + (6.25 * h) - (5 * a) + 5
                                } else {
                                    (10 * w) + (6.25 * h) - (5 * a) - 161
                                }
                                // ضریب فعالیت سبک (1.375) برای کالری روزانه
                                calorieValue = (bmr * 1.375).toInt()
                                showResult = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("محاسبه کن", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // نمایش نتیجه
            if (showResult) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = resultColor.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, resultColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("نتیجه محاسبه", fontWeight = FontWeight.Bold, color = resultColor, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("شاخص توده بدنی (BMI):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(String.format("%.1f", bmiValue), fontWeight = FontWeight.ExtraBold, color = resultColor, fontSize = 18.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("وضعیت شما:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(bmiCategory, fontWeight = FontWeight.ExtraBold, color = resultColor)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = resultColor.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("کالری روزانه تخمینی (برای حفظ وزن فعلی):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$calorieValue کیلوکالری", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                        Text("(با سطح فعالیت سبک محاسبه شده است)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFF3E0)).padding(16.dp)) {
                    Text("⚠️ این محاسبات تخمینی است و جایگزین نظر متخصص تغذیه یا پزشک نیست.", color = Color(0xFFE65100), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}