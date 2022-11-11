package me.codeenzyme.reminder

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import me.codeenzyme.reminder.home.MedicationModel

class MedicationHistoryRepositoryImpl: MedicationHistoryRepository {
    override fun addMedicationHistory(
        medicationHistory: MedicationHistory,
        onCompleteCallback: (MedicationRepoStatus) -> Unit
    ) {
        Firebase.auth.uid?.let {
            Firebase.firestore.collection("medication_histories_${it}").document(medicationHistory.id!!).set(medicationHistory)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onCompleteCallback(MedicationRepoStatus.Success)
                    } else {
                        onCompleteCallback(MedicationRepoStatus.Failure)
                    }
                }
        }
    }

    override fun getMedicationHistory(callback: (medicationHistories: List<MedicationHistory>) -> Unit) {
        Firebase.auth.uid?.let {
            Firebase.firestore.collection("medication_histories_${it}").addSnapshotListener { value, _ ->
                Log.d("REPO_FIREBASE", "DATA")
                value?.let {
                    val docs = it.toObjects(MedicationHistory::class.java)
                    callback(docs)
                }
            }
        }
    }
}