package com.example.getirmultideneme

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


class SharedViewModel @Inject constructor(
    private val localRepository: LocalProductRepository
) : BaseViewModel<List<ProductEntity>>() {
    private val _products = MutableStateFlow<Resource<List<ProductEntity>>>(Resource.Loading())
    val products: StateFlow<Resource<List<ProductEntity>>> = _products.asStateFlow()
    private val _totalPrice = MutableStateFlow<Double>(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()
    fun addProductToCart(product: ProductEntity) {
        viewModelScope.launch {
            try {
                // Ürünün zaten var olup olmadığını kontrol edin
                val existingProduct = localRepository.getProductById(product.productId).firstOrNull()
                if (existingProduct != null) {
                    // Ürün zaten varsa, mevcut miktarı artırın
                    val updatedProduct = existingProduct.copy(quantity = existingProduct.quantity + product.quantity)
                    localRepository.updateProduct(updatedProduct)  // Güncellenmiş ürünü veritabanında güncelleyin
                    getAllProducts()  // Güncel ürün listesini yeniden yükleyin
                } else {
                    // Ürün yoksa, yeni olarak ekleyin
                    localRepository.addProductToCart(product)
                    getAllProducts()  // Güncel ürün listesini yeniden yükleyin
                }
            } catch (e: Exception) {
                postState(Resource.Error("Failed to add product to cart: ${e.localizedMessage}"))
            }
        }
    }


    fun updateQuantity(product: ProductEntity, increase: Boolean) {
        viewModelScope.launch {
            try {
                val existingProduct = localRepository.getProductById(product.productId).firstOrNull()
                if (existingProduct != null) {
                    val newQuantity = if (increase) existingProduct.quantity + 1 else existingProduct.quantity - 1
                    if (newQuantity > 0) {
                        localRepository.updateProduct(existingProduct.copy(quantity = newQuantity))
                    } else {
                        localRepository.deleteProduct(existingProduct.productId)
                    }
                    getAllProducts()  // Refresh the products list after update
                }
            } catch (e: Exception) {
                postState(Resource.Error("Failed to update product quantity: ${e.localizedMessage}"))
            }
        }
    }

    fun deleteProductFromCart(product: ProductEntity) {
        viewModelScope.launch {
            try {
                localRepository.deleteProduct(product.productId)
                getAllProducts()  // Refresh the products list after deletion
            } catch (e: Exception) {
                postState(Resource.Error("Failed to delete product from cart: ${e.localizedMessage}"))
            }
        }
    }


    fun deleteAllProducts() {
        viewModelScope.launch {
            localRepository.deleteAllProducts()
        }
    }
    fun getAllProducts() {
        viewModelScope.launch {
            try {
                localRepository.getAllProducts().collect { productsList ->
                    _products.value = if (productsList.isNotEmpty()) {
                        Resource.Success(productsList)
                    } else {
                        Resource.Success(emptyList<ProductEntity>())
                    }
                    updateTotalPrice(productsList)
                }
            } catch (e: Exception) {
                _products.value = Resource.Error("Failed to fetch products: ${e.localizedMessage}")
            }
        }
    }
    private fun updateTotalPrice(products: List<ProductEntity>) {
        val total = products.sumOf { it.price * it.quantity }
        _totalPrice.value = total
    }
}
