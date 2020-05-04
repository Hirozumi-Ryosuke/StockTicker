package com.example.stockticker.ticker.repo.data

@Entity
data class QuoteRow(
    @PrimaryKey @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "last_trade_price") val lastTradePrice: Float,
    @ColumnInfo(name = "change_percent") val changeInPercent: Float,
    @ColumnInfo(name = "change") val change: Float,
    @ColumnInfo(name = "exchange") val stockExchange: String,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "description") val description: String)