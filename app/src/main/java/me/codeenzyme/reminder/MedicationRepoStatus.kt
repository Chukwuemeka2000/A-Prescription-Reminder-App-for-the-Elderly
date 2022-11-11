package me.codeenzyme.reminder

sealed class MedicationRepoStatus {
    object Success: MedicationRepoStatus()
    object Failure: MedicationRepoStatus()
}
