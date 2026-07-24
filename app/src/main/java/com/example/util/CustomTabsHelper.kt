package com.example.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object CustomTabsHelper {

    fun launchCustomTab(
        context: Context,
        url: String,
        toolbarColor: Color = Color(0xFF1C1B1F)
    ) {
        val parsedUri = try {
            Uri.parse(url)
        } catch (e: Exception) {
            Uri.parse("https://google.com")
        }

        val colorParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(toolbarColor.toArgb())
            .build()

        val customTabsIntent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorParams)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(false)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .build()

        try {
            customTabsIntent.launchUrl(context, parsedUri)
        } catch (e: Exception) {
            // Fallback to default browser intent if custom tabs fail
            val fallbackIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, parsedUri)
            context.startActivity(fallbackIntent)
        }
    }
}
