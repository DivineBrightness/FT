package com.yijing.divination.ui.screen.divination

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.yijing.divination.ui.components.HexagramView
import com.yijing.divination.ui.screen.divination.components.CoinTossButton
import com.yijing.divination.ui.screen.divination.components.TossResultItem

/**
 * 占卜界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DivinationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Long) -> Unit,
    viewModel: DivinationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 错误提示
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // TODO: 显示 Snackbar
            viewModel.clearError()
        }
    }

    // 完成后自动保存并跳转
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete && uiState.divinationResult != null && uiState.savedRecordId == null) {
            viewModel.saveRecord()
        }
    }

    LaunchedEffect(uiState.savedRecordId) {
        uiState.savedRecordId?.let { recordId ->
            onNavigateToResult(recordId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("铜钱卜卦") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (uiState.currentStep > 0) {
                        IconButton(onClick = { viewModel.reset() }) {
                            Icon(Icons.Default.Refresh, "重新开始")
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                // 步骤 0：输入问题
                uiState.currentStep == 0 -> {
                    QuestionInput(
                        question = uiState.question,
                        onQuestionChange = viewModel::updateQuestion,
                        onStart = viewModel::performToss
                    )
                }

                // 步骤 1-6：投掷过程
                !uiState.isComplete -> {
                    TossProgress(
                        currentStep = uiState.currentStep,
                        tossResults = uiState.tossResults,
                        isProcessing = uiState.isProcessing,
                        onToss = viewModel::performToss
                    )
                }

                // 完成：显示卦象
                else -> {
                    uiState.divinationResult?.let { result ->
                        ResultPreview(
                            result = result,
                            isLoading = uiState.savedRecordId == null
                        )
                    }
                }
            }
        }
    }
}

/**
 * 问题输入界面
 */
@Composable
private fun QuestionInput(
    question: String,
    onQuestionChange: (String) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "请输入您想要占卜的问题",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = question,
            onValueChange = onQuestionChange,
            label = { Text("问题（可选）") },
            placeholder = { Text("例如：今年事业发展如何？") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "开始占卜",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "占卜前请静心凝神，专注于您的问题",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 投掷进度界面
 */
@Composable
private fun TossProgress(
    currentStep: Int,
    tossResults: List<com.yijing.divination.data.model.CoinTossResult>,
    isProcessing: Boolean,
    onToss: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 进度指示
        Text(
            text = "第 $currentStep 爻 / 共 6 爻",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = currentStep / 6f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 已投掷的结果
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "投掷记录",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 显示已投掷的结果（从下往上）
                tossResults.forEachIndexed { index, result ->
                    TossResultItem(
                        position = index + 1,
                        result = result
                    )
                    if (index < tossResults.size - 1) {
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 投掷按钮
        CoinTossButton(
            enabled = !isProcessing,
            onClick = onToss
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "点击铜钱进行投掷",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 结果预览界面
 */
@Composable
private fun ResultPreview(
    result: com.yijing.divination.data.model.DivinationResult,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("正在保存...")
        } else {
            Text(
                text = "卦象已生成",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 本卦
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "本卦",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${result.originalHexagram.unicode} ${result.originalHexagram.name}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HexagramView(
                        lines = result.originalHexagram.lines,
                        changingPositions = result.changingYaoPositions
                    )
                }
            }

            // 变卦（如果有）
            result.changedHexagram?.let { changedHexagram ->
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "变卦",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${changedHexagram.unicode} ${changedHexagram.name}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HexagramView(lines = changedHexagram.lines)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "正在跳转到结果页面...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
