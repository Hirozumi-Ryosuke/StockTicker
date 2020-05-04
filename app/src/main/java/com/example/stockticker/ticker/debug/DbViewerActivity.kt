package com.example.stockticker.ticker.debug

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.stockticker.R
import com.example.stockticker.ticker.base.BaseActivity
import com.example.stockticker.ticker.components.Injector
import kotlinx.android.synthetic.main.activity_db_viewer.*

class DbViewerActivity : BaseActivity() {

    override val simpleName: String
        get() = "DbViewerActivity"

    private val viewModel: DbViewerViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))
            .get(DbViewerViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.appComponent.inject(this)
        setContentView(R.layout.activity_db_viewer)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.htmlFile.observe(this, Observer {
            webview.loadUrl("file://${it.absolutePath}")
        })

        viewModel.showProgress.observe(this, Observer { show ->
            progress.visibility = if (show) View.VISIBLE else View.GONE
        })

        viewModel.generateDatabaseHtml()
    }
}