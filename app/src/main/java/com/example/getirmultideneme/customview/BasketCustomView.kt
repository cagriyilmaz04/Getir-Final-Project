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
    private var constraintLayout:ConstraintLayout


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_basket_view, this, true)
        textViewPrice = view.findViewById(R.id.tvPrice)
        imageViewBasket = view.findViewById(R.id.customBasket)
        constraintLayout = view.findViewById(R.id.clTextBg)
        imageViewBasket.setOnClickListener { }
    }

    fun setOnBasketClickListener(action: () -> Unit) {
        imageViewBasket.setOnClickListener { action() }
    }

    fun setPrice(price: Double) {
        textViewPrice.text = String.format("${context.getString(R.string.turkish_lira)}%.2f", price)
    }
    fun getConstraint(): ConstraintLayout {
        return constraintLayout
    }

    fun getImage(): ImageView{
        return imageViewBasket
    }
}
