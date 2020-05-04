package com.example.stockticker.ticker.repo

import com.example.stockticker.ticker.repo.data.HoldingRow
import com.example.stockticker.ticker.repo.data.QuoteRow

@Database(entities = [QuoteRow::class, HoldingRow::class], version = 1)
abstract class QuotesDB : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}