package com.example.getirmultideneme.details

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.getirmultideneme.R
import com.example.getirmultideneme.databinding.FragmentDetailBinding
import presentation.base.BaseFragment


class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitialViews()
    }

    private fun setupInitialViews() {
        binding.buttonAddToCart.setOnClickListener {
            fadeOutView(binding.buttonAddToCart)
        }
        binding.buttonIncrease.setOnClickListener {
            changeQuantity(true)
        }
        binding.buttonDecrease.setOnClickListener {
            changeQuantity(false)
        }
    }

    private fun fadeOutView(view: View) {
        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.GONE
                fadeInView(binding.layoutQuantitySelector)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(fadeOutAnimation)
    }

    private fun fadeInView(view: View) {
        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        view.visibility = View.VISIBLE
        view.startAnimation(fadeInAnimation)
    }

    private fun changeQuantity(increase: Boolean) {
        var quantity = binding.textQuantity.text.toString().toInt()
        if (increase) {
            quantity++
        } else {
            if (quantity > 1) {
                quantity--
            } else {
                binding.textQuantity.text = "1"
                fadeOutView(binding.layoutQuantitySelector)
                fadeInView(binding.buttonAddToCart)
                return
            }
        }
        binding.textQuantity.text = quantity.toString()
        updateIcons(quantity)
    }

    private fun updateIcons(quantity: Int) {
        if (quantity == 1) {
            binding.buttonDecrease.setImageResource(R.drawable.delete)
        } else {
            binding.buttonDecrease.setImageResource(R.drawable.subtract)
        }
    }
}
