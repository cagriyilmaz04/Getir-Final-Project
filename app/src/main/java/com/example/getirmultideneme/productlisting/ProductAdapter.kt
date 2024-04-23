package com.example.getirmultideneme.productlisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.models.BeveragePack
import com.example.data.models.Product
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.RecylerRowBinding
import com.example.getirmultideneme.util.Extension.convertToProductEntity
import com.example.getirmultideneme.util.Extension.fadeInView
import com.example.getirmultideneme.util.Extension.fadeOutView

class ProductAdapter(
    private var products: List<BeveragePack>,
    private val context: Context,
    private val viewModel: ProductsViewModel,
    private val navController: NavController
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    fun updateData(newProducts: List<BeveragePack>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = RecylerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productsList = products.firstOrNull()?.products ?: emptyList()
        val product = productsList.getOrNull(position)
        product?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = products.firstOrNull()?.products?.size ?: 0

    inner class ProductViewHolder(private val binding: RecylerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                textViewProductName.text = product.name
                textViewProductAttribute.text = product.attribute
                textViewProductPrice.text = String.format("${context.getString(R.string.turkish_lira)}%.2f", product.price)
                Glide.with(itemView.context).load(product.imageURL).into(imageViewProduct)

                val isInCart = viewModel.isProductInCart(product.id)
                csImageViewAdd.visibility = if (isInCart) View.GONE else View.VISIBLE
                layoutQuantitySelector.visibility = if (isInCart) View.VISIBLE else View.GONE

                if (!isInCart) {
                    csImageViewAdd.setOnClickListener {
                        textQuantity.text = "1"
                        csImageViewAdd.visibility = View.GONE
                        layoutQuantitySelector.visibility = View.VISIBLE
                        viewModel.addToCart(convertToProductEntity(product))
                        val cx = constraintLayoutProductImage.width
                        val cy = 0
                        val finalRadius = Math.hypot(cx.toDouble(), constraintLayoutProductImage.height.toDouble()).toFloat()
                        val reveal = ViewAnimationUtils.createCircularReveal(constraintLayoutProductImage, cx, cy, 0f, finalRadius)
                        reveal.duration = 600
                        reveal.start()
                    }
                }

                buttonIncrease.setOnClickListener {
                    updateQuantity(product, true)
                }

                buttonDecrease.setOnClickListener {
                    updateQuantity(product, false)
                }

                imageViewContainer.setOnClickListener {
                    val action = ProductListingFragmentDirections.actionProductListingFragmentToDetailFragment(product)
                    navController.navigate(action)
                }
            }
        }

        private fun updateQuantity(product: Product, increase: Boolean) {
            val currentQuantity = binding.textQuantity.text.toString().toInt()
            val newQuantity = if (increase) currentQuantity + 1 else Math.max(1, currentQuantity - 1)
            binding.textQuantity.text = newQuantity.toString()
            val productEntity = convertToProductEntity(product)
            productEntity.quantity = newQuantity
            viewModel.updateQuantity(productEntity, increase)
        }
    }
}
