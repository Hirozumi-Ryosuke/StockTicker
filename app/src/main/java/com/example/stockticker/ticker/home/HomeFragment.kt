package com.example.stockticker.ticker.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.stockticker.R
import com.example.stockticker.ticker.base.BaseFragment
import com.example.stockticker.ticker.components.AsyncBus
import com.example.stockticker.ticker.components.InAppMessage
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.events.RefreshEvent
import com.example.stockticker.ticker.getStatusBarHeight
import com.example.stockticker.ticker.isNetworkOnline
import com.example.stockticker.ticker.model.IStocksProvider
import com.example.stockticker.ticker.portfolio.PortfolioFragment
import com.example.stockticker.ticker.widget.WidgetDataProvider
import kotlinx.android.synthetic.main.activity_db_viewer.*
import kotlinx.android.synthetic.main.activity_db_viewer.toolbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment(), ChildFragment, PortfolioFragment.Parent {

    companion object {
        private const val MAX_FETCH_COUNT = 3
    }

    interface Parent {
        fun showWhatsNew()
        fun showTutorial()
    }

    @Inject internal lateinit var stocksProvider: IStocksProvider
    @Inject internal lateinit var widgetDataProvider: WidgetDataProvider
    @Inject internal lateinit var bus: AsyncBus
    override val simpleName: String = "HomeFragment"

    private var attemptingFetch = false
    private var fetchCount = 0
    private lateinit var adapter: HomePagerAdapter

    private val subtitleText: String
        get() = getString(R.string.last_and_next_fetch, stocksProvider.lastFetched(), stocksProvider.nextFetch())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (toolbar.layoutParams as ViewGroup.MarginLayoutParams).topMargin = requireContext().getStatusBarHeight()
        swipe_container.setColorSchemeResources(R.color.color_primary_dark, R.color.spicy_salmon,
            R.color.sea)
        swipe_container.setOnRefreshListener { fetch() }
        adapter = HomePagerAdapter(childFragmentManager)
        view_pager.adapter = adapter
        tabs.setupWithViewPager(view_pager)
        subtitle.text = subtitleText
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) updateHeader()
    }

    override fun onResume() {
        super.onResume()
        update()
        lifecycleScope.launch {
            val flow = bus.receive<RefreshEvent>()
            flow.collect {
                if (isResumed) {
                    updateHeader()
                }
            }
        }
    }

    private fun updateHeader() {
        tabs.visibility = if (widgetDataProvider.hasWidget()) View.VISIBLE else View.INVISIBLE
        adapter.notifyDataSetChanged()
        subtitle.text = subtitleText
    }

    private fun fetch() {
        if (!attemptingFetch) {
            if (requireActivity().isNetworkOnline()) {
                fetchCount++
                // Don't attempt to make many requests in a row if the stocks don't fetch.
                if (fetchCount <= MAX_FETCH_COUNT) {
                    attemptingFetch = true
                    lifecycleScope.launch {
                        stocksProvider.fetch()
                        attemptingFetch = false
                        swipe_container?.isRefreshing = false
                        update()
                    }
                } else {
                    attemptingFetch = false
                    InAppMessage.showMessage(requireActivity(), R.string.refresh_failed, error = true)
                    swipe_container?.isRefreshing = false
                }
            } else {
                attemptingFetch = false
                InAppMessage.showMessage(requireActivity(), R.string.no_network_message, error = true)
                swipe_container?.isRefreshing = false
            }
        }
    }

    private fun update() {
        adapter.notifyDataSetChanged()
        updateHeader()
        fetchCount = 0
    }

    // PortfolioFragment.Parent

    override fun onDragStarted() {
        swipe_container.isEnabled = false
    }

    override fun onDragEnded() {
        swipe_container.isEnabled = true
    }

    // ChildFragment

    override fun setData(bundle: Bundle) {

    }
}