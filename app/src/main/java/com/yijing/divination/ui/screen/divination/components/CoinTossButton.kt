package com.yijing.divination.ui.screen.divination.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * 铜钱投掷按钮
 *
 * 模拟铜钱的外观，点击后有旋转动画
 */
@Composable
fun CoinTossButton(
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }

    // 旋转动画
    val rotation by animateFloatAsState(
        targetValue = if (isAnimating) 360f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { isAnimating = false }
    )

    val coinColor = if (enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    Canvas(
        modifier = modifier
            .size(120.dp)
            .clickable(enabled = enabled) {
                if (!isAnimating) {
                    isAnimating = true
                    onClick()
                }
            }
    ) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)

        // 外圈
        drawCircle(
            color = coinColor,
            radius = radius,
            center = center,
            style = Stroke(width = 4.dp.toPx())
        )

        // 内圈
        drawCircle(
            color = coinColor,
            radius = radius * 0.8f,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // 中心方孔（简化版）
        val holeSize = radius * 0.3f
        drawRect(
            color = coinColor,
            topLeft = Offset(center.x - holeSize / 2, center.y - holeSize / 2),
            size = androidx.compose.ui.geometry.Size(holeSize, holeSize)
        )

        // 装饰线条
        val decorRadius = radius * 0.6f
        repeat(4) { i ->
            val angle = (rotation + i * 90f) * Math.PI / 180f
            val startX = center.x + (decorRadius * 0.4f * kotlin.math.cos(angle)).toFloat()
            val startY = center.y + (decorRadius * 0.4f * kotlin.math.sin(angle)).toFloat()
            val endX = center.x + (decorRadius * kotlin.math.cos(angle)).toFloat()
            val endY = center.y + (decorRadius * kotlin.math.sin(angle)).toFloat()

            drawLine(
                color = coinColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
