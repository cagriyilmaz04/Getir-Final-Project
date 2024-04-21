package com.example.getirmultideneme.shoppingcart

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.FragmentShoppingCartBinding
import com.example.getirmultideneme.util.Extension.calculatePrice
import com.example.presentation.base.util.Resource
import presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
@AndroidEntryPoint
class ShoppingCartFragment : BaseFragment<FragmentShoppingCartBinding>(FragmentShoppingCartBinding::inflate) {
    private val viewModel: ShoppingCartViewModel by viewModels()
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageDelete.setOnClickListener {
            viewModel.deleteAllProducts()
        }
    }

    private fun setupRecyclerViews() {
        shoppingCartAdapter = ShoppingCartAdapter(listOf(), viewModel, requireContext())


        binding.recyclerViewLocal.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = shoppingCartAdapter
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading indicator if needed
                    }
                    is Resource.Success -> {
                        shoppingCartAdapter.products = resource.data
                        val price = calculatePrice(resource.data)
                        binding.textViewPrice.text = getString(R.string.turkish_lira) + String.format("%.2f", price)
                        shoppingCartAdapter.notifyDataSetChanged()
                    }
                    is Resource.Error -> {
                        // Show error message
                    }
                }
            }
        }

    }


}
