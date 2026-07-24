package com.example.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SocialPlatform
import com.example.ui.screens.AccountsScreen
import com.example.ui.screens.ComposerScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ThemeManagerScreen
import com.example.ui.screens.WebBrowserScreen
import com.example.ui.viewmodel.SocialViewModel

enum class NavigationTab(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    FEED("feed", "OmniFeed", Icons.Default.DynamicFeed),
    INBOX("inbox", "Pesan", Icons.Default.Chat),
    COMPOSER("composer", "Publikasi", Icons.Default.AddCircle),
    WEB("web", "Web App", Icons.Default.Language),
    ACCOUNTS("accounts", "Akun", Icons.Default.ManageAccounts)
}

enum class ActiveScreen {
    TAB,
    SETTINGS,
    THEME_MANAGER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(NavigationTab.FEED) }
    var activeScreen by remember { mutableStateOf(ActiveScreen.TAB) }
    var initialWebPlatform by remember { mutableStateOf(SocialPlatform.TELEGRAM) }

    val messages by viewModel.messages.collectAsState()
    val unreadCount = remember(messages) { messages.count { it.isUnread } }

    Scaffold(
        topBar = {
            if (activeScreen == ActiveScreen.TAB) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Text(
                                    text = "S",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Social Hub",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "14 platform terhubung • Smart Routing v2",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { activeScreen = ActiveScreen.THEME_MANAGER },
                            modifier = Modifier.testTag("topbar_theme_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme Manager",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { activeScreen = ActiveScreen.SETTINGS },
                            modifier = Modifier.testTag("topbar_settings_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = activeScreen == ActiveScreen.TAB && currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            activeScreen = ActiveScreen.TAB
                            currentTab = tab
                        },
                        icon = {
                            if (tab == NavigationTab.INBOX && unreadCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge {
                                            Text(text = "$unreadCount")
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.route}")
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = activeScreen,
                label = "MainCrossfade"
            ) { screen ->
                when (screen) {
                    ActiveScreen.THEME_MANAGER -> ThemeManagerScreen(
                        viewModel = viewModel,
                        onBack = { activeScreen = ActiveScreen.TAB }
                    )
                    ActiveScreen.SETTINGS -> SettingsScreen(
                        viewModel = viewModel,
                        onNavigateToThemeManager = { activeScreen = ActiveScreen.THEME_MANAGER }
                    )
                    ActiveScreen.TAB -> {
                        when (currentTab) {
                            NavigationTab.FEED -> DashboardScreen(
                                viewModel = viewModel,
                                onOpenWeb = { platform ->
                                    initialWebPlatform = platform
                                    activeScreen = ActiveScreen.TAB
                                    currentTab = NavigationTab.WEB
                                },
                                onNavigateToComposer = {
                                    activeScreen = ActiveScreen.TAB
                                    currentTab = NavigationTab.COMPOSER
                                }
                            )
                            NavigationTab.INBOX -> InboxScreen(
                                viewModel = viewModel
                            )
                            NavigationTab.COMPOSER -> ComposerScreen(
                                viewModel = viewModel
                            )
                            NavigationTab.WEB -> WebBrowserScreen(
                                initialPlatform = initialWebPlatform,
                                onNavigateHome = {
                                    activeScreen = ActiveScreen.TAB
                                    currentTab = NavigationTab.FEED
                                }
                            )
                            NavigationTab.ACCOUNTS -> AccountsScreen(
                                viewModel = viewModel,
                                onNavigateToSettings = { activeScreen = ActiveScreen.SETTINGS },
                                onNavigateToThemeManager = { activeScreen = ActiveScreen.THEME_MANAGER }
                            )
                        }
                    }
                }
            }
        }
    }
}
