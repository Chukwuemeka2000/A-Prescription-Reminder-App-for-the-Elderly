package me.codeenzyme.reminder.home

interface MedicationRepository {
    fun addMedication(medicationModel: MedicationModel, onCompleteCallback: (MedicationRepoStatus) -> Unit)
    fun removeMedication(medicationModel: MedicationModel, onCompleteCallback: ((MedicationRepoStatus) -> Unit)?)
    fun updateMedication(medicationModel: MedicationModel, onCompleteCallback: (MedicationRepoStatus) -> Unit)
    fun getMedications(callback: (List<MedicationModel>) -> Unit)
}