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
import android.util.Log
import androidx.activity.OnBackPressedCallback
import com.example.getirmultideneme.customview.CustomAlertDialog
import com.example.getirmultideneme.util.Extension.convertToProduct
import com.example.getirmultideneme.util.Extension.hasVisitedShoppingCart
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE")
@AndroidEntryPoint
class ShoppingCartFragment : BaseFragment<FragmentShoppingCartBinding>
(FragmentShoppingCartBinding::inflate) {
    private val viewModel: ShoppingCartViewModel by viewModels()
    private lateinit var shoppingCartAdapter: ShoppingCartAdapter
    private var customAlertDialog: CustomAlertDialog? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()

        binding.imageBack.setOnClickListener {
                getSynchronization()
        }
        customAlertDialog = CustomAlertDialog(requireContext())
        binding.imageDelete.setOnClickListener {
            lifecycleScope.launch {
                viewModel.products.collect{ resource ->
                    if(resource is Resource.Success){
                        val data = resource.data as ArrayList<ProductEntity>
                        if(data.isNotEmpty()){

                            customAlertDialog?.let {
                                it.show()
                                it.setOnYesClick {
                                    viewModel.deleteAllProducts()
                                    customAlertDialog!!.dismiss()
                                    customAlertDialog = null
                                    findNavController().navigate(R.id.action_shoppingCartFragment_to_productListingFragment)
                                }

                                it.setOnNoClick {
                                    customAlertDialog!!.dismiss()
                                    customAlertDialog = null
                                }
                            }
                        }
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getSynchronization()
            }
        })
    }

    private fun setupRecyclerViews() {
        val navigation = findNavController()
        shoppingCartAdapter = ShoppingCartAdapter(ArrayList(), viewModel, requireContext(),navigation)
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
                        shoppingCartAdapter.notifyDataSetChanged()
                        if (resource.data.isEmpty()) {
                            //Toast.makeText(context, "The cart is now empty.", Toast.LENGTH_SHORT).show()
                        }
                        val turkishLira = requireContext().getString(R.string.turkish_lira)
                        val price = calculatePrice(shoppingCartAdapter.products)
                        binding.textViewPrice.text = String.format("$turkishLira%.2f", price)
                    }
                    is Resource.Error -> {
                        Toast.makeText(context, resource.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun getSynchronization() {
        if(hasVisitedShoppingCart == true){
            viewLifecycleOwner.lifecycleScope.launch {
                val bundle = ShoppingCartFragmentArgs.fromBundle(requireArguments())
                val id = bundle.id
                if(!id.toString().equals("null")){
                    viewModel.getProductById(id).collect{
                        if(it!=null){
                            val product = convertToProduct(it)
                            val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToDetailFragment(product,
                                it.quantity)
                            findNavController().navigate(action)
                        }else {
                            findNavController().navigate(R.id.action_shoppingCartFragment_to_productListingFragment)
                        }

                    }
                }else{
                    findNavController().navigate(R.id.action_shoppingCartFragment_to_productListingFragment)
                }

            }
        } else{
            findNavController().navigate(R.id.action_shoppingCartFragment_to_productListingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customAlertDialog?.dismiss()
        customAlertDialog = null
    }

}