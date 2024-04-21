package com.example.getirmultideneme.shoppingcart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.RecyclerRowBasketBinding


class ShoppingCartAdapter(
    var products: List<ProductEntity>,
    private val viewModel: ShoppingCartViewModel,
    val context:Context
) : RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerRowBasketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(private val binding: RecyclerRowBasketBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductEntity) {
            with(binding) {
                textProductName.text = product.name
                if(textProductAttribute.text.toString().isNullOrEmpty()){
                    textProductAttribute.visibility = View.GONE
                }else {
                    textProductAttribute.text = product.attribute
                }

                textProductPrice.text = String.format("${context.getString(R.string.turkish_lira)}"+"%.2f", product.price)
                textQuantity.text = product.quantity.toString()
                Glide.with(imageProduct.context).load(product.imageURL).into(imageProduct)

                updateDecreaseButton(product.quantity)

                buttonIncrease.setOnClickListener {
                    viewModel.increaseQuantity(product)
                }

                buttonDecrease.setOnClickListener {
                    if (product.quantity > 1) {
                        viewModel.decreaseQuantity(product)
                    } else if (product.quantity == 1) {
                        viewModel.deleteProduct(product)
                    }
                }
            }
        }

        private fun updateDecreaseButton(quantity: Int) {
            binding.buttonDecrease.setImageResource(if (quantity > 1) R.drawable.subtract else R.drawable.delete)
        }
    }
}
