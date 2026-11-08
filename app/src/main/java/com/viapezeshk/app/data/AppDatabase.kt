package com.viapezeshk.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Disease::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun diseaseDao(): DiseaseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "viapezeshk_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.diseaseDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(diseaseDao: DiseaseDao) {
            val initialDiseases = listOf(
                // --- مغز و اعصاب ---
                Disease(
                    id = "migraine",
                    title = "میگرن",
                    subtitle = "سردردهای شدید و ضربان‌دار",
                    iconName = "Psychology",
                    category = "مغز و اعصاب",
                    content = "میگرن یک نوع سردرد ضربان‌دار است که معمولاً در یک طرف سر رخ می‌دهد و با تهوع و حساسیت به نور همراه است.\n\nمحرک‌ها:\n- استرس و کم‌خوابی\n- گشنگی\n- غذاهای خاص (پنیر، شکلات)\n\nدرمان: استراحت در اتاق تاریک و مصرف مسکن."
                ),
                Disease(
                    id = "alzheimer",
                    title = "آلزایمر",
                    subtitle = "زوال عقل و فراموشی",
                    iconName = "Psychology",
                    category = "مغز و اعصاب",
                    content = "آلزایمر شایع‌ترین علت دمانس (زوال عقل) است که به تدریج حافظه و توانایی‌های شناختی را تحت تاثیر قرار می‌دهد."
                ),

                // --- دهان و دندان ---
                Disease(
                    id = "cavity",
                    title = "پوسیدگی دندان",
                    subtitle = "درد و حساسیت دندانی",
                    iconName = "Face",
                    category = "دهان و دندان",
                    content = "پوسیدگی دندان (کاریز) زمانی رخ می‌دهد که باکتری‌های دهان اسید تولید کرده و مینای دندان را تخریب می‌کنند."
                ),

                // --- قلب و ریه ---
                Disease(
                    id = "hypertension",
                    title = "فشار خون بالا",
                    subtitle = "قاتل خاموش",
                    iconName = "Favorite",
                    category = "قلب و ریه",
                    content = "فشار خون بالا معمولاً بی‌علامت است اما ریسک سکته قلبی و مغزی را به شدت افزایش می‌دهد. کنترل وزن و کاهش نمک بسیار موثر است."
                ),

                // --- معده و گوارش ---
                Disease(
                    id = "reflux",
                    title = "ریفلاکس معده",
                    subtitle = "سوزش سر دل و برگشت اسید",
                    iconName = "Fastfood",
                    category = "معده و گوارش",
                    content = "ریفلاکس زمانی رخ می‌دهد که اسید معده به مری برمی‌گردد.\n\nدرمان خانگی:\n- شام سبک و حداقل ۳ ساعت قبل از خواب\n- کاهش وزن"
                ),

                // --- پوست و مو ---
                Disease(
                    id = "hair_loss",
                    title = "ریزش مو",
                    subtitle = "علل، انواع و درمان‌های ریزش مو",
                    iconName = "Face",
                    category = "پوست و مو",
                    content = "ریزش مو دلایل مختلفی دارد (آندروژنتیک، استرس، کمبود آهن). مینوکسیدیل و مکمل‌ها (با تجویز پزشک) از درمان‌های رایج هستند."
                ),
                Disease(
                    id = "acne",
                    title = "آکنه (جوش)",
                    subtitle = "التهاب پوستی",
                    iconName = "Face",
                    category = "پوست و مو",
                    content = "آکنه در اثر انسداد منافذ پوست با چربی و سلول‌های مرده ایجاد می‌شود. شستشوی منظم و پرهیز از دست زدن به جوش‌ها توصیه می‌شود."
                ),

                // --- عوامل بیماری‌زا ---
                Disease(
                    id = "hpv",
                    title = "HPV (پاپیلوما)",
                    subtitle = "ویروس شایع در زنان و مردان",
                    iconName = "Coronavirus",
                    category = "عوامل بیماری‌زا",
                    content = "HPV شایع‌ترین عفونت مقاربتی است. واکسن گارداسیل بهترین روش پیشگیری است. انواع پرخطر آن می‌توانند باعث سرطان شوند."
                ),
                Disease(
                    id = "flu",
                    title = "سرماخوردگی و آنفلوآنزا",
                    subtitle = "عفونت‌های ویروسی تنفسی",
                    iconName = "Coronavirus",
                    category = "عوامل بیماری‌زا",
                    content = "بیماری‌های ویروسی شایع فصل سرد. استراحت، مصرف مایعات و مسکن‌ها برای تسکین علائم موثرند. آنتی‌بیوتیک روی ویروس اثر ندارد."
                )
            )
            diseaseDao.insertAll(initialDiseases)
        }
    }
}