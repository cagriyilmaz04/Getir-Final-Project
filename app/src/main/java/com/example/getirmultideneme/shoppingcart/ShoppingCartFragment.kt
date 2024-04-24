package com.example.getirmultideneme.shoppingcart

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import android.annotation.SuppressLint
import androidx.activity.OnBackPressedCallback
import com.example.getirmultideneme.util.Extension.hasVisitedShoppingCart
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
@AndroidEntryPoint
class ShoppingCartFragment : BaseFragment<FragmentShoppingCartBinding>
(FragmentShoppingCartBinding::inflate) {
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
                findNavController().navigate(R.id.action_shoppingCartFragment_to_productListingFragment)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                hasVisitedShoppingCart = false
                findNavController().popBackStack()
            }
        })
    }

    private fun setupRecyclerViews() {
        shoppingCartAdapter = ShoppingCartAdapter(ArrayList(), viewModel, requireContext())

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
                    }
                    is Resource.Success -> {
                        shoppingCartAdapter.products = resource.data as ArrayList<ProductEntity>
                        shoppingCartAdapter.notifyDataSetChanged() // Ensure this is called.
                        if (resource.data.isEmpty()) {
                            Toast.makeText(context, "The cart is now empty.", Toast.LENGTH_SHORT).show()
                        }
                        val turkishLira = requireContext().getString(R.string.turkish_lira)
                        val price = calculatePrice(shoppingCartAdapter.products)
                        binding.textViewPrice.text = String.format(turkishLira + "%.2f", price)
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}