package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPlatform
import com.example.data.entities.SocialAccountEntity

@Composable
fun AccountCard(
    account: SocialAccountEntity,
    onSyncToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val platform = SocialPlatform.fromId(account.platform)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            // Platform & Profile Avatar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(platform.brandColor.copy(alpha = 0.2f))
            ) {
                Text(
                    text = account.accountName.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = platform.brandColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = account.accountName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    SocialBrandBadge(platform = platform)
                }

                Text(
                    text = account.handle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${account.followerCount} • Sinkron: ${account.lastSynced}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (account.syncEnabled) platform.brandColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sync Toggle Switch
            Switch(
                checked = account.syncEnabled,
                onCheckedChange = { onSyncToggle(account.syncEnabled) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = platform.brandColor,
                    checkedTrackColor = platform.brandColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}
