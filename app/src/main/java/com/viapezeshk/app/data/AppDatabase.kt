package com.viapezeshk.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Disease::class], version = 1, exportSchema = false)
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
            // پر کردن دیتابیس با اطلاعات اولیه
            val initialDiseases = listOf(
                Disease(
                    id = "hpv",
                    title = "HPV (پاپیلوما)",
                    subtitle = "ویروس شایع، انواع و پیشگیری",
                    iconName = "Coronavirus",
                    category = "عفونت‌ها",
                    content = "HPV یک ویروس بسیار شایع است که از طریق تماس پوستی منتقل می‌شود. بیشتر افراد اصلاً متوجه نمی‌شوند چون معمولاً بی‌علامت است. بدن اکثر افراد طی ۱ تا ۲ سال خودش ویروس را پاک می‌کند. اما بعضی تایپ‌های خاص اگر سال‌ها بمانند، می‌توانند باعث سرطان شوند.\n\nپیشگیری:\n- واکسن گارداسیل\n- غربالگری (پاپ‌اسمیر)\n- استفاده از کاندوم"
                ),
                Disease(
                    id = "hair_loss",
                    title = "ریزش مو",
                    subtitle = "علل، انواع و درمان‌های ریزش مو",
                    iconName = "Face",
                    category = "پوست و مو",
                    content = "ریزش مو دلایل مختلفی دارد:\n\nانواع:\n- آندروژنتیک (ارثی): شایع‌ترین نوع\n- تلوژن افلوویوم: بعد از استرس یا زایمان\n- آلوپسی آره‌آتا: خودایمنی\n\nعلل شایع:\n- کمبود آهن، ویتامین D یا روی\n- اختلالات تیروئید\n\nدرمان:\n- مینوکسیدیل\n- فیناستراید (با نسخه پزشک)"
                ),
                Disease(
                    id = "migraine",
                    title = "میگرن",
                    subtitle = "سردردهای شدید و ضربان‌دار",
                    iconName = "Psychology",
                    category = "اعصاب",
                    content = "میگرن یک نوع سردرد ضربان‌دار است که معمولاً در یک طرف سر رخ می‌دهد و با تهوع و حساسیت به نور همراه است.\n\nمحرک‌ها:\n- استرس و کم‌خوابی\n- گشنگی\n- غذاهای خاص (پنیر، شکلات)\n\nدرمان: استراحت در اتاق تاریک و مصرف مسکن."
                ),
                Disease(
                    id = "reflux",
                    title = "ریفلاکس معده",
                    subtitle = "سوزش سر دل و برگشت اسید",
                    iconName = "Fastfood",
                    category = "گوارش",
                    content = "ریفلاکس زمانی رخ می‌دهد که اسید معده به مری برمی‌گردد.\n\nعلائم:\n- سوزش سر دل\n- برگشت غذا به دهان\n\nدرمان خانگی:\n- شام سبک و حداقل ۳ ساعت قبل از خواب\n- کاهش وزن\n- بالاتر گذاشتن سر تخت"
                )
            )
            diseaseDao.insertAll(initialDiseases)
        }
    }
}