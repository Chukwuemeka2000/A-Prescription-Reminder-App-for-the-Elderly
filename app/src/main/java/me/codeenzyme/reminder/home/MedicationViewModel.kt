package me.codeenzyme.reminder.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MedicationViewModel(private val medicationRepository: MedicationRepository): ViewModel() {

    private val data = mutableStateOf<List<MedicationModel>?>(null)

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