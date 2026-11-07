// اتصال به مینی‌اپ تلگرام
const tg = window.Telegram.WebApp;
tg.expand(); // باز شدن تمام‌صفحه
tg.ready();

// نمایش نام کاربر (اگر توی تلگرام باز شده باشه)
if (tg.initDataUnsafe && tg.initDataUnsafe.user) {
  document.getElementById('userName').innerText = tg.initDataUnsafe.user.first_name || "مدیر";
}

// تغییر بین تب‌ها
function switchTab(tabName) {
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
  
  if (tabName === 'add') {
    document.querySelectorAll('.tab')[0].classList.add('active');
    document.getElementById('tab-add').classList.add('active');
  } else {
    document.querySelectorAll('.tab')[1].classList.add('active');
    document.getElementById('tab-list').classList.add('active');
  }
}

// تابع ثبت و انتشار بنر
function addBanner() {
  // گرفتن مقادیر از فرم
  const position = document.getElementById('bannerPosition').value;
  const title = document.getElementById('bannerTitle').value;
  const subtitle = document.getElementById('bannerSubtitle').value;
  const bgColor = document.getElementById('bannerColor').value;
  const city = document.getElementById('bannerCity').value;
  const actionType = document.getElementById('actionType').value;
  const actionValue = document.getElementById('actionValue').value;

  // بررسی خالی نبودن فیلدهای ضروری
  if (!title || !actionValue) {
    tg.showAlert("لطفاً عنوان و لینک/شماره تماس را وارد کنید.");
    return;
  }

  // ساخت آبجکت بنر مطابق با استاندارد config.json
  const newBanner = {
    id: "banner_" + Date.now(),
    position: position,
    type: "text_only", // فعلا متنی است، بعدا می‌تونه عکس داشته باشه
    active: true,
    priority: 1,
    targeting: {
      cities: city === "all" ? [] : [city]
    },
    content: {
      title: title,
      subtitle: subtitle,
      bgColor: bgColor,
      textColor: "#ffffff"
    },
    action: {
      type: actionType,
      value: actionValue
    }
  };

  // ارسال داده‌ها به ربات تلگرام (بک‌اند ما)
  // این اطلاعات به عنوان یک پیام به ربات فرستاده میشه
  tg.sendData(JSON.stringify(newBanner));
  
  // بستن مینی‌اپ بعد از ارسال
  tg.close();
}