package com.example.getirmultideneme

import androidx.lifecycle.viewModelScope
import com.example.data.models.BeveragePack
import com.example.data.repository.ProductRepository
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : BaseViewModel<List<BeveragePack>>() {

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getProducts().catch { e ->
                postState(Resource.Error("Failed to load products: ${e.localizedMessage}", e))
            }.collect { products ->
                postState(Resource.Success(products))
            }
        }
    }
}
