package me.codeenzyme.reminder.home

sealed class MedicationRepoStatus {
    object Success: MedicationRepoStatus()
    object Failure: MedicationRepoStatus()
}
