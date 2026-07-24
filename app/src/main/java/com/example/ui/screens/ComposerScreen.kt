package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPlatform
import com.example.ui.components.SocialBrandBadge
import com.example.ui.viewmodel.SocialViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ComposerScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val crossPostsHistory by viewModel.crossPosts.collectAsState()

    var captionText by remember { mutableStateOf("") }
    var selectedPlatforms = remember {
        mutableStateListOf(
            SocialPlatform.TELEGRAM,
            SocialPlatform.WHATSAPP,
            SocialPlatform.INSTAGRAM,
            SocialPlatform.TWITTER_X
        )
    }

    var isMediaAttached by remember { mutableStateOf(false) }
    var isScheduled by remember { mutableStateOf(false) }
    var isAiPolishing by remember { mutableStateOf(false) }
    var showSuccessToast by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 90.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Publikasi Multi-Platform",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Tulis sekali dan posting serentak ke Telegram, WhatsApp, FB, IG, X, dan TikTok",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Caption Input Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Isi Konten / Caption",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // AI Polish Button
                            OutlinedButton(
                                onClick = {
                                    if (captionText.isNotBlank()) {
                                        isAiPolishing = true
                                        viewModel.enhanceCaption(captionText) { enhanced ->
                                            captionText = enhanced
                                            isAiPolishing = false
                                        }
                                    }
                                },
                                enabled = captionText.isNotBlank() && !isAiPolishing,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("ai_polish_caption_button")
                            ) {
                                if (isAiPolishing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "AI Polish",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Poles AI", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = captionText,
                            onValueChange = { captionText = it },
                            placeholder = { Text("Ketik pesan, pengumuman, atau caption postingan Anda di sini...") },
                            minLines = 4,
                            maxLines = 8,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("composer_text_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Media Attachment Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isMediaAttached) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { isMediaAttached = !isMediaAttached }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Media",
                                tint = if (isMediaAttached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isMediaAttached) "Gambar Terlampir (img_social_post.png)" else "Lampirkan Gambar / Video",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isMediaAttached) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Platform Selectors
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pilih Platform Tujuan:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SocialPlatform.entries.forEach { platform ->
                            val isChecked = selectedPlatforms.contains(platform)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedPlatforms.remove(platform)
                                        else selectedPlatforms.add(platform)
                                    }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked == true) selectedPlatforms.add(platform)
                                        else selectedPlatforms.remove(platform)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = platform.brandColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                SocialBrandBadge(platform = platform)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Schedule Switch & Publish Button
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = "Jadwalkan",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Jadwalkan Posting",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = if (isScheduled) "Besok jam 09:00 WIB" else "Publikasi langsung sekarang",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = isScheduled,
                                onCheckedChange = { isScheduled = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (captionText.isNotBlank() && selectedPlatforms.isNotEmpty()) {
                                    viewModel.publishCrossPost(
                                        caption = captionText,
                                        mediaUri = if (isMediaAttached) "sample_media" else null,
                                        selectedPlatforms = selectedPlatforms,
                                        isScheduled = isScheduled,
                                        scheduledTime = if (isScheduled) "Besok 09:00" else null,
                                        onComplete = {
                                            captionText = ""
                                            isMediaAttached = false
                                            showSuccessToast = true
                                        }
                                    )
                                }
                            },
                            enabled = captionText.isNotBlank() && selectedPlatforms.isNotEmpty(),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("publish_cross_post_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Publish")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isScheduled) "Jadwalkan Posting Multi-Platform" else "Publikasikan ke ${selectedPlatforms.size} Platform",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Cross Posting History Log Header
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Riwayat Publikasi Terakhir",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (crossPostsHistory.isEmpty()) {
            item {
                Text(
                    text = "Belum ada riwayat publikasi.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            items(crossPostsHistory, key = { it.id }) { post ->
                val formattedTime = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(post.createdAt))
                val targetPlatforms = post.targetPlatformsCsv.split(",").map { SocialPlatform.fromId(it) }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Status: ${post.status} • $formattedTime",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = post.caption,
                            fontSize = 13.sp,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            targetPlatforms.forEach { platform ->
                                SocialBrandBadge(
                                    platform = platform,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
