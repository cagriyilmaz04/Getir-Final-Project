package com.example.getirmultideneme.shoppingcart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.RecyclerRowBasketBinding


class ShoppingCartAdapter(
    var products: List<ProductEntity>,
    private val viewModel: ShoppingCartViewModel
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
                textProductAttribute.text = product.attribute ?: "N/A"
                textProductPrice.text = String.format("$%.2f", product.price)
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
