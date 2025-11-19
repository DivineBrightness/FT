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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 铜钱占卜方法选择屏幕
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
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { },
            contentAlignment = Alignment.Center
        ) {
            // ★★★ 修改 1：尺寸改为 140.dp (原280dp的一半) ★★★
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clickable(
                        onClick = {
                            scope.launch {
                                // 点击弹跳动画
                                scale.animateTo(0.9f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                scale.animateTo(1.1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
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
 * 铜钱绘制视图 - 全动态比例
 */
@Composable
private fun CoinView(scale: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        // 基础半径
        val radius = (size.minDimension / 2) * scale

        // ★★★ 修改 2：定义相对比例系数，确保缩小后描边不会太粗 ★★★
        val borderWidth = radius * 0.09f  // 外框厚度 (约占半径的9%)
        val innerRimWidth = radius * 0.04f // 内环装饰线厚度
        val holeStrokeWidth = radius * 0.035f // 方孔描边厚度

        // 1. 外部悬浮投影 (根据radius动态调整阴影距离)
        drawCircle(
            brush = Brush.radialGradient(
                0.8f to Color.Black.copy(alpha = 0.2f),
                1.0f to Color.Transparent,
                center = Offset(centerX, centerY + (radius * 0.1f)), // 阴影下移量
                radius = radius * 1.2f
            ),
            radius = radius * 1.2f,
            center = Offset(centerX, centerY + (radius * 0.08f))
        )

        // 2. 铜钱主体背景 (古铜色渐变)
        val bodyBrush = Brush.linearGradient(
            colors = listOf(Color(0xFFB87333), Color(0xFFA55D30), Color(0xFF8B4513)),
            start = Offset(centerX - radius, centerY - radius),
            end = Offset(centerX + radius, centerY + radius)
        )
        drawCircle(
            brush = bodyBrush,
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // 3. 质感光泽 (高光与暗部)
        // 左上亮部
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent),
                center = Offset(centerX - radius * 0.4f, centerY - radius * 0.4f),
                radius = radius * 0.8f
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )
        // 右下暗部
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Transparent),
                center = Offset(centerX + radius * 0.4f, centerY + radius * 0.4f),
                radius = radius * 0.8f
            ),
            radius = radius,
            center = Offset(centerX, centerY)
        )

        // 4. 厚重外框 (使用相对比例 borderWidth)
        drawCircle(
            color = Color(0xFFD4AF37),
            radius = radius - borderWidth / 2,
            center = Offset(centerX, centerY),
            style = Stroke(width = borderWidth)
        )

        // 外框微弱内发光
        drawCircle(
            color = Color.Black.copy(alpha = 0.15f),
            radius = radius - borderWidth,
            center = Offset(centerX, centerY),
            style = Stroke(width = radius * 0.015f)
        )

        // 5. 内部装饰圆环
        drawCircle(
            color = Color(0xFFD4AF37),
            radius = radius * 0.75f,
            center = Offset(centerX, centerY),
            style = Stroke(width = innerRimWidth)
        )

        // 6. 方孔 (CSS比例: 70px / 360px ≈ 0.42 of radius)
        val holeWidth = radius * 0.42f
        val holeLeft = centerX - holeWidth / 2
        val holeTop = centerY - holeWidth / 2

        // 方孔深色背景
        drawRect(
            color = Color(0xFF4A2B0E),
            topLeft = Offset(holeLeft, holeTop),
            size = Size(holeWidth, holeWidth)
        )

        // 方孔内阴影 (模拟立体凹陷)
        // 顶部内阴影
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                startY = holeTop,
                endY = holeTop + holeWidth * 0.3f
            ),
            topLeft = Offset(holeLeft, holeTop),
            size = Size(holeWidth, holeWidth * 0.3f)
        )
        // 左侧内阴影
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent),
                startX = holeLeft,
                endX = holeLeft + holeWidth * 0.3f
            ),
            topLeft = Offset(holeLeft, holeTop),
            size = Size(holeWidth * 0.3f, holeWidth)
        )

        // 方孔金色边框 (使用相对比例 holeStrokeWidth)
        drawRect(
            color = Color(0xFFD4AF37),
            topLeft = Offset(holeLeft, holeTop),
            size = Size(holeWidth, holeWidth),
            style = Stroke(width = holeStrokeWidth)
        )
    }
}