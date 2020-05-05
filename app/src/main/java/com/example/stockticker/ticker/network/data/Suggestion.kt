package com.example.stockticker.ticker.network.data

data class Suggestion(
    var symbol: String = ""
) {
    var name: String = ""
    var exch: String = ""
    var type: String = ""
    var exchDisp: String = ""
    var typeDisp: String = ""
    var exists: Boolean = false

    companion object {
        fun fromSuggestionNet(suggestionNet: SuggestionsNet.SuggestionNet): Suggestion {
            val suggestion = Suggestion(suggestionNet.symbol)
            suggestion.name = suggestionNet.name
            suggestion.exch = suggestionNet.exch
            suggestion.type = suggestionNet.type
            suggestion.exchDisp = suggestionNet.exchDisp
            suggestion.typeDisp = suggestionNet.typeDisp
            return suggestion
        }
    }
}