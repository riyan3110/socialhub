package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.entities.SocialFeedEntity
import com.example.ui.components.ChatListItem
import com.example.ui.components.SocialBrandBadge
import com.example.ui.viewmodel.SocialViewModel

@Composable
fun InboxScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val smartReplies by viewModel.smartReplies.collectAsState()
    val isSmartRepliesLoading by viewModel.isSmartRepliesLoading.collectAsState()

    var selectedPlatformFilter by remember { mutableStateOf<SocialPlatform?>(null) }
    var activeChatForReply by remember { mutableStateOf<SocialFeedEntity?>(null) }
    var replyInputText by remember { mutableStateOf("") }
    var showSuccessToast by remember { mutableStateOf(false) }

    val filteredMessages = remember(messages, selectedPlatformFilter) {
        if (selectedPlatformFilter == null) messages
        else messages.filter { it.platform.equals(selectedPlatformFilter!!.id, ignoreCase = true) }
    }

    val unreadCount = remember(messages) {
        messages.count { it.isUnread }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 90.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Stats
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kotak Masuk Terpadu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Semua obrolan & DM dari Telegram, WhatsApp, IG, X, dan TikTok",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Unread",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "$unreadCount Pesan Belum Dibaca",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Klik pesan untuk membalas cepat dengan AI Smart Reply",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Platform Filter Row
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedPlatformFilter == null,
                            onClick = { selectedPlatformFilter = null },
                            label = { Text("Semua Chat") },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                    items(SocialPlatform.entries) { platform ->
                        FilterChip(
                            selected = selectedPlatformFilter == platform,
                            onClick = { selectedPlatformFilter = platform },
                            label = { Text(platform.displayName) },
                            modifier = Modifier.padding(end = 6.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Message list
            if (filteredMessages.isEmpty()) {
                item {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp)
                    ) {
                        Text(
                            text = "Tidak ada pesan pada kategori ini.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredMessages, key = { it.id }) { message ->
                    ChatListItem(
                        message = message,
                        onReplyClick = {
                            activeChatForReply = it
                            replyInputText = ""
                            viewModel.markAsRead(it.id)
                            viewModel.generateSmartReplies(it.content, it.authorName)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Quick Reply AI Dialog Modal
        if (activeChatForReply != null) {
            val chat = activeChatForReply!!
            val platform = SocialPlatform.fromId(chat.platform)

            AlertDialog(
                onDismissRequest = { activeChatForReply = null },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Balas ${chat.authorName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            SocialBrandBadge(platform = platform)
                        }
                        IconButton(onClick = { activeChatForReply = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                        }
                    }
                },
                text = {
                    Column {
                        // Original message text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "\"${chat.content}\"",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Gemini AI Smart Reply Suggestions Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Smart Reply AI",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rekomendasi Balasan AI:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (isSmartRepliesLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Column {
                                smartReplies.forEach { suggestion ->
                                    SuggestionChip(
                                        onClick = { replyInputText = suggestion },
                                        label = { Text(suggestion, fontSize = 12.sp) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom reply input field
                        OutlinedTextField(
                            value = replyInputText,
                            onValueChange = { replyInputText = it },
                            placeholder = { Text("Ketik balasan Anda...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (replyInputText.isNotBlank()) {
                                // Simulate sending reply
                                activeChatForReply = null
                                replyInputText = ""
                                showSuccessToast = true
                            }
                        },
                        enabled = replyInputText.isNotBlank(),
                        modifier = Modifier.testTag("send_reply_button")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = "Kirim")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim Balasan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { activeChatForReply = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}
