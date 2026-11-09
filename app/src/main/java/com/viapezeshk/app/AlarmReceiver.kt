package com.viapezeshk.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medName = intent.getStringExtra("med_name") ?: "دارو"
        val dosage = intent.getStringExtra("med_dosage") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medication_reminder_channel"

        // ساخت کانال اعلان برای اندروید ۸ به بالا
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "یادآور دارو",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "اعلان‌های مربوط به زمان مصرف دارو"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // باز کردن اپ با کلیک روی اعلان
        val openAppIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ساخت اعلان
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("وقت مصرف دارو! 💊")
            .setContentText("لطفاً $dosage از $medName را مصرف کنید.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(medName.hashCode(), notification)
    }
}