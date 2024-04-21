package com.example.data.repository

import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.Product
import com.example.data.models.SuggestedProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(private val apiService: ApiService) : ApiRepository {
    override fun getProducts(): Flow<List<BeveragePack>> = apiService.getProducts()

    override fun getSuggestedProducts(): Flow<List<BeverageSuggestedPack>> = apiService.getSuggestedProducts()
}
