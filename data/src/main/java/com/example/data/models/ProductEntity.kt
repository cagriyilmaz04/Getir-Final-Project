package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products_table_getir")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val name: String,
    val attribute: String? = null,
    val thumbnailURL: String? = null,
    val price: Double,
    val imageURL: String,
    val description: String? = null,
    var quantity: Int = 0
)
