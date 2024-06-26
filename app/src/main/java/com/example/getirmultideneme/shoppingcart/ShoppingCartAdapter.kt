package com.example.getirmultideneme.shoppingcart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.RecyclerRowBasketBinding
import com.example.getirmultideneme.util.Extension.convertToProduct

class ShoppingCartAdapter(
    var products: ArrayList<ProductEntity>,
    private val viewModel: ShoppingCartViewModel,
    val context: Context,
    val navController: NavController
) : RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {

    fun setProducts(newProducts: List<ProductEntity>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RecyclerRowBasketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(private val binding: RecyclerRowBasketBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductEntity) {
            with(binding) {
                textProductName.text = product.name

                textProductPrice.text =
                    String.format("${context.getString(R.string.turkish_lira)}%.2f", product.price)
                textQuantity.text = product.quantity.toString()
                Glide.with(imageProduct.context).load(product.imageURL).into(imageProduct)

                val string = StringBuilder()
                if(product.attribute.toString().equals("null")){
                    if(!product.description.toString().equals("null")){
                        string.append(product.description.toString())
                    }else{
                        textProductAttribute.visibility = View.GONE
                    }

                }else{
                    string.append(product.attribute.toString())
                }
                textProductAttribute.text = string

                updateDecreaseButton(product.quantity)

                buttonIncrease.setOnClickListener {
                    viewModel.increaseQuantity(product)
                    updateQuantity(product.quantity + 1)
                }

                buttonDecrease.setOnClickListener {
                    if (product.quantity > 1) {
                        viewModel.decreaseQuantity(product)
                        updateQuantity(product.quantity - 1)
                    } else {
                        removeItemWithAnimation(product)
                    }
                }
                csClick.setOnClickListener {
                    val action = ShoppingCartFragmentDirections.actionShoppingCartFragmentToDetailFragment(convertToProduct(product),textQuantity.text.toString().toInt())
                    navController.navigate(action)
                }
            }
        }

        private fun updateDecreaseButton(quantity: Int) {
            if (quantity > 1) {
                binding.buttonDecrease.setImageResource(R.drawable.subtract)
            } else {
                binding.buttonDecrease.setImageResource(R.drawable.trash_small)
            }
        }


        private fun updateQuantity(newQuantity: Int) {
            binding.textQuantity.text = newQuantity.toString()
            updateDecreaseButton(newQuantity)
        }

        private fun removeItemWithAnimation(product: ProductEntity) {
            binding.root.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        viewModel.deleteProduct(product)
                        products.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
        }
    }
}
