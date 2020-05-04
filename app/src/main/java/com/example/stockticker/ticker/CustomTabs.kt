package com.example.stockticker.ticker

import android.R.anim.fade_in
import android.R.anim.fade_out
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_RESOLVED_FILTER
import android.net.Uri
import android.net.Uri.parse
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import androidx.core.content.ContextCompat
import com.example.stockticker.R
import com.example.stockticker.R.color.color_primary
import com.example.stockticker.R.color.icon_tint
import com.example.stockticker.R.drawable.ic_close
import android.graphics.PorterDuff.Mode.SRC_IN

object CustomTabs {

    private var packageNameToUse: String? = null
    private const val chromePackage = "com.android.chrome"
    private const val firefoxPreviewPackage = "org.mozilla.fenix"
    private const val firefoxPackage = "org.mozilla.firefox"

    fun openTab(
        context: Context,
        url: String
    ) {
        val closeButton = context.getDrawable(ic_close)
        closeButton!!.setTint(context.getColor(icon_tint))
        closeButton.setTintMode(SRC_IN)
        val customTabsIntent = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .setToolbarColor(context.getColor(color_primary))
            .setShowTitle(true)
            .setCloseButtonIcon(closeButton.toBitmap())
            .setExitAnimations(context, fade_in, fade_out)
            .build()
        val packageName = getPackageNameToUse(context, url)
        if (packageName == null) {
            val browserActivityIntent = Intent(ACTION_VIEW, parse(url))
            context.startActivity(browserActivityIntent)
        } else {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(context, parse(url))
        }
    }

    private fun getPackageNameToUse(
        context: Context,
        url: String
    ): String? {
        packageNameToUse?.let {
            return it
        }
        val pm = context.packageManager
        val activityIntent = Intent(ACTION_VIEW, parse(url))
        val defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0)
        var defaultViewHandlerPackageName: String? = null
        defaultViewHandlerInfo?.let {
            defaultViewHandlerPackageName = it.activityInfo.packageName
        }
        val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)
        val packagesSupportingCustomTabs = ArrayList<String>()
        for (info in resolvedActivityList) {
            val serviceIntent = Intent()
            serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION
            serviceIntent.setPackage(info.activityInfo.packageName)

            pm.resolveService(serviceIntent, 0)
                ?.let {
                    packagesSupportingCustomTabs.add(info.activityInfo.packageName)
                }
        }

        when {
            packagesSupportingCustomTabs.isEmpty() -> packageNameToUse = null
            packagesSupportingCustomTabs.size == 1 -> packageNameToUse = packagesSupportingCustomTabs[0]
            !defaultViewHandlerPackageName.isNullOrEmpty()
                    && !hasSpecializedHandlerIntents(context, activityIntent)
                    && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName!!) ->
                packageNameToUse = defaultViewHandlerPackageName
            packagesSupportingCustomTabs.contains(chromePackage) -> packageNameToUse = chromePackage
            packagesSupportingCustomTabs.contains(firefoxPreviewPackage) -> packageNameToUse =
                firefoxPreviewPackage
            packagesSupportingCustomTabs.contains(firefoxPackage) -> packageNameToUse = firefoxPackage
        }
        return packageNameToUse
    }

    private fun hasSpecializedHandlerIntents(
        context: Context,
        intent: Intent
    ): Boolean {
        try {
            val pm = context.packageManager
            val handlers = pm.queryIntentActivities(
                intent,
                GET_RESOLVED_FILTER
            )
            if (handlers.size == 0) {
                return false
            }
            for (resolveInfo in handlers) {
                val filter = resolveInfo.filter ?: continue
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) continue
                if (resolveInfo.activityInfo == null) continue
                return true
            }
        } catch (e: RuntimeException) {
        }
        return false
    }
}