package com.yijing.divination.ui.screen.result

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yijing.divination.data.model.Hexagram
import com.yijing.divination.ui.components.HexagramView
import com.yijing.divination.ui.theme.YiJingTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 结果展示屏幕
 *
 * @param onNavigateBack 返回上一页
 * @param viewModel ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("占卜结果") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingView(modifier = Modifier.padding(paddingValues))
            }

            uiState.errorMessage != null -> {
                ErrorView(
                    message = uiState.errorMessage!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            uiState.originalHexagram != null -> {
                ResultContent(
                    uiState = uiState,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * 加载视图
 */
@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "加载中...", style = MaterialTheme.typography.bodyLarge)
    }
}

/**
 * 错误视图
 */
@Composable
private fun ErrorView(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 结果内容
 */
@Composable
private fun ResultContent(
    uiState: ResultUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 占卜问题
        uiState.record?.question?.let { question ->
            QuestionCard(question = question)
        }

        // 时间戳
        uiState.record?.let { record ->
            Text(
                text = "占卜时间：${formatTimestamp(record.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 本卦
        HexagramCard(
            title = "本卦",
            hexagram = uiState.originalHexagram!!,
            changingPositions = uiState.changingYaoPositions
        )

        // 变卦（如果有）
        uiState.changedHexagram?.let { changedHexagram ->
            HexagramCard(
                title = "变卦",
                hexagram = changedHexagram,
                changingPositions = emptyList()
            )
        }

        // 卦辞解读
        InterpretationCard(hexagram = uiState.originalHexagram!!)
    }
}

/**
 * 问题卡片
 */
@Composable
private fun QuestionCard(question: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "占卜问题",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * 卦象卡片
 */
@Composable
private fun HexagramCard(
    title: String,
    hexagram: Hexagram,
    changingPositions: List<Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 卦象显示
            HexagramView(
                hexagram = hexagram,
                changingPositions = changingPositions,
                showLabels = true,
                showTrigramDivider = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 卦名和八卦信息
            Text(
                text = "${hexagram.name} (${hexagram.unicode})",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "上${hexagram.upperTrigram}下${hexagram.lowerTrigram}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 卦辞解读卡片
 */
@Composable
private fun InterpretationCard(hexagram: Hexagram) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "卦辞解读",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 卦辞
            if (hexagram.guaCi.isNotBlank()) {
                SectionContent(title = "卦辞", content = hexagram.guaCi)
            }

            // 彖辞
            if (hexagram.tuanCi.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                SectionContent(title = "彖曰", content = hexagram.tuanCi)
            }

            // 象辞
            if (hexagram.xiangCi.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                SectionContent(title = "象曰", content = hexagram.xiangCi)
            }
        }
    }
}

/**
 * 章节内容组件
 */
@Composable
private fun SectionContent(title: String, content: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5f
        )
    }
}

/**
 * 格式化时间戳为北京时间
 */
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA)
    sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Shanghai")
    return sdf.format(Date(timestamp))
}
