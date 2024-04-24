package com.example.getirmultideneme.util

import android.content.Context
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.data.models.Product
import com.example.data.models.ProductEntity
import com.example.data.models.SuggestedProduct
import com.example.getirmultideneme.R
import com.example.getirmultideneme.customview.BasketCustomView

object Extension {
    var hasVisitedShoppingCart = false
    fun calculatePrice(data: List<ProductEntity>): Double {
        var price = 0.0
        for (i in data) {
            price += ((i.price) * (i.quantity))
        }
        return price
    }

    fun setStausBar(activity: AppCompatActivity, color :Int) {
        // Set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = activity.resources.getColor(color, activity.theme)
        }
    }

    fun convertToProduct(suggestedProduct: SuggestedProduct): Product{
        val product = Product(id = suggestedProduct.id,
            name = suggestedProduct.name,
            attribute = null,
            thumbnailURL = suggestedProduct.squareThumbnailURL,
            price = suggestedProduct.price,
            priceText = suggestedProduct.priceText,
            imageURL = suggestedProduct.imageURL,
            description = suggestedProduct.shortDescription,
        )
        return product
    }

    fun convertToProductEntity(product: Product): ProductEntity {
        val productEntity = ProductEntity(
            productId = product.id,
            name = product.name,
            attribute = product.attribute.toString(),
            thumbnailURL = product.thumbnailURL,
            price = product.price,
            imageURL = product.imageURL.toString(),
            description = product.description,
            quantity = 1
        )

        return productEntity
    }

    fun convertToProduct(productEntity: ProductEntity):Product{
        val product = Product (id = productEntity.productId.toString(),
            name = productEntity.name,
            attribute = productEntity.attribute,
            thumbnailURL = productEntity.thumbnailURL,
            price = productEntity.price,
            priceText = "",
            imageURL = productEntity.imageURL,
            description = productEntity.description)
        return product

    }

    fun convertToProductSuggestedToEntity(product: SuggestedProduct): ProductEntity {
        val prod = convertToProduct(product)
        return convertToProductEntity(prod)
    }

    fun fadeInView(view: View, context: Context) {
        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        view.startAnimation(fadeInAnimation)
        view.visibility = View.VISIBLE
    }

    fun fadeOutView(view: View, context: Context) {
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        view.startAnimation(fadeOutAnimation)
        view.visibility = View.GONE
    }

    private var isAnimating = false
    fun updateBasketPriceWithAnimation(newPrice: Double, basketCustomView: BasketCustomView) {
        if (isAnimating) return

        val imageView = basketCustomView.getImage()
        val constraintLayout = basketCustomView.getConstraint()
        constraintLayout.post {
            val originalX = imageView.x
            val originalY = imageView.y
            isAnimating = true
            // Animasyonu başlat
            imageView.animate()
                .x(constraintLayout.x)
                .y(constraintLayout.y)
                .setDuration(500)
                .withStartAction {
                    constraintLayout.visibility = View.INVISIBLE
                }
                .withEndAction {
                    // Yeni fiyatı güncelle ve animasyonu geri döndür
                    basketCustomView.setPrice(newPrice)
                    imageView.animate()
                        .x(originalX)
                        .y(originalY)
                        .setDuration(500)
                        .withEndAction {
                            isAnimating = false
                            constraintLayout.visibility = View.VISIBLE
                        }
                        .start()
                }
                .start()
        }
    }
}