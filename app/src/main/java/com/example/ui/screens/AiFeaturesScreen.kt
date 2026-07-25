package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

data class AiToolItem(
    val title: String,
    val description: String,
    val icon: ImageVector
)

class ToolState(
    input: String = "",
    output: String = "",
    isLoading: Boolean = false,
    error: String? = null
) {
    var input by mutableStateOf(input)
    var output by mutableStateOf(output)
    var isLoading by mutableStateOf(isLoading)
    var error by mutableStateOf(error)
    val chatHistory = mutableStateListOf<Map<String, String>>()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiFeaturesScreen(
    viewModel: SocialViewModel,
    onBack: () -> Unit,
    initialToolTitle: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val toolStates = remember { mutableStateMapOf<String, ToolState>() }
    fun getToolState(title: String): ToolState {
        return toolStates.getOrPut(title) { ToolState() }
    }

    val tools = listOf(
        AiToolItem("AI Chat", "General AI assistant for questions and conversations.", Icons.Default.Chat),
        AiToolItem("AI Writer", "Generate captions, posts, articles, and replies.", Icons.Default.Edit),
        AiToolItem("AI Translate", "Translate text between multiple languages.", Icons.Default.Translate),
        AiToolItem("AI Summarizer", "Summarize long text, articles, and conversations.", Icons.Default.Description),
        AiToolItem("AI Grammar", "Correct grammar and improve writing.", Icons.Default.Check),
        AiToolItem("AI Rewrite", "Rewrite text in different styles and tones.", Icons.Default.Refresh),
        AiToolItem("AI Image Prompt", "Generate professional prompts for AI image generators.", Icons.Default.Image),
        AiToolItem("AI Video Prompt", "Generate prompts for AI video generators.", Icons.Default.PlayArrow),
        AiToolItem("AI Hashtag Generator", "Create optimized hashtags for social media.", Icons.Default.List),
        AiToolItem("AI Bio Generator", "Generate social media bios.", Icons.Default.Person),
        AiToolItem("AI Caption Generator", "Generate engaging captions for posts.", Icons.Default.Title),
        AiToolItem("AI Content Ideas", "Generate content ideas for TikTok, Instagram, Facebook, and X.", Icons.Default.Lightbulb)
    )

    var selectedTool by remember {
        mutableStateOf<AiToolItem?>(
            initialToolTitle?.let { title -> tools.find { it.title.equals(title, ignoreCase = true) } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Features",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("ai_features_back")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(tools) { tool ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTool = tool
                        }
                        .testTag("ai_tool_${tool.title.lowercase().replace(" ", "_")}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = tool.title,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = tool.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tool.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }

    if (selectedTool != null) {
        val tool = selectedTool!!
        val state = getToolState(tool.title)

        AlertDialog(
            onDismissRequest = { selectedTool = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = tool.title)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(text = tool.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (tool.title == "AI Chat" && state.chatHistory.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 180.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            for (msg in state.chatHistory) {
                                val isUser = msg["role"] == "user"
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${if (isUser) "Anda" else "AI"}: ${msg["content"]}",
                                        modifier = Modifier.padding(8.dp),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = state.input,
                        onValueChange = { state.input = it },
                        label = { Text(getPlaceholderForTool(tool.title)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_input_field"),
                        enabled = !state.isLoading
                    )

                    if (state.isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Menghubungkan ke 9Router AI Gateway...", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    if (state.error != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.error ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (state.output.isNotBlank() && !state.isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hasil AI (Markdown supported):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = state.output,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (state.input.isNotBlank() && !state.isLoading) {
                            state.isLoading = true
                            state.error = null
                            val currentInput = state.input
                            state.input = ""

                            viewModel.runAiFeature(
                                toolTitle = tool.title,
                                userPrompt = currentInput,
                                chatHistory = if (tool.title == "AI Chat") state.chatHistory.toList() else null,
                                onResult = { result ->
                                    state.isLoading = false
                                    state.output = result
                                    if (tool.title == "AI Chat") {
                                        state.chatHistory.add(mapOf("role" to "user", "content" to currentInput))
                                        state.chatHistory.add(mapOf("role" to "assistant", "content" to result))
                                    }
                                },
                                onError = { errorMsg ->
                                    state.isLoading = false
                                    state.error = errorMsg
                                }
                            )
                        }
                    },
                    enabled = !state.isLoading && state.input.isNotBlank(),
                    modifier = Modifier.testTag("ai_generate_button")
                ) {
                    Text("Buat / Generate")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedTool = null },
                    enabled = !state.isLoading,
                    modifier = Modifier.testTag("ai_close_button")
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}

private fun getPlaceholderForTool(title: String): String {
    return when (title) {
        "AI Chat" -> "Ketik pesan atau pertanyaan..."
        "AI Writer" -> "Masukkan topik atau draft tulisan..."
        "AI Translate" -> "Masukkan teks yang ingin diterjemahkan..."
        "AI Summarizer" -> "Masukkan teks atau artikel panjang..."
        "AI Grammar" -> "Masukkan kalimat yang ingin dikoreksi..."
        "AI Rewrite" -> "Masukkan teks yang ingin ditulis ulang..."
        "AI Image Prompt" -> "Deskripsikan gambar yang diinginkan..."
        "AI Video Prompt" -> "Deskripsikan adegan video cinematic..."
        "AI Hashtag Generator" -> "Masukkan topik untuk hashtag..."
        "AI Bio Generator" -> "Masukkan deskripsi diri..."
        "AI Caption Generator" -> "Masukkan topik postingan..."
        "AI Content Ideas" -> "Masukkan niche / platform..."
        else -> "Masukkan teks atau topik..."
    }
}
