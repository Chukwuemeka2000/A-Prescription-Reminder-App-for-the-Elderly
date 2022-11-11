package me.codeenzyme.reminder

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

data class MedicationHistory(

    @get:PropertyName("medication_complete")
    @set:PropertyName("medication_complete")
    var complete: Boolean?,

    @get:PropertyName("medication_name")
    @set:PropertyName("medication_name")
    var medicationName: String?,

    @get:PropertyName("medication_description")
    @set:PropertyName("medication_description")
    var medicationDescription: String?,

    @get:PropertyName("medication_interval")
    @set:PropertyName("medication_interval")
    var medicationInterval: Int?,

    @get:PropertyName("medication_dosage")
    @set:PropertyName("medication_dosage")
    var medicationDosage: Int?,

    @get:PropertyName("medication_dosage_type")
    @set:PropertyName("medication_dosage_type")
    var medicationDosageType: String?,

    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String?,

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    @ServerTimestamp
    var createdAt: Timestamp?,
) {
    constructor() : this(null, null,null, null, null, null, null, null,)
}
