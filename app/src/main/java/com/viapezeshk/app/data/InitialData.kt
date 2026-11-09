package com.viapezeshk.app.data

object InitialData {

    // اینجا همه بیماری‌های فایل‌های جداگانه رو با هم ترکیب می‌کنیم
    val diseases = listOf(
        // مغز و اعصاب (همون‌هایی که قبلاً توی این فایل نوشتیم)
        Disease(
            id = "migraine",
            title = "میگرن",
            subtitle = "سردردهای ضربان‌دار و شدید",
            iconName = "Psychology",
            category = "مغز و اعصاب",
            content = "🔹 تعریف:\nمیگرن یک اختلال عصبی است که با سردردهای شدید، معمولاً یک‌طرفه و ضربان‌دار همراه است.\n\n🔹 علائم شایع:\n- درد ضربان‌دار در یک طرف سر\n- تهوع و استفراغ\n- حساسیت به نور و صدا\n\n🔹 چی خوبه:\n- استراحت در اتاق تاریک\n- خواب منظم\n\n🔹 چی مضره:\n- گشنگی، پنیر کهنه، شکلات، کافئین زیاد\n\n🔹 درمان:\nمسکن‌ها (با تجویز پزشک).\n\n🚨 علائم خطر:\n- سردرد ناگهانی و بسیار شدید\n- ضعف یا فلج ناگهانی"
        ),
        Disease(
            id = "tension_headache",
            title = "سردرد تنشی",
            subtitle = "شایع‌ترین نوع سردرد",
            iconName = "Psychology",
            category = "مغز و اعصاب",
            content = "🔹 تعریف:\nسردردی که به صورت احساس فشار در دو طرف سر یا گردن حس می‌شود.\n\n🔹 علائم شایع:\n- احساس فشار دور سر\n- درد در ناحیه گردن و شانه\n\n🔹 چی خوبه:\n- مدیریت استرس\n- ماساژ عضلات گردن\n\n🔹 درمان:\nمسکن‌های ساده و فیزیوتراپی.\n\n🚨 علائم خطر:\n- سردردی که ناگهان بدتر می‌شود"
        ),
        Disease(
            id = "epilepsy",
            title = "صرع (اپی‌لپسی)",
            subtitle = "تشنج‌های مکرر",
            iconName = "Psychology",
            category = "مغز و اعصاب",
            content = "🔹 تعریف:\nاختلال عصبی مزمن با تشنج‌های مکرر.\n\n🔹 علائم شایع:\n- انقباضات عضلانی شدید\n- از دست دادن هوشیاری\n\n🔹 چی خوبه:\n- مصرف منظم داروهای ضدتشنج\n- خواب کافی\n\n🔹 چی مضره:\n- قطع خودسرانه دارو\n\n🚨 علائم خطر:\n- تشنج بیش از ۵ دقیقه"
        ),
        Disease(
            id = "alzheimer",
            title = "آلزایمر",
            subtitle = "زوال عقل و فراموشی",
            iconName = "Psychology",
            category = "مغز و اعصاب",
            content = "🔹 تعریف:\nشایع‌ترین علت دمانس که حافظه را تحت تاثیر قرار می‌دهد.\n\n🔹 علائم شایع:\n- فراموشی وقایع اخیر\n- گم کردن وسایل\n\n🔹 چی خوبه:\n- فعالیت ذهنی (پازل، مطالعه)\n- ورزش منظم\n\n🚨 علائم خطر:\n- گیجی حاد و شدید\n- تغییرات ناگهانی در رفتار"
        ),
        Disease(
            id = "parkinson",
            title = "پارکینسون",
            subtitle = "اختلال حرکتی پیش‌رونده",
            iconName = "Psychology",
            category = "مغز و اعصاب",
            content = "🔹 تعریف:\nاختلال عصبی پیش‌رونده که بر حرکت و تعادل تأثیر می‌گذارد.\n\n🔹 علائم شایع:\n- لرزش در حالت استراحت\n- سفتی عضلات\n\n🔹 چی خوبه:\n- ورزش و فیزیوتراپی\n\n🔹 درمان:\nداروهای حاوی لوودوپا (با تجویز پزشک).\n\n🚨 علائم خطر:\n- زمین خوردن‌های مکرر\n- مشکل در بلع"
        ),
        Disease(
            id = "stroke",
            title = "سکته مغزی",
            subtitle = "اورژانس نورولوژیک",
            iconName = "Psychology",
            category = "مغز و اعصاب",
            content = "🔹 تعریف:\nوقفه در خون‌رسانی به مغز.\n\n🔹 علائم شایع (FAST):\n- افتادگی صورت\n- ضعف در یک دست\n- اختلال در تکلم\n\n🔹 چی خوبه:\n- کنترل فشار خون و قند خون\n\n🔹 چی مضره:\n- سیگار و چاقی\n\n🚨 علائم خطر:\n- هرگونه علامت FAST (فوراً با ۱۱۵ تماس بگیرید)"
        )
    ) + EyeDiseases.diseases + EntDiseases.diseases + DentalDiseases.diseases + 
        HeartLungDiseases.diseases + GastroDiseases.diseases + UrologyDiseases.diseases + 
        ReproductiveDiseases.diseases + OrthopedicDiseases.diseases + SkinHairDiseases.diseases + 
        EndocrineBloodDiseases.diseases + PathogenDiseases.diseases + PsychiatricDiseases.diseases

    // لیست داروها
    val drugs = listOf(
        Drug(
            id = "acetaminophen",
            name = "استامینوفن",
            genericName = "Acetaminophen",
            category = "مسکن و تب‌بر",
            usage = "کاهش درد خفیف تا متوسط و پایین آوردن تب.",
            sideEffects = "در دوزهای معمول بی‌خطر است. دوز بالا به کبد آسیب می‌زند.",
            warnings = "⚠️ مصرف همزمان با الکل ممنوع است. افراد دارای بیماری کبدی حتماً با پزشک مشورت کنند."
        ),
        Drug(
            id = "ibuprofen",
            name = "ایبوپروفن",
            genericName = "Ibuprofen",
            category = "مسکن و ضدالتهاب (NSAID)",
            usage = "کاهش درد، التهاب و تب.",
            sideEffects = "سوزش معده، تهوع.",
            warnings = "⚠️ حتماً بعد از غذا مصرف شود. در افراد با زخم معده یا نارسایی کلیه ممنوع است."
        ),
        Drug(
            id = "metformin",
            name = "متفورمین",
            genericName = "Metformin",
            category = "داروی دیابت",
            usage = "کنترل قند خون در دیابت نوع ۲.",
            sideEffects = "تهوع، اسهال در روزهای اول.",
            warnings = "⚠️ فقط با نسخه پزشک. در نارسایی شدید کلیوی ممنوع است."
        ),
        Drug(
            id = "omeprazole",
            name = "امپرازول",
            genericName = "Omeprazole",
            category = "کاهنده اسید معده (PPI)",
            usage = "درمان ریفلاکس معده، زخم معده و سوزش سر دل.",
            sideEffects = "سردرد، اسهال، یبوست.",
            warnings = "⚠️ معمولاً نیم ساعت قبل از صبحانه مصرف می‌شود."
        ),
        Drug(
            id = "amoxicillin",
            name = "آموکسی‌سیلین",
            genericName = "Amoxicillin",
            category = "آنتی‌بیوتیک (پنی‌سیلین)",
            usage = "درمان عفونت‌های باکتریایی.",
            sideEffects = "اسهال، تهوع، راش پوستی.",
            warnings = "⚠️ در صورت حساسیت به پنی‌سیلین ممنوع است. دوره درمان را کامل کنید."
        ),
        Drug(
            id = "cetirizine",
            name = "ستیریزین",
            genericName = "Cetirizine",
            category = "آنتی‌هیستامین",
            usage = "درمان آلرژی (عطسه، آبریزش بینی، کهیر).",
            sideEffects = "خواب‌آلودگی خفیف، خشکی دهان.",
            warnings = "⚠️ ممکن است باعث خواب‌آلودگی شود. در رانندگی احتیاط کنید."
        ),
        Drug(
            id = "losartan",
            name = "لوزارتان",
            genericName = "Losartan",
            category = "داروی فشار خون (ARB)",
            usage = "کنترل فشار خون بالا.",
            sideEffects = "سرگیجه، خستگی.",
            warnings = "⚠️ فقط با نسخه پزشک. در بارداری ممنوع است."
        ),
        Drug(
            id = "sertraline",
            name = "سرترالین",
            genericName = "Sertraline",
            category = "داروی ضد افسردگی (SSRI)",
            usage = "درمان افسردگی، اضطراب و وسواس.",
            sideEffects = "تهوع، بی‌خوابی، سردرد.",
            warnings = "⚠️ فقط با نسخه پزشک. قطع ناگهانی علائم ترک ایجاد می‌کند."
        ),
        Drug(
            id = "salbutamol",
            name = "سالبوتامول",
            genericName = "Salbutamol",
            category = "گشادکننده برونش (اسپری)",
            usage = "تسکین تنگی نفس در آسم.",
            sideEffects = "لرزش خفیف، تپش قلب.",
            warnings = "⚠️ استفاده بیش از حد از اسپری نشانه عدم کنترل آسم است."
        ),
        Drug(
            id = "levothyroxine",
            name = "لووتیروکسین",
            genericName = "Levothyroxine",
            category = "هورمون تیروئید",
            usage = "درمان کم‌کاری تیروئید.",
            sideEffects = "معمولاً بدون عوارض در دوز صحیح.",
            warnings = "⚠️ باید ناشتا مصرف شود."
        )
    )
}