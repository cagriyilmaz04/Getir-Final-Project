package com.example.getirmultideneme.shoppingcart

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getirmultideneme.databinding.FragmentShoppingCartBinding
import com.example.presentation.base.util.Resource
import presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCartFragment : BaseFragment<FragmentShoppingCartBinding>(FragmentShoppingCartBinding::inflate) {
    private val viewModel: ShoppingCartViewModel by viewModels()
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageDelete.setOnClickListener {
            viewModel.deleteAllProducts()
        }
    }

    private fun setupRecyclerView() {
        shoppingCartAdapter = ShoppingCartAdapter(listOf(), viewModel)
        binding.recyclerViewLocal.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = shoppingCartAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading indicator if needed
                    }

                    is Resource.Success -> {
                        shoppingCartAdapter.products = resource.data
                        shoppingCartAdapter.notifyDataSetChanged()

                    }

                    is Resource.Error -> {
                        // Show error message if needed
                    }

                    else -> {
                        // Optional: handle other cases if necessary
                    }
                }
            }
        }
    }
}