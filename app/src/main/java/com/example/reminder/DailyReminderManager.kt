package com.example.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

object DailyReminderManager {

    private const val TAG = "DailyReminderManager"
    const val CHANNEL_ID = "quran_daily_reminder_channel"
    private const val PREFS_NAME = "quran_reminder_prefs"
    private const val KEY_ENABLED = "daily_reminder_enabled"
    private const val KEY_HOUR = "daily_reminder_hour"
    private const val KEY_MINUTE = "daily_reminder_minute"
    private const val KEY_MESSAGE_INDEX = "daily_reminder_message_index"

    const val NOTIFICATION_ID = 1001
    const val TEST_NOTIFICATION_ID = 1002

    val REMINDER_MESSAGES = listOf(
        Pair(
            "﴿أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ﴾",
            "حان وقت وردك اليومي المبارك من القرآن الكريم لراحة قلبك وسكينتك."
        ),
        Pair(
            "﴿وَرَتِّلِ الْقُرْآنَ تَرْتِيلًا﴾",
            "دقائق معدودة مع كتاب الله تصنع بركة يومك، افتح المصحف المعلم الآن."
        ),
        Pair(
            "«خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ»",
            "استكمل تدريب التلاوة وضبط أحكام التجويد في ورد اليوم."
        ),
        Pair(
            "﴿إِنَّ هَٰذَا الْقُرْآنَ يَهْدِي لِلَّتِي هِيَ أَقْوَمُ﴾",
            "نور دربك بتلاوة وتدبر آيات كتاب الله العزيز."
        ),
        Pair(
            "﴿كِتَابٌ أَنزَلْنَاهُ إِلَيْكَ مُبَارَكٌ لِّيَدَّبَّرُوا آيَاتِهِ﴾",
            "لا تحرم نفسك من أجر ونور قراءة ورد اليوم."
        )
    )

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isReminderEnabled(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ENABLED, true)
    }

    fun setReminderEnabled(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
        if (enabled) {
            val (hour, minute) = getReminderTime(context)
            scheduleAlarm(context, hour, minute)
        } else {
            cancelAlarm(context)
        }
    }

    fun getReminderTime(context: Context): Pair<Int, Int> {
        val prefs = getPrefs(context)
        val hour = prefs.getInt(KEY_HOUR, 8)
        val minute = prefs.getInt(KEY_MINUTE, 30)
        return Pair(hour, minute)
    }

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        getPrefs(context).edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
        if (isReminderEnabled(context)) {
            scheduleAlarm(context, hour, minute)
        }
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "تذكير الورد القرآني اليومي"
            val channelDesc = "تنبيهات يومية لتشجيع الطالب على تلاوة وتدبر القرآن الكريم"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                description = channelDesc
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun scheduleAlarm(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DailyQuranReminderReceiver::class.java).apply {
            action = DailyQuranReminderReceiver.ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
            Log.d(TAG, "Daily reminder scheduled for ${calendar.time}")
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling daily reminder alarm", e)
        }
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DailyQuranReminderReceiver::class.java).apply {
            action = DailyQuranReminderReceiver.ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        try {
            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "Daily reminder alarm cancelled")
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling alarm", e)
        }
    }

    fun sendNotification(context: Context, isTest: Boolean = false) {
        createNotificationChannel(context)

        val prefs = getPrefs(context)
        val currentIdx = prefs.getInt(KEY_MESSAGE_INDEX, 0)
        val messagePair = REMINDER_MESSAGES[currentIdx % REMINDER_MESSAGES.size]
        prefs.edit().putInt(KEY_MESSAGE_INDEX, (currentIdx + 1) % REMINDER_MESSAGES.size).apply()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            if (isTest) 1 else 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isTest) "تجربة تذكير الورد القرآني 🔔" else "تذكير الورد القرآني المبارك 📖"
        val body = "${messagePair.first}\n${messagePair.second}"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messagePair.first)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notifId = if (isTest) TEST_NOTIFICATION_ID else NOTIFICATION_ID
        try {
            notificationManager?.notify(notifId, builder.build())
            Log.d(TAG, "Notification sent with ID $notifId")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }

    fun sendImmediateTestNotification(context: Context) {
        sendNotification(context, isTest = true)
    }
}
