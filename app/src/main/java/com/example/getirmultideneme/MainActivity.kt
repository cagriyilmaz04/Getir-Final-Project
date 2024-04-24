package com.example.getirmultideneme


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.getirmultideneme.util.Extension.setStausBar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setStausBar(this,R.color.text_primary)

    }
}
