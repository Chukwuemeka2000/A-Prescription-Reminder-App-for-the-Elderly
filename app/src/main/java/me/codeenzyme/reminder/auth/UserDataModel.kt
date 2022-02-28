package me.codeenzyme.reminder.auth

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize


/*
* Model for user's data persisted in database
* Model is a parcelable so that it can be deserialized/serialized when passing around
*/
@Parcelize // from 'kotlin-parcelize` gradle plugin to convert plain object to parcelable
data class UserDataModel(
    @get:PropertyName("first_name") // used by firebase for serializing and deserializing
    @set:PropertyName("first_name") // // used by firebase for serializing and deserializing
    var firstName: String,
    @get:PropertyName("last_name")
    @set:PropertyName("last_name")
    var lastName: String,
    @get:PropertyName("phone")
    @set:PropertyName("phone")
    var phone: String,
): Parcelable // Parcelables are a preferred way for passing serializable objects in Android
