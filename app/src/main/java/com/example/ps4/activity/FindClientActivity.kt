package com.example.ps4.activity

import android.os.Bundle
import com.example.ps4.R

class FindClientActivity : BaseActivity(1) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_client)
        enableBottomNavMenu()
    }
}
