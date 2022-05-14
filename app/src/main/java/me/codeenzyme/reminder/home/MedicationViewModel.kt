package me.codeenzyme.reminder.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import me.codeenzyme.reminder.ALARM_ACTION
import me.codeenzyme.reminder.ALARM_MESSAGE
import me.codeenzyme.reminder.ALARM_TITLE
import me.codeenzyme.reminder.AlarmReceiver

class MedicationViewModel(private val medicationRepository: MedicationRepository, private val context: Context): ViewModel() {

    private val data = mutableStateOf<List<MedicationModel>?>(null)

    private var alarmManager: AlarmManager? = null

    init {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    fun setAlarm(title: String, message: String, reqId: Int, time: Long, interval: Long) {
        val pendingIntent = Intent(context, AlarmReceiver::class.java).let {
            it.putExtra(ALARM_TITLE, title)
            it.putExtra(ALARM_MESSAGE, message)
            it.action = ALARM_ACTION
            PendingIntent.getBroadcast(context, reqId, it, 0)
        }
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
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