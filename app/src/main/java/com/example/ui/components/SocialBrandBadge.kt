package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPlatform

@Composable
fun SocialBrandBadge(
    platform: SocialPlatform,
    modifier: Modifier = Modifier
) {
    val iconVector = when (platform) {
        SocialPlatform.TWITTER_X -> Icons.Default.Tag
        SocialPlatform.REDDIT -> Icons.Default.Forum
        SocialPlatform.DISCORD -> Icons.Default.Chat
        SocialPlatform.TELEGRAM -> Icons.Default.Send
        SocialPlatform.GITHUB -> Icons.Default.Code
        SocialPlatform.LINKEDIN -> Icons.Default.Work
        SocialPlatform.THREADS -> Icons.Default.AlternateEmail
        SocialPlatform.MEDIUM -> Icons.Default.Article
        SocialPlatform.TIKTOK -> Icons.Default.Videocam
        SocialPlatform.WHATSAPP -> Icons.Default.Chat
        SocialPlatform.FACEBOOK -> Icons.Default.ThumbUp
        SocialPlatform.MESSENGER -> Icons.Default.QuestionAnswer
        SocialPlatform.INSTAGRAM -> Icons.Default.CameraAlt
        SocialPlatform.GOOGLE_LOGIN -> Icons.Default.AccountCircle
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(platform.bentoContainerColor)
            .border(
                BorderStroke(1.dp, platform.bentoBorderColor),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = platform.displayName,
            tint = platform.bentoTextColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = platform.shortName,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = platform.bentoTextColor
        )
    }
}
