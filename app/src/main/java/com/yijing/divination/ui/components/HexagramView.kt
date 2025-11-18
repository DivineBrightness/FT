package com.yijing.divination.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yijing.divination.data.model.YaoType

/**
 * 卦象可视化组件
 *
 * 显示六爻组成的完整卦象
 *
 * @param lines 六爻列表（从下到上：初爻、二爻、三爻、四爻、五爻、上爻）
 * @param changingPositions 变爻位置列表（0-based）
 * @param modifier 修饰符
 * @param lineWidth 爻线宽度
 * @param lineSpacing 爻线间距
 * @param showLabels 是否显示爻位标签
 */
@Composable
fun HexagramView(
    lines: List<YaoType>,
    modifier: Modifier = Modifier,
    changingPositions: List<Int> = emptyList(),
    lineWidth: Dp = 120.dp,
    lineSpacing: Dp = 16.dp,
    showLabels: Boolean = false
) {
    require(lines.size == 6) { "卦象必须有6爻" }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(lineSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 从上到下显示（第6爻到第1爻）
        lines.reversed().forEachIndexed { index, yaoType ->
            val actualPosition = 5 - index // 实际爻位（0-based）
            val isChanging = changingPositions.contains(actualPosition)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 爻位标签（如果需要）
                if (showLabels) {
                    Text(
                        text = getYaoLabel(actualPosition),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(40.dp)
                    )
                }

                // 爻线
                YaoLine(
                    yaoType = yaoType,
                    isChanging = isChanging,
                    lineWidth = lineWidth
                )
            }
        }
    }
}

/**
 * 单个爻线组件
 *
 * @param yaoType 爻类型（阳爻或阴爻）
 * @param isChanging 是否为变爻
 * @param lineWidth 线宽
 */
@Composable
fun YaoLine(
    yaoType: YaoType,
    isChanging: Boolean = false,
    lineWidth: Dp = 120.dp,
    lineHeight: Dp = 8.dp
) {
    val lineColor = if (isChanging) {
        MaterialTheme.colorScheme.error // 变爻用红色
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .width(lineWidth)
            .height(lineHeight)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = lineHeight.toPx()
            val centerY = size.height / 2

            when (yaoType) {
                YaoType.YANG -> {
                    // 阳爻：实线 ————
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, centerY),
                        end = Offset(size.width, centerY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
                YaoType.YIN -> {
                    // 阴爻：断线 —— ——
                    val gapWidth = size.width * 0.2f
                    val leftLineEnd = (size.width - gapWidth) / 2
                    val rightLineStart = leftLineEnd + gapWidth

                    // 左半段
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, centerY),
                        end = Offset(leftLineEnd, centerY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    // 右半段
                    drawLine(
                        color = lineColor,
                        start = Offset(rightLineStart, centerY),
                        end = Offset(size.width, centerY),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

/**
 * 获取爻位标签
 */
private fun getYaoLabel(position: Int): String {
    return when (position) {
        0 -> "初"
        1 -> "二"
        2 -> "三"
        3 -> "四"
        4 -> "五"
        5 -> "上"
        else -> ""
    }
}

/**
 * 简化版卦象显示（小尺寸）
 */
@Composable
fun CompactHexagramView(
    lines: List<YaoType>,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    HexagramView(
        lines = lines,
        modifier = modifier,
        lineWidth = size,
        lineSpacing = 4.dp,
        showLabels = false
    )
}
