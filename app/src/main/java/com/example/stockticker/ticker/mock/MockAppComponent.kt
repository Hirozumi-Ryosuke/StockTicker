package com.example.stockticker.ticker.mock

import com.example.stockticker.ticker.TestActivity
import com.example.stockticker.ticker.components.AppComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [MockAppModule::class])
interface MockAppComponent : AppComponent {

    fun inject(activity: TestActivity)
}