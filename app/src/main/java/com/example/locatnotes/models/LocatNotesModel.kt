package com.example.locatnotes.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class LocatNotesModel (
    val id: Int,
    val title: String?,
    val description: String?,
    val imageUri: String?,
    val location: String?,
    val date: String?,
    val latitude: Double,
    val longitude: Double
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(imageUri)
        parcel.writeString(location)
        parcel.writeString(date)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocatNotesModel> {
        override fun createFromParcel(parcel: Parcel): LocatNotesModel {
            return LocatNotesModel(parcel)
        }

        override fun newArray(size: Int): Array<LocatNotesModel?> {
            return arrayOfNulls(size)
        }
    }
}