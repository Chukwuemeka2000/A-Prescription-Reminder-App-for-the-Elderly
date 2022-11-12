package me.codeenzyme.reminder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.codeenzyme.reminder.home.MedicationRepository

class MedicationHistoryViewModelFactory(private val medicationHistoryRepository: MedicationHistoryRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  modelClass.getConstructor(MedicationHistoryRepository::class.java).newInstance(medicationHistoryRepository)
    }

}