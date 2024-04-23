package com.example.getirmultideneme.productlisting

import ProductAdapter
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getirmultideneme.R
import com.example.getirmultideneme.customview.BasketCustomView
import com.example.getirmultideneme.databinding.FragmentProductListingBinding
import com.example.presentation.base.util.Resource
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import presentation.base.BaseFragment

@AndroidEntryPoint
class ProductListingFragment : BaseFragment<FragmentProductListingBinding>(FragmentProductListingBinding::inflate) {
    private val viewModel: ProductsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBasketUpdates()
        setupRecyclerView()
        observeProducts()

        binding.basketCustom.setOnBasketClickListener {
            findNavController().navigate(R.id.action_productListingFragment_to_shoppingCartFragment)
        }
    }

    private fun setupRecyclerView() {
        val navigation = findNavController()
        binding.verticalRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = ProductAdapter(emptyList(), requireContext(), viewModel, navigation)
        }

        binding.rvSuggestedProducts.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = SuggestedProductAdapter(emptyList(), requireContext(), viewModel, navigation)
        }
    }

    private fun observeProducts() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        toggleShimmerEffect(true,binding.rvShimmer,binding.verticalRecyclerView)

                    }
                    is Resource.Success -> {
                        toggleShimmerEffect(false,binding.rvShimmer,binding.verticalRecyclerView)
                        (binding.verticalRecyclerView.adapter as? ProductAdapter)?.updateData(resource.data)
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, "Error: ${resource.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.suggestedProducts.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        toggleShimmerEffect(true,binding.rvShimmerHorizontal,binding.rvSuggestedProducts)
                         }
                    is Resource.Success -> {
                       toggleShimmerEffect(false,binding.rvShimmerHorizontal,binding.rvSuggestedProducts)
                       (binding.rvSuggestedProducts.adapter as? SuggestedProductAdapter)?.updateData(resource.data)
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, "Error loading suggested products: ${resource.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private var isAnimating = false
    private fun observeBasketUpdates() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sharedViewModel.products.collect { resource ->
                if (isAdded && isVisible && view != null) {
                    when (resource) {
                        is Resource.Success -> {
                            val totalPrice = resource.data.sumOf { it.price * it.quantity }
                            updateBasketPriceWithAnimation(totalPrice,binding.basketCustom)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun toggleShimmerEffect(show: Boolean,rvShimmer:ShimmerFrameLayout,rv:RecyclerView) {
        if (show) {
            // Show shimmer effect
            rvShimmer.visibility = View.VISIBLE
            rvShimmer.startLayoutAnimation()
            rv.visibility = View.GONE
        } else {
            // Show actual content
            rvShimmer.stopShimmer()
            rvShimmer.visibility = View.GONE
            rv.visibility = View.VISIBLE
        }

    }


    private fun updateBasketPriceWithAnimation(newPrice: Double,basketCustomView: BasketCustomView) {
        if(isAnimating) return

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



    override fun onResume() {
        super.onResume()
        observeProducts()
    }
}
