package com.example.stockticker.ticker.base

import androidx.fragment.app.Fragment
import com.example.stockticker.ticker.analytics.Analytics
import com.example.stockticker.ticker.components.Injector
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    protected val analytics: Analytics
        get() = holder.analytics
    private val holder: InjectionHolder by lazy { InjectionHolder() }

    abstract val simpleName: String

    class InjectionHolder {
        @Inject
        internal lateinit var analytics: Analytics

        init {
            Injector.appComponent.inject(this)
        }
    }
}