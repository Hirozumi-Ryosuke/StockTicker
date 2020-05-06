package com.example.stockticker.ticker.portfolio.drag_drop

interface OnStartDragListener {

    fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder)
    fun onStopDrag()
}