package com.example.getirmultideneme.shoppingcart

import androidx.lifecycle.viewModelScope
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.SharedViewModel
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingCartViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel  // SharedViewModel injected for shared functionality
) : BaseViewModel<List<ProductEntity>>() {

    private val _products = MutableStateFlow<Resource<List<ProductEntity>>>(Resource.Loading())
    val products: StateFlow<Resource<List<ProductEntity>>> = _products.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            postState(Resource.Loading())
            try {
                sharedViewModel.getAllProducts()
                sharedViewModel.products.collect { resource ->
                    _products.value = resource
                }
            } catch (e: Exception) {
                _products.value = Resource.Error("Failed to fetch products: ${e.localizedMessage}")
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        sharedViewModel.deleteProductFromCart(product)
    }

    fun increaseQuantity(product: ProductEntity) {
        sharedViewModel.updateQuantity(product, true)
    }

    fun decreaseQuantity(product: ProductEntity) {
        sharedViewModel.updateQuantity(product, false) // Use SharedViewModel to decrease quantity
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            try {
                sharedViewModel.deleteAllProducts()
                postState(Resource.Success(emptyList()))
            } catch (e: Exception) {
                postState(Resource.Error("Failed to delete all products: ${e.localizedMessage}"))
            }
        }
    }

    fun getProductById(productId: String): Flow<ProductEntity?> {
        // Delegate to SharedViewModel to fetch the product by ID
        return sharedViewModel.getProductById(productId)
    }
}
