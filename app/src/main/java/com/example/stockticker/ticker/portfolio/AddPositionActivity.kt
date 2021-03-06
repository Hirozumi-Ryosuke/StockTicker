package com.example.stockticker.ticker.portfolio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.widget.TextView
import com.example.stockticker.R
import com.example.stockticker.R.id.*
import com.example.stockticker.R.string.error_symbol
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.base.BaseActivity
import com.example.stockticker.ticker.components.InAppMessage
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.model.IStocksProvider
import com.example.stockticker.ticker.network.data.Holding
import kotlinx.android.synthetic.main.activity_db_viewer.*
import kotlinx.android.synthetic.main.activity_db_viewer.toolbar
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.android.synthetic.main.activity_graph.tickerName
import kotlinx.android.synthetic.main.activity_positions.*
import java.util.regex.Pattern
import javax.inject.Inject

class AddPositionActivity : BaseActivity() {

    companion object {
        const val QUOTE = "QUOTE"
        const val TICKER = "TICKER"
        private val PATTERN: Pattern =
            Pattern.compile("[0-9]{0," + (5) + "}+((\\.[0-9]{0," + (5) + "})?)||(\\.)?")

        private class DecimalDigitsInputFilter() : InputFilter {

            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dStart: Int,
                dEnd: Int
            ): CharSequence? {
                val matcher = PATTERN.matcher(dest)
                return if (!matcher.matches()) "" else null
            }
        }
    }

    @Inject
    internal lateinit var stocksProvider: IStocksProvider
    internal lateinit var ticker: String
    override val simpleName: String = "AddPositionActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positions)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        if (intent.hasExtra(TICKER) && intent.getStringExtra(TICKER) != null) {
            ticker = intent.getStringExtra(TICKER)
        } else {
            ticker = ""
            InAppMessage.showToast(this, error_symbol)
            finish()
            return
        }
        tickerName.text = ticker

        addButton.setOnClickListener { onAddClicked() }

        price.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter())
        shares.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter())

        positionsHolder.removeAllViews()
        val position = stocksProvider.getPosition(ticker)
        position?.let {
            for (holding in position.holdings) {
                addPositionView(holding)
            }
        }
        updateTotal()
    }

    private fun onAddClicked() {
        val sharesView = shares
        val priceView = price
        val priceText = priceView.text.toString()
        val sharesText = sharesView.text.toString()
        if (priceText.isNotEmpty() && sharesText.isNotEmpty()) {
            var price = 0f
            var shares = 0f
            var success = true
            try {
                price = priceText.toFloat()
            } catch (e: NumberFormatException) {
                priceInputLayout.error = getString(R.string.invalid_number)
                success = false
            }
            try {
                shares = sharesText.toFloat()
            } catch (e: NumberFormatException) {
                sharesInputLayout.error = getString(R.string.invalid_number)
                success = false
            }
            if (success) {
                priceInputLayout.error = null
                sharesInputLayout.error = null
                val holding = stocksProvider.addHolding(ticker, shares, price)
                priceView.setText("")
                sharesView.setText("")
                addPositionView(holding)
                updateTotal()
                updateActivityResult()
            }
        }
        dismissKeyboard()
    }

    private fun updateActivityResult() {
        val quote = checkNotNull(stocksProvider.getStock(ticker))
        val data = Intent()
        data.putExtra(QUOTE, quote)
        setResult(Activity.RESULT_OK, data)
    }

    private fun addPositionView(holding: Holding) {
        val view = layoutInflater.inflate(R.layout.layout_position_holding, null)
        val positionNumShares = view.findViewById<TextView>(positionShares)
        val positionPrice = view.findViewById<TextView>(positionPrice)
        val positionTotalValue = view.findViewById<TextView>(positionTotalValue)
        positionNumShares.text = AppPreferences.DECIMAL_FORMAT.format(holding.shares)
        positionPrice.text = AppPreferences.DECIMAL_FORMAT.format(holding.price)
        positionTotalValue.text = AppPreferences.DECIMAL_FORMAT.format(holding.totalValue())
        positionsHolder.addView(view)
        view.tag = holding
        view.setOnClickListener {
            stocksProvider.removePosition(ticker, holding)
            positionsHolder.removeView(view)
            updateTotal()
        }
    }

    private fun updateTotal() {
        val quote = checkNotNull(stocksProvider.getStock(ticker))
        totalShares.text = quote.numSharesString()
        averagePrice.text = quote.averagePositionPrice()
        totalValue.text = quote.totalSpentString()
    }
}