package me.codeenzyme.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "REMINDER ALARM", NotificationManager.IMPORTANCE_HIGH))
        }

        intent?.let { newIntent ->
            if (newIntent.action != ALARM_ACTION) {
                return
            }
            val title = newIntent.getStringExtra(ALARM_TITLE)
            val message = newIntent.getStringExtra(ALARM_MESSAGE)
            // context?.startActivity()


            val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(ALARM_TITLE, title)
                putExtra(ALARM_MESSAGE, message)
            }
            val fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_IMMUTABLE)

            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_round_alarm_on_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setFullScreenIntent(fullScreenPendingIntent, true)


            notificationManager.notify(1, builder.build())
            //
        }
    }

    companion object {
        const val CHANNEL_ID = "reminder_alarm"
    }
}