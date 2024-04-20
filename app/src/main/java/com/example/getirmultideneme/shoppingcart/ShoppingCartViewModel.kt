package com.example.getirmultideneme.shoppingcart

import androidx.lifecycle.viewModelScope
import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingCartViewModel@Inject constructor(
    private val repository: LocalProductRepository
) : BaseViewModel<List<ProductEntity>>() {

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            postState(Resource.Loading())
            try {
                repository.getAllProducts().collect { products ->
                    if (products.isNotEmpty()) {
                        postState(Resource.Success(products))
                    } else {
                        postState(Resource.Error("No products found in the cart"))
                    }
                }
            } catch (e: Exception) {
                postState(Resource.Error("Failed to fetch products: ${e.localizedMessage}"))
            }
        }
    }

    fun increaseQuantity(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.increaseProductQuantity(product.productId, 1)
                loadProducts()
            } catch (e: Exception) {
                postState(Resource.Error("Failed to increase quantity: ${e.localizedMessage}"))
            }
        }
    }

    fun decreaseQuantity(product: ProductEntity) {
        viewModelScope.launch {
            if (product.quantity > 1) {
                try {
                    repository.decreaseProductQuantity(product.productId, 1)
                    loadProducts()
                } catch (e: Exception) {
                    postState(Resource.Error("Failed to decrease quantity: ${e.localizedMessage}"))
                }
            } else {
                deleteProduct(product)
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(product.productId)
                loadProducts()
                postState(Resource.Success<List<ProductEntity>>(listOf()))
            } catch (e: Exception) {
                postState(Resource.Error("Failed to delete product: ${e.localizedMessage}"))
            }
        }
    }


    fun deleteAllProducts() {
        viewModelScope.launch {
            try {
                repository.deleteAllProducts()
                postState(Resource.Success(emptyList()))
            } catch (e: Exception) {
                postState(Resource.Error("Failed to delete all products: ${e.localizedMessage}"))
            }
        }
    }
}
