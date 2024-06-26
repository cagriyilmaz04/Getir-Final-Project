package com.example.getirmultideneme

import androidx.lifecycle.viewModelScope
import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import kotlinx.coroutines.flow.Flow
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

    init {
        getAllProducts()
    }


    fun addProductToCart(product: ProductEntity) {
        viewModelScope.launch {
            val existingProduct = localRepository.getProductById(product.productId).firstOrNull()
            if (existingProduct != null) {
                existingProduct.quantity += product.quantity
                localRepository.updateProduct(existingProduct)
            } else {
                localRepository.addProductToCart(product)
            }
            getAllProducts()
        }
    }

    fun updateQuantity(product: ProductEntity, increase: Boolean) {
        viewModelScope.launch {
            val existingProduct = localRepository.getProductById(product.productId).firstOrNull()
            if (existingProduct != null) {
                existingProduct.quantity = if (increase) existingProduct.quantity + 1 else existingProduct.quantity - 1
                if (existingProduct.quantity > 0) {
                    localRepository.updateProduct(existingProduct)
                } else {
                    localRepository.deleteProduct(existingProduct.productId)
                }
                getAllProducts()
            }
        }
    }

    fun deleteProductFromCart(product: ProductEntity) {
        viewModelScope.launch {
            localRepository.deleteProduct(product.productId)
            getAllProducts()
        }
    }

    fun getAllProducts() {
        viewModelScope.launch {
            _products.value = Resource.Loading()
            try {
                localRepository.getAllProducts().collect { products ->
                    _products.value = Resource.Success(products)
                }
            } catch (e: Exception) {
                _products.value = Resource.Error("Failed to load products: ${e.localizedMessage}")
            }
        }
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            localRepository.deleteAllProducts()
            getAllProducts()  // Tüm ürünleri yeniden yükle
        }
    }
    fun getProductById(productId: String): Flow<ProductEntity?> {
        return localRepository.getProductById(productId)
    }

}
