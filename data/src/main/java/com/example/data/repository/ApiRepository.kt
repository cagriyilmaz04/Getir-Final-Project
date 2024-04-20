package com.example.data.repository

import com.example.data.models.BeveragePack
import com.example.data.models.Product
import com.example.data.models.SuggestedProduct
import kotlinx.coroutines.flow.Flow

interface ApiRepository {
    fun getProducts(): Flow<List<BeveragePack>>
    fun getSuggestedProducts(): Flow<List<SuggestedProduct>>
}