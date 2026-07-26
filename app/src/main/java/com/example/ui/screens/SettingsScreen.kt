package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.SocialViewModel

@Composable
fun SettingsScreen(
  viewModel: SocialViewModel,
  onNavigateToThemeManager: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val userSettings by viewModel.userSettings.collectAsState()

  var showLanguageDialog by remember { mutableStateOf(false) }
  var showBrowserModeDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showClearStorageDialog by remember { mutableStateOf(false) }

  var aboutTapCount by remember { mutableStateOf(0) }
  var lastTapTime by remember { mutableStateOf(0L) }

  // --- AI Gateway states ---
  var gwUrl by remember { mutableStateOf(userSettings.gatewayUrl) }
  var gwToken by remember { mutableStateOf(userSettings.gatewayToken) }
  var gwModel by remember { mutableStateOf(if (userSettings.gatewayModel.isNotBlank()) userSettings.gatewayModel else "My2") }
  var testConnStatus by remember { mutableStateOf<String?>(null) }
  var isTesting by remember { mutableStateOf(false) }

  LazyColumn(
    contentPadding = PaddingValues(bottom = 100.dp),
    modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    // Header
    item {
      Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
          text = "Pengaturan Aplikasi",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 24.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Sesuaikan tema, notifikasi, browser, AI, dan privasi aplikasi",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // 1. Theme Manager
    item {
      SettingsCategoryCard(
        title = "Tampilan & Tema",
        subtitle = "Ubah tema, mode gelap, dan warna aksen secara realtime",
        icon = Icons.Default.Palette,
        onClick = onNavigateToThemeManager,
        badgeText = userSettings.accentColor.displayName,
        tag = "setting_item_theme"
      )
    }

    // 2. Notification
    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Notifikasi & Peringatan",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Atur preferensi pesan dan pemberitahuan lintas platform",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          SettingsSwitchRow(
            title = "Aktifkan Notifikasi",
            subtitle = "Izinkan aplikasi menampilkan pemberitahuan",
            checked = userSettings.notificationsEnabled,
            onCheckedChange = { viewModel.updateNotificationsEnabled(it) }
          )

          SettingsSwitchRow(
            title = "Peringatan Pesan Masuk",
            subtitle = "Tampilkan ringkasan push alert saat ada pesan baru",
            checked = userSettings.pushAlertsEnabled,
            onCheckedChange = { viewModel.updatePushAlerts(it) }
          )

          SettingsSwitchRow(
            title = "Suara & Getar",
            subtitle = "Mainkan efek suara notifikasi",
            checked = userSettings.soundEnabled,
            onCheckedChange = { viewModel.updateSoundEnabled(it) }
          )
        }
      }
    }

    // 3. Browser Settings
    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
              Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Browser & Perayapan Web",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Routing cerdas Embedded WebView & Custom Tabs",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          SettingsClickRow(
            title = "Mode Routing Utama",
            subtitle = when (userSettings.browserMode) {
              "SMART" -> "Otomatis (Embedded Web & Custom Tabs)"
              "EMBEDDED" -> "Selalu Pakai Embedded WebView"
              else -> "Selalu Pakai Chrome Custom Tabs"
            },
            onClick = { showBrowserModeDialog = true }
          )

          SettingsSwitchRow(
            title = "Blokir Iklan & Pelacak",
            subtitle = "Percepat pemuatan halaman web",
            checked = userSettings.blockAds,
            onCheckedChange = { viewModel.updateBlockAds(it) }
          )

          SettingsSwitchRow(
            title = "Bersihkan Cache Saat Keluar",
            subtitle = "Menjaga privasi sesi selancar Anda",
            checked = userSettings.clearCacheOnExit,
            onCheckedChange = { viewModel.updateClearCacheExit(it) }
          )
        }
      }
    }

    // 4. AI Gateway Settings (OPEN - tidak hidden)
    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "AI Gateway Settings",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Konfigurasi gateway OpenAI-compatible (9Router)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          OutlinedTextField(
            value = gwUrl,
            onValueChange = { gwUrl = it },
            label = { Text("Gateway URL Base") },
            placeholder = { Text("http://43.159.50.231:20128/v1") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = gwToken,
            onValueChange = { gwToken = it },
            label = { Text("API Key (Bearer Token)") },
            placeholder = { Text("sk-xxxxx...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = gwModel,
            onValueChange = { gwModel = it },
            label = { Text("Model") },
            placeholder = { Text("My2") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Button(
              onClick = {
                isTesting = true
                testConnStatus = null
                if (gwUrl.trim().isBlank() || gwToken.trim().isBlank() || gwModel.trim().isBlank()) {
                  isTesting = false
                  return@Button
                }
                viewModel.testGatewayConnection(gwUrl.trim(), gwToken.trim(), gwModel.trim()) { success, message ->
                  isTesting = false
                  testConnStatus = if (success) "✅ $message" else "❌ $message"
                  if (success) {
                    Toast.makeText(context, "Connection Success!", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              enabled = !isTesting && gwUrl.isNotBlank() && gwToken.isNotBlank() && gwModel.isNotBlank()
            ) {
              if (isTesting) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Testing...")
              } else {
                Text("Test Connection")
              }
            }

            if (testConnStatus != null) {
              Text(
                text = testConnStatus ?: "",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (testConnStatus!!.startsWith("✅")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Catatan: Endpoint otomatis menjadi /v1/chat/completions",
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Button(
            onClick = {
              viewModel.updateGatewayConfig(gwUrl.trim(), gwToken.trim(), gwModel.trim())
              Toast.makeText(context, "Gateway Configuration Saved!", Toast.LENGTH_SHORT).show()
            },
            enabled = gwUrl.isNotBlank() && gwToken.isNotBlank() && gwModel.isNotBlank()
          ) {
            Text("Save Configuration")
          }
        }
      }
    }

    // 5. Language
    item {
      SettingsCategoryCard(
        title = "Bahasa (Language)",
        subtitle = if (userSettings.appLanguage == "id") "Bahasa Indonesia" else "English (US)",
        icon = Icons.Default.Language,
        onClick = { showLanguageDialog = true },
        badgeText = if (userSettings.appLanguage == "id") "ID" else "EN",
        tag = "setting_item_language"
      )
    }

    // 6. Storage
    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
              Icon(
                imageVector = Icons.Default.SdStorage,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Penyimpanan & Cache",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Kelola memori lokal dan data Room database",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
          ) {
            Column {
              Text(
                text = "Penggunaan Cache Web & Database",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Total terpakai: ~14.2 MB",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            OutlinedButton(
              onClick = { showClearStorageDialog = true }
            ) {
              Icon(
                imageVector = Icons.Default.CleaningServices,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Bersihkan", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // 7. Privacy & Security
    item {
      Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Privasi & Keamanan",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Data tersimpan lokal secara aman di Room SQLite",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          SettingsSwitchRow(
            title = "Perlindungan Penjelajahan Aman",
            subtitle = "Deteksi otomatis situs phishing dan malware",
            checked = userSettings.secureBrowsing,
            onCheckedChange = { viewModel.updateSecureBrowsing(it) }
          )

          SettingsSwitchRow(
            title = "Telemetri & Laporan Analitik Local",
            subtitle = "Simpan statistik riwayat penggunaan hanya di HP",
            checked = userSettings.analyticsEnabled,
            onCheckedChange = { viewModel.updateAnalyticsEnabled(it) }
          )
        }
      }
    }

    // 8. About
    item {
      SettingsCategoryCard(
        title = "Tentang Social Hub V2",
        subtitle = "Versi 2.0.0 • Smart Routing & Multi-Platform Hub",
        icon = Icons.Default.Info,
        onClick = { showAboutDialog = true },
        badgeText = "v2.0",
        tag = "setting_item_about"
      )
    }
  }

  // --- Dialogs ---

  // Language Dialog
  if (showLanguageDialog) {
    AlertDialog(
      onDismissRequest = { showLanguageDialog = false },
      title = { Text("Pilih Bahasa") },
      text = {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable {
              viewModel.updateLanguage("id")
              showLanguageDialog = false
            }.padding(vertical = 8.dp)
          ) {
            RadioButton(
              selected = userSettings.appLanguage == "id",
              onClick = {
                viewModel.updateLanguage("id")
                showLanguageDialog = false
              }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Bahasa Indonesia")
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable {
              viewModel.updateLanguage("en")
              showLanguageDialog = false
            }.padding(vertical = 8.dp)
          ) {
            RadioButton(
              selected = userSettings.appLanguage == "en",
              onClick = {
                viewModel.updateLanguage("en")
                showLanguageDialog = false
              }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("English (US)")
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showLanguageDialog = false }) {
          Text("Batal")
        }
      }
    )
  }

  // Browser Mode Dialog
  if (showBrowserModeDialog) {
    AlertDialog(
      onDismissRequest = { showBrowserModeDialog = false },
      title = { Text("Mode Routing Browser") },
      text = {
        Column {
          val modes = listOf(
            "SMART" to "Otomatis (Recommended)",
            "EMBEDDED" to "Selalu Embedded WebView",
            "CUSTOM_TABS" to "Selalu Chrome Custom Tabs"
          )

          modes.forEach { (key, label) ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth().clickable {
                viewModel.updateBrowserMode(key)
                showBrowserModeDialog = false
              }.padding(vertical = 8.dp)
            ) {
              RadioButton(
                selected = userSettings.browserMode == key,
                onClick = {
                  viewModel.updateBrowserMode(key)
                  showBrowserModeDialog = false
                }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(label)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showBrowserModeDialog = false }) {
          Text("Tutup")
        }
      }
    )
  }

  // Clear Storage Confirmation
  if (showClearStorageDialog) {
    AlertDialog(
      onDismissRequest = { showClearStorageDialog = false },
      title = { Text("Bersihkan Cache") },
      text = { Text("Apakah Anda yakin ingin membersihkan cache temporer web dan riwayat pencarian?") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearRecentVisits()
            Toast.makeText(context, "Cache & Riwayat berhasil dibersihkan", Toast.LENGTH_SHORT).show()
            showClearStorageDialog = false
          }
        ) {
          Text("Bersihkan")
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearStorageDialog = false }) {
          Text("Batal")
        }
      }
    )
  }

  // About Dialog
  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("Social Hub V2") },
      text = {
        Column {
          Text(
            text = "Social Hub V2 adalah platform manajemen media sosial modern lintas 14 platform.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Fitur Utama:\n• Smart Routing (Embedded WebView & Custom Tabs)\n• Theme Manager & Dynamic Color\n• Multi-Platform Feed & Cross Publishing\n• 9Router AI Gateway Integration",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(onClick = { showAboutDialog = false }) {
          Text("Tutup")
        }
      }
    )
  }
}

@Composable
fun SettingsCategoryCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  onClick: () -> Unit,
  badgeText: String? = null,
  tag: String = ""
) {
  Card(
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxSize()
      .padding(bottom = 12.dp)
      .clickable { onClick() }
      .testTag(tag)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(22.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = subtitle,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        badgeText?.let {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = it,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
fun SettingsSwitchRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.primary,
        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
      )
    )
  }
}

@Composable
fun SettingsClickRow(
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(vertical = 10.dp)
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForward,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(16.dp)
    )
  }
}
