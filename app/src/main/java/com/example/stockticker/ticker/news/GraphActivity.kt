package com.example.stockticker.ticker.news

import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.stockticker.R.layout.activity_graph
import com.example.stockticker.ticker.base.BaseGraphActivity
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.network.data.Quote
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance
import com.example.stockticker.R
import com.example.stockticker.R.string.no_network_message
import com.example.stockticker.ticker.isNetworkOnline
import com.example.stockticker.ticker.model.IHistoryProvider.Range.Companion.MAX
import com.example.stockticker.ticker.model.IHistoryProvider.Range.Companion.ONE_MONTH
import com.example.stockticker.ticker.model.IHistoryProvider.Range.Companion.ONE_YEAR
import com.example.stockticker.ticker.model.IHistoryProvider.Range.Companion.THREE_MONTH
import com.example.stockticker.ticker.showDialog
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import kotlinx.android.synthetic.main.activity_graph.desc
import kotlinx.android.synthetic.main.activity_graph.graph_holder
import kotlinx.android.synthetic.main.activity_graph.max
import kotlinx.android.synthetic.main.activity_graph.one_month
import kotlinx.android.synthetic.main.activity_graph.one_year
import kotlinx.android.synthetic.main.activity_graph.progress
import kotlinx.android.synthetic.main.activity_graph.three_month
import kotlinx.android.synthetic.main.activity_graph.tickerName

class GraphActivity : BaseGraphActivity() {

    companion object {
        const val TICKER = "TICKER"
        private const val DURATION = 2000
    }

    override val simpleName: String = "GraphActivity"
    private var range = THREE_MONTH
    private lateinit var ticker: String
    protected lateinit var quote: Quote
    private lateinit var viewModel: GraphViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(activity_graph)
        setupGraphView()
        ticker = checkNotNull(intent.getStringExtra(TICKER))
        viewModel = ViewModelProvider(this, getInstance(application))
            .get(GraphViewModel::class.java)
        viewModel.quote.observe(this, Observer { quote ->
            this.quote = quote
            tickerName.text = ticker
            desc.text = quote.name
        })
        viewModel.data.observe(this, Observer { data ->
            dataPoints = data
            loadGraph(ticker)
        })
        viewModel.error.observe(this, Observer {
            showErrorAndFinish()
        })
        viewModel.fetchStock(ticker)
        var view: View? = null
        when (range) {
            ONE_MONTH -> view = one_month
            THREE_MONTH -> view = three_month
            ONE_YEAR -> view = one_year
            MAX -> view = max
        }
        view?.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        if (dataPoints == null) {
            getData()
        } else {
            loadGraph(ticker)
        }
    }

    private fun getData() {
        if (isNetworkOnline()) {
            graph_holder.visibility = GONE
            progress.visibility = VISIBLE
            viewModel.fetchHistoricalDataByRange(ticker, range)
        } else {
            showDialog(getString(no_network_message),
                OnClickListener { _, _ -> finish() }, cancelable = false)
        }
    }

    override fun onGraphDataAdded(graphView: LineChart) {
        progress.visibility = GONE
        graph_holder.visibility = VISIBLE
        graphView.animateX(DURATION, Easing.EasingOption.EaseInOutCubic)
    }

    override fun onNoGraphData(graphView: LineChart) {
        progress.visibility = GONE
        graph_holder.visibility = VISIBLE
    }

    /**
     * xml OnClick
     * @param v
     */
    fun updateRange(v: View) {
        when (v.id) {
            R.id.one_month -> range = ONE_MONTH
            R.id.three_month -> range = THREE_MONTH
            R.id.one_year -> range = ONE_YEAR
            R.id.max -> range = MAX
        }
        val parent = v.parent as ViewGroup
        (0 until parent.childCount).map { parent.getChildAt(it) }
            .forEach { it.isEnabled = it != v }
        getData()
    }
}