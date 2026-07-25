package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class SocialPlatform(
    val id: String,
    val displayName: String,
    val shortName: String,
    val handlePrefix: String,
    val brandColor: Color,
    val bentoContainerColor: Color,
    val bentoBorderColor: Color,
    val bentoTextColor: Color,
    val webUrl: String,
    val iconName: String,
    val useCustomTabs: Boolean = false,
    val category: String = "Social",
    val description: String = ""
) {
    // --- Embedded Modern WebView Platforms ---
    TWITTER_X(
        id = "TWITTER_X",
        displayName = "Twitter / X",
        shortName = "X",
        handlePrefix = "@",
        brandColor = Color(0xFF1DA1F2),
        bentoContainerColor = BentoXContainer,
        bentoBorderColor = BentoXBorder,
        bentoTextColor = BentoXText,
        webUrl = "https://x.com",
        iconName = "Tag",
        useCustomTabs = false,
        category = "Social",
        description = "Trending topics, posts, and real-time updates"
    ),
    REDDIT(
        id = "REDDIT",
        displayName = "Reddit",
        shortName = "RD",
        handlePrefix = "u/",
        brandColor = Color(0xFFFF4500),
        bentoContainerColor = BentoRedditContainer,
        bentoBorderColor = BentoRedditBorder,
        bentoTextColor = BentoRedditText,
        webUrl = "https://www.reddit.com",
        iconName = "Forum",
        useCustomTabs = false,
        category = "Social",
        description = "Dive into anything & community discussions"
    ),
    DISCORD(
        id = "DISCORD",
        displayName = "Discord",
        shortName = "DC",
        handlePrefix = "@",
        brandColor = Color(0xFF5865F2),
        bentoContainerColor = BentoDiscordContainer,
        bentoBorderColor = BentoDiscordBorder,
        bentoTextColor = BentoDiscordText,
        webUrl = "https://discord.com/app",
        iconName = "Chat",
        useCustomTabs = false,
        category = "Messaging",
        description = "Talk, chat, hang out, and stay close"
    ),
    TELEGRAM(
        id = "TELEGRAM",
        displayName = "Telegram",
        shortName = "TG",
        handlePrefix = "@",
        brandColor = Color(0xFF229ED9),
        bentoContainerColor = BentoTelegramContainer,
        bentoBorderColor = BentoTelegramBorder,
        bentoTextColor = BentoTelegramText,
        webUrl = "https://web.telegram.org",
        iconName = "Send",
        useCustomTabs = false,
        category = "Messaging",
        description = "Fast and secure cloud messaging web app"
    ),
    GITHUB(
        id = "GITHUB",
        displayName = "GitHub",
        shortName = "GH",
        handlePrefix = "@",
        brandColor = Color(0xFF24292E),
        bentoContainerColor = BentoGitHubContainer,
        bentoBorderColor = BentoGitHubBorder,
        bentoTextColor = BentoGitHubText,
        webUrl = "https://github.com",
        iconName = "Code",
        useCustomTabs = false,
        category = "Developer",
        description = "Code hosting, pull requests, & developer hub"
    ),
    LINKEDIN(
        id = "LINKEDIN",
        displayName = "LinkedIn",
        shortName = "IN",
        handlePrefix = "in/",
        brandColor = Color(0xFF0A66C2),
        bentoContainerColor = BentoLinkedInContainer,
        bentoBorderColor = BentoLinkedInBorder,
        bentoTextColor = BentoLinkedInText,
        webUrl = "https://www.linkedin.com",
        iconName = "Work",
        useCustomTabs = false,
        category = "Professional",
        description = "Professional networking & career opportunities"
    ),
    THREADS(
        id = "THREADS",
        displayName = "Threads",
        shortName = "TH",
        handlePrefix = "@",
        brandColor = Color(0xFF101010),
        bentoContainerColor = BentoThreadsContainer,
        bentoBorderColor = BentoThreadsBorder,
        bentoTextColor = BentoThreadsText,
        webUrl = "https://www.threads.net",
        iconName = "AlternateEmail",
        useCustomTabs = false,
        category = "Social",
        description = "Say more with text & public conversations"
    ),
    MEDIUM(
        id = "MEDIUM",
        displayName = "Medium",
        shortName = "MD",
        handlePrefix = "@",
        brandColor = Color(0xFF000000),
        bentoContainerColor = BentoMediumContainer,
        bentoBorderColor = BentoMediumBorder,
        bentoTextColor = BentoMediumText,
        webUrl = "https://medium.com",
        iconName = "Article",
        useCustomTabs = false,
        category = "Developer",
        description = "Read, write, and discover big ideas"
    ),

    // --- Chrome Custom Tabs Platforms (Automated Smart Routing) ---
    TIKTOK(
        id = "TIKTOK",
        displayName = "TikTok",
        shortName = "TIKTOK",
        handlePrefix = "@",
        brandColor = Color(0xFFFE2C55),
        bentoContainerColor = BentoTikTokContainer,
        bentoBorderColor = BentoTikTokBorder,
        bentoTextColor = BentoTikTokText,
        webUrl = "https://www.tiktok.com",
        iconName = "Videocam",
        useCustomTabs = false,
        category = "Social",
        description = "Short-form video content & trending sound"
    ),
    WHATSAPP(
        id = "WHATSAPP",
        displayName = "WhatsApp",
        shortName = "WA",
        handlePrefix = "+",
        brandColor = Color(0xFF25D366),
        bentoContainerColor = BentoWhatsAppContainer,
        bentoBorderColor = BentoWhatsAppBorder,
        bentoTextColor = BentoWhatsAppText,
        webUrl = "https://web.whatsapp.com",
        iconName = "Chat",
        useCustomTabs = false,
        category = "Messaging",
        description = "Simple, reliable private messaging"
    ),
    FACEBOOK(
        id = "FACEBOOK",
        displayName = "Facebook",
        shortName = "FB",
        handlePrefix = "",
        brandColor = Color(0xFF1877F2),
        bentoContainerColor = BentoFacebookContainer,
        bentoBorderColor = BentoFacebookBorder,
        bentoTextColor = BentoFacebookText,
        webUrl = "https://m.facebook.com",
        iconName = "ThumbUp",
        useCustomTabs = false,
        category = "Social",
        description = "Connect with friends, groups, & marketplace"
    ),
    MESSENGER(
        id = "MESSENGER",
        displayName = "Messenger",
        shortName = "MSG",
        handlePrefix = "@",
        brandColor = Color(0xFF0084FF),
        bentoContainerColor = BentoMessengerContainer,
        bentoBorderColor = BentoMessengerBorder,
        bentoTextColor = BentoMessengerText,
        webUrl = "https://www.messenger.com",
        iconName = "QuestionAnswer",
        useCustomTabs = false,
        category = "Messaging",
        description = "Instant chat and video calling"
    ),
    INSTAGRAM(
        id = "INSTAGRAM",
        displayName = "Instagram",
        shortName = "IG",
        handlePrefix = "@",
        brandColor = Color(0xFFE4405F),
        bentoContainerColor = BentoInstagramContainer,
        bentoBorderColor = BentoInstagramBorder,
        bentoTextColor = BentoInstagramText,
        webUrl = "https://www.instagram.com",
        iconName = "CameraAlt",
        useCustomTabs = false,
        category = "Social",
        description = "Photos, reels, stories, and creator posts"
    ),
    GOOGLE_LOGIN(
        id = "GOOGLE_LOGIN",
        displayName = "Google",
        shortName = "G",
        handlePrefix = "@gmail.com",
        brandColor = Color(0xFFEA4335),
        bentoContainerColor = BentoGoogleContainer,
        bentoBorderColor = BentoGoogleBorder,
        bentoTextColor = BentoGoogleText,
        webUrl = "https://accounts.google.com",
        iconName = "AccountCircle",
        useCustomTabs = false,
        category = "Professional",
        description = "Secure Google Accounts & OAuth portal"
    );

    companion object {
        fun fromId(id: String): SocialPlatform {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: TWITTER_X
        }
    }
}

enum class ContentType(val label: String) {
    ALL("Semua"),
    MESSAGES("Pesan"),
    POSTS("Postingan"),
    STORIES("Story"),
    REELS("Reels / Shorts"),
    NOTIFICATIONS("Notifikasi")
}
