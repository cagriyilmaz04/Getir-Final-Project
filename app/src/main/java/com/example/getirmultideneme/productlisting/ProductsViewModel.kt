package com.example.getirmultideneme.productlisting

import androidx.lifecycle.viewModelScope
import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.SuggestedProduct
import com.example.data.repository.ProductRepository
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
    private val productRepository: ProductRepository
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
}
