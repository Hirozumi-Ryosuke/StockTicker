package com.example.stockticker.ticker.ui

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.example.stockticker.R
import com.example.stockticker.R.dimen.setting_padding
import com.example.stockticker.R.layout.layout_widget_setting
import com.example.stockticker.R.styleable.*
import kotlinx.android.synthetic.main.layout_widget_setting.view.*

class SettingsTextView : LinearLayout {

    var editable: Boolean = false

    constructor(context: Context) : this(context, null)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, attrs, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(
        context, attrs,
        defStyleAttr
    ) {
        orientation = VERTICAL
        val inflater = LayoutInflater.from(context)
        inflater.inflate(layout_widget_setting, this, true)
        val pad = resources.getDimensionPixelSize(setting_padding)
        setPadding(pad, pad, pad, pad)
        attrs?.let {
            val array = context.obtainStyledAttributes(it, SettingsTextView)
            val title = array.getString(SettingsTextView_title_text)
            setTitle(title)
            val subtitle = array.getString(SettingsTextView_subtitle_text)
            setSubtitle(subtitle)
            array.recycle()
        }
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : this(
        context, attrs, defStyleAttr
    )

    fun setTitle(text: CharSequence?) {
        setting_title.text = text
    }

    fun setSubtitle(text: CharSequence?) {
        setting_subtitle.text = text
    }

    fun setIsEditable(
        isEditable: Boolean,
        callback: (s: String) -> Unit = {}
    ) {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (isEditable != editable) {
            editable = isEditable
            if (editable) {
                text_flipper.displayedChild = 1
                setting_edit_text.setText(setting_subtitle.text)
                setting_edit_text.setSelection(setting_edit_text.text.length)
                setting_edit_text.setOnEditorActionListener { v, actionId, _ ->
                    if (actionId == IME_ACTION_DONE) {
                        callback.invoke(v.text.toString())
                        true
                    } else {
                        false
                    }
                }
                setting_edit_text.requestFocus()
                imm.showSoftInput(setting_edit_text, 0)
            } else {
                setting_edit_text.setOnEditorActionListener(null)
                imm.hideSoftInputFromWindow(setting_edit_text.windowToken, 0)
                setting_edit_text.clearFocus()
                text_flipper.displayedChild = 2
            }
        }
    }

    fun setTextColor(color: Int) {
        setting_title.setTextColor(color)
        setting_subtitle.setTextColor(color)
        setting_edit_text.setTextColor(color)
    }
}