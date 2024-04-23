package com.example.getirmultideneme.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.customview.BasketCustomView
import com.example.getirmultideneme.databinding.FragmentDetailBinding
import com.example.getirmultideneme.util.Extension.fadeInView
import com.example.getirmultideneme.util.Extension.fadeOutView
import com.example.getirmultideneme.util.Extension.hasVisitedShoppingCart
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
    private var isAnimating = false
    private fun updateBasketPriceWithAnimation(newPrice: Double,basketCustomView: BasketCustomView) {
        if (isAnimating) return

        val imageView = basketCustomView.getImage()
        val constraintLayout = basketCustomView.getConstraint()
        constraintLayout.post {
            val originalX = imageView.x
            val originalY = imageView.y
            isAnimating = true
            // Animasyonu başlat
            imageView.animate()
                .x(constraintLayout.x)
                .y(constraintLayout.y)
                .setDuration(500)
                .withStartAction {
                    // constraintLayout3'ün görünürlüğünü gizle
                    constraintLayout.visibility = View.INVISIBLE
                }
                .withEndAction {
                    // Yeni fiyatı güncelle ve animasyonu geri döndür
                    basketCustomView.setPrice(newPrice)
                    imageView.animate()
                        .x(originalX)
                        .y(originalY)
                        .setDuration(500)
                        .withEndAction {
                            // constraintLayout3'ü tekrar görünür yap
                            isAnimating = false
                            constraintLayout.visibility = View.VISIBLE
                        }
                        .start()
                }
                .start()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBasketUpdates()
        setupInitialViews()
        if(hasVisitedShoppingCart){
            hasVisitedShoppingCart = false
            findNavController().navigate(R.id.action_detailFragment_to_productListingFragment)
        }
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
            val product = DetailFragmentArgs.fromBundle(it).product
            binding.apply {
                Glide.with(requireContext()).load(product.imageURL ?: product.thumbnailURL).into(imageProduct)
                textProductPrice.text = String.format("₺%.2f", product.price)
                textProductName.text = product.name
                textProductAttribute.text = product.attribute ?: ""
                textProductAttribute.visibility = if (product.attribute.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            }
        }

        binding.buttonAddToCart.setOnClickListener {
            val productArgs = DetailFragmentArgs.fromBundle(requireArguments()).product
            val newProductEntity = ProductEntity(
                name = productArgs.name,
                productId = productArgs.id,
                attribute = productArgs.attribute,
                thumbnailURL = productArgs.thumbnailURL,
                price = productArgs.price,
                imageURL = productArgs.imageURL!!,
                description = productArgs.description,
                quantity = 1
            )
            viewModel.setProduct(newProductEntity)
            viewModel.addProductToCart()
            fadeOutView(binding.buttonAddToCart, requireContext())
            fadeInView(binding.layoutQuantitySelector, requireContext())
        }

        binding.buttonIncrease.setOnClickListener {
            viewModel.updateQuantity(true)
            updateUI()
        }

        binding.buttonDecrease.setOnClickListener {
            viewModel.updateQuantity(false)
            updateUI()
        }
    }

    private fun updateUI() {
        binding.textQuantity.text = viewModel.product?.quantity.toString()
        updateIcons(viewModel.product?.quantity ?: 0)
    }

    private fun updateIcons(quantity: Int) {
        binding.buttonDecrease.setImageResource(if (quantity > 1) R.drawable.subtract else R.drawable.delete)
        binding.buttonDecrease.setOnClickListener {
            if (quantity > 1) {
                viewModel.updateQuantity(false)
            } else {
                viewModel.deleteProductFromCart()
                fadeInView(binding.buttonAddToCart, requireContext())
                fadeOutView(binding.layoutQuantitySelector, requireContext())
            }
            updateUI()
        }
    }
}
