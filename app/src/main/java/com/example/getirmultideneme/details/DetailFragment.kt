package com.example.getirmultideneme.details

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.FragmentDetailBinding
import com.example.getirmultideneme.util.Extension.fadeInView
import com.example.getirmultideneme.util.Extension.fadeOutView
import presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {
    private val viewModel: DetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialViews()
        binding.imageCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageBasket.setOnClickListener {
            findNavController().navigate(R.id.action_detailFragment_to_shoppingCartFragment)
        }

    }

    private fun setupInitialViews() {

        arguments?.let {
            val product = DetailFragmentArgs.fromBundle(it).product
            binding.apply {
                var image = ""

                if(product.imageURL!=null){
                    image += product.imageURL

                }else{
                    image += product.thumbnailURL
                }
                Glide.with(requireContext()).load(image).into(imageProduct)
                textProductPrice.text = product.priceText.toString()
                textProductName.text = product.name.toString()
                if(product.attribute.isNullOrEmpty()){
                    textProductAttribute.visibility = View.INVISIBLE
                }else {
                    textProductAttribute.text = product.attribute
                }
            }
        }

        binding.buttonAddToCart.setOnClickListener {
            fadeInView(binding.cartPrice,requireContext())
            arguments?.let {
                val productArgs = DetailFragmentArgs.fromBundle(it).product
                viewModel.product = ProductEntity(
                    name = productArgs.name,
                    productId = productArgs.id,
                    attribute = productArgs.attribute,
                    thumbnailURL = productArgs.thumbnailURL,
                    price = productArgs.price,
                    imageURL = productArgs.imageURL!!,
                    description = productArgs.description,
                    quantity = binding.textQuantity.text.toString().toInt()
                )
                viewModel.addProductToCart()
                fadeOutView(binding.buttonAddToCart,requireContext())
                fadeInView(binding.layoutQuantitySelector,requireContext())
            }
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
        updateIcons(viewModel.product?.quantity ?: 1)
    }

    private fun updateIcons(quantity: Int) {
        if (quantity == 1) {
            binding.buttonDecrease.setImageResource(R.drawable.delete)
            binding.buttonDecrease.setOnClickListener {
                viewModel.deleteProductFromCart()
                fadeInView(binding.buttonAddToCart,requireContext())
                fadeOutView(binding.layoutQuantitySelector,requireContext())
                fadeOutView(binding.cartPrice,requireContext())

            }
        } else {
            binding.buttonDecrease.setImageResource(R.drawable.subtract)
            binding.buttonDecrease.setOnClickListener {
                viewModel.updateQuantity(false)
                updateUI()
            }
        }
    }


}
