package me.codeenzyme.reminder.home

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicationModel(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String?,

    @get:PropertyName("medication_name")
    @set:PropertyName("medication_name")
    var medicationName: String?,

    @get:PropertyName("medication_description")
    @set:PropertyName("medication_description")
    var medicationDescription: String?,

    @get:PropertyName("medication_dosage")
    @set:PropertyName("medication_dosage")
    var medicationDosage: Int?,

    @get:PropertyName("medication_dosage_type")
    @set:PropertyName("medication_dosage_type")

    var medicationDosageType: String?,

    @get:PropertyName("medication_interval")
    @set:PropertyName("medication_interval")
    var medicationInterval: Int?,

    @get:PropertyName("start_time")
    @set:PropertyName("start_time")
    var startTime: Timestamp?,

    @get:PropertyName("next_time")
    @set:PropertyName("next_time")
    var nextTime: Timestamp?,

    @get:PropertyName("alarm_id")
    @set:PropertyName("alarm_id")
    var alarmId: Int?,

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    @ServerTimestamp
    var createdAt: Timestamp?
): Parcelable {
    constructor() : this(null,null, null, null, null, null, null, null, null, null)
}
