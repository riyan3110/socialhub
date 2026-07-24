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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(NavigationTab.FEED) }
    var initialWebPlatform by remember { mutableStateOf(SocialPlatform.TELEGRAM) }

    val messages by viewModel.messages.collectAsState()
    val unreadCount = remember(messages) { messages.count { it.isUnread } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(com.example.ui.theme.BentoPillActive)
                        ) {
                            Text(
                                text = "S",
                                fontWeight = FontWeight.ExtraBold,
                                color = com.example.ui.theme.BentoPrimaryAccent,
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
                                text = "6 platform terhubung • Bento Grid",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.example.ui.theme.BentoCanvas
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = com.example.ui.theme.BentoSurface,
                tonalElevation = 0.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
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
                            selectedIconColor = com.example.ui.theme.BentoPrimaryAccent,
                            selectedTextColor = com.example.ui.theme.BentoPrimaryAccent,
                            indicatorColor = com.example.ui.theme.BentoPillActive,
                            unselectedIconColor = com.example.ui.theme.BentoTextSecondary,
                            unselectedTextColor = com.example.ui.theme.BentoTextSecondary
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
                targetState = currentTab,
                label = "MainCrossfade"
            ) { tab ->
                when (tab) {
                    NavigationTab.FEED -> DashboardScreen(
                        viewModel = viewModel,
                        onOpenWeb = { platform ->
                            initialWebPlatform = platform
                            currentTab = NavigationTab.WEB
                        },
                        onNavigateToComposer = {
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
                        initialPlatform = initialWebPlatform
                    )
                    NavigationTab.ACCOUNTS -> AccountsScreen(
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
