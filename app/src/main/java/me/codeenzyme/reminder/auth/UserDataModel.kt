package me.codeenzyme.reminder.auth

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDataModel(
    @get:PropertyName("first_name")
    @set:PropertyName("first_name")
    var firstName: String,
    @get:PropertyName("last_name")
    @set:PropertyName("last_name")
    var lastName: String,
    @get:PropertyName("phone")
    @set:PropertyName("phone")
    var phone: String,
): Parcelable
