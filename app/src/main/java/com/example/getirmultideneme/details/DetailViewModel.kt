package com.example.getirmultideneme.details


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.data.models.ProductEntity
import com.example.data.repository.LocalProductRepository
import com.example.presentation.base.BaseViewModel
import com.example.presentation.base.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: LocalProductRepository
) : BaseViewModel<ProductEntity>() {  // Extend from BaseViewModel with ProductEntity as the generic type

    var product: ProductEntity? = null
        set(value) {
            field = value
            value?.let { postState(Resource.Success(it)) }
        }

    fun addProductToCart() {
        product?.let {
            viewModelScope.launch {
                try {
                    repository.addProductToCart(it)
                    postState(Resource.Success(it))
                } catch (e: Exception) {
                    postState(Resource.Error("Failed to add product to cart", e))
                }
            }
        }
    }

    fun updateQuantity(increase: Boolean) {
        product?.let { prod ->
            viewModelScope.launch {
                try {
                    if (increase) {
                        prod.quantity++
                        repository.increaseProductQuantity(prod.productId, 1)
                    } else {
                        if (prod.quantity > 1) {
                            prod.quantity--
                            repository.decreaseProductQuantity(prod.productId, 1)
                        } else if (prod.quantity == 1) {
                            deleteProductFromCart()
                        }
                    }
                    postState(Resource.Success(prod))
                } catch (e: Exception) {
                    postState(Resource.Error("Failed to update product quantity", e))
                }
            }
        }
    }

    fun deleteProductFromCart() {

        product?.let {
            viewModelScope.launch {
                try {
                    repository.deleteProduct(it.productId)
                    Log.e("TAG","çAĞRI YILMAZ")
                    product = null  // Reset product after deletion
                    postState(Resource.Success(it))
                } catch (e: Exception) {
                    postState(Resource.Error("Failed to delete product from cart", e))
                }
            }
        }
    }
}



