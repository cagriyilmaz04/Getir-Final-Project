package com.example.data.models

import com.google.gson.annotations.SerializedName

data class BeverageSuggestedPack(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("productCount")
    val productCount: Int,
    @SerializedName("products")
    val products: List<SuggestedProduct>
)