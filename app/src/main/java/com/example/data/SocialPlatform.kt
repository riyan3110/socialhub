package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BentoFacebookBorder
import com.example.ui.theme.BentoFacebookContainer
import com.example.ui.theme.BentoFacebookText
import com.example.ui.theme.BentoInstagramBorder
import com.example.ui.theme.BentoInstagramContainer
import com.example.ui.theme.BentoInstagramText
import com.example.ui.theme.BentoTelegramBorder
import com.example.ui.theme.BentoTelegramContainer
import com.example.ui.theme.BentoTelegramText
import com.example.ui.theme.BentoTikTokBorder
import com.example.ui.theme.BentoTikTokContainer
import com.example.ui.theme.BentoTikTokText
import com.example.ui.theme.BentoWhatsAppBorder
import com.example.ui.theme.BentoWhatsAppContainer
import com.example.ui.theme.BentoWhatsAppText
import com.example.ui.theme.BentoXBorder
import com.example.ui.theme.BentoXContainer
import com.example.ui.theme.BentoXText

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
    val iconName: String
) {
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
        iconName = "Send"
    ),
    WHATSAPP(
        id = "WHATSAPP",
        displayName = "WhatsApp",
        shortName = "WA",
        handlePrefix = "+62",
        brandColor = Color(0xFF25D366),
        bentoContainerColor = BentoWhatsAppContainer,
        bentoBorderColor = BentoWhatsAppBorder,
        bentoTextColor = BentoWhatsAppText,
        webUrl = "https://web.whatsapp.com",
        iconName = "Chat"
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
        iconName = "ThumbUp"
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
        iconName = "CameraAlt"
    ),
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
        iconName = "Tag"
    ),
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
        iconName = "Videocam"
    );

    companion object {
        fun fromId(id: String): SocialPlatform {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: TELEGRAM
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
