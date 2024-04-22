package com.example.getirmultideneme.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.getirmultideneme.R


class BasketCustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var textViewPrice: TextView
    private var imageViewBasket: ImageView
    init {
        // Inflate the layout
        val view = LayoutInflater.from(context).inflate(R.layout.custom_basket_view, this, true)

        // Reference to the TextView
        textViewPrice = view.findViewById(R.id.textView3)
        imageViewBasket = view.findViewById(R.id.imageBasket)
        imageViewBasket.setOnClickListener { }
        // You can handle custom attributes here if needed
    }
    fun setOnBasketClickListener(action: () -> Unit) {
        imageViewBasket.setOnClickListener { action() }
    }
    fun setPrice(price: Double) {
        textViewPrice.text = String.format("${context.getString(R.string.turkish_lira)}%.2f", price)
    }
}