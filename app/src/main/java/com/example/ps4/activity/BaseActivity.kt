package com.example.ps4.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ps4.R
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

abstract class BaseActivity(private val navNumber : Int) : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_client)
    }

    fun enableBottomNavMenu() {
        val navView: BottomNavigationViewEx = findViewById(R.id.bnve)

        navView.setIconSize(30f, 30f)
        navView.enableAnimation(false)
        navView.setTextVisibility(false)

        navView.menu.getItem(navNumber).isChecked = true

        navView.setOnNavigationItemSelectedListener {
            val nextActivity = when (it.itemId) {
                R.id.nav_item_list -> ClientListActivity::class.java
                R.id.nav_item_find -> FindClientActivity::class.java
                R.id.nav_item_create -> CreateClientActivity::class.java
                else -> null
            }

            if (nextActivity != null    ) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                overridePendingTransition(0, 0)
                startActivity(intent)
                finish()
                true
            } else {
                false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}