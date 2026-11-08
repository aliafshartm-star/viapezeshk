package com.viapezeshk.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CheckCircle
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

data class VaccineItem(
    val id: String,
    val age: String,
    val vaccines: String
)

val vaccineList = listOf(
    VaccineItem("1", "بدو تولد", "BCG (سل)، هپاتیت B (نوبت اول)، فلج اطفال خوراکی"),
    VaccineItem("2", "۲ ماهگی", "پنج‌گانه (نوبت اول)، فلج اطفال، پنوموکوک"),
    VaccineItem("3", "۴ ماهگی", "پنج‌گانه (نوبت دوم)، فلج اطفال، پنوموکوک"),
    VaccineItem("4", "۶ ماهگی", "پنج‌گانه (نوبت سوم)، فلج اطفال"),
    VaccineItem("5", "۱۲ ماهگی", "MMR (سرخک، اوریون، سرخجه) - نوبت اول"),
    VaccineItem("6", "۱۸ ماهگی", "پنج‌گانه (یادآور)، فلج اطفال، MMR (نوبت دوم)"),
    VaccineItem("7", "۶ سالگی", "دیفتری-کزاز (DT)، فلج اطفال، MMR (یادآور)")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PediatricScreen(navController: NavController) {
    // برای دمو، واکسن‌های بدو تولد و ۲ ماهگی رو تیک خورده در نظر می‌گیریم
    val checkedVaccines = remember { mutableStateMapOf("1" to true, "2" to true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دفترچه واکسیناسیون کودک", fontWeight = FontWeight.Bold) },
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
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryTeal.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChildCare, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("با تیک زدن هر مورد، واکسن زده شده ثبت می‌شود.", color = TextPrimary, fontSize = 13.sp)
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(vaccineList) { vaccine ->
                    val isChecked = checkedVaccines[vaccine.id] == true
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) Color(0xFFE8F5E9) else SurfaceWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(vaccine.age, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(vaccine.vaccines, fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { isCheckedNow ->
                                    checkedVaccines[vaccine.id] = isCheckedNow
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                            )
                        }
                    }
                }
            }
        }
    }
}