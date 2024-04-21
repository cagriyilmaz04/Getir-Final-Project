package com.example.data.repository


import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.SuggestedProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productService: ApiService) {
    fun getProducts(): Flow<List<BeveragePack>> = productService.getProducts()

    fun getSuggestedProducts(): Flow<List<BeverageSuggestedPack>> = productService.getSuggestedProducts()
}
