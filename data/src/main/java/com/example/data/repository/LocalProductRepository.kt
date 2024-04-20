package com.example.data.repository

import android.util.Log
import com.example.data.dao.ProductDao
import com.example.data.models.ProductEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalProductRepository @Inject constructor(private val productDao: ProductDao) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    suspend fun addProductToCart(product: ProductEntity) {
        productDao.insert(product)
    }
    suspend fun insertProduct(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun deleteAllProducts() {
        productDao.deleteAllProducts()
    }
    suspend fun increaseProductQuantity(productId: String, amount: Int) {
        productDao.increaseProductQuantity(productId, amount)
    }

    suspend fun decreaseProductQuantity(productId: String, amount: Int) {
        productDao.decreaseProductQuantity(productId, amount)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun deleteProduct(productId: String) {
        productDao.delete(productId)
        Log.d("LocalProductRepository", "Product deleted: $productId")
    }

    fun getProductById(productId: String): Flow<ProductEntity> = productDao.getProduct(productId)
}
