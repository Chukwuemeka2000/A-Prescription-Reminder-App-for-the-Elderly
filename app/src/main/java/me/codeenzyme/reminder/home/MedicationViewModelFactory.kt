package me.codeenzyme.reminder.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MedicationViewModelFactory(private val medicationRepository: MedicationRepository, private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  modelClass.getConstructor(MedicationRepository::class.java, Context::class.java).newInstance(medicationRepository, context)
    }
}