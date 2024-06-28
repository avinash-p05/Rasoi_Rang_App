package com.example.recipesharingappxml.data

import android.os.Parcel
import android.os.Parcelable

data class UserData(
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val savedRecipes: List<String> = listOf(),
    val postedRecipes: List<String> = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.createStringArrayList() ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeStringList(savedRecipes)
        parcel.writeStringList(postedRecipes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserData> {
        override fun createFromParcel(parcel: Parcel): UserData {
            return UserData(parcel)
        }

        override fun newArray(size: Int): Array<UserData?> {
            return arrayOfNulls(size)
        }
    }
}
