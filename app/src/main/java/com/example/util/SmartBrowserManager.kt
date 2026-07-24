package com.example.util

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.data.SocialPlatform

object SmartBrowserManager {

    /**
     * Determines whether a given URL or platform should automatically open in Chrome Custom Tabs
     * instead of the Embedded Modern WebView due to OAuth restrictions, bot protection,
     * or known WebView compatibility issues.
     */
    fun shouldUseCustomTabs(url: String): Boolean {
        val lowerUrl = url.lowercase()

        // Explicit Custom Tabs domains
        if (lowerUrl.contains("tiktok.com") ||
            lowerUrl.contains("whatsapp.com") || lowerUrl.contains("wa.me") ||
            lowerUrl.contains("facebook.com") || lowerUrl.contains("fb.com") ||
            lowerUrl.contains("messenger.com") ||
            lowerUrl.contains("accounts.google.com") ||
            lowerUrl.contains("instagram.com")
        ) {
            return true
        }

        // Known WebView OAuth / Auth blocker endpoints
        if (lowerUrl.contains("appleid.apple.com") ||
            lowerUrl.contains("login.live.com") ||
            lowerUrl.contains("id.twitch.tv") ||
            lowerUrl.contains("/oauth2/authorize") ||
            lowerUrl.contains("accounts.youtube.com")
        ) {
            return true
        }

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
