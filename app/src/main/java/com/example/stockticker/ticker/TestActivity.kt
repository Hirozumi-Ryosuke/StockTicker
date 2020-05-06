package com.example.stockticker.ticker

import android.os.Bundle
import com.example.stockticker.R
import com.example.stockticker.R.layout.activity_test
import com.example.stockticker.ticker.base.BaseActivity
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.mock.MockAppComponent

class TestActivity : BaseActivity() {
    override val simpleName: String = "TestActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (Injector.appComponent as MockAppComponent).inject(this)
        setContentView(activity_test)
    }
}