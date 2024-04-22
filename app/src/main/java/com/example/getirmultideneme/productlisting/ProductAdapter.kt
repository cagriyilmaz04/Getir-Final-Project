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



class ProductAdapter(private var products: List<BeveragePack>,
                     val context: Context,
                     private val viewModel: ProductsViewModel,
                     private val navController: NavController) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

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
                textViewProductPrice.text = String.format("${context.getString(R.string.turkish_lira)}"+"%.2f", product.price)
                Glide.with(itemView.context).load(product.imageURL).into(imageViewProduct)

                csImageViewAdd.setOnClickListener {
                    textQuantity.text = "1"
                    csImageViewAdd.visibility = View.GONE
                    layoutQuantitySelector.visibility = View.VISIBLE
                    viewModel.addToCart(convertToProductEntity(product))
                    constraintLayoutProductImage.setBackgroundResource(R.drawable.constraint_background_transition)

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
                    val productEntity = convertToProductEntity(product)
                    productEntity.quantity = newQuantity
                    viewModel.updateQuantity(productEntity,true)
                }
                buttonDecrease.setOnClickListener {
                    val currentQuantity = textQuantity.text.toString().toInt()
                    if (currentQuantity > 1) {
                        val newQuantity = currentQuantity - 1
                        textQuantity.text = newQuantity.toString()
                        buttonDecrease.setImageResource(if (newQuantity > 1) R.drawable.subtract else R.drawable.trash_small)
                        val productEntity = convertToProductEntity(product)
                        productEntity.quantity = newQuantity  // Burada azalan miktar güncellenmeli
                        viewModel.updateQuantity(productEntity, false)
                    } else {
                        viewModel.deleteProductFromCart(convertToProductEntity(product))
                        layoutQuantitySelector.visibility = View.GONE
                        imageViewAddToCart.visibility = View.VISIBLE
                        fadeInView(imageViewAddToCart, context)
                        fadeOutView(layoutQuantitySelector, context)
                        constraintLayoutProductImage.setBackgroundResource(R.drawable.bg_last_product_image)
                    }
                }




                imageViewContainer.setOnClickListener {
                    val action = ProductListingFragmentDirections.actionProductListingFragmentToDetailFragment(product)
                    navController.navigate(action)
                }

            }
        }
    }



}
