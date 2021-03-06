package com.example.stockticker.ticker.portfolio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stockticker.R
import com.example.stockticker.R.layout.item_position
import com.example.stockticker.R.layout.item_stock
import com.example.stockticker.ticker.network.data.Quote
import com.example.stockticker.ticker.portfolio.drag_drop.ItemTouchHelperAdapter
import com.example.stockticker.ticker.portfolio.drag_drop.OnStartDragListener
import com.example.stockticker.ticker.widget.WidgetData

class StocksAdapter constructor(
    private val widgetData: WidgetData,
    private val listener: QuoteClickListener,
    private val dragStartListener: OnStartDragListener
) :
    RecyclerView.Adapter<PortfolioVH>(), ItemTouchHelperAdapter {

    interface QuoteClickListener {
        fun onClickQuoteOptions(
            view: View,
            quote: Quote,
            position: Int
        )

        fun onOpenQuote(
            view: View,
            quote: Quote,
            position: Int
        )
    }

    companion object {
        const val TYPE_STOCK = 1
        const val TYPE_POSITION = 2
    }

    private val quoteList: MutableList<Quote>

    init {
        quoteList = ArrayList()
        quoteList.addAll(widgetData.getStocks())
    }

    fun remove(quote: Quote) {
        val index = quoteList.indexOf(quote)
        val removed = quoteList.remove(quote)
        if (index >= 0 && removed) {
            notifyItemRemoved(index)
        }
    }

    fun refresh() {
        quoteList.clear()
        quoteList.addAll(widgetData.getStocks())
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val stock = quoteList[position]
        return when {
            stock.hasPositions() -> TYPE_POSITION
            else -> TYPE_STOCK
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PortfolioVH {
        val context = parent.context
        val portfolioVH: PortfolioVH
        portfolioVH = if (viewType == TYPE_POSITION) {
            val itemView = LayoutInflater.from(context)
                .inflate(item_position, parent, false)
            PortfolioVH.PositionVH(itemView)
        } else {
            val itemView = LayoutInflater.from(context)
                .inflate(item_stock, parent, false)
            PortfolioVH.StockVH(itemView)
        }
        return portfolioVH
    }

    override fun onBindViewHolder(
        holder: PortfolioVH,
        position: Int
    ) {
        holder.update(quoteList[position], listener)
        holder.itemView.setOnLongClickListener {
            dragStartListener.onStartDrag(holder)
            true
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount() = quoteList.size

    override fun onItemMove(
        fromPosition: Int,
        toPosition: Int
    ): Boolean {
        quoteList.add(toPosition, quoteList.removeAt(fromPosition))
        val newTickerList = quoteList.mapTo(ArrayList()) { it.symbol }
        widgetData.rearrange(newTickerList)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        dragStartListener.onStopDrag()
    }
}