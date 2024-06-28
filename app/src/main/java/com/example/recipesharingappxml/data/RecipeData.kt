package com.example.recipesharingappxml.data

import android.os.Parcel
import android.os.Parcelable

data class RecipeData(
    val id: String,
    val title: String,
    val type: String,
    val ingredients: String,
    val steps: String,
    val tags: String,
    val saves: Int,
    val imageUrl: String,
    val date: String,
    val userEmail: String,
    val userName: String,
    val user: UserData?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString()?:" ",
        parcel.readParcelable(UserData::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(type)
        parcel.writeString(ingredients)
        parcel.writeString(steps)
        parcel.writeString(tags)
        parcel.writeInt(saves)
        parcel.writeString(imageUrl)
        parcel.writeString(date)
        parcel.writeString(userEmail)
        parcel.writeString(userName)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeData> {
        override fun createFromParcel(parcel: Parcel): RecipeData {
            return RecipeData(parcel)
        }

        override fun newArray(size: Int): Array<RecipeData?> {
            return arrayOfNulls(size)
        }
    }
}
