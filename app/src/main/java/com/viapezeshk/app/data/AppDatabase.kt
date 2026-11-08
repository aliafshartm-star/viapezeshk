package com.viapezeshk.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Disease::class, Drug::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun diseaseDao(): DiseaseDao
    abstract fun drugDao(): DrugDao

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
                        populateDatabase(database.diseaseDao(), database.drugDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(diseaseDao: DiseaseDao, drugDao: DrugDao) {
            // ---------------- پر کردن بیماری‌ها ----------------
            val initialDiseases = listOf(
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
                    content = "🔹 تعریف:\nصرع یک اختلال عصبی مزمن است که با تشنج‌های مکرر مشخص می‌شود.\n\n" +
                            "🔹 علائم شایع:\n- تشنج (انقباضات عضلانی شدید)\n- گیجی و فراموشی موقت\n- از دست دادن هوشیاری\n\n" +
                            "🔹 چی خوبه:\n- مصرف منظم داروهای ضدتشنج\n- خواب کافی و منظم\n\n" +
                            "🔹 چی مضره:\n- قطع خودسرانه دارو\n- کم‌خوابی شدید و استرس\n- مصرف الکل\n\n" +
                            "🚨 علائم خطر:\n- تشنج بیش از ۵ دقیقه\n- تشنج‌های پشت سر هم بدون به هوش آمدن"
                ),
                Disease(
                    id = "cavity",
                    title = "پوسیدگی دندان",
                    subtitle = "درد و حساسیت دندانی",
                    iconName = "Face",
                    category = "دهان و دندان",
                    content = "🔹 تعریف:\nتخریب مینای دندان توسط اسیدهای تولید شده توسط باکتری‌ها.\n\n🔹 چی خوبه:\n- مسواک زدن ۲ بار در روز\n- استفاده از نخ دندان\n\n🔹 چی مضره:\n- مصرف زیاد قند و نوشابه\n\n🔹 درمان:\nپر کردن دندان، عصب‌کشی یا کشیدن در موارد شدید."
                ),
                Disease(
                    id = "hypertension",
                    title = "فشار خون بالا",
                    subtitle = "قاتل خاموش",
                    iconName = "Favorite",
                    category = "قلب و ریه",
                    content = "🔹 تعریف:\nافزایش فشار خون در شریان‌ها که معمولاً بدون علامت است اما به قلب، مغز و کلیه‌ها آسیب می‌زند.\n\n🔹 چی خوبه:\n- کاهش مصرف نمک\n- ورزش منظم و کاهش وزن\n\n🔹 چی مضره:\n- نمک زیاد، فست‌فود، استرس، سیگار\n\n🔹 درمان:\nتغییر سبک زندگی و مصرف داروهای کاهنده فشار خون (با تجویز پزشک)."
                ),
                Disease(
                    id = "reflux",
                    title = "ریفلاکس معده",
                    subtitle = "سوزش سر دل",
                    iconName = "Fastfood",
                    category = "معده و گوارش",
                    content = "🔹 تعریف:\nبرگشت اسید معده به مری که باعث سوزش سر دل می‌شود.\n\n🔹 چی خوبه:\n- شام سبک و حداقل ۳ ساعت قبل از خواب\n- بالاتر گذاشتن سر تخت\n\n🔹 چی مضره:\n- غذاهای چرب، تند، شکلات و کافئین\n\n🔹 درمان:\nداروهای کاهنده اسید (با نظر پزشک)."
                ),
                Disease(
                    id = "hair_loss",
                    title = "ریزش مو",
                    subtitle = "علل و درمان",
                    iconName = "Face",
                    category = "پوست و مو",
                    content = "🔹 علل شایع:\nکمبود آهن، استرس، ژنتیک، اختلالات تیروئید.\n\n🔹 چی خوبه:\n- مصرف پروتئین و آهن\n- کاهش استرس\n\n🔹 درمان:\nمینوکسیدیل، مکمل‌ها (با تجویز پزشک)."
                ),
                Disease(
                    id = "hpv",
                    title = "HPV (پاپیلوما)",
                    subtitle = "ویروس شایع در زنان و مردان",
                    iconName = "Coronavirus",
                    category = "عوامل بیماری‌زا",
                    content = "🔹 تعریف:\nویروس پاپیلومای انسانی، شایع‌ترین عفونت مقاربتی.\n\n🔹 چی خوبه:\n- تزریق واکسن گارداسیل\n- غربالگری منظم (پاپ‌اسمیر)\n\n🔹 درمان:\nدرمان قطعی برای خود ویروس وجود ندارد، اما زگیل‌ها با روش‌های پزشکی درمان می‌شوند."
                )
            )
            diseaseDao.insertAll(initialDiseases)

            // ---------------- پر کردن داروها ----------------
            val initialDrugs = listOf(
                Drug(
                    id = "acetaminophen",
                    name = "استامینوفن",
                    genericName = "Acetaminophen",
                    category = "مسکن و تب‌بر",
                    usage = "کاهش درد خفیف تا متوسط و پایین آوردن تب.",
                    sideEffects = "در دوزهای معمول عوارض خاصی ندارد. در دوزهای بسیار بالا می‌تواند باعث آسیب شدید کبدی شود.",
                    warnings = "⚠️ مصرف همزمان با الکل ممنوع است. افراد دارای بیماری کبدی حتماً باید با پزشک مشورت کنند. مصرف بیش از حد (اوردوز) خطرناک است."
                ),
                Drug(
                    id = "ibuprofen",
                    name = "ایبوپروفن",
                    genericName = "Ibuprofen",
                    category = "مسکن، ضدالتهاب (NSAID)",
                    usage = "کاهش درد، التهاب و تب (مانند دردهای عضلانی، سردرد، دنداندرد).",
                    sideEffects = "سوزش معده، تهوع، سرگیجه. مصرف طولانی‌مدت ممکن است به معده و کلیه آسیب بزند.",
                    warnings = "⚠️ حتماً بعد از غذا مصرف شود. در افراد با سابقه زخم معده، نارسایی کلیه یا بارداری (سه ماهه سوم) ممنوع است."
                ),
                Drug(
                    id = "metformin",
                    name = "متفورمین",
                    genericName = "Metformin",
                    category = "داروی دیابت (خوراکی)",
                    usage = "کنترل قند خون در بیماران مبتلا به دیابت نوع ۲.",
                    sideEffects = "تهوع، اسهال، دل‌پیچه (معمولاً در روزهای اول)، طعم فلزی در دهان.",
                    warnings = "⚠️ فقط با نسخه پزشک. در بیماران با نارسایی شدید کلیوی یا کبدی ممنوع است. قبل از انجام تصویربرداری با ماده حاجب (کنتراست) حتماً پزشک را در جریان بگذارید."
                )
            )
            drugDao.insertAll(initialDrugs)
        }
    }
}