package com.example.stockticker.ticker

import android.content.Intent
import android.os.Bundle

abstract class BaseActivityUnitTest<T : androidx.fragment.app.FragmentActivity> @JvmOverloads constructor(
    private val mClass: Class<T>, private val mAutoCreate: Boolean = true) : BaseUnitTest() {

    lateinit protected var mActivity: T
    lateinit protected var mController: ActivityController<T>

    @Before fun beforeTestCreate() {
        mController = Robolectric.buildActivity(mClass)
        if (mAutoCreate) {
            createActivity()
        }
    }

    @After fun afterTestDestroy() {
        mController.pause().stop().destroy()
        Mocker.clearMocks()
    }

    protected fun setIntent(intent: Intent) {
        assertNull("Cannot set intent after starting the activity", mActivity)
        mController.newIntent(intent)
    }

    protected fun createWithStringExtra(key: String, value: String) {
        val intent = Intent(RuntimeEnvironment.application, mClass)
        intent.putExtra(key, value)
        setIntent(intent)
    }

    protected fun createWithExtras(extras: Bundle) {
        val intent = Intent(RuntimeEnvironment.application, mClass)
        intent.putExtras(extras)
        setIntent(intent)
    }

    protected fun createActivity() {
        mActivity = mController.setup().get()
    }

    protected fun findFragment(tag: String): androidx.fragment.app.Fragment {
        return mActivity.supportFragmentManager.findFragmentByTag(tag)!!
    }

    protected fun verifyVisibleFragment(tag: String): androidx.fragment.app.Fragment {
        val fragment = findFragment(tag)
        assertNotNull(fragment)
        assertTrue(fragment.isAdded)
        assertTrue(fragment.isVisible)
        return fragment
    }
}