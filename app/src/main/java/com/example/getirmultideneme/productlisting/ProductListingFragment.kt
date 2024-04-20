package com.example.getirmultideneme.productlisting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.data.models.BeveragePack
import com.example.getirmultideneme.ProductsViewModel
import com.example.getirmultideneme.databinding.FragmentProductListingBinding
import com.example.presentation.base.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import presentation.base.BaseFragment

@AndroidEntryPoint
class ProductListingFragment : BaseFragment<FragmentProductListingBinding>(
    FragmentProductListingBinding::inflate
) {


    private val viewModel: ProductsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeProducts()
        }

    private fun setupRecyclerView() {
        binding.verticalRecyclerView.apply {
            val data = ArrayList<BeveragePack>()
            layoutManager = GridLayoutManager(context, 3)
            adapter = ProductAdapter(ArrayList(), requireContext(), findNavController())// Initially empty list
        }
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.verticalRecyclerView.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        if (resource.data.isNullOrEmpty()) {
                            Toast.makeText(context, "No products available.", Toast.LENGTH_LONG).show()
                            binding.verticalRecyclerView.visibility = View.GONE
                        } else {
                            (binding.verticalRecyclerView.adapter as? ProductAdapter)?.updateData(resource.data)
                            binding.verticalRecyclerView.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, "Error: ${resource.message}", Toast.LENGTH_LONG).show()
                        binding.verticalRecyclerView.visibility = View.GONE // Hata durumunda görünürlüğü kapat
                    }

                    null -> {

                    }
                }
            }
        }
    }


}
