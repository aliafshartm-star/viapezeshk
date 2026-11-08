// اتصال به مینی‌اپ تلگرام
const tg = window.Telegram.WebApp;
tg.expand();
tg.ready();

if (tg.initDataUnsafe && tg.initDataUnsafe.user) {
  document.getElementById('userName').innerText = tg.initDataUnsafe.user.first_name || "مدیر";
}

// ======== مدیریت تب‌ها ========
function switchTab(tabName) {
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
  
  if (tabName === 'add') {
    document.querySelectorAll('.tab')[0].classList.add('active');
    document.getElementById('tab-add').classList.add('active');
  } else if (tabName === 'list') {
    document.querySelectorAll('.tab')[1].classList.add('active');
    document.getElementById('tab-list').classList.add('active');
  } else if (tabName === 'contact') {
    document.querySelectorAll('.tab')[2].classList.add('active');
    document.getElementById('tab-contact').classList.add('active');
  }
}

// ======== منطق بنرها ========
function addBanner() {
  const position = document.getElementById('bannerPosition').value;
  const image = document.getElementById('bannerImage').value;
  const title = document.getElementById('bannerTitle').value;
  const subtitle = document.getElementById('bannerSubtitle').value;
  const bgColor = document.getElementById('bannerColor').value;
  const city = document.getElementById('bannerCity').value;
  const actionType = document.getElementById('actionType').value;
  const actionValue = document.getElementById('actionValue').value;

  if (!title || !actionValue) {
    tg.showAlert("لطفاً عنوان و لینک/شماره تماس را وارد کنید.");
    return;
  }

  let bannerType = image.trim() !== "" ? "image_text" : "text_only";

  const newBanner = {
    id: "banner_" + Date.now(),
    position: position,
    type: bannerType,
    active: true,
    priority: 1,
    targeting: {
      cities: city === "all" ? [] : [city]
    },
    content: {
      title: title,
      subtitle: subtitle,
      bgColor: bgColor,
      textColor: "#ffffff",
      image: image.trim() !== "" ? image : null
    },
    action: {
      type: actionType,
      value: actionValue
    }
  };

  // ارسال به ربات با مشخص کردن نوع عملیات
  const payload = { action: "add_banner", data: newBanner };
  tg.sendData(JSON.stringify(payload));
  tg.close();
}

// ======== منطق ارتباطات پویا ========
let tempContacts = [];

function addContactItem() {
  const icon = document.getElementById('contactIcon').value;
  const title = document.getElementById('contactTitle').value;
  const value = document.getElementById('contactValue').value;
  const type = document.getElementById('contactType').value;

  if (!title || !value) {
    tg.showAlert("لطفاً عنوان و لینک/شماره را وارد کنید.");
    return;
  }

  const newItem = { id: Date.now(), icon, title, value, type };
  tempContacts.push(newItem);
  renderContactsPreview();
  
  // پاک کردن فرم
  document.getElementById('contactTitle').value = '';
  document.getElementById('contactValue').value = '';
}

function renderContactsPreview() {
  const list = document.getElementById('contactPreviewList');
  list.innerHTML = '';
  
  if (tempContacts.length === 0) {
    list.innerHTML = '<p style="color: #888; font-size: 13px; text-align: center;">هنوز کادری اضافه نشده است.</p>';
    return;
  }

  tempContacts.forEach(item => {
    const div = document.createElement('div');
    div.style.cssText = 'display:flex; justify-content:space-between; align-items:center; padding:12px; background:#f0f4f4; border-radius:10px; margin-bottom:8px; border: 1px solid #e0e8e8;';
    div.innerHTML = `
      <div style="font-size: 14px;">
        <span style="font-weight: bold;">${item.title}</span><br>
        <span style="color: #7a8b8b; font-size: 12px;">${item.icon} - ${item.type}</span>
      </div>
      <button onclick="removeContact(${item.id})" style="background:#ff5252; color:white; border:none; padding:6px 12px; border-radius:8px; cursor:pointer; font-family:inherit;">حذف</button>
    `;
    list.appendChild(div);
  });
}

function removeContact(id) {
  tempContacts = tempContacts.filter(c => c.id !== id);
  renderContactsPreview();
}

function saveAllContacts() {
  if (tempContacts.length === 0) {
    tg.showAlert("لیست ارتباطات خالی است!");
    return;
  }
  
  const payload = { action: "update_contacts", data: tempContacts };
  tg.sendData(JSON.stringify(payload));
  tg.close();
}