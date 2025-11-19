package com.yijing.divination.ui.screen.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 太极图首页
 *
 * @param onNavigateToMethod 导航到占卜方法选择页面
 */
@Composable
fun HomeScreen(
    onNavigateToMethod: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    // 太极旋转：12秒一圈，恒定速度
    val taichiRotation = remember { Animatable(0f) }
    // 八卦旋转：60秒一圈反向，恒定速度
    val baguaRotation = remember { Animatable(0f) }

    // 太极持续旋转动画 - 12秒一圈
    LaunchedEffect(Unit) {
        taichiRotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 12000, easing = LinearEasing)
            )
        )
    }

    // 八卦持续旋转动画 - 60秒一圈反向
    LaunchedEffect(Unit) {
        baguaRotation.animateTo(
            targetValue = -360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 60000, easing = LinearEasing)
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 太极八卦图
        Box(
            modifier = Modifier
                .size(300.dp)
                .clickable(
                    onClick = {
                        scope.launch {
                            // 点击缩放动画
                            scale.animateTo(
                                targetValue = 0.9f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            scale.animateTo(
                                targetValue = 1.1f,
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
                            // 导航到下一页
                            onNavigateToMethod()
                        }
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            TaichiDiagram(
                taichiRotation = taichiRotation.value,
                baguaRotation = baguaRotation.value,
                scale = scale.value
            )
        }
    }
}

/**
 * 绘制太极八卦图
 */
@Composable
private fun TaichiDiagram(
    taichiRotation: Float,
    baguaRotation: Float,
    scale: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = (size.minDimension / 2) * scale

        // 绘制八卦（外圈，反向旋转，60秒一圈）
        rotate(baguaRotation, pivot = Offset(centerX, centerY)) {
            drawBagua(centerX, centerY, radius)
        }

        // 绘制太极（旋转，12秒一圈）
        rotate(taichiRotation, pivot = Offset(centerX, centerY)) {
            drawTaichi(centerX, centerY, radius * 0.43f)
        }
    }
}

/**
 * 绘制太极图
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTaichi(
    centerX: Float,
    centerY: Float,
    radius: Float
) {
    val white = Color(0xFFFFFFFF)
    val black = Color(0xFF1A1A1A)

    // 整个圆的背景（白色半圆 + 黑色半圆）
    // 白色半圆（右半部分）
    drawArc(
        color = white,
        startAngle = -90f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - radius, centerY - radius),
        size = Size(radius * 2, radius * 2)
    )

    // 黑色半圆（左半部分）
    drawArc(
        color = black,
        startAngle = 90f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - radius, centerY - radius),
        size = Size(radius * 2, radius * 2)
    )

    // 上半部小圆（黑色）
    drawCircle(
        color = black,
        radius = radius / 2,
        center = Offset(centerX, centerY - radius / 2)
    )

    // 下半部小圆（白色）
    drawCircle(
        color = white,
        radius = radius / 2,
        center = Offset(centerX, centerY + radius / 2)
    )

    // 鱼眼（上半部白点）
    drawCircle(
        color = white,
        radius = radius / 5,
        center = Offset(centerX, centerY - radius / 2)
    )

    // 鱼眼（下半部黑点）
    drawCircle(
        color = black,
        radius = radius / 5,
        center = Offset(centerX, centerY + radius / 2)
    )
}

/**
 * 绘制八卦
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBagua(
    centerX: Float,
    centerY: Float,
    radius: Float
) {
    val guaColor = Color(0xFF2B2B2B)
    val bagua = listOf(
        listOf(true, false, true),   // 离
        listOf(false, false, false),  // 坤
        listOf(false, true, true),    // 兑
        listOf(true, true, true),     // 乾
        listOf(false, true, false),   // 坎
        listOf(true, false, false),   // 艮
        listOf(false, false, true),   // 震
        listOf(true, true, false)     // 巽
    )

    // 调整八卦大小和位置，使其更明显
    val guaRadius = radius * 0.68f  // 稍微调远一点
    val guaWidth = 48f   // 增加宽度
    val guaHeight = 6f
    val guaSpacing = 3f

    bagua.forEachIndexed { index, yao ->
        val angle = index * 45.0 * PI / 180.0
        val x = centerX + guaRadius * cos(angle).toFloat()
        val y = centerY + guaRadius * sin(angle).toFloat()

        // 绘制三个爻（从上到下）
        yao.forEachIndexed { yaoIndex, isYang ->
            val yaoY = y - guaHeight - guaSpacing + (yaoIndex * (guaHeight + guaSpacing))

            if (isYang) {
                // 阳爻（完整的线）
                drawLine(
                    color = guaColor,
                    start = Offset(x - guaWidth / 2, yaoY),
                    end = Offset(x + guaWidth / 2, yaoY),
                    strokeWidth = guaHeight,
                    cap = StrokeCap.Round
                )
            } else {
                // 阴爻（两段线）
                val gap = guaWidth * 0.2f
                drawLine(
                    color = guaColor,
                    start = Offset(x - guaWidth / 2, yaoY),
                    end = Offset(x - gap / 2, yaoY),
                    strokeWidth = guaHeight,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = guaColor,
                    start = Offset(x + gap / 2, yaoY),
                    end = Offset(x + guaWidth / 2, yaoY),
                    strokeWidth = guaHeight,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}
