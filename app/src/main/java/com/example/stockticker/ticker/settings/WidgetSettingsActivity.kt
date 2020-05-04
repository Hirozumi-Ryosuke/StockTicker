package com.example.stockticker.ticker.settings

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.example.stockticker.R
import com.example.stockticker.ticker.base.BaseActivity
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.portfolio.search.SearchActivity
import kotlinx.android.synthetic.main.activity_db_viewer.*

class WidgetSettingsActivity : BaseActivity(), WidgetSettingsFragment.Parent {

    companion object {
        const val ARG_WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
    }

    internal var widgetId = 0
    override val simpleName: String = "WidgetSettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_settings)
        toolbar.setNavigationOnClickListener {
            setOkResult()
            finish()
        }
        toolbar.navigationIcon?.setTint(resources.getColor(R.color.icon_tint))
        toolbar.navigationIcon?.setTintMode(PorterDuff.Mode.SRC_IN)
        toolbar.inflateMenu(R.menu.menu_widget_settings)
        toolbar.setOnMenuItemClickListener {
            setOkResult()
            finish()
            return@setOnMenuItemClickListener true
        }
        widgetId = intent.getIntExtra(ARG_WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            setOkResult()
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, WidgetSettingsFragment.newInstance(widgetId, true))
                .commit()
        }
    }

    private fun setOkResult() {
        val result = Intent()
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, result)
    }

    override fun openSearch(widgetId: Int) {
        val intent = SearchActivity.launchIntent(this, widgetId)
        startActivity(intent)
    }
}