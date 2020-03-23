package com.example.ps4.util

import android.app.Dialog
import android.content.Context
import com.example.ps4.R

class ClientDialog(context: Context) : Dialog(context) {



    override fun show() {
        super.show()


        setContentView(R.layout.dialog_client)
        setCancelable(true)
    }

}