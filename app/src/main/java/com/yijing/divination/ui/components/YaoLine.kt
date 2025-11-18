package com.yijing.divination.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yijing.divination.data.model.YaoType
import com.yijing.divination.ui.theme.YiJingTheme

/**
 * 爻线组件
 *
 * 用于显示易经中的单个爻（阳爻或阴爻）
 *
 * @param yaoType 爻的类型（阳爻或阴爻）
 * @param isChanging 是否为变爻（老阳、老阴）
 * @param modifier 修饰符
 * @param lineColor 爻线颜色（默认使用主题色）
 * @param changingColor 变爻颜色（默认为金色）
 * @param lineHeight 爻线高度
 * @param strokeWidth 线条宽度
 */
@Composable
fun YaoLine(
    yaoType: YaoType,
    isChanging: Boolean = false,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onSurface,
    changingColor: Color = Color(0xFFD4AF37), // 金色
    lineHeight: Dp = 12.dp,
    strokeWidth: Dp = 4.dp
) {
    val color = if (isChanging) changingColor else lineColor

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(lineHeight)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2

        when (yaoType) {
            YaoType.YANG -> {
                // 阳爻：实线 ━━━━━
                drawLine(
                    color = color,
                    start = Offset(0f, centerY),
                    end = Offset(canvasWidth, centerY),
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }

            YaoType.YIN -> {
                // 阴爻：虚线 ━━  ━━
                val gap = canvasWidth * 0.15f // 中间间隙占15%
                val lineLength = (canvasWidth - gap) / 2

                // 左段
                drawLine(
                    color = color,
                    start = Offset(0f, centerY),
                    end = Offset(lineLength, centerY),
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )

                // 右段
                drawLine(
                    color = color,
                    start = Offset(lineLength + gap, centerY),
                    end = Offset(canvasWidth, centerY),
                    strokeWidth = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

// ==================== 预览 ====================

@Preview(name = "阳爻 - 少阳", showBackground = true)
@Composable
fun YaoLineYangPreview() {
    YiJingTheme {
        YaoLine(
            yaoType = YaoType.YANG,
            isChanging = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "阳爻 - 老阳（变爻）", showBackground = true)
@Composable
fun YaoLineChangingYangPreview() {
    YiJingTheme {
        YaoLine(
            yaoType = YaoType.YANG,
            isChanging = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "阴爻 - 少阴", showBackground = true)
@Composable
fun YaoLineYinPreview() {
    YiJingTheme {
        YaoLine(
            yaoType = YaoType.YIN,
            isChanging = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "阴爻 - 老阴（变爻）", showBackground = true)
@Composable
fun YaoLineChangingYinPreview() {
    YiJingTheme {
        YaoLine(
            yaoType = YaoType.YIN,
            isChanging = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
