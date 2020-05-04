package com.example.stockticker.ticker.repo.data

import androidx.browser.customtabs.CustomTabsService

data class QuoteWithHoldings(
    @Embedded
    val quote: QuoteRow,
    @CustomTabsService.Relation(parentColumn = "symbol",
        entityColumn = "quote_symbol")
    val holdings: List<HoldingRow>
)