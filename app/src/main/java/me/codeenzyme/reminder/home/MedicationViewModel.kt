package me.codeenzyme.reminder.home

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.type.DateTime
import me.codeenzyme.reminder.*
import java.util.*

class MedicationViewModel(private val medicationRepository: MedicationRepository, private val context: Context): ViewModel() {

    private val data = mutableStateOf<List<MedicationModel>?>(null)

    private var alarmManager: AlarmManager? = null

    init {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    @SuppressLint("InlinedApi")
    fun setAlarm(title: String, message: String, reqId: Int, time: Long, interval: Long) {

        // test for below android 10 whether activity will open from background
        // use notification for >= android 10

        val currentTime = System.currentTimeMillis()

        if (currentTime > time) return

        val pendingIntent = Intent(context, AlarmReceiver::class.java).let {
            it.putExtra(ALARM_TITLE, title)
            it.putExtra(ALARM_MESSAGE, message)
            it.putExtra(ALARM_INTERVAL, interval)
            it.putExtra(ALARM_CURRENT_RING_TIME, time)
            it.action = ALARM_ACTION
            it.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
            PendingIntent.getBroadcast(context, reqId, it, PendingIntent.FLAG_IMMUTABLE)
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        else
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)*/

        val clockInfo = AlarmManager.AlarmClockInfo(time, pendingIntent)
        alarmManager?.setAlarmClock(clockInfo, pendingIntent)
        Toast.makeText(context, "Alarm set successfully", Toast.LENGTH_SHORT).show()
    }

    fun addMedication(medicationModel: MedicationModel, onCompleteCallback: (MedicationRepoStatus) -> Unit) {
        medicationRepository.addMedication(medicationModel, onCompleteCallback)
    }

    fun getMedications(): MutableState<List<MedicationModel>?> {
        medicationRepository.getMedications {
            data.value = it
        }
        return data
    }

    fun deleteMedication(medicationModel: MedicationModel, onCompleteCallback: ((MedicationRepoStatus) -> Unit)? = null) {
        medicationRepository.removeMedication(medicationModel, onCompleteCallback)
    }
}