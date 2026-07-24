package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPlatform

@Composable
fun BentoGridSummary(
    onPlatformClick: (SocialPlatform) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("bento_grid_summary")
    ) {
        // Tile 1: WhatsApp (Full Width Span)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = SocialPlatform.WHATSAPP.bentoContainerColor
            ),
            border = BorderStroke(1.dp, SocialPlatform.WHATSAPP.bentoBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlatformClick(SocialPlatform.WHATSAPP) }
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "WhatsApp",
                            tint = SocialPlatform.WHATSAPP.bentoTextColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "WHATSAPP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SocialPlatform.WHATSAPP.bentoTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Mama: Sudah sampai mana?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color(0xFF1D1B20)
                )
                Text(
                    text = "24 pesan baru dari 3 obrolan",
                    fontSize = 12.sp,
                    color = Color(0xFF49454F)
                )
            }
        }

        // Row 2: Instagram (Half) + Telegram (Half)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Instagram Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SocialPlatform.INSTAGRAM.bentoContainerColor
                ),
                border = BorderStroke(1.dp, SocialPlatform.INSTAGRAM.bentoBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPlatformClick(SocialPlatform.INSTAGRAM) }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "IG",
                                tint = SocialPlatform.INSTAGRAM.bentoTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "IG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SocialPlatform.INSTAGRAM.bentoTextColor.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "12",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SocialPlatform.INSTAGRAM.bentoTextColor
                    )
                    Text(
                        text = "Menyukai postingan Anda",
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        color = SocialPlatform.INSTAGRAM.bentoTextColor.copy(alpha = 0.85f)
                    )
                }
            }

            // Telegram Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SocialPlatform.TELEGRAM.bentoContainerColor
                ),
                border = BorderStroke(1.dp, SocialPlatform.TELEGRAM.bentoBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPlatformClick(SocialPlatform.TELEGRAM) }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "TG",
                                tint = SocialPlatform.TELEGRAM.bentoTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "TG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SocialPlatform.TELEGRAM.bentoTextColor.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pesan Tersimpan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20)
                    )
                    Text(
                        text = "3 berkas diunggah",
                        fontSize = 11.sp,
                        color = Color(0xFF49454F)
                    )
                }
            }
        }

        // Row 3: Twitter/X (Half) + Facebook (Half)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Twitter / X Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SocialPlatform.TWITTER_X.bentoContainerColor
                ),
                border = BorderStroke(1.dp, SocialPlatform.TWITTER_X.bentoBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPlatformClick(SocialPlatform.TWITTER_X) }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "X",
                                tint = SocialPlatform.TWITTER_X.bentoTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "X",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SocialPlatform.TWITTER_X.bentoTextColor.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "2 sebutan baru dalam utas teknologi",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp,
                        color = SocialPlatform.TWITTER_X.bentoTextColor
                    )
                }
            }

            // Facebook Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SocialPlatform.FACEBOOK.bentoContainerColor
                ),
                border = BorderStroke(1.dp, SocialPlatform.FACEBOOK.bentoBorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPlatformClick(SocialPlatform.FACEBOOK) }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "FB",
                                tint = SocialPlatform.FACEBOOK.bentoTextColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "FB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SocialPlatform.FACEBOOK.bentoTextColor.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Andi memberikan Colekan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp,
                        color = SocialPlatform.FACEBOOK.bentoTextColor
                    )
                }
            }
        }

        // Row 4: TikTok Banner Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = SocialPlatform.TIKTOK.bentoContainerColor
            ),
            border = BorderStroke(1.dp, SocialPlatform.TIKTOK.bentoBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPlatformClick(SocialPlatform.TIKTOK) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "TikTok",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "TikTok Trending",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1D1B20)
                        )
                        Text(
                            text = "#DesignInspiration • Live: @fauzan streaming",
                            fontSize = 11.sp,
                            color = Color(0xFF49454F)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Red
                    )
                }
            }
        }
    }
}
