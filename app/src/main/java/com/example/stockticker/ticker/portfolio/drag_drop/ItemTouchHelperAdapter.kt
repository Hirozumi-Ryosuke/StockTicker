package com.example.stockticker.ticker.portfolio.drag_drop

internal interface ItemTouchHelperAdapter {

    fun onItemMove(
        fromPosition: Int,
        toPosition: Int
    ): Boolean

    fun onItemDismiss(position: Int)
}