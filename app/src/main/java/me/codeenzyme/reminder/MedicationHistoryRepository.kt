package me.codeenzyme.reminder

interface MedicationHistoryRepository {

    fun addMedicationHistory(medicationModel: MedicationHistory, onCompleteCallback: (MedicationRepoStatus) -> Unit)
    fun getMedicationHistory(callback: (medicationHistories: List<MedicationHistory>) -> Unit)

}