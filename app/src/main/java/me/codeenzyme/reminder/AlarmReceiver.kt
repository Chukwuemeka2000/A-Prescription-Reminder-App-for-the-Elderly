package me.codeenzyme.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("InlinedApi")
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("alarm", "Alarm fired")
        val serviceIntent = Intent(context, AlarmService::class.java)
        serviceIntent.apply {
            putExtra(ALARM_TITLE, intent?.getStringExtra(ALARM_TITLE) ?: "")
            putExtra(ALARM_MESSAGE, intent?.getStringExtra(ALARM_MESSAGE) ?: "")
            putExtra(ALARM_INTERVAL, intent?.getLongExtra(ALARM_INTERVAL, 0) ?: 0)
            putExtra(ALARM_CURRENT_RING_TIME, intent?.getLongExtra(ALARM_CURRENT_RING_TIME, 0) ?: 0)
            putExtra(ALARM_ID, intent?.getIntExtra(ALARM_ID, 0))
            action = AlarmService.REMINDER_ALARM_SERVICE
        }
        context?.startForegroundService(serviceIntent)
    }

    companion object {
        const val CHANNEL_ID = "reminder_alarm"
    }
}