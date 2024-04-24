package com.example.getirmultideneme.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.SharedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    val sharedViewModel: SharedViewModel
) : ViewModel() {

    var product: ProductEntity? = null
        private set

    fun setProduct(productEntity: ProductEntity) {
        this.product = productEntity
    }

    fun addProductToCart() {
        product?.let {
            sharedViewModel.addProductToCart(it)
        }
    }

    fun updateQuantity(increase: Boolean) {
        product = product?.let {
            val updatedProduct = it.copy(quantity = if (increase) it.quantity + 1 else Math.max(0, it.quantity - 1))
            sharedViewModel.updateQuantity(updatedProduct, increase)
            updatedProduct
        }
    }

    fun deleteProductFromCart() {
        product?.let {
            sharedViewModel.deleteProductFromCart(it)
        }
    }



}
