package com.example.stockticker.ticker.repo.data

@Entity
data class HoldingRow(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name = "quote_symbol") val quoteSymbol: String,
    @ColumnInfo(name = "shares") val shares: Float = 0.0f,
    @ColumnInfo(name = "price") val price: Float = 0.0f
)