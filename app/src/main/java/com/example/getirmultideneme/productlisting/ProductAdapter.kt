import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.data.models.BeveragePack
import com.example.data.models.Product
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.RecylerRowBinding
import com.example.getirmultideneme.productlisting.ProductListingFragmentDirections
import com.example.getirmultideneme.productlisting.ProductsViewModel
import com.example.getirmultideneme.util.Extension
import com.example.getirmultideneme.util.Extension.convertToProductEntity

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
                var str = product.attribute.toString()
                if (product.attribute != null) {
                    str = product.attribute.toString()
                }else if (product.description != null) {
                    str = product.description.toString()
                }else {
                    textViewProductAttribute.visibility = View.INVISIBLE
                }
                textViewProductAttribute.text = str
                Log.e("TAG",product.toString())
                textViewProductPrice.text = String.format("${context.getString(R.string.turkish_lira)}%.2f", product.price)
                Glide.with(itemView.context).load(product.imageURL).into(imageViewProduct)

                val currentQuantity = viewModel.getProductQuantity(product.id)
                textQuantity.text = currentQuantity.toString()
                updateDecreaseButtonIcon(currentQuantity)

                val isInCart = currentQuantity > 0
                csImageViewAdd.visibility = if (isInCart) View.GONE else View.VISIBLE
                layoutQuantitySelector.visibility = if (isInCart) View.VISIBLE else View.GONE
                cardViewQuantitySelector.visibility = if (isInCart) View.VISIBLE else View.GONE

                if (!isInCart) {

                    csImageViewAdd.setOnClickListener {
                        csImageViewAdd.visibility = View.GONE
                        textQuantity.text = "1"
                        layoutQuantitySelector.visibility = View.VISIBLE
                        cardViewQuantitySelector.visibility = View.VISIBLE
                        viewModel.addToCart(convertToProductEntity(product))
                        updateDecreaseButtonIcon(1)
                        imageViewBackground.setBackgroundResource(R.drawable.constraint_background_transition)
                        val cx = imageViewBackground.width
                        val cy = 0

                        val finalRadius = Math.hypot(cx.toDouble(), imageViewBackground.height.toDouble()).toFloat()
                        val reveal = ViewAnimationUtils.createCircularReveal(imageViewBackground, cx, cy, 0f, finalRadius)
                        reveal.duration = 600
                        reveal.start()
                        imageViewBackground.setBackgroundResource(R.drawable.constraint_background_transition)
                    }
                }else{
                    imageViewBackground.setBackgroundResource(R.drawable.constraint_background_transition)

                }

                buttonIncrease.setOnClickListener {
                    updateQuantity(product, true)
                }

                buttonDecrease.setOnClickListener {
                    updateQuantity(product, false)
                }

                imageViewContainer.setOnClickListener {
                    val action = ProductListingFragmentDirections.actionProductListingFragmentToDetailFragment(product, binding.textQuantity.text.toString().toInt())
                    navController.navigate(action)
                }
            }
        }

        private fun updateQuantity(product: Product, increase: Boolean) {
            val currentQuantity = binding.textQuantity.text.toString().toInt()
            val newQuantity = if (increase) currentQuantity + 1 else Math.max(0, currentQuantity - 1)

            updateDecreaseButtonIcon(newQuantity)

            if (newQuantity == 0) {
                Extension.fadeInView(binding.csImageViewAdd, context)
                Extension.fadeOutView(binding.layoutQuantitySelector, context)
                binding.imageViewBackground.setBackgroundResource(R.drawable.bg_last_product_image)
            } else {
                binding.textQuantity.text = newQuantity.toString()
                binding.csImageViewAdd.visibility = View.GONE
                binding.layoutQuantitySelector.visibility = View.VISIBLE
            }

            val productEntity = convertToProductEntity(product)
            productEntity.quantity = newQuantity
            viewModel.updateQuantity(productEntity, increase)
        }

        private fun updateDecreaseButtonIcon(quantity: Int) {
            binding.buttonDecrease.setImageResource(if (quantity > 1) R.drawable.subtract else R.drawable.trash_small)
        }
    }
}