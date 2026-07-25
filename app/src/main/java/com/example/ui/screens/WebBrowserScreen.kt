package com.example.ui.screens

import android.Manifest
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DesktopMac
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.example.data.SocialPlatform
import com.example.util.CustomTabsHelper

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
private const val MOBILE_USER_AGENT =
    "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebBrowserScreen(
    initialPlatform: SocialPlatform = SocialPlatform.TWITTER_X,
    initialUrl: String? = null,
    onNavigateHome: () -> Unit = {},
    webViewsMap: MutableMap<SocialPlatform, WebView> = remember { mutableMapOf() },
    desktopModeMap: MutableMap<SocialPlatform, Boolean> = remember { mutableMapOf() },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var activePlatform by remember(initialPlatform) { mutableStateOf(initialPlatform) }
    var currentUrl by remember(initialPlatform, initialUrl) { mutableStateOf(initialUrl ?: initialPlatform.webUrl) }
    var urlInputText by remember(initialPlatform, initialUrl) { mutableStateOf(currentUrl) }
    var pageTitle by remember(initialPlatform) { mutableStateOf(activePlatform.displayName) }

    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }
    var isDesktopSite by remember { mutableStateOf(desktopModeMap[activePlatform] ?: false) }

    androidx.compose.runtime.LaunchedEffect(activePlatform) {
        val wv = webViewsMap[activePlatform]
        currentUrl = wv?.url ?: (if (activePlatform == initialPlatform && initialUrl != null) initialUrl else activePlatform.webUrl)
        urlInputText = currentUrl
        pageTitle = wv?.title ?: activePlatform.displayName
        canGoBack = wv?.canGoBack() ?: false
        canGoForward = wv?.canGoForward() ?: false
        isDesktopSite = desktopModeMap[activePlatform] ?: false
        webViewRef = wv
    }

    var menuExpanded by remember { mutableStateOf(false) }
    var fileUploadCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    var pendingWebPermissionRequest by remember { mutableStateOf<PermissionRequest?>(null) }

    // Fullscreen video view reference
    var customVideoView by remember { mutableStateOf<View?>(null) }
    var customVideoCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    var isFullScreen by remember { mutableStateOf(false) }

    // Colors for secure/search icon inside composable scope
    val searchIconColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // File Chooser Launcher for HTML5 Uploads
    val fileChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            fileUploadCallback?.onReceiveValue(uris.toTypedArray())
        } else {
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }

    // Camera & Audio Permissions Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantedCamera = permissions[Manifest.permission.CAMERA] ?: false
        val grantedAudio = permissions[Manifest.permission.RECORD_AUDIO] ?: false

        pendingWebPermissionRequest?.let { req ->
            val grantedResources = mutableListOf<String>()
            if (grantedCamera && req.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                grantedResources.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
            }
            if (grantedAudio && req.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                grantedResources.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
            }
            if (grantedResources.isNotEmpty()) {
                req.grant(grantedResources.toTypedArray())
            } else {
                req.deny()
            }
        }
        pendingWebPermissionRequest = null
    }

    // Back Handler logic
    BackHandler(enabled = customVideoView != null || isFullScreen || canGoBack) {
        if (customVideoView != null) {
            customVideoCallback?.onCustomViewHidden()
            customVideoView = null
            customVideoCallback = null
        } else if (isFullScreen) {
            isFullScreen = false
        } else if (canGoBack) {
            webViewRef?.goBack()
        }
    }

    // Fullscreen Video View Overlay
    if (customVideoView != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { _ ->
                    customVideoView!!
                },
                modifier = Modifier.fillMaxSize()
            )
            IconButton(
                onClick = {
                    customVideoCallback?.onCustomViewHidden()
                    customVideoView = null
                    customVideoCallback = null
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup Layar Penuh",
                    tint = Color.White
                )
            }
        }
        return
    }

    Column(
        modifier = if (isFullScreen) Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background) else modifier.fillMaxSize()
    ) {
        // Top Platform Tab Selector Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            items(SocialPlatform.entries) { platform ->
                FilterChip(
                    selected = activePlatform == platform,
                    onClick = {
                        focusManager.clearFocus()
                        activePlatform = platform
                    },
                    label = { Text(platform.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = platform.brandColor,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .testTag("web_platform_tab_${platform.id.lowercase()}")
                )
            }
        }

        // Chrome-like Omnibox & Controls Header
        Surface(
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onNavigateHome,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Beranda",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { isFullScreen = !isFullScreen },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("browser_fullscreen_toggle")
                    ) {
                        Icon(
                            imageVector = if (isFullScreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isFullScreen) "Keluar Layar Penuh" else "Layar Penuh",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Editable URL / Search Address Bar (Occupies all remaining horizontal space)
                    OutlinedTextField(
                        value = urlInputText,
                        onValueChange = { urlInputText = it },
                        singleLine = true,
                        leadingIcon = {
                            val isHttps = currentUrl.startsWith("https://")
                            Icon(
                                imageVector = if (isHttps) Icons.Default.Lock else Icons.Default.Search,
                                contentDescription = "Security",
                                tint = if (isHttps) Color(0xFF2E7D32) else searchIconColor,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            if (isLoading) {
                                IconButton(
                                    onClick = { webViewRef?.stopLoading() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hentikan",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { webViewRef?.reload() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Muat Ulang",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = {
                            focusManager.clearFocus()
                            val formattedUrl = when {
                                urlInputText.startsWith("http://") || urlInputText.startsWith("https://") -> urlInputText
                                urlInputText.contains(".") && !urlInputText.contains(" ") -> "https://$urlInputText"
                                else -> "https://www.google.com/search?q=${Uri.encode(urlInputText)}"
                            }
                            currentUrl = formattedUrl
                            urlInputText = formattedUrl
                            webViewRef?.loadUrl(formattedUrl)
                        }),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = activePlatform.brandColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = onSurfaceColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("address_bar_input")
                    )

                    // Overflow Menu Button
                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu Opsional",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Situs Desktop")
                                        if (isDesktopSite) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Aktif",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DesktopMac,
                                        contentDescription = "Desktop Site"
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    val newDesktop = !isDesktopSite
                                    isDesktopSite = newDesktop
                                    desktopModeMap[activePlatform] = newDesktop

                                    webViewRef?.let { wv ->
                                        wv.settings.userAgentString = if (newDesktop) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                                        wv.settings.useWideViewPort = true
                                        wv.settings.loadWithOverviewMode = true

                                        val url = wv.url ?: currentUrl
                                        if (newDesktop && url.contains("m.facebook.com")) {
                                            val desktopFbUrl = url.replace("m.facebook.com", "www.facebook.com")
                                            currentUrl = desktopFbUrl
                                            urlInputText = desktopFbUrl
                                            wv.loadUrl(desktopFbUrl)
                                        } else if (!newDesktop && url.contains("www.facebook.com")) {
                                            val mobileFbUrl = url.replace("www.facebook.com", "m.facebook.com")
                                            currentUrl = mobileFbUrl
                                            urlInputText = mobileFbUrl
                                            wv.loadUrl(mobileFbUrl)
                                        } else {
                                            wv.reload()
                                        }
                                    }
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Buka di Custom Tab") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.OpenInBrowser,
                                        contentDescription = "Custom Tab"
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    CustomTabsHelper.launchCustomTab(
                                        context = context,
                                        url = currentUrl,
                                        toolbarColor = activePlatform.brandColor
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Buka di Aplikasi Eksternal") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Launch,
                                        contentDescription = "External App"
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Tidak dapat membuka aplikasi eksternal", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Bagikan Tautan") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share"
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, currentUrl)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan Tautan"))
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Salin Tautan") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy"
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", currentUrl))
                                    Toast.makeText(context, "Tautan berhasil disalin", Toast.LENGTH_SHORT).show()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Hapus Cache & Cookie") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Clear Cache"
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    webViewRef?.clearCache(true)
                                    CookieManager.getInstance().removeAllCookies(null)
                                    Toast.makeText(context, "Cache & Cookies telah dibersihkan", Toast.LENGTH_SHORT).show()
                                    webViewRef?.reload()
                                }
                            )
                        }
                    }
                }

                // Sub Navigation Controls (Back, Forward, Refresh, Title)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { webViewRef?.goBack() },
                            enabled = canGoBack,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = if (canGoBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }

                        IconButton(
                            onClick = { webViewRef?.goForward() },
                            enabled = canGoForward,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Maju",
                                tint = if (canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }

                    Text(
                        text = pageTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Loading Progress Indicator
        if (isLoading) {
            LinearProgressIndicator(
                progress = { loadingProgress / 100f },
                color = activePlatform.brandColor,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Pull to Refresh Box wrapping Multi-WebView persistent container
        PullToRefreshBox(
            isRefreshing = isLoading && loadingProgress < 20,
            onRefresh = { webViewRef?.reload() },
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        // Helper function to create fully configured WebView instance
                        fun createWebViewForPlatform(platform: SocialPlatform, initialUrlToLoad: String): WebView {
                            return WebView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                                val isDesktop = desktopModeMap[platform] ?: false

                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    @Suppress("DEPRECATION")
                                    databaseEnabled = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    javaScriptCanOpenWindowsAutomatically = true
                                    setSupportMultipleWindows(true)
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    mediaPlaybackRequiresUserGesture = false
                                    userAgentString = if (isDesktop) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                                }

                                val cookieMgr = CookieManager.getInstance()
                                cookieMgr.setAcceptCookie(true)
                                cookieMgr.setAcceptThirdPartyCookies(this, true)

                                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                                    WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
                                }

                                setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                                    try {
                                        val request = DownloadManager.Request(Uri.parse(url)).apply {
                                            setMimeType(mimetype)
                                            addRequestHeader("User-Agent", userAgent)
                                            addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url) ?: "")
                                            setTitle("SocialHub Download")
                                            setDescription("Mengunduh berkas...")
                                            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                            setDestinationInExternalPublicDir(
                                                Environment.DIRECTORY_DOWNLOADS,
                                                URLUtil.guessFileName(url, contentDisposition, mimetype)
                                            )
                                        }
                                        val dm = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                        dm.enqueue(request)
                                        Toast.makeText(ctx, "Proses unduh dimulai...", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(ctx, "Gagal mengunduh: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                        if (activePlatform == platform) {
                                            isLoading = true
                                            url?.let {
                                                currentUrl = it
                                                urlInputText = it
                                            }
                                            canGoBack = view?.canGoBack() ?: false
                                            canGoForward = view?.canGoForward() ?: false
                                        }
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        if (activePlatform == platform) {
                                            isLoading = false
                                            canGoBack = view?.canGoBack() ?: false
                                            canGoForward = view?.canGoForward() ?: false
                                            pageTitle = view?.title ?: platform.displayName
                                        }
                                        CookieManager.getInstance().flush()
                                    }

                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        val url = request?.url?.toString() ?: return false
                                        val scheme = request?.url?.scheme?.lowercase()

                                        if (scheme != null && scheme != "http" && scheme != "https" && scheme != "about" && scheme != "javascript") {
                                            if (scheme == "intent") {
                                                try {
                                                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                                    if (!fallbackUrl.isNullOrEmpty()) {
                                                        view?.loadUrl(fallbackUrl)
                                                        return true
                                                    }
                                                } catch (_: Exception) {}
                                            }
                                            // Do NOT automatically launch external native apps for fb://, instagram://, tiktok://, etc.
                                            return true
                                        }
                                        return false
                                    }
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        if (activePlatform == platform) {
                                            loadingProgress = newProgress
                                            if (newProgress == 100) isLoading = false
                                        }
                                    }

                                    override fun onReceivedTitle(view: WebView?, title: String?) {
                                        if (activePlatform == platform && title != null) {
                                            pageTitle = title
                                        }
                                    }

                                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                        customVideoView = view
                                        customVideoCallback = callback
                                    }

                                    override fun onHideCustomView() {
                                        customVideoCallback?.onCustomViewHidden()
                                        customVideoView = null
                                        customVideoCallback = null
                                    }

                                    override fun onShowFileChooser(
                                        webView: WebView?,
                                        filePathCallback: ValueCallback<Array<Uri>>?,
                                        fileChooserParams: FileChooserParams?
                                    ): Boolean {
                                        fileUploadCallback?.onReceiveValue(null)
                                        fileUploadCallback = filePathCallback
                                        fileChooserLauncher.launch("*/*")
                                        return true
                                    }

                                    override fun onPermissionRequest(request: PermissionRequest?) {
                                        request?.let { req ->
                                            val neededPermissions = mutableListOf<String>()
                                            if (req.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                                                neededPermissions.add(Manifest.permission.CAMERA)
                                            }
                                            if (req.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                                                neededPermissions.add(Manifest.permission.RECORD_AUDIO)
                                            }

                                            if (neededPermissions.isNotEmpty()) {
                                                pendingWebPermissionRequest = req
                                                permissionLauncher.launch(neededPermissions.toTypedArray())
                                            } else {
                                                req.grant(req.resources)
                                            }
                                        }
                                    }

                                    override fun onGeolocationPermissionsShowPrompt(
                                        origin: String?,
                                        callback: GeolocationPermissions.Callback?
                                    ) {
                                        callback?.invoke(origin, true, false)
                                    }

                                    override fun onCreateWindow(
                                        view: WebView?,
                                        isDialog: Boolean,
                                        isUserGesture: Boolean,
                                        resultMsg: android.os.Message?
                                    ): Boolean {
                                        val newWebView = WebView(ctx)
                                        newWebView.settings.javaScriptEnabled = true
                                        newWebView.webViewClient = object : WebViewClient() {
                                            override fun shouldOverrideUrlLoading(v: WebView?, req: WebResourceRequest?): Boolean {
                                                req?.url?.toString()?.let { popupUrl ->
                                                    view?.loadUrl(popupUrl)
                                                }
                                                return true
                                            }
                                        }
                                        val transport = resultMsg?.obj as? WebView.WebViewTransport
                                        transport?.webView = newWebView
                                        resultMsg?.sendToTarget()
                                        return true
                                    }
                                }

                                loadUrl(initialUrlToLoad)
                            }
                        }

                        val existingWV = webViewsMap[activePlatform]
                        val initialWV = if (existingWV != null) {
                            (existingWV.parent as? ViewGroup)?.removeView(existingWV)
                            existingWV
                        } else {
                            val newWV = createWebViewForPlatform(activePlatform, currentUrl)
                            webViewsMap[activePlatform] = newWV
                            newWV
                        }
                        addView(initialWV)
                        webViewRef = initialWV
                    }
                },
                update = { container ->
                    // Hide all inactive WebViews
                    for ((p, wv) in webViewsMap) {
                        wv.visibility = if (p == activePlatform) View.VISIBLE else View.GONE
                    }

                    // Retrieve or create WebView for activePlatform
                    var activeWV = webViewsMap[activePlatform]
                    if (activeWV == null) {
                        val targetUrl = if (activePlatform == initialPlatform && initialUrl != null) initialUrl else activePlatform.webUrl
                        activeWV = WebView(container.context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)

                            val isDesktop = desktopModeMap[activePlatform] ?: false

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                @Suppress("DEPRECATION")
                                databaseEnabled = true
                                allowFileAccess = true
                                allowContentAccess = true
                                javaScriptCanOpenWindowsAutomatically = true
                                setSupportMultipleWindows(true)
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                mediaPlaybackRequiresUserGesture = false
                                userAgentString = if (isDesktop) DESKTOP_USER_AGENT else MOBILE_USER_AGENT
                            }

                            val cookieMgr = CookieManager.getInstance()
                            cookieMgr.setAcceptCookie(true)
                            cookieMgr.setAcceptThirdPartyCookies(this, true)

                            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                                WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_AUTO)
                            }

                            setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                                try {
                                    val request = DownloadManager.Request(Uri.parse(url)).apply {
                                        setMimeType(mimetype)
                                        addRequestHeader("User-Agent", userAgent)
                                        addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url) ?: "")
                                        setTitle("SocialHub Download")
                                        setDescription("Mengunduh berkas...")
                                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                        setDestinationInExternalPublicDir(
                                            Environment.DIRECTORY_DOWNLOADS,
                                            URLUtil.guessFileName(url, contentDisposition, mimetype)
                                        )
                                    }
                                    val dm = container.context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                    dm.enqueue(request)
                                    Toast.makeText(container.context, "Proses unduh dimulai...", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(container.context, "Gagal mengunduh: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    if (activePlatform == activePlatform) {
                                        isLoading = true
                                        url?.let {
                                            currentUrl = it
                                            urlInputText = it
                                        }
                                        canGoBack = view?.canGoBack() ?: false
                                        canGoForward = view?.canGoForward() ?: false
                                    }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    if (activePlatform == activePlatform) {
                                        isLoading = false
                                        canGoBack = view?.canGoBack() ?: false
                                        canGoForward = view?.canGoForward() ?: false
                                        pageTitle = view?.title ?: activePlatform.displayName
                                    }
                                    CookieManager.getInstance().flush()
                                }

                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    val scheme = request?.url?.scheme?.lowercase()

                                    if (scheme != null && scheme != "http" && scheme != "https" && scheme != "about" && scheme != "javascript") {
                                        if (scheme == "intent") {
                                            try {
                                                val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                                if (!fallbackUrl.isNullOrEmpty()) {
                                                    view?.loadUrl(fallbackUrl)
                                                    return true
                                                }
                                            } catch (_: Exception) {}
                                        }
                                        return true
                                    }
                                    return false
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    loadingProgress = newProgress
                                    if (newProgress == 100) isLoading = false
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    if (title != null) pageTitle = title
                                }

                                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                    customVideoView = view
                                    customVideoCallback = callback
                                }

                                override fun onHideCustomView() {
                                    customVideoCallback?.onCustomViewHidden()
                                    customVideoView = null
                                    customVideoCallback = null
                                }

                                override fun onShowFileChooser(
                                    webView: WebView?,
                                    filePathCallback: ValueCallback<Array<Uri>>?,
                                    fileChooserParams: FileChooserParams?
                                ): Boolean {
                                    fileUploadCallback?.onReceiveValue(null)
                                    fileUploadCallback = filePathCallback
                                    fileChooserLauncher.launch("*/*")
                                    return true
                                }

                                override fun onPermissionRequest(request: PermissionRequest?) {
                                    request?.let { req ->
                                        val neededPermissions = mutableListOf<String>()
                                        if (req.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                                            neededPermissions.add(Manifest.permission.CAMERA)
                                        }
                                        if (req.resources.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                                            neededPermissions.add(Manifest.permission.RECORD_AUDIO)
                                        }

                                        if (neededPermissions.isNotEmpty()) {
                                            pendingWebPermissionRequest = req
                                            permissionLauncher.launch(neededPermissions.toTypedArray())
                                        } else {
                                            req.grant(req.resources)
                                        }
                                    }
                                }

                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: GeolocationPermissions.Callback?
                                ) {
                                    callback?.invoke(origin, true, false)
                                }

                                override fun onCreateWindow(
                                    view: WebView?,
                                    isDialog: Boolean,
                                    isUserGesture: Boolean,
                                    resultMsg: android.os.Message?
                                ): Boolean {
                                    val newWebView = WebView(container.context)
                                    newWebView.settings.javaScriptEnabled = true
                                    newWebView.webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(v: WebView?, req: WebResourceRequest?): Boolean {
                                            req?.url?.toString()?.let { popupUrl ->
                                                view?.loadUrl(popupUrl)
                                            }
                                            return true
                                        }
                                    }
                                    val transport = resultMsg?.obj as? WebView.WebViewTransport
                                    transport?.webView = newWebView
                                    resultMsg?.sendToTarget()
                                    return true
                                }
                            }

                            loadUrl(targetUrl)
                        }
                        webViewsMap[activePlatform] = activeWV
                        container.addView(activeWV)
                    }

                    activeWV.visibility = View.VISIBLE
                    webViewRef = activeWV

                    // Sync top bar state with active WebView
                    val wvUrl = activeWV.url ?: activePlatform.webUrl
                    currentUrl = wvUrl
                    urlInputText = wvUrl
                    canGoBack = activeWV.canGoBack()
                    canGoForward = activeWV.canGoForward()
                    pageTitle = activeWV.title ?: activePlatform.displayName
                    isDesktopSite = desktopModeMap[activePlatform] ?: false
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("embedded_modern_web_view")
            )
        }
    }

    // Detach WebViews when WebBrowserScreen is disposed to preserve session and scroll position in webViewsMap
    DisposableEffect(Unit) {
        onDispose {
            webViewsMap.values.forEach { webView ->
                (webView.parent as? ViewGroup)?.removeView(webView)
            }
            webViewRef = null
        }
    }
}

// Utility extension for guessing file name from headers
private object URLUtil {
    fun guessFileName(url: String, contentDisposition: String?, mimeType: String?): String {
        return android.webkit.URLUtil.guessFileName(url, contentDisposition, mimeType)
    }
}
