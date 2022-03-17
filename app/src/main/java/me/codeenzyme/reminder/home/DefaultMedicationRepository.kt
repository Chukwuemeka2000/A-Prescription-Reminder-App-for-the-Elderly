package me.codeenzyme.reminder.home

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DefaultMedicationRepository : MedicationRepository {

    override fun addMedication(
        medicationModel: MedicationModel,
        onCompleteCallback: (MedicationRepoStatus) -> Unit
    ) {
        Firebase.auth.uid?.let {
            Firebase.firestore.collection("medications_${it}").add(medicationModel)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        onCompleteCallback(MedicationRepoStatus.Success)
                    } else {
                        onCompleteCallback(MedicationRepoStatus.Failure)
                    }
                }
        }
    }

    override fun removeMedication(
        medicationModel: MedicationModel,
        onCompleteCallback: (MedicationRepoStatus) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateMedication(
        medicationModel: MedicationModel,
        onCompleteCallback: (MedicationRepoStatus) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getMedications(callback: (List<MedicationModel>) -> Unit) {
        Firebase.auth.uid?.let {
            Firebase.firestore.collection("medications_${it}").addSnapshotListener { value, _ ->
                value?.let {
                    val docs = it.toObjects(MedicationModel::class.java)
                    callback(docs)
                }
            }
        }
    }


}