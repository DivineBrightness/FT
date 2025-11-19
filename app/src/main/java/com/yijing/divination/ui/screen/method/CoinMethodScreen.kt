package com.yijing.divination.ui.screen.method

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 铜钱占卜方法选择屏幕
 *
 * @param onNavigateToCoinToss 导航到铜钱占卜
 * @param onNavigateToHistory 导航到历史记录
 * @param onNavigateBack 返回上一页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinMethodScreen(
    onNavigateToCoinToss: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 历史记录按钮（家图标）
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.Home, contentDescription = "历史记录")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(Modifier),
            contentAlignment = Alignment.Center
        ) {
            // 铜钱按钮
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .clickable(
                        onClick = {
                            scope.launch {
                                // 点击缩放动画
                                scale.animateTo(
                                    targetValue = 0.92f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                scale.animateTo(
                                    targetValue = 1.05f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                scale.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                                // 导航到占卜页面
                                onNavigateToCoinToss()
                            }
                        },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            ) {
                CoinView(scale = scale.value)
            }
        }
    }
}

/**
 * 铜钱视图
 */
@Composable
private fun CoinView(scale: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = (size.minDimension / 2) * scale

        // 古铜色渐变
        val coinGradient = Brush.radialGradient(
            0.0f to Color(0xFFD4AF37),
            0.4f to Color(0xFFB87333),
            0.6f to Color(0xFFA55D30),
            1.0f to Color(0xFF8B4513),
            center = Offset(centerX, centerY),
            radius = radius
        )

        // 绘制外圆（铜钱主体）
        drawCircle(
            brush = coinGradient,
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // 金色外框
        drawCircle(
            color = Color(0xFFD4AF37),
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 12f * scale)
        )

        // 内部装饰圆环
        drawCircle(
            color = Color(0xFFD4AF37),
            radius = radius * 0.78f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 6f * scale)
        )

        // 方孔
        val holeSize = 70f * scale
        val holeLeft = centerX - holeSize / 2
        val holeTop = centerY - holeSize / 2

        // 方孔背景（深色）
        drawRect(
            color = Color(0xFF4A2B0E),
            topLeft = Offset(holeLeft, holeTop),
            size = androidx.compose.ui.geometry.Size(holeSize, holeSize)
        )

        // 方孔金色边框
        drawRect(
            color = Color(0xFFD4AF37),
            topLeft = Offset(holeLeft, holeTop),
            size = androidx.compose.ui.geometry.Size(holeSize, holeSize),
            style = Stroke(width = 5f * scale)
        )

        // 添加立体感的阴影效果
        drawCircle(
            color = Color(0x40000000),
            radius = radius * 1.02f,
            center = Offset(centerX + 3, centerY + 3),
            alpha = 0.3f
        )
    }
}
