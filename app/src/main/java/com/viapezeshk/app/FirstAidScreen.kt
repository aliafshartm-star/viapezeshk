package com.viapezeshk.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.viapezeshk.app.ui.theme.*

data class FirstAidTopic(val id: String, val title: String, val iconName: String)

val firstAidTopics = listOf(
    FirstAidTopic("cpr", "احیای قلبی ریوی (CPR)", "Favorite"),
    FirstAidTopic("choking", "خفگی", "Warning"),
    FirstAidTopic("burn", "سوختگی", "LocalFireDepartment"),
    FirstAidTopic("bleeding", "خونریزی", "Bloodtype"),
    FirstAidTopic("fracture", "شکستگی", "Healing"),
    FirstAidTopic("poisoning", "مسمومیت", "Science"),
    FirstAidTopic("heatstroke", "گرمازدگی", "WbSunny"),
    FirstAidTopic("snake_bite", "مارگزیدگی", "BugReport")
)

fun getFirstAidIcon(name: String) = when (name) {
    "Favorite" -> Icons.Default.Favorite
    "Warning" -> Icons.Default.Warning
    "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
    "Bloodtype" -> Icons.Default.Bloodtype
    "Healing" -> Icons.Default.Healing
    "Science" -> Icons.Default.Science
    "WbSunny" -> Icons.Default.WbSunny
    "BugReport" -> Icons.Default.BugReport
    else -> Icons.Default.MedicalServices
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstAidListScreen(navController: NavController) {
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("کمک‌های اولیه", fontWeight = FontWeight.Bold) }, 
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
            
            // هشدار اورژانس
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC62828))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFFC62828))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("در موارد اورژانسی فوراً با ۱۱۵ تماس بگیرید.", color = Color(0xFFC62828), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(firstAidTopics) { topic ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { navController.navigate("first_aid_detail/${topic.id}") }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(getFirstAidIcon(topic.iconName), contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(topic.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstAidDetailScreen(navController: NavController, topicId: String) {
    val topic = firstAidTopics.find { it.id == topicId }
    
    // محتوای دستورالعمل‌ها
    val content = when (topicId) {
        "cpr" -> "۱. اطمینان از امنیت محیط.\n۲. بررسی هوشیاری و تنفس بیمار.\n۳. تماس فوری با اورژانس (۱۱۵).\n۴. قرار دادن فرد به پشت روی سطح سخت.\n۵. ۳۰ بار ماساژ قفسه سینه (وسط سینه، عمق ۵ سانتی‌متر).\n۶. ۲ بار تنفس نجات‌دهنده (در صورت آموزش).\n۷. تکرار چرخه ۳۰ به ۲ تا رسیدن اورژانس."
        "choking" -> "۱. اگر فرد سرفه می‌کند، تشویق به سرفه کنید.\n۲. اگر نمی‌تواند سرفه یا صحبت کند، مانور هایملیک را انجام دهید.\n۳. از پشت فرد بایستید، دستان خود را دور کمر او حلقه کنید.\n۴. یک مشت را بالای ناف و زیر جناغ قرار دهید.\n۵. با دست دیگر فشار محکم و رو به بالا وارد کنید.\n۶. تکرار تا خارج شدن جسم."
        "burn" -> "۱. فوراً محل سوختگی را با آب ولرم (نه سرد) به مدت ۱۰-۲۰ دقیقه بشویید.\n۲. لباس یا جواهرات چسبیده را جدا نکنید.\n۳. از زدن خمیردندان، روغن یا یخ خودداری کنید.\n۴. محل را با گاز استریل بپوشانید.\n۵. در سوختگی‌های شدید (تاول، سیاه شدن پوست) فوراً به بیمارستان بروید."
        "bleeding" -> "۱. با یک پارچه تمیز یا گاز استریل روی زخم فشار مستقیم وارد کنید.\n۲. عضو خونریزی‌دهنده را بالاتر از سطح قلب نگه دارید.\n۳. فشار را حداقل ۱۰ دقیقه قطع نکنید.\n۴. در صورت خونریزی شدید و عدم توقف، فوراً با اورژانس تماس بگیرید.\n۵. از تورنیکت فقط در شرایط بحرانی و خونریزی غیرقابل کنترل استفاده کنید."
        "fracture" -> "۱. عضو آسیب‌دیده را حرکت ندهید.\n۲. از آتل یا وسایل سخت (مثل چوب) برای بی‌حرکت کردن عضو استفاده کنید.\n۳. روی محل آسیب یخ بگذارید (با پارچه پیچیده شده).\n۴. در شکستگی باز (خروج استخوان) زخم را با گاز استریل بپوشانید.\n۵. فوراً به پزشک مراجعه کنید."
        "poisoning" -> "۱. فرد را از منبع سم دور کنید.\n۲. دهان فرد را با آب بشویید.\n۳. هرگز فرد را وادار به استفراغ نکنید (مخصوصاً در مواد نفتی و اسیدی).\n۴. برچسب یا ظرف ماده سمی را همراه داشته باشید.\n۵. فوراً با مرکز اطلاعات سموم یا اورژانس (۱۱۵) تماس بگیرید."
        "heatstroke" -> "۱. فرد را به محیط خنک منتقل کنید.\n۲. لباس‌های اضافی را درآورید.\n۳. با پارچه خیس و باد زدن، بدن را خنک کنید.\n۴. اگر هوشیار است، آب خنک بنوشاند.\n۵. در صورت تب بالا، گیجی یا تشنج، فوراً به اورژانس زنگ بزنید."
        "snake_bite" -> "۱. فرد را آرام کنید (استرس سم را سریع‌تر پخش می‌کند).\n۲. عضو گزیده شده را بی‌حرکت و پایین‌تر از سطح قلب نگه دارید.\n۳. از بریدن، مکیدن یا بستن محکم (تورنیکت) خودداری کنید.\n۴. محل گزش را با آب و صابون بشویید.\n۵. فوراً به بیمارستان بروید و مشخصات مار را (اگر دیدید) بگویید."
        else -> "اطلاعات در دسترس نیست."
    }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text(topic?.title ?: "جزئیات", fontWeight = FontWeight.Bold) }, 
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
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())) {
            
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("اقدامات ضروری:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryTeal)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = content,
                        fontSize = 16.sp,
                        lineHeight = 32.sp,
                        color = TextPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)).padding(16.dp)) {
                Text("⚠️ این اطلاعات جایگزین آموزش‌های تخصصی پزشکی نیست. در موارد اورژانسی همیشه با ۱۱۵ تماس بگیرید.", color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}