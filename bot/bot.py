import os
import json
import base64
import requests
import logging
import asyncio
from telegram import Update
from telegram.ext import Application, CommandHandler, MessageHandler, filters, ContextTypes

logging.basicConfig(format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)

TELEGRAM_TOKEN = os.environ.get("TELEGRAM_BOT_TOKEN", "YOUR_TELEGRAM_TOKEN")
GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN", "YOUR_GITHUB_TOKEN")
GITHUB_USER = "aliafshartm-star"
GITHUB_REPO = "viapezeshk"
GITHUB_FILE_PATH = "data/config.json"

GITHUB_API_URL = f"https://api.github.com/repos/{GITHUB_USER}/{GITHUB_REPO}/contents/{GITHUB_FILE_PATH}"

HEADERS = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json"
}

async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "سلام مدیر عزیز! 👋\n"
        "به ربات مدیریت ویاپزشک خوش آمدید.\n"
        "برای باز کردن پنل مدیریت، روی دکمه زیر بزنید:",
        reply_markup={
            "inline_keyboard": [[
                {"text": "🚀 باز کردن پنل مدیریت", "web_app": {"url": "https://aliafshartm-star.github.io/viapezeshk/miniapp/"}}
            ]]
        }
    )

async def handle_web_app_data(update: Update, context: ContextTypes.DEFAULT_TYPE):
    try:
        data = json.loads(update.effective_message.web_app_data.data)
        await update.message.reply_text("⏳ در حال پردازش و ارسال به گیت‌هاب...")
        
        response = requests.get(GITHUB_API_URL, headers=HEADERS)
        if response.status_code != 200:
            await update.message.reply_text("❌ خطا در خواندن فایل از گیت‌هاب.")
            return

        file_info = response.json()
        sha = file_info['sha']
        content_base64 = file_info['content']
        content_json = json.loads(base64.b64decode(content_base64).decode('utf-8'))
        
        content_json['banners'].append(data)
        
        updated_content = json.dumps(content_json, indent=2, ensure_ascii=False)
        updated_content_base64 = base64.b64encode(updated_content.encode('utf-8')).decode('utf-8')
        
        payload = {
            "message": f"Update banner from Telegram: {data['id']}",
            "content": updated_content_base64,
            "sha": sha
        }
        
        put_response = requests.put(GITHUB_API_URL, headers=HEADERS, json=payload)
        
        if put_response.status_code == 200:
            await update.message.reply_text(f"✅ بنر «{data['content']['title']}» با موفقیت در گیت‌هاب ثبت شد!")
        else:
            await update.message.reply_text(f"❌ خطا در ذخیره در گیت‌هاب: {put_response.status_code}")
            
    except Exception as e:
        logger.error(f"Error: {e}")
        await update.message.reply_text("❌ خطایی رخ داد. لطفاً دوباره تلاش کنید.")

def main():
    asyncio.set_event_loop(asyncio.new_event_loop())
    app = Application.builder().token(TELEGRAM_TOKEN).build()
    app.add_handler(CommandHandler("start", start))
    app.add_handler(MessageHandler(filters.StatusUpdate.WEB_APP_DATA, handle_web_app_data))
    logger.info("ربات شروع به کار کرد...")
    app.run_polling(close_loop=False)

if __name__ == "__main__":
    main()