package com.viapezeshk.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Disease::class], version = 4, exportSchema = false)
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
                // ================= مغز و اعصاب =================
                Disease(
                    id = "migraine",
                    title = "میگرن",
                    subtitle = "سردردهای ضربان‌دار و شدید",
                    iconName = "Psychology",
                    category = "مغز و اعصاب",
                    content = "🔹 تعریف:\nمیگرن یک اختلال عصبی است که با سردردهای شدید، ضربان‌دار و معمولاً یک‌طرفه همراه است. ممکن است با تهوع و حساسیت به نور و صدا همراه باشد.\n\n" +
                            "🔹 علائم شایع:\n- درد ضربان‌دار در یک طرف سر\n- تهوع و استفراغ\n- حساسیت شدید به نور و صدا\n- دیدن جرقه یا هاله نوری قبل از سردرد\n\n" +
                            "🔹 چی خوبه:\n- استراحت در اتاق تاریک و ساکت\n- خواب منظم و کافی\n- نوشیدن آب کافی و پرهیز از گشنگی\n- تمرینات آرام‌سازی و یوگا\n\n" +
                            "🔹 چی مضره:\n- استرس و اضطراب\n- کم‌خوابی یا خواب بیش از حد\n- مصرف غذاهای محرک (پنیر کهنه، شکلات تلخ، کافئین زیاد)\n- دود سیگار و بوی عطرهای تند\n\n" +
                            "🔹 درمان:\nدر حملات حاد از مسکن‌های ساده (با تجویز پزشک) و استراحت استفاده می‌شود. برای پیشگیری، پزشک ممکن است داروهای اختصاصی تجویز کند.\n\n" +
                            "🚨 علائم خطر (فوری به پزشک):\n- سردرد ناگهانی و بسیار شدید (مثل ضربه رعد)\n- همراه با تب، سفتی گردن و بثورات پوستی\n- ضعف یا فلج ناگهانی در یک طرف بدن\n- تاری دید ناگهانی"
                ),
                Disease(
                    id = "epilepsy",
                    title = "صرع (اپی‌لپسی)",
                    subtitle = "تشنج‌های مکرر",
                    iconName = "Psychology",
                    category = "مغز و اعصاب",
                    content = "🔹 تعریف:\nصرع یک اختلال عصبی مزمن است که با تشنج‌های مکرر (ناشی از فعالیت الکتریکی غیرطبیعی در مغز) مشخص می‌شود.\n\n" +
                            "🔹 علائم شایع:\n- تشنج (انقباضات عضلانی شدید و کنترل‌نشده)\n- گیجی و فراموشی موقت\n- خیره شدن به یک نقطه (در تشنج‌های کوچک)\n- از دست دادن هوشیاری\n\n" +
                            "🔹 چی خوبه:\n- مصرف منظم داروهای ضدتشنج (با تجویز پزشک)\n- خواب کافی و منظم\n- ثبت زمان و نوع تشنج‌ها در دفترچه یادداشت\n\n" +
                            "🔹 چی مضره:\n- قطع خودسرانه دارو\n- کم‌خوابی شدید و استرس\n- مصرف الکل و مواد مخدر\n- نورهای چشمک‌زن و شدید (در برخی افراد)\n\n" +
                            "🔹 کمک‌های اولیه در هنگام تشنج:\n- فرد را در محیط امن بخوابانید و اشیاء خطرناک را دور کنید.\n- چیزی در دهان فرد نگذارید و سعی نکنید او را مهار کنید.\n- زمان تشنج را اندازه بگیرید (اگر بیش از ۵ دقیقه طول کشید فوراً با اورژانس تماس بگیرید).\n\n" +
                            "🚨 علائم خطر:\n- تشنج بیش از ۵ دقیقه\n- تشنج‌های پشت سر هم بدون به هوش آمدن\n- آسیب جدی در حین تشنج"
                ),

                // ================= دهان و دندان =================
                Disease(
                    id = "cavity",
                    title = "پوسیدگی دندان",
                    subtitle = "درد و حساسیت دندانی",
                    iconName = "Face",
                    category = "دهان و دندان",
                    content = "🔹 تعریف:\nتخریب مینای دندان توسط اسیدهای تولید شده توسط باکتری‌ها.\n\n🔹 چی خوبه:\n- مسواک زدن ۲ بار در روز\n- استفاده از نخ دندان\n- مصرف لبنیات و فلوراید\n\n🔹 چی مضره:\n- مصرف زیاد قند و نوشابه\n- عدم رعایت بهداشت دهان\n\n🔹 درمان:\nپر کردن دندان، عصب‌کشی یا کشیدن در موارد شدید."
                ),

                // ================= قلب و ریه =================
                Disease(
                    id = "hypertension",
                    title = "فشار خون بالا",
                    subtitle = "قاتل خاموش",
                    iconName = "Favorite",
                    category = "قلب و ریه",
                    content = "🔹 تعریف:\nافزایش فشار خون در شریان‌ها که معمولاً بدون علامت است اما به قلب، مغز و کلیه‌ها آسیب می‌زند.\n\n🔹 چی خوبه:\n- کاهش مصرف نمک\n- ورزش منظم و کاهش وزن\n- مصرف میوه و سبزیجات\n\n🔹 چی مضره:\n- نمک زیاد، فست‌فود، استرس، سیگار\n\n🔹 درمان:\nتغییر سبک زندگی و مصرف داروهای کاهنده فشار خون (با تجویز پزشک)."
                ),

                // ================= معده و گوارش =================
                Disease(
                    id = "reflux",
                    title = "ریفلاکس معده",
                    subtitle = "سوزش سر دل",
                    iconName = "Fastfood",
                    category = "معده و گوارش",
                    content = "🔹 تعریف:\nبرگشت اسید معده به مری که باعث سوزش سر دل می‌شود.\n\n🔹 چی خوبه:\n- شام سبک و حداقل ۳ ساعت قبل از خواب\n- کاهش وزن\n- بالاتر گذاشتن سر تخت\n\n🔹 چی مضره:\n- غذاهای چرب، تند، شکلات و کافئین\n- دراز کشیدن بلافاصله بعد از غذا\n\n🔹 درمان:\nداروهای کاهنده اسید (با نظر پزشک)."
                ),

                // ================= پوست و مو =================
                Disease(
                    id = "hair_loss",
                    title = "ریزش مو",
                    subtitle = "علل و درمان",
                    iconName = "Face",
                    category = "پوست و مو",
                    content = "🔹 علل شایع:\nکمبود آهن، استرس، ژنتیک، اختلالات تیروئید.\n\n🔹 چی خوبه:\n- مصرف پروتئین و آهن\n- کاهش استرس\n- مشورت با متخصص پوست\n\n🔹 چی مضره:\n- رژیم‌های سخت و ناگهانی\n- کشیدن مو و استفاده از مواد شیمیایی خشن\n\n🔹 درمان:\nمینوکسیدیل، مکمل‌ها (با تجویز پزشک)."
                ),

                // ================= عوامل بیماری‌زا =================
                Disease(
                    id = "hpv",
                    title = "HPV (پاپیلوما)",
                    subtitle = "ویروس شایع در زنان و مردان",
                    iconName = "Coronavirus",
                    category = "عوامل بیماری‌زا",
                    content = "🔹 تعریف:\nویروس پاپیلومای انسانی، شایع‌ترین عفونت مقاربتی که هم مردان و هم زنان را درگیر می‌کند.\n\n" +
                            "🔹 انواع پرخطر:\nتایپ‌های ۱۶ و ۱۸ که می‌توانند باعث سرطان دهانه رحم و برخی سرطان‌های دیگر شوند.\n\n" +
                            "🔹 چی خوبه:\n- تزریق واکسن گارداسیل (بهترین پیشگیری)\n- غربالگری منظم (پاپ‌اسمیر) برای بانوان\n- استفاده از کاندوم (کاهش ریسک)\n\n" +
                            "🔹 چی مضره:\n- رفتارهای جنسی پرخطر\n- سیگار کشیدن (تشدید عفونت)\n\n" +
                            "🔹 درمان:\nدرمان قطعی برای خود ویروس وجود ندارد، اما زگیل‌ها و ضایعات با روش‌های پزشکی (فریز، لیزر) درمان می‌شوند."
                ),
                Disease(
                    id = "flu",
                    title = "سرماخوردگی و آنفلوآنزا",
                    subtitle = "عفونت ویروسی تنفسی",
                    iconName = "Coronavirus",
                    category = "عوامل بیماری‌زا",
                    content = "🔹 تعریف:\nعفونت‌های ویروسی که دستگاه تنفسی فوقانی را درگیر می‌کنند.\n\n🔹 چی خوبه:\n- استراحت و مصرف مایعات گرم\n- سوپ و بخور\n\n🔹 چی مضره:\n- آنتی‌بیوتیک (روی ویروس اثر ندارد)\n- تماس نزدیک با دیگران\n\n🔹 درمان:\nدرمان علامتی (مسکن، تب‌بر)."
                )
            )
            diseaseDao.insertAll(initialDiseases)
        }
    }
}