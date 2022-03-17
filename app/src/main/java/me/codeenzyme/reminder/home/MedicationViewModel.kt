package me.codeenzyme.reminder.home

import androidx.lifecycle.ViewModel

class MedicationViewModel(private val medicationRepository: MedicationRepository): ViewModel() {

    fun addMedication(medicationModel: MedicationModel, onCompleteCallback: (MedicationRepoStatus) -> Unit) {
        medicationRepository.addMedication(medicationModel, onCompleteCallback)
    }

    fun getMedications(onCompleteCallback: (List<MedicationModel>) -> Unit) {
        medicationRepository.getMedications {
            onCompleteCallback(it)
        }
    }

}