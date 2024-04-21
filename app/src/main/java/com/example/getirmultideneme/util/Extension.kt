package com.example.getirmultideneme.util

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.example.data.models.ProductEntity
import com.example.getirmultideneme.R

object Extension {

    fun calculatePrice(data: List<ProductEntity>): Double {
        var price = 0.0
        for(i in data) {
            price += ((i.price) * (i.quantity))
        }
        return price
    }

    fun setStausBar(activity:AppCompatActivity,color:Int) {
        // Set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = activity.resources.getColor(color, activity.theme)
        }
    }
}