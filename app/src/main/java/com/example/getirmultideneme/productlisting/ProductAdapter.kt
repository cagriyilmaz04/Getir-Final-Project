package com.example.getirmultideneme.productlisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.data.models.BeveragePack
import com.example.data.models.Product
import com.example.getirmultideneme.databinding.RecylerRowBinding


class ProductAdapter(private var products: List<BeveragePack>, val context: Context, private val navController: NavController) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateData(newProducts: List<BeveragePack>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecylerRowBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productsList = products.firstOrNull()?.products ?: emptyList()
        val product = productsList.getOrNull(position)
        if (product != null) {
            holder.bind(product)
        } else {
            holder.itemView.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int {
        return if (products.isNotEmpty()) products[0].products.size else 0
    }


    inner class ProductViewHolder(private val binding: RecylerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                textViewProductName.text = product.name
                textViewProductAttribute.text = product.attribute
                textViewProductPrice.text = product.priceText
                Glide.with(itemView.context).load(product.imageURL).transform(CenterCrop(), RoundedCorners(8)).into(imageViewProduct)

                imageViewAddToCart.setOnClickListener {

                }
                imageViewContainer.setOnClickListener {
                    val action = ProductListingFragmentDirections.actionProductListingFragmentToDetailFragment(product)
                    navController.navigate(action)
                }

            }
        }
    }
}
