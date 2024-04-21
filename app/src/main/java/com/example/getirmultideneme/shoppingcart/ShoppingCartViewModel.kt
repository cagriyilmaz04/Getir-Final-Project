package com.example.getirmultideneme.shoppingcart

import androidx.lifecycle.viewModelScope
import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.ProductEntity
import com.example.data.models.SuggestedProduct
import com.example.data.repository.ApiRepository
import com.example.data.repository.LocalProductRepository
import com.example.data.repository.ProductRepository
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingCartViewModel @Inject constructor(
    private val localRepository: LocalProductRepository,
    private val apiRepository: ProductRepository
) : BaseViewModel<List<ProductEntity>>() {

    // Mevcut product listesi için StateFlow
    private val _products = MutableStateFlow<Resource<List<ProductEntity>>>(Resource.Loading())
    val products: StateFlow<Resource<List<ProductEntity>>> = _products.asStateFlow()



    init {
        loadProducts()

    }

    private fun loadProducts() {
        viewModelScope.launch {
            postState(Resource.Loading())
            try {
                localRepository.getAllProducts().collect { products ->
                    if (products.isNotEmpty()) {
                        _products.value = Resource.Success(products)
                    } else {
                        _products.value = Resource.Error("No products found in the cart")
                    }
                }
            } catch (e: Exception) {
                _products.value = Resource.Error("Failed to fetch products: ${e.localizedMessage}")
            }
        }
    }


    fun increaseQuantity(product: ProductEntity) {
        viewModelScope.launch {
            try {
                localRepository.increaseProductQuantity(product.productId, 1)
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
                    localRepository.decreaseProductQuantity(product.productId, 1)
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
                localRepository.deleteProduct(product.productId)
                loadProducts()  // Reload to update UI after deletion
                postState(Resource.Success(emptyList()))
            } catch (e: Exception) {
                postState(Resource.Error("Failed to delete product: ${e.localizedMessage}"))
            }
        }
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            try {
                localRepository.deleteAllProducts()
                postState(Resource.Success(emptyList()))
            } catch (e: Exception) {
                postState(Resource.Error("Failed to delete all products: ${e.localizedMessage}"))
            }
        }
    }
}
