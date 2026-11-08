import os
import json
import base64
import requests
import logging
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer
from telegram import Update
from telegram.ext import Application, CommandHandler, MessageHandler, filters, ContextTypes

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)

TELEGRAM_TOKEN = os.environ.get("TELEGRAM_BOT_TOKEN", "YOUR_TELEGRAM_TOKEN")
GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN", "YOUR_GITHUB_TOKEN")
ADMIN_ID = int(os.environ.get("ADMIN_ID", "0"))

GITHUB_USER = "aliafshartm-star"
GITHUB_REPO = "viapezeshk"
API_BASE_URL = f"https://api.github.com/repos/{GITHUB_USER}/{GITHUB_REPO}/contents"

HEADERS = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json"
}

# ======== سرور فیک برای رندر ========
class DummyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.end_headers()
        self.wfile.write(b"Bot is running successfully!")

def run_dummy_server():
    port = int(os.environ.get("PORT", 8080))
    server = HTTPServer(('0.0.0.0', port), DummyHandler)
    logger.info(f"Fake server started on port {port}")
    server.serve_forever()

# ======== دستور شروع ========
async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    user_id = update.effective_user.id
    if user_id != ADMIN_ID:
        await update.message.reply_text("⛔ شما دسترسی به این ربات ندارید.")
        return

    await update.message.reply_text(
        "سلام مدیر عزیز! 👋\n"
        "به ربات مدیریت ویاپزشک خوش آمدید.",
        reply_markup={
            "inline_keyboard": [[
                {"text": "🚀 باز کردن پنل مدیریت", "web_app": {"url": "https://aliafshartm-star.github.io/viapezeshk/miniapp/"}}
            ]]
        }
    )

# ======== تابع کمکی برای آپدیت فایل در گیت‌هاب ========
def update_github_file(file_path, new_content_json, commit_message):
    url = f"{API_BASE_URL}/{file_path}"
    response = requests.get(url, headers=HEADERS)
    
    sha = None
    if response.status_code == 200:
        sha = response.json()['sha']
    elif response.status_code != 404:
        # خطایی به جز "فایل وجود ندارد"
        return False, f"خطا در خواندن فایل: {response.status_code}"

    updated_content = json.dumps(new_content_json, indent=2, ensure_ascii=False)
    content_base64 = base64.b64encode(updated_content.encode('utf-8')).decode('utf-8')

    payload = {
        "message": commit_message,
        "content": content_base64
    }
    if sha:
        payload["sha"] = sha

    put_response = requests.put(url, headers=HEADERS, json=payload)
    return put_response.status_code in [200, 201], put_response.status_code

# ======== دریافت اطلاعات از مینی‌اپ ========
async def handle_web_app_data(update: Update, context: ContextTypes.DEFAULT_TYPE):
    if update.effective_user.id != ADMIN_ID:
        return

    try:
        payload = json.loads(update.effective_message.web_app_data.data)
        action = payload.get("action")
        data = payload.get("data")

        await update.message.reply_text("⏳ در حال پردازش و ارسال به گیت‌هاب...")

        # ---- حالت اول: ثبت بنر جدید ----
        if action == "add_banner":
            # خواندن فایل config.json فعلی
            url = f"{API_BASE_URL}/data/config.json"
            res = requests.get(url, headers=HEADERS)
            if res.status_code != 200:
                await update.message.reply_text("❌ خطا در خواندن config.json")
                return
            
            config_json = json.loads(base64.b64decode(res.json()['content']).decode('utf-8'))
            config_json['banners'].append(data)
            
            # ذخیره فایل
            success, code = update_github_file("data/config.json", config_json, f"Add banner: {data['id']}")
            
            if success:
                await update.message.reply_text(f"✅ بنر «{data['content']['title']}» با موفقیت ثبت شد!")
            else:
                await update.message.reply_text(f"❌ خطا در ذخیره بنر. کد: {code}")

        # ---- حالت دوم: آپدیت ارتباطات ----
        elif action == "update_contacts":
            contact_data = {"contacts": data} # ساختار فایل جدید
            
            success, code = update_github_file("data/contact.json", contact_data, "Update contact links")
            
            if success:
                await update.message.reply_text("✅ لیست ارتباطات با موفقیت آپدیت شد!")
            else:
                await update.message.reply_text(f"❌ خطا در ذخیره ارتباطات. کد: {code}")

    except Exception as e:
        logger.error(f"Error: {e}")
        await update.message.reply_text("❌ خطایی رخ داد. لطفاً دوباره تلاش کنید.")

# ======== اجرای ربات ========
def main():
    threading.Thread(target=run_dummy_server, daemon=True).start()
    app = Application.builder().token(TELEGRAM_TOKEN).build()
    app.add_handler(CommandHandler("start", start))
    app.add_handler(MessageHandler(filters.StatusUpdate.WEB_APP_DATA, handle_web_app_data))
    
    logger.info("ربات تلگرام شروع به کار کرد...")
    app.run_polling(close_loop=False)

if __name__ == "__main__":
    main()