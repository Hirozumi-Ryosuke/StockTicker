package com.example.stockticker.ticker

import com.example.stockticker.R
import com.example.stockticker.ticker.base.BaseFragment
import com.example.stockticker.ticker.model.FetchResult
import com.example.stockticker.ticker.model.IStocksProvider
import org.xml.sax.Parser

@RunWith(RobolectricTestRunner::class)
abstract class BaseUnitTest : TestCase() {

    companion object {

        /**
         * Attach a fragment to a new instance of {@link TestActivity.java}
         *
         * @param fragment Fragment to add to the activity.
         */
        fun attachFragmentToTestActivity(fragment: BaseFragment): ActivityController<TestActivity> {
            val controller = Robolectric.buildActivity(TestActivity::class.java).create()
            controller.start()
            controller.resume()
            val activity = controller.get()
            val fm = activity.supportFragmentManager
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit()
            return controller
        }
    }

    private val parser = Parser()

    @Before public override fun setUp() = runBlockingTest {
        super.setUp()
        val iStocksProvider = Mocker.provide(IStocksProvider::class)
        doNothing().whenever(iStocksProvider).schedule()
        whenever(iStocksProvider.fetch()).thenReturn(FetchResult.success(ArrayList()))
        whenever(iStocksProvider.getTickers()).thenReturn(emptyList())
        whenever(iStocksProvider.addStock(any())).thenReturn(emptyList())
        whenever(iStocksProvider.lastFetched()).thenReturn("--")
        whenever(iStocksProvider.nextFetch()).thenReturn("--")
    }

    fun parseJsonFile(fileName: String): JsonElement {
        return parser.parseJsonFile(fileName)
    }

    fun <T> parseJsonFile(type: Type, fileName: String): T {
        return parser.parseJsonFile(type, fileName)
    }
}