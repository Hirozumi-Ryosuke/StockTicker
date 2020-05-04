package com.example.stockticker.ticker.home

import android.app.Activity
import android.app.AlertDialog.Builder
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.stockticker.R
import com.example.stockticker.ticker.analytics.ClickEvent
import com.example.stockticker.ticker.base.BaseActivity
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.settings.WidgetSettingsFragment
import com.example.stockticker.ticker.showDialog
import com.github.mikephil.charting.BuildConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_paranormal.bottom_navigation
import javax.inject.Inject

/**
 * Created by premnirmal on 2/25/16.
 */
class ParanormalActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    HomeFragment.Parent, SettingsFragment.Parent, WidgetSettingsFragment.Parent {

    companion object {
        private const val DIALOG_SHOWN: String = "DIALOG_SHOWN"
        private val FRAGMENT_MAP =
            mapOf<Int, String>(
                R.id.action_portfolio to HomeFragment::class.java.name,
                R.id.action_widgets to WidgetsFragment::class.java.name,
                R.id.action_search to SearchFragment::class.java.name,
                R.id.action_settings to SettingsFragment::class.java.name)
    }

    @Inject internal lateinit var appPreferences: AppPreferences
    @Inject internal lateinit var widgetDataProvider: WidgetDataProvider

    private var currentChild: ChildFragment? = null
    private var rateDialogShown = false
    override val simpleName: String = "ParanormalActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paranormal)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        savedInstanceState?.let { rateDialogShown = it.getBoolean(DIALOG_SHOWN, false) }

        bottom_navigation.setOnNavigationItemSelectedListener(this)

        currentChild = if (savedInstanceState == null) {
            val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, fragment.javaClass.name)
                .show(fragment)
                .commit()
            fragment
        } else {
            supportFragmentManager.findFragmentById(R.id.fragment_container) as ChildFragment
        }

        val tutorialShown = appPreferences.tutorialShown()
        if (!tutorialShown) {
            showTutorial()
        }

        if (appPreferences.getLastSavedVersionCode() < BuildConfig.VERSION_CODE) {
            showWhatsNew()
        }
    }

    override fun onResume() {
        super.onResume()
        bottom_navigation.menu.findItem(R.id.action_widgets).isEnabled = widgetDataProvider.hasWidget()
    }

    override fun onBackPressed() {
        val eaten = onNavigationItemSelected(bottom_navigation.menu.findItem(R.id.action_portfolio))
        if (eaten) {
            bottom_navigation.selectedItemId = R.id.action_portfolio
        }
        if (!eaten && !maybeAskToRate()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(DIALOG_SHOWN, rateDialogShown)
        super.onSaveInstanceState(outState)
    }

    private fun maybeAskToRate(): Boolean {
        if (!rateDialogShown && appPreferences.shouldPromptRate()) {
            Builder(this).setTitle(R.string.like_our_app).setMessage(R.string.please_rate)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    sendToPlayStore()
                    appPreferences.userDidRate()
                    dialog.dismiss()
                }.setNegativeButton(R.string.later) { dialog, _ ->
                    dialog.dismiss()
                }.create().show()
            rateDialogShown = true
            return true
        }
        return false
    }

    private fun sendToPlayStore() {
        val marketUri: Uri = Uri.parse("market://details?id=$packageName")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
        marketIntent.resolveActivity(packageManager)?.let {
            startActivity(marketIntent)
        }
    }

    // BottomNavigationView.OnNavigationItemSelectedListener

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        var fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_MAP[itemId])
        if (fragment == null) {
            fragment = when (itemId) {
                R.id.action_portfolio -> HomeFragment()
                R.id.action_widgets -> WidgetsFragment()
                R.id.action_search -> SearchFragment()
                R.id.action_settings -> SettingsFragment()
                else -> {
                    throw IllegalStateException("Unknown bottom nav itemId: $itemId - ${item.title}")
                }
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, fragment::class.java.name)
                .hide(fragment)
                .show(currentChild as Fragment)
                .commitNowAllowingStateLoss()
        }
        if (fragment.isHidden) {
            supportFragmentManager.beginTransaction()
                .hide(currentChild as Fragment)
                .show(fragment)
                .commit()
            currentChild = fragment as ChildFragment
            analytics.trackClickEvent(
                ClickEvent("BottomNavClick")
                    .addProperty("NavItem", item.title.toString())
            )
            return true
        }
        return false
    }

    // SettingsFragment.Parent

    override fun showTutorial() {
        showDialog(getString(R.string.how_to_title), getString(R.string.how_to))
        appPreferences.setTutorialShown(true)
    }

    override fun showWhatsNew() {
        appPreferences.saveVersionCode(BuildConfig.VERSION_CODE)
        val stringBuilder = StringBuilder()
        val whatsNew = resources.getStringArray(R.array.whats_new)
        whatsNew.indices.forEach {
            stringBuilder.append("- ")
            stringBuilder.append(whatsNew[it])
            if (it != whatsNew.size - 1) {
                stringBuilder.append("\n")
            }
        }
        showDialog(getString(R.string.whats_new_in, BuildConfig.VERSION_NAME), stringBuilder.toString())
    }

    // WidgetSettingsFragment.Parent

    override fun openSearch(widgetId: Int) {
        bottom_navigation.selectedItemId = R.id.action_search
    }
}