package com.example.stockticker.ticker.base

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.stockticker.R
import com.example.stockticker.ticker.analytics.Analytics
import com.example.stockticker.ticker.components.AsyncBus
import com.example.stockticker.ticker.components.InAppMessage
import com.example.stockticker.ticker.events.ErrorEvent
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseActivity : AppCompatActivity() {

    abstract val simpleName: String
    @Inject internal lateinit var bus: AsyncBus
    @Inject internal lateinit var analytics: Analytics

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onCreate(savedInstanceState, persistentState)
        analytics.trackScreenView(simpleName, this)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val errorFlow = bus.receive<ErrorEvent>()
            errorFlow.collect { event ->
                if (this.isActive) {
                    showDialog(event.message)
                }
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        try {
            super.onRestoreInstanceState(savedInstanceState)
        } catch (ex: Throwable) {
            // android bug
            Timber.w(ex)
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun showErrorAndFinish() {
        InAppMessage.showToast(this, R.string.error_symbol)
        finish()
    }

    protected fun dismissKeyboard() {
        val view = currentFocus
        if (view is TextView) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}