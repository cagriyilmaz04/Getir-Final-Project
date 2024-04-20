package com.example.data.models

import com.google.gson.annotations.SerializedName

data class BeveragePack(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("productCount")
    val productCount: Int,
    @SerializedName("products")
    val products: List<Product>
)