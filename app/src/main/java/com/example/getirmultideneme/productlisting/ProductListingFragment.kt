package com.example.getirmultideneme.productlisting

import ProductAdapter
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
import com.example.getirmultideneme.databinding.FragmentProductListingBinding
import com.example.getirmultideneme.util.Extension.updateBasketPriceWithAnimation
import com.example.presentation.base.util.Resource
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

        binding.basketCustom.setOnBasketCs {
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
                        toggleShimmerEffect(true, binding.rvShimmer, binding.verticalRecyclerView)

                    }
                    is Resource.Success -> {
                        toggleShimmerEffect(false, binding.rvShimmer, binding.verticalRecyclerView)
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

    private fun observeBasketUpdates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sharedViewModel.products.collect { resource ->
                if (isAdded && isVisible && view != null) {
                    when (resource) {
                        is Resource.Success -> {
                            val totalPrice = resource.data.sumOf { it.price * it.quantity }
                            val totalQuantity = resource.data.sumOf { it.quantity }
                            updateBasketPriceWithAnimation(totalPrice, binding.basketCustom)


                            binding.basketCustom.constraintBasket.isEnabled = totalQuantity > 0
                        }
                        else -> {}
                    }
                }
            }
        }
    }


    fun toggleShimmerEffect(show: Boolean, rvShimmer: ShimmerFrameLayout, rv:RecyclerView) {
        if (show) {
            rvShimmer.visibility = View.VISIBLE
            rvShimmer.startLayoutAnimation()
            rv.visibility = View.GONE
        } else {
            rvShimmer.stopShimmer()
            rvShimmer.visibility = View.GONE
            rv.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        observeProducts()
    }
}
