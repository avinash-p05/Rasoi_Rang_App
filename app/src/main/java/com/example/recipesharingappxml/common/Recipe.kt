package com.example.recipesharingappxml.common

import android.os.Parcel
import android.os.Parcelable

class Recipe(
    val recipeId: String,
    val userName: String,
    val title: String,
    val ingredients: String,
    val steps: String,
    val imageUrl: String,
    val type: String,
    val tags: String,
    val date: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(recipeId)
        parcel.writeString(userName)
        parcel.writeString(title)
        parcel.writeString(ingredients)
        parcel.writeString(steps)
        parcel.writeString(imageUrl)
        parcel.writeString(type)
        parcel.writeString(tags)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}

class RecipeTrend(
    val recipeId: String,
    val userName: String,
    val title: String,
    val ingredients: String,
    val steps: String,
    val imageUrl: String,
    val type: String,
    val tags: String,
    val date: String,
    val save: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(recipeId)
        parcel.writeString(userName)
        parcel.writeString(title)
        parcel.writeString(ingredients)
        parcel.writeString(steps)
        parcel.writeString(imageUrl)
        parcel.writeString(type)
        parcel.writeString(tags)
        parcel.writeString(date)
        parcel.writeString(save)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RecipeTrend> {
        override fun createFromParcel(parcel: Parcel): RecipeTrend {
            return RecipeTrend(parcel)
        }

        override fun newArray(size: Int): Array<RecipeTrend?> {
            return arrayOfNulls(size)
        }
    }
}


data class RecipeOwn(
    val title: String,
    val type: String,
    val image:String,
    val date: String
)


//data class RecipeTrend(
//    val title: String,
//    val image:String,
//    val by: String,
//    val saves: String
//)