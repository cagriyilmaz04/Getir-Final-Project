package com.example.data.models

import com.google.gson.annotations.SerializedName

data class SuggestedProduct(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("attribute")
    val attribute:String?=null,
    @SerializedName("priceText")
    val priceText:String,
    @SerializedName("imageURL")
    val imageURL: String?=null,
    @SerializedName("shortDescription")
    val shortDescription: String?=null,
    @SerializedName("unitPrice")
    val unitPrice: Double ?= null,
    @SerializedName("squareThumbnailURL")
    val squareThumbnailURL: String ?= null

)