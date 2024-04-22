package com.example.getirmultideneme.details


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.getirmultideneme.SharedViewModel
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel
) : BaseViewModel<ProductEntity>() {

    var product: ProductEntity? = null
        set(value) {
            field = value
            value?.let { postState(Resource.Success(it)) }
        }

    fun addProductToCart() {
        product?.let {
            sharedViewModel.addProductToCart(it)
        }
    }

    fun updateQuantity(increase: Boolean) {
        product?.let { prod ->
            sharedViewModel.updateQuantity(prod, increase)
        }
    }

    fun deleteProductFromCart() {
        product?.let {
            sharedViewModel.deleteProductFromCart(it)
        }
    }
}


