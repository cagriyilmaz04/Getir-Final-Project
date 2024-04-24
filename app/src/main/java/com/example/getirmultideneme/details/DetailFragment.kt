package com.example.getirmultideneme.details

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.FragmentDetailBinding
import com.example.getirmultideneme.util.Extension.convertToProduct
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

    private var id = ""
    private var productEnt:ProductEntity?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        observeBasketUpdates()
        setupInitialViews()

        binding.imageCancel.setOnClickListener {
            hasVisitedShoppingCart = false
            findNavController().navigate(R.id.action_detailFragment_to_productListingFragment)
        }
        binding.basketCustom.setOnBasketCs {
            hasVisitedShoppingCart = true
            if(binding.buttonAddToCart.visibility==View.VISIBLE){
                val action = DetailFragmentDirections.actionDetailFragmentToShoppingCartFragment("null",
                    convertToProduct(productEnt!!))
                            findNavController().navigate(action)
            }else{
                    val action = DetailFragmentDirections.actionDetailFragmentToShoppingCartFragment(id,
                        convertToProduct(productEnt!!))
                    findNavController().navigate(action)

            }
        }
        binding.buttonAddToCart.setOnClickListener {
            addFirstItem()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(hasVisitedShoppingCart){
                    val action = DetailFragmentDirections.actionDetailFragmentToShoppingCartFragment(id,
                        convertToProduct(productEnt!!))
                    findNavController().navigate(action)
                }else{
                    findNavController().navigate(R.id.action_detailFragment_to_productListingFragment)
                }

            }
        })
    }
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
    private fun setupInitialViews() {
        arguments?.let {
            val args = DetailFragmentArgs.fromBundle(it)
            val productEntity = args.product?.let { convertToProductEntity(it) }
            val quantity = args.quantity
            if (productEntity != null) {
                id = productEntity.productId
                productEnt = productEntity
                viewModel.setProduct(productEntity.copy(quantity = quantity))
                binding.apply {
                    if (quantity >= 1) {
                        buttonAddToCart.visibility = View.INVISIBLE
                        layoutQuantitySelector.visibility = View.VISIBLE
                    } else {
                        buttonAddToCart.visibility = View.VISIBLE
                        layoutQuantitySelector.visibility = View.INVISIBLE

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
                    val string = StringBuilder()
                    if(productEntity.attribute.toString().equals("null")){
                        if(!productEntity.description.toString().equals("null")){
                            string.append(productEntity.description.toString())
                        }else{
                            textProductAttribute.visibility = View.INVISIBLE
                        }
                    }else{
                        string.append(productEntity.attribute.toString())
                    }
                    textProductAttribute.text = string
                }
            } else {
                findNavController().popBackStack()
            }
        }
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
    private fun updateQuantity(increase: Boolean) {
        viewModel.updateQuantity(increase)
        val currentQuantity = viewModel.product?.quantity ?: 0
        binding.textQuantity.text = currentQuantity.toString()
        updateUI(viewModel.product!!, currentQuantity)
    }
    private fun addFirstItem() {
            val productArgs = DetailFragmentArgs.fromBundle(requireArguments()).product
        val productEntity = productArgs?.let { convertToProductEntity(it) }
        val quantity = productEntity!!.quantity
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
                updateUI(newProductEntity, 1)
                with(binding){
                    textQuantity.text = quantity.toString()
                    Glide.with(requireContext()).load(productEntity.imageURL ?: productEntity.thumbnailURL).into(imageProduct)
                    textProductPrice.text = String.format("₺%.2f", productEntity.price)
                    textProductName.text = productEntity.name
                    val string = StringBuilder()
                    if(productEntity.attribute.toString().equals("null")){
                        if(!productEntity.description.toString().equals("null")){
                            string.append(productEntity.description.toString())
                        }else{
                            textProductAttribute.visibility = View.INVISIBLE
                        }
                    }else{
                        string.append(productEntity.attribute.toString())
                    }
                    textProductAttribute.text = string
                }

            }
        }

}