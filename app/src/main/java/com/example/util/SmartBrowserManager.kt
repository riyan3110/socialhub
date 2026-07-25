package com.example.util

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.data.SocialPlatform

object SmartBrowserManager {

    /**
     * Determines whether a given URL should automatically open in Chrome Custom Tabs.
     * Always returns false for standard social media and web pages so they remain inside Social Hub's browser.
     */
    fun shouldUseCustomTabs(url: String): Boolean {
        // Return false so Facebook, Instagram, TikTok, etc. stay in the embedded browser.
        return false
    }

    /**
     * Smart Navigation router: Automatically detects whether to open Chrome Custom Tabs
     * or the embedded WebView, without prompting the user.
     */
    fun navigateToPlatform(
        context: Context,
        platform: SocialPlatform,
        onOpenEmbeddedWebView: (SocialPlatform) -> Unit
    ) {
        if (platform.useCustomTabs || shouldUseCustomTabs(platform.webUrl)) {
            CustomTabsHelper.launchCustomTab(
                context = context,
                url = platform.webUrl,
                toolbarColor = platform.brandColor
            )
        } else {
            onOpenEmbeddedWebView(platform)
        }
    }

    /**
     * Smart Navigation router for arbitrary URLs typed by the user in search/address bar.
     */
    fun navigateToUrl(
        context: Context,
        url: String,
        brandColor: Color = Color(0xFF1C1B1F),
        onOpenEmbeddedWebView: (String) -> Unit
    ) {
        val formattedUrl = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.contains(".") && !url.contains(" ") -> "https://$url"
            else -> "https://www.google.com/search?q=${Uri.encode(url)}"
        }

        if (shouldUseCustomTabs(formattedUrl)) {
            CustomTabsHelper.launchCustomTab(
                context = context,
                url = formattedUrl,
                toolbarColor = brandColor
            )
        } else {
            onOpenEmbeddedWebView(formattedUrl)
        }
    }
}
