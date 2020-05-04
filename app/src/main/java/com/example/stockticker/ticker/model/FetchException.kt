package com.example.stockticker.ticker.model

import java.io.IOException

class FetchException(message: String, ex: Throwable? = null): IOException(message, ex)
