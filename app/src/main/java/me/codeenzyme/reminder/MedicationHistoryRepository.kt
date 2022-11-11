package me.codeenzyme.reminder

interface MedicationHistoryRepository {

    fun addMedicationHistory(medicationModel: MedicationHistory, onCompleteCallback: (MedicationRepoStatus) -> Unit)
    fun getMedicationHistory(callback: (medicationHistories: List<MedicationHistory>) -> Unit)
    fun getUsername(callback: (name: String) -> Unit)
    fun getPhone(callback: (phone: String) -> Unit)

}