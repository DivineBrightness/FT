package com.yijing.divination.ui.screen.divination

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yijing.divination.data.model.YaoType
import com.yijing.divination.ui.components.YaoLine
import com.yijing.divination.ui.theme.YiJingTheme

/**
 * 占卜屏幕
 *
 * @param onNavigateToResult 导航到结果页面
 * @param onNavigateBack 返回上一页
 * @param viewModel ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivinationScreen(
    onNavigateToResult: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: DivinationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 当占卜完成时，自动导航到结果页面
    LaunchedEffect(uiState.divinationState) {
        if (uiState.divinationState == DivinationState.COMPLETED && uiState.savedRecordId != null) {
            onNavigateToResult(uiState.savedRecordId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("铜钱占卜") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (uiState.divinationState != DivinationState.IDLE) {
                        IconButton(onClick = { viewModel.reset() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "重新开始")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 问题输入区域
            QuestionInput(
                question = uiState.question,
                onQuestionChange = { viewModel.setQuestion(it) },
                enabled = uiState.divinationState == DivinationState.IDLE
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 投掷进度
            TossProgress(
                currentIndex = uiState.currentTossIndex,
                state = uiState.divinationState
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 显示已投掷的爻
            if (uiState.tossResults.isNotEmpty()) {
                TossResultsDisplay(
                    results = uiState.tossResults,
                    state = uiState.divinationState
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 状态提示
            StateMessage(state = uiState.divinationState, errorMessage = uiState.errorMessage)

            Spacer(modifier = Modifier.height(24.dp))

            // 开始占卜按钮
            StartButton(
                state = uiState.divinationState,
                onClick = { viewModel.startDivination() }
            )
        }
    }
}

/**
 * 问题输入组件
 */
@Composable
private fun QuestionInput(
    question: String,
    onQuestionChange: (String) -> Unit,
    enabled: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "请输入您的问题（可选）",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = question,
            onValueChange = onQuestionChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("例如：事业发展如何？") },
            enabled = enabled,
            maxLines = 3
        )
    }
}

/**
 * 投掷进度显示
 */
@Composable
private fun TossProgress(
    currentIndex: Int,
    state: DivinationState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "投掷进度：$currentIndex / 6",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = currentIndex / 6f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )

        if (state == DivinationState.TOSSING) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "投掷中...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * 投掷结果显示（显示已投掷的爻）
 */
@Composable
private fun TossResultsDisplay(
    results: List<com.yijing.divination.data.model.CoinTossResult>,
    state: DivinationState
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "已投掷的爻",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 从上到下显示（最新的在下方）
        results.forEachIndexed { index, result ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 爻位标签
                    Text(
                        text = getYaoPositionName(index),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.size(width = 48.dp, height = 24.dp),
                        textAlign = TextAlign.End
                    )

                    // 爻线
                    YaoLine(
                        yaoType = result.yaoType,
                        isChanging = result.isChanging,
                        modifier = Modifier.weight(1f)
                    )

                    // 结果说明
                    Text(
                        text = result.getDescription(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.size(width = 80.dp, height = 24.dp)
                    )
                }
            }
        }
    }
}

/**
 * 状态消息显示
 */
@Composable
private fun StateMessage(
    state: DivinationState,
    errorMessage: String?
) {
    when (state) {
        DivinationState.GENERATING -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text(
                    text = "正在生成卦象...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        DivinationState.ERROR -> {
            Text(
                text = errorMessage ?: "发生错误",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }

        else -> {
            // 不显示消息
        }
    }
}

/**
 * 开始占卜按钮
 */
@Composable
private fun StartButton(
    state: DivinationState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = state == DivinationState.IDLE,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = when (state) {
                DivinationState.IDLE -> "开始占卜"
                DivinationState.TOSSING -> "投掷中..."
                DivinationState.GENERATING -> "生成卦象中..."
                DivinationState.COMPLETED -> "已完成"
                DivinationState.ERROR -> "重新开始"
            },
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * 获取爻位名称
 */
private fun getYaoPositionName(index: Int): String {
    return when (index) {
        0 -> "初爻"
        1 -> "二爻"
        2 -> "三爻"
        3 -> "四爻"
        4 -> "五爻"
        5 -> "上爻"
        else -> ""
    }
}

// ==================== 预览 ====================

@Preview(showBackground = true)
@Composable
fun DivinationScreenPreview() {
    YiJingTheme {
        // 预览需要提供假数据
        // 实际使用时会通过 Hilt 注入
    }
}
