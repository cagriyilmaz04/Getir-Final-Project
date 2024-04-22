package com.example.getirmultideneme.shoppingcart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.models.BeveragePack
import com.example.data.models.BeverageSuggestedPack
import com.example.data.models.SuggestedProduct
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.RecylerRowBinding
import com.example.getirmultideneme.productlisting.ProductListingFragmentDirections
import com.example.getirmultideneme.productlisting.ProductsViewModel
import com.example.getirmultideneme.util.Extension
import com.example.getirmultideneme.util.Extension.convertToProduct


class SuggestedProductAdapter(
    var products: List<BeverageSuggestedPack>,
    val context: Context,
    private val viewModel: ProductsViewModel,
    private val navController: NavController
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

    inner class ProductViewHolder(private val binding: RecylerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: SuggestedProduct) {
            with(binding) {
                textViewProductName.text = product.name
                textViewProductPrice.text = String.format("${context.getString(R.string.turkish_lira)}"+"%.2f", product.price)
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
                val returnedProduct = convertToProduct(product)
                imageViewContainer.setOnClickListener {
                    val action = ProductListingFragmentDirections.actionProductListingFragmentToDetailFragment(returnedProduct)
                    navController.navigate(action)
                }

                csImageViewAdd.setOnClickListener {
                    textQuantity.text = "1"
                    csImageViewAdd.visibility = View.GONE
                    layoutQuantitySelector.visibility = View.VISIBLE
                    constraintLayoutProductImage.setBackgroundResource(R.drawable.constraint_background_transition)
                    viewModel.addToCart(Extension.convertToProductSuggesterdToEntity(product))
                    val cx = constraintLayoutProductImage.width
                    val cy = 0

                    val finalRadius = Math.hypot(cx.toDouble(), constraintLayoutProductImage.height.toDouble()).toFloat()

                    val reveal = ViewAnimationUtils.createCircularReveal(constraintLayoutProductImage, cx, cy, 0f, finalRadius)
                    reveal.duration = 600
                    reveal.start()
                }
                buttonIncrease.setOnClickListener {
                    val currentQuantity = textQuantity.text.toString().toInt()
                    val newQuantity = currentQuantity + 1
                    textQuantity.text = newQuantity.toString()
                    buttonDecrease.setImageResource(if (newQuantity > 1) R.drawable.subtract else R.drawable.trash_small)
                    val productEntity = Extension.convertToProductSuggesterdToEntity(product)
                    productEntity.quantity = newQuantity
                    viewModel.updateQuantity(productEntity,true)
                }
                buttonDecrease.setOnClickListener {
                    val currentQuantity = textQuantity.text.toString().toInt()
                    if (currentQuantity > 1) {
                        val newQuantity = currentQuantity - 1
                        textQuantity.text = newQuantity.toString()
                        buttonDecrease.setImageResource(if (newQuantity > 1) R.drawable.subtract else R.drawable.trash_small)
                        val productEntity = Extension.convertToProductSuggesterdToEntity(product)
                        productEntity.quantity = newQuantity  // Burada azalan miktar güncellenmeli
                        viewModel.updateQuantity(productEntity, false)
                    } else {
                        viewModel.deleteProductFromCart(Extension.convertToProductSuggesterdToEntity(product))
                        layoutQuantitySelector.visibility = View.GONE
                        imageViewAddToCart.visibility = View.VISIBLE
                        Extension.fadeInView(imageViewAddToCart, context)
                        Extension.fadeOutView(layoutQuantitySelector, context)
                        constraintLayoutProductImage.setBackgroundResource(R.drawable.bg_last_product_image)
                    }
                }

            }
        }
    }
}
