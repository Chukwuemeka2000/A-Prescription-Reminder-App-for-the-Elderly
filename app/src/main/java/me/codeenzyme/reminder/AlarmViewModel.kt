package me.codeenzyme.reminder

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.codeenzyme.reminder.home.MedicationModel

class AlarmViewModel(
    private val medicationHistoryRepository: MedicationHistoryRepository
): ViewModel() {

    private val data = mutableStateOf<List<MedicationHistory>?>(null)

    val name = mutableStateOf<String?>(null)
    val phone = mutableStateOf<String?>(null)

    fun addMedicationHistory(medicationHistory: MedicationHistory, callback: (MedicationRepoStatus) -> Unit) {
        viewModelScope.launch {
            medicationHistoryRepository.addMedicationHistory(medicationHistory) {
                callback(it)
            }
        }
    }

    fun getMedicationHistory(scope: CoroutineScope): MutableState<List<MedicationHistory>?> {
        scope.launch {
            medicationHistoryRepository.getUsername {
                name.value = it
            }
            medicationHistoryRepository.getPhone {
                phone.value = it
            }
            medicationHistoryRepository.getMedicationHistory {
                data.value = it
            }
        }
        return data
    }

}