package com.example.getirmultideneme.shoppingcart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.SuggestedProduct
import com.example.getirmultideneme.databinding.RecylerRowBinding


class SuggestedProductAdapter(
    var products: List<BeverageSuggestedPack>
) : RecyclerView.Adapter<SuggestedProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = RecylerRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
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

    fun updateData(newProducts: List<BeverageSuggestedPack>) {
        products = newProducts
        notifyDataSetChanged()
    }

    class ProductViewHolder(private val binding: RecylerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: SuggestedProduct) {
            with(binding) {
                textViewProductName.text = product.name
                textViewProductPrice.text = String.format("$%.2f", product.price)
                if (product.attribute.isNullOrEmpty()) {
                    textViewProductAttribute.visibility = ViewGroup.GONE
                } else {
                    textViewProductAttribute.text = product.attribute
                }
                var strImage = ""
                if(product.imageURL == null){
                    strImage = product.squareThumbnailURL.toString()
                }else{
                    strImage = product.imageURL.toString()
                }
                Glide.with(imageViewProduct.context)
                    .load(strImage)
                    .into(imageViewProduct)
            }
        }
    }
}
