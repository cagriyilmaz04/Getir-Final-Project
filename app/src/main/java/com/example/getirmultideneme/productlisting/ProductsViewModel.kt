package com.example.getirmultideneme.productlisting

import androidx.lifecycle.viewModelScope
import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.ProductEntity
import com.example.data.repository.ProductRepository
import com.example.getirmultideneme.SharedViewModel
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    val sharedViewModel: SharedViewModel  // SharedViewModel injected for shared functionality
) : BaseViewModel<List<BeveragePack>>() {

    private val _suggestedProducts = MutableStateFlow<Resource<List<BeverageSuggestedPack>>>(Resource.Loading())
    val suggestedProducts: StateFlow<Resource<List<BeverageSuggestedPack>>> = _suggestedProducts.asStateFlow()

    init {
        loadProducts()
        loadSuggestedProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getProducts()
                .catch { e ->
                    postState(Resource.Error("Failed to load products: ${e.localizedMessage}"))
                }
                .collect { products ->
                    postState(Resource.Success(products))
                }
        }
    }
    fun getProductQuantity(productId: String): Int {
        return sharedViewModel.products.value.let { resource ->
            when (resource) {
                is Resource.Success -> resource.data.find { it.productId == productId }?.quantity ?: 0
                else -> 0
            }
        }
    }

    private fun loadSuggestedProducts() {
        viewModelScope.launch {
            productRepository.getSuggestedProducts()
                .catch { e ->
                    _suggestedProducts.value = Resource.Error("Failed to load suggested products: ${e.localizedMessage}")
                }
                .collect { suggestedProducts ->
                    _suggestedProducts.value = Resource.Success(suggestedProducts)
                }
        }
    }

    fun isProductInCart(productId: String): Boolean {
        return sharedViewModel.products.value.let { resource ->
            when (resource) {
                is Resource.Success -> resource.data.any { it.productId == productId }
                else -> false
            }
        }
    }

    fun addToCart(product: ProductEntity) {
        sharedViewModel.addProductToCart(product) // Delegate to SharedViewModel
    }

    fun updateQuantity(product: ProductEntity, increase: Boolean) {
        sharedViewModel.updateQuantity(product, increase) // Delegate to SharedViewModel
    }

    fun deleteProductFromCart(product: ProductEntity) {
        sharedViewModel.deleteProductFromCart(product) // Delegate to SharedViewModel
    }
}
