package com.yijing.divination.ui.screen.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    onNavigateToDivination: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    // 太极旋转：12秒一圈，恒定速度
    val taichiRotation = remember { Animatable(0f) }
    // 八卦旋转：60秒一圈反向，恒定速度
    val baguaRotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        taichiRotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 12000, easing = LinearEasing)
            )
        )
    }

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
        // 1. 太极八卦图（居中）
        Box(
            modifier = Modifier
                .size(300.dp)
                .clickable(
                    onClick = {
                        scope.launch {
                            scale.animateTo(0.9f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            scale.animateTo(1.1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
                            onNavigateToDivination()
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

        // 2. 竖排文字：上联在右，下联在左
        // 右侧文字：人能常清净
        VerticalText(
            text = "人能常清净",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp) // 距离右边距
        )

        // 左侧文字：天地悉皆归
        VerticalText(
            text = "天地悉皆归",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp) // 距离左边距
        )
    }
}

/**
 * 竖排文字组件
 */
@Composable
fun VerticalText(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp) // 字间距
    ) {
        text.forEach { char ->
            Text(
                text = char.toString(),
                style = TextStyle(
                    fontSize = 22.sp,
                    color = Color(0xFF333333), // 深灰色，很有质感
                    fontFamily = FontFamily.Serif, // 使用衬线体（类似宋体）
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 4.sp
                )
            )
        }
    }
}

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

        // 绘制八卦（外圈）
        rotate(baguaRotation, pivot = Offset(centerX, centerY)) {
            drawBagua(centerX, centerY, radius)
        }

        // 绘制太极（内圈）
        rotate(taichiRotation, pivot = Offset(centerX, centerY)) {
            drawTaichi3D(centerX, centerY, radius * 0.43f) // 使用 3D 绘制方法
        }
    }
}

/**
 * 绘制立体质感太极（还原 CSS 的 box-shadow 和 gradient）
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTaichi3D(
    centerX: Float,
    centerY: Float,
    radius: Float
) {
    val white = Color(0xFFFFFFFF)
    val black = Color(0xFF1A1A1A)

    // 1. 基础太极形状
    // 右半白
    drawArc(
        color = white,
        startAngle = -90f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - radius, centerY - radius),
        size = Size(radius * 2, radius * 2)
    )
    // 左半黑
    drawArc(
        color = black,
        startAngle = 90f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(centerX - radius, centerY - radius),
        size = Size(radius * 2, radius * 2)
    )
    // 上小黑圆
    drawCircle(
        color = black,
        radius = radius / 2,
        center = Offset(centerX, centerY - radius / 2)
    )
    // 下小白圆
    drawCircle(
        color = white,
        radius = radius / 2,
        center = Offset(centerX, centerY + radius / 2)
    )

    // 2. 鱼眼
    drawCircle(
        color = white,
        radius = radius / 5,
        center = Offset(centerX, centerY - radius / 2)
    )
    drawCircle(
        color = black,
        radius = radius / 5,
        center = Offset(centerX, centerY + radius / 2)
    )

    // 3. 添加玉石质感的高光和阴影 (模拟 CSS: inset 4px 4px 10px rgba(255,255,255,0.5))
    // 这里使用一个径向渐变覆盖在上面，中心点稍微偏左上，营造立体球感
    val sphereHighlight = Brush.radialGradient(
        0.0f to Color.White.copy(alpha = 0.15f),
        0.6f to Color.Transparent,
        center = Offset(centerX - radius * 0.5f, centerY - radius * 0.5f),
        radius = radius * 1.2f
    )
    drawCircle(
        brush = sphereHighlight,
        radius = radius,
        center = Offset(centerX, centerY)
    )

    // 边缘微弱内阴影
    drawCircle(
        color = Color.Black.copy(alpha = 0.1f),
        radius = radius,
        center = Offset(centerX, centerY),
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBagua(
    centerX: Float,
    centerY: Float,
    radius: Float
) {
    val guaColor = Color(0xFF2B2B2B)
    // 八卦数据 (从上到下)
    val bagua = listOf(
        listOf(true, false, true),   // 离 (Top, 0deg in CSS logic)
        listOf(false, false, false), // 坤
        listOf(false, true, true),   // 兑
        listOf(true, true, true),    // 乾
        listOf(false, true, false),  // 坎
        listOf(true, false, false),  // 艮
        listOf(false, false, true),  // 震
        listOf(true, true, false)    // 巽
    )

    val guaRadius = radius * 0.72f
    val guaWidth = 52f
    val guaHeight = 6f
    val guaSpacing = 5f

    bagua.forEachIndexed { index, yao ->
        val angleDeg = index * 45f
        val angleRad = (angleDeg - 90) * (PI / 180.0)

        val x = centerX + guaRadius * cos(angleRad).toFloat()
        val y = centerY + guaRadius * sin(angleRad).toFloat()

        rotate(degrees = angleDeg, pivot = Offset(x, y)) {
            yao.forEachIndexed { yaoIndex, isYang ->
                val totalHeight = 3 * guaHeight + 2 * guaSpacing
                val startY = y - totalHeight / 2
                val yaoY = startY + yaoIndex * (guaHeight + guaSpacing) + guaHeight/2

                if (isYang) {
                    drawLine(
                        color = guaColor,
                        start = Offset(x - guaWidth / 2, yaoY),
                        end = Offset(x + guaWidth / 2, yaoY),
                        strokeWidth = guaHeight,
                        cap = StrokeCap.Round
                    )
                } else {
                    val gap = guaWidth * 0.15f
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
}