package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DailyQuranReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_REMINDER = "com.example.ACTION_DAILY_REMINDER"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("DailyReminderReceiver", "onReceive triggered with action: ${intent?.action}")
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-schedule alarm on device reboot if enabled
                if (DailyReminderManager.isReminderEnabled(context)) {
                    val (hour, minute) = DailyReminderManager.getReminderTime(context)
                    DailyReminderManager.scheduleAlarm(context, hour, minute)
                }
            }
            ACTION_REMINDER, null -> {
                if (DailyReminderManager.isReminderEnabled(context)) {
                    DailyReminderManager.sendNotification(context, isTest = false)
                }
            }
        }
    }
}
