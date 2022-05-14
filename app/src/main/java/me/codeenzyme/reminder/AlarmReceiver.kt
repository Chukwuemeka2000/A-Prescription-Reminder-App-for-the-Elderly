package me.codeenzyme.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { newIntent ->
            if (newIntent.action != ALARM_ACTION) {
                return
            }

            startActivity(context!!, Intent(context, AlarmActivity::class.java), null)
        }
    }
}