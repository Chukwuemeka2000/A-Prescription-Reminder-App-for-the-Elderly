package me.codeenzyme.reminder.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MedicationViewModelFactory(private val medicationRepository: MedicationRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  modelClass.getConstructor(MedicationRepository::class.java).newInstance(medicationRepository)
    }
}