package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPlatform
import com.example.ui.viewmodel.SocialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SocialViewModel,
    onOpenWeb: (SocialPlatform) -> Unit,
    onNavigateToComposer: () -> Unit = {},
    onNavigateToAiFeatures: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // List of social media launcher platforms required
    val launcherPlatforms = listOf(
        SocialPlatform.YOUTUBE,
        SocialPlatform.TIKTOK,
        SocialPlatform.FACEBOOK,
        SocialPlatform.TWITTER_X,
        SocialPlatform.INSTAGRAM,
        SocialPlatform.TELEGRAM,
        SocialPlatform.LINKEDIN
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxSize()
    ) {
        // Banner Header Item (Span 2 columns)
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("social_hub_launcher_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = "v2",
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Social Hub Launcher",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Peluncur Web App Sosial Media",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Pilih platform sosial media di bawah ini untuk membuka web app terpadu tanpa aplikasi eksternal.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Section Title (Span 2 columns)
        item(span = { GridItemSpan(2) }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Platform Sosial Media",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Grid Items for YouTube, TikTok, Facebook, X, Instagram, Telegram, LinkedIn
        items(launcherPlatforms, key = { it.id }) { platform ->
            SocialPlatformLauncherCard(
                platform = platform,
                onClick = {
                    viewModel.addRecentVisit(platform.id, platform.displayName, platform.webUrl)
                    onOpenWeb(platform)
                }
            )
        }
    }
}

@Composable
private fun SocialPlatformLauncherCard(
    platform: SocialPlatform,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconVector: ImageVector = when (platform) {
        SocialPlatform.YOUTUBE -> Icons.Default.PlayArrow
        SocialPlatform.TIKTOK -> Icons.Default.Videocam
        SocialPlatform.FACEBOOK -> Icons.Default.ThumbUp
        SocialPlatform.TWITTER_X -> Icons.Default.Tag
        SocialPlatform.INSTAGRAM -> Icons.Default.CameraAlt
        SocialPlatform.TELEGRAM -> Icons.Default.Send
        SocialPlatform.LINKEDIN -> Icons.Default.Work
        else -> Icons.Default.OpenInBrowser
    }

    val displayUrl = when (platform) {
        SocialPlatform.YOUTUBE -> "youtube.com"
        SocialPlatform.TIKTOK -> "tiktok.com"
        SocialPlatform.FACEBOOK -> "facebook.com"
        SocialPlatform.TWITTER_X -> "x.com"
        SocialPlatform.INSTAGRAM -> "instagram.com"
        SocialPlatform.TELEGRAM -> "telegram.org"
        SocialPlatform.LINKEDIN -> "linkedin.com"
        else -> platform.webUrl.removePrefix("https://").removePrefix("www.")
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = platform.bentoContainerColor
        ),
        border = BorderStroke(1.dp, platform.bentoBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("launcher_icon_${platform.id.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(platform.brandColor)
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = platform.displayName,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Buka",
                    tint = platform.bentoTextColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = platform.displayName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = platform.bentoTextColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = displayUrl,
                fontSize = 11.sp,
                color = platform.bentoTextColor.copy(alpha = 0.75f)
            )
        }
    }
}
