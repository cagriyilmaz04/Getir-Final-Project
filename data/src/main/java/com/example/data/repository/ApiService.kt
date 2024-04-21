package com.example.data.repository

import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.Product
import com.example.data.models.SuggestedProduct
import retrofit2.http.GET

import kotlinx.coroutines.flow.Flow

interface ApiService {
    @GET("products")
    fun getProducts(): Flow<List<BeveragePack>>

    @GET("suggestedProducts")
    fun getSuggestedProducts(): Flow<List<BeverageSuggestedPack>>
}