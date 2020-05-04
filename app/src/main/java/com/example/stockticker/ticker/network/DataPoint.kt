package com.example.stockticker.ticker.network

import android.os.Build
import android.os.DropBoxManager
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import com.example.stockticker.ticker.network.data.HistoricalValue
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DataPoint : DropBoxManager.Entry, Serializable, Comparable<DataPoint> {

    constructor(
        x: Float,
        y: Float
    ) : super(x, y)

    constructor(
        x: Float,
        y: Float,
        data: HistoricalValue
    ) : super(x, y, data)

    constructor(source: Parcel) : super(source)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(): LocalDate = LocalDate.ofEpochDay(x.toLong())

    override fun compareTo(other: DataPoint): Int = x.compareTo(other.x)

    @RequiresApi(Build.VERSION_CODES.O)
    companion object {

        private val FORMATTER: DateTimeFormatter by lazy { DateTimeFormatter.ofPattern("MMMM d") }
        private const val serialVersionUID = 42L

        @JvmField
        val CREATOR: Parcelable.Creator<DataPoint> = object : Parcelable.Creator<DataPoint> {
            override fun createFromParcel(source: Parcel): DataPoint {
                return DataPoint(source)
            }

            override fun newArray(size: Int): Array<DataPoint?> {
                return arrayOfNulls(size)
            }
        }
    }
}