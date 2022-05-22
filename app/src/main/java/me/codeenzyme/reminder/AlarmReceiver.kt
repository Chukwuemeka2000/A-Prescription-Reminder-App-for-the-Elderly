package me.codeenzyme.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { newIntent ->
            if (newIntent.action != ALARM_ACTION) {
                return
            }
            val title = newIntent.getStringExtra(ALARM_TITLE)
            val message = newIntent.getStringExtra(ALARM_MESSAGE)
            context?.startActivity(Intent(context, AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(ALARM_TITLE, title)
                putExtra(ALARM_MESSAGE, message)
            })
        }
    }
}