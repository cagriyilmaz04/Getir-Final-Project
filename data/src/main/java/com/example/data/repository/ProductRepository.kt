package com.example.data.repository


import com.example.data.models.BeveragePack
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor(private val productService: ApiService) {
    fun getProducts(): Flow<List<BeveragePack>> = productService.getProducts()
}
