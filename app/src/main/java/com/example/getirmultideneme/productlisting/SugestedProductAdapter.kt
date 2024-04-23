package com.example.getirmultideneme.productlisting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.SuggestedProduct
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.RecylerRowBinding
import com.example.getirmultideneme.util.Extension


class SuggestedProductAdapter(
    var products: List<BeverageSuggestedPack>,
    val context: Context,
    private val viewModel: ProductsViewModel,
    private val navController: NavController
) : RecyclerView.Adapter<SuggestedProductAdapter.ProductViewHolder>() {
    fun updateData(newProducts: List<BeverageSuggestedPack>) {
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

    override fun getItemCount(): Int {
        return if (products.isNotEmpty()) products[0].products.size else 0
    }

    inner class ProductViewHolder(private val binding: RecylerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: SuggestedProduct) {
            binding.apply {
                textViewProductName.text = product.name
                textViewProductPrice.text = String.format("${context.getString(R.string.turkish_lira)}%.2f", product.price)
                textViewProductAttribute.visibility = if (product.attribute.isNullOrEmpty()) View.GONE else View.VISIBLE
                textViewProductAttribute.text = product.attribute
                Glide.with(itemView.context).load(product.imageURL ?: product.squareThumbnailURL).into(imageViewProduct)

                val currentQuantity = viewModel.getProductQuantity(product.id)
                textQuantity.text = currentQuantity.toString()
                updateDecreaseButtonIcon(currentQuantity)

                val isInCart = currentQuantity > 0
                csImageViewAdd.visibility = if (isInCart) View.GONE else View.VISIBLE
                layoutQuantitySelector.visibility = if (isInCart) View.VISIBLE else View.GONE
                cardViewQuantitySelector.visibility = if (isInCart) View.VISIBLE else View.GONE
                if (!isInCart) {
                    csImageViewAdd.setOnClickListener {
                        textQuantity.text = "1"
                        csImageViewAdd.visibility = View.GONE
                        layoutQuantitySelector.visibility = View.VISIBLE
                        cardViewQuantitySelector.visibility = View.VISIBLE
                        viewModel.addToCart(Extension.convertToProductSuggestedToEntity(product))
                        updateDecreaseButtonIcon(1)
                        constraintLayoutProductImage.setBackgroundResource(R.drawable.constraint_background_transition)
                        val cx = constraintLayoutProductImage.width
                        val cy = 0

                        val finalRadius = Math.hypot(cx.toDouble(), constraintLayoutProductImage.height.toDouble()).toFloat()

                        val reveal = ViewAnimationUtils.createCircularReveal(constraintLayoutProductImage, cx, cy, 0f, finalRadius)
                        reveal.duration = 600
                        reveal.start()
                        constraintLayoutProductImage.setBackgroundResource(R.drawable.constraint_background_transition)
                    }
                }

                buttonIncrease.setOnClickListener {
                    updateQuantity(product, true)
                }

                buttonDecrease.setOnClickListener {
                    updateQuantity(product, false)
                }

                imageViewContainer.setOnClickListener {
                    val action = ProductListingFragmentDirections.actionProductListingFragmentToDetailFragment(Extension.convertToProduct(product))
                    navController.navigate(action)
                }
            }
        }

        private fun updateQuantity(product: SuggestedProduct, increase: Boolean) {
            val currentQuantity = binding.textQuantity.text.toString().toInt()
            val newQuantity = if (increase) currentQuantity + 1 else Math.max(0, currentQuantity - 1)
            updateDecreaseButtonIcon(newQuantity)

            if (newQuantity == 0) {
                Extension.fadeInView(binding.csImageViewAdd, context)
                Extension.fadeOutView(binding.layoutQuantitySelector, context)
                binding.constraintLayoutProductImage.setBackgroundResource(R.drawable.bg_last_product_image)
            } else {
                binding.textQuantity.text = newQuantity.toString()
                binding.csImageViewAdd.visibility = View.GONE
                binding.layoutQuantitySelector.visibility = View.VISIBLE
            }

            val productEntity = Extension.convertToProductSuggestedToEntity(product)
            productEntity.quantity = newQuantity
            viewModel.updateQuantity(productEntity, increase)
        }

        private fun updateDecreaseButtonIcon(quantity: Int) {
            binding.buttonDecrease.setImageResource(if (quantity > 1) R.drawable.subtract else R.drawable.trash_small)
        }

    }
}
