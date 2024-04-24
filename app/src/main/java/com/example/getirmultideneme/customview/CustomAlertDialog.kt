package com.example.getirmultideneme.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.getirmultideneme.R

class CustomAlertDialog @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var alertDialog: AlertDialog? = null
    var yesButton: Button
    var cancelButton: Button

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, this, true)
        yesButton = view.findViewById(R.id.dialogYesButton)
        cancelButton = view.findViewById(R.id.dialogNoButton)

        yesButton.setOnClickListener {


        }

        cancelButton.setOnClickListener {


        }
    }
    fun setOnYesClick(action: () -> Unit) {
        yesButton.setOnClickListener { action() }
    }

    fun setOnNoClick(action: () -> Unit) {
        cancelButton.setOnClickListener { action() }
    }
    fun dismiss(){
        alertDialog?.dismiss()

    }


    fun show() {
        alertDialog = AlertDialog.Builder(context)
            .setView(this)
            .setCancelable(false)
            .create()
        alertDialog!!.show()
    }
}
