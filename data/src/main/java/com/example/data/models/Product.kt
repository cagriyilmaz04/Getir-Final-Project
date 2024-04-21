package com.example.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("attribute")
    val attribute:String?=null,
    @SerializedName("thumbnailURL")
    val thumbnailURL:String?=null,
    @SerializedName("price")
    val price: Double,
    @SerializedName("priceText")
    val priceText:String,
    @SerializedName("imageURL")
    val imageURL: String? = null,
    @SerializedName("shortDescription")
    val description: String? = null
):Parcelable