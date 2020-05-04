package com.example.stockticker.ticker.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import androidx.lifecycle.lifecycleScope
import com.example.stockticker.R
import com.example.stockticker.R.layout.activity_splash
import com.example.stockticker.ticker.base.BaseActivity
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.components.Injector.appComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {
    override val simpleName: String = "SplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(activity_splash)
        val decorView = window.decorView
        // Hide the status bar.
        decorView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        lifecycleScope.launch {
            delay(300)
            openApp()
        }
    }

    private fun openApp() {
        if (!isFinishing) {
            startActivity(Intent(this, ParanormalActivity::class.java))
            finish()
        }
    }
}