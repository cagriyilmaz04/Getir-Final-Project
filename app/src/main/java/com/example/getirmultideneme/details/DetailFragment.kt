package com.example.getirmultideneme.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.customview.BasketCustomView
import com.example.getirmultideneme.databinding.FragmentDetailBinding
import com.example.getirmultideneme.util.Extension.convertToProductEntity
import com.example.getirmultideneme.util.Extension.fadeInView
import com.example.getirmultideneme.util.Extension.fadeOutView
import com.example.getirmultideneme.util.Extension.hasVisitedShoppingCart
import com.example.getirmultideneme.util.Extension.updateBasketPriceWithAnimation
import com.example.presentation.base.util.Resource
import presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {
    private val viewModel: DetailViewModel by viewModels()
    private fun observeBasketUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sharedViewModel.products.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val totalPrice = resource.data.sumOf { it.price * it.quantity }
                        updateBasketPriceWithAnimation(totalPrice, binding.basketCustom)
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(hasVisitedShoppingCart){
            hasVisitedShoppingCart = false
            findNavController().navigate(R.id.action_detailFragment_to_productListingFragment)
        }

        observeBasketUpdates()
        setupInitialViews()

        binding.imageCancel.setOnClickListener {
            hasVisitedShoppingCart = false
            findNavController().popBackStack()
        }
        binding.basketCustom.setOnBasketClickListener {
            hasVisitedShoppingCart = true
            findNavController().navigate(R.id.action_detailFragment_to_shoppingCartFragment)
        }


    }

    private fun setupInitialViews() {
        arguments?.let {
            val args = DetailFragmentArgs.fromBundle(it)
            val productEntity = args.product?.let { convertToProductEntity(it) }
            val quantity = args.quantity

            if (productEntity != null) {
                viewModel.setProduct(productEntity.copy(quantity = quantity))
                binding.apply {
                    if (quantity >= 1) {
                        buttonAddToCart.visibility = View.INVISIBLE
                        layoutQuantitySelector.visibility = View.VISIBLE
                    } else {
                        buttonAddToCart.visibility = View.VISIBLE
                        layoutQuantitySelector.visibility = View.INVISIBLE
                        buttonAddToCart.setOnClickListener {
                            val productArgs = DetailFragmentArgs.fromBundle(requireArguments()).product
                            if (productArgs != null) {
                                val newProductEntity = ProductEntity(
                                    name = productArgs.name,
                                    productId = productArgs.id,
                                    attribute = productArgs.attribute,
                                    thumbnailURL = productArgs.thumbnailURL,
                                    price = productArgs.price,
                                    imageURL = productArgs.imageURL!!,
                                    description = productArgs.description,
                                    quantity = 1  // Burada miktarı doğrudan 1 olarak ayarlıyoruz
                                )
                                viewModel.setProduct(newProductEntity)
                                viewModel.addProductToCart()
                                fadeInView(binding.layoutQuantitySelector, requireContext())
                                fadeOutView(binding.buttonAddToCart, requireContext())
                                updateUI(newProductEntity, 1)  // UI'yi güncelleyin, miktar şimdi 1 olmalı
                            }
                        }
                    }
                    buttonIncrease.setOnClickListener {
                        updateQuantity(true)
                    }
                    buttonDecrease.setOnClickListener {
                        updateQuantity(false)
                    }

                    textQuantity.text = quantity.toString()
                    Glide.with(requireContext()).load(productEntity.imageURL ?: productEntity.thumbnailURL).into(imageProduct)
                    textProductPrice.text = String.format("₺%.2f", productEntity.price)
                    textProductName.text = productEntity.name
                    textProductAttribute.text = productEntity.attribute ?: ""
                    textProductAttribute.visibility = if (productEntity.attribute.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                }
            } else {
                Toast.makeText(context, "Ürün bilgisi yüklenemedi.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }


    private fun updateQuantity(increase: Boolean) {
        viewModel.updateQuantity(increase)
        val currentQuantity = viewModel.product?.quantity ?: 0
        binding.textQuantity.text = currentQuantity.toString()
        updateUI(viewModel.product!!, currentQuantity)
    }
    private fun updateUI(product: ProductEntity, quantity: Int) {
        binding.apply {
            buttonAddToCart.visibility = if (quantity > 0) View.INVISIBLE else View.VISIBLE
            layoutQuantitySelector.visibility = if (quantity > 0) View.VISIBLE else View.INVISIBLE
            textQuantity.text = quantity.toString()

            buttonDecrease.setImageResource(
                if (quantity > 1) R.drawable.subtract
                else R.drawable.trash_small
            )
        }
    }

    override fun onResume() {
        super.onResume()

    }

}