package com.example.stockticker.ticker.network

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.io.InputStream

internal abstract class BaseConverter<T>(val gson: Gson) : Converter<ResponseBody, T> {

    @Throws(IOException::class) protected fun getString(`is`: InputStream): String {
        var ch: Int
        val sb = StringBuilder()
        do {
            ch = `is`.read()
            sb.append(ch.toChar())
        } while (ch != -1)
        return sb.toString()
    }
}