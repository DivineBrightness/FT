package com.yijing.divination.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yijing.divination.data.model.Hexagram
import com.yijing.divination.data.model.YaoType
import com.yijing.divination.ui.theme.YiJingTheme

/**
 * 卦象可视化组件
 *
 * 显示完整的六爻卦象，从下到上排列（初爻在最下方，上爻在最上方）
 *
 * @param lines 六个爻的类型列表（从下到上：初爻到上爻）
 * @param hexagramName 卦名（可选）
 * @param changingPositions 变爻位置列表（0-5，0表示初爻）
 * @param modifier 修饰符
 * @param showLabels 是否显示爻位标签
 * @param showTrigramDivider 是否显示上下卦分隔
 * @param lineSpacing 爻线间距
 */
@Composable
fun HexagramView(
    lines: List<YaoType>,
    hexagramName: String? = null,
    changingPositions: List<Int> = emptyList(),
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    showTrigramDivider: Boolean = true,
    lineSpacing: Dp = 8.dp
) {
    require(lines.size == 6) { "卦象必须有6个爻" }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 卦名
        if (hexagramName != null) {
            Text(
                text = hexagramName,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 六个爻（从上到下显示，但索引是从下到上的）
        // 显示顺序：上爻(5) -> 五爻(4) -> 四爻(3) -> 三爻(2) -> 二爻(1) -> 初爻(0)
        lines.reversed().forEachIndexed { reverseIndex, yaoType ->
            val position = 5 - reverseIndex // 实际位置（0-5）
            val isChanging = position in changingPositions

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // 左侧标签
                if (showLabels) {
                    Text(
                        text = getYaoPositionName(position),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.End
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // 爻线
                YaoLine(
                    yaoType = yaoType,
                    isChanging = isChanging,
                    modifier = Modifier.weight(1f)
                )

                // 右侧标签（变爻标记）
                if (showLabels) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isChanging) "●" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD4AF37), // 金色
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }

            // 上下卦分隔线（在第3爻和第4爻之间）
            if (showTrigramDivider && position == 3) {
                Spacer(modifier = Modifier.height(lineSpacing * 2))
            } else if (position > 0) {
                Spacer(modifier = Modifier.height(lineSpacing))
            }
        }
    }
}

/**
 * 简化版本：直接传入 Hexagram 对象
 */
@Composable
fun HexagramView(
    hexagram: Hexagram,
    changingPositions: List<Int> = emptyList(),
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    showTrigramDivider: Boolean = true,
    lineSpacing: Dp = 8.dp
) {
    HexagramView(
        lines = hexagram.lines,
        hexagramName = hexagram.name,
        changingPositions = changingPositions,
        modifier = modifier,
        showLabels = showLabels,
        showTrigramDivider = showTrigramDivider,
        lineSpacing = lineSpacing
    )
}

/**
 * 获取爻位名称
 *
 * @param position 爻位（0-5）
 * @return 爻位名称
 */
private fun getYaoPositionName(position: Int): String {
    return when (position) {
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

@Preview(name = "乾卦（无变爻）", showBackground = true)
@Composable
fun HexagramViewQianPreview() {
    YiJingTheme {
        HexagramView(
            lines = List(6) { YaoType.YANG },
            hexagramName = "乾",
            changingPositions = emptyList()
        )
    }
}

@Preview(name = "坤卦（无变爻）", showBackground = true)
@Composable
fun HexagramViewKunPreview() {
    YiJingTheme {
        HexagramView(
            lines = List(6) { YaoType.YIN },
            hexagramName = "坤",
            changingPositions = emptyList()
        )
    }
}

@Preview(name = "泰卦（有变爻）", showBackground = true)
@Composable
fun HexagramViewTaiPreview() {
    YiJingTheme {
        HexagramView(
            lines = listOf(
                YaoType.YANG, // 初爻
                YaoType.YANG, // 二爻
                YaoType.YANG, // 三爻
                YaoType.YIN,  // 四爻
                YaoType.YIN,  // 五爻
                YaoType.YIN   // 上爻
            ),
            hexagramName = "泰",
            changingPositions = listOf(0, 3) // 初爻和四爻为变爻
        )
    }
}

@Preview(name = "否卦（不显示标签）", showBackground = true)
@Composable
fun HexagramViewPiPreview() {
    YiJingTheme {
        HexagramView(
            lines = listOf(
                YaoType.YIN,  // 初爻
                YaoType.YIN,  // 二爻
                YaoType.YIN,  // 三爻
                YaoType.YANG, // 四爻
                YaoType.YANG, // 五爻
                YaoType.YANG  // 上爻
            ),
            hexagramName = "否",
            showLabels = false,
            changingPositions = emptyList()
        )
    }
}

@Preview(name = "混合卦象", showBackground = true)
@Composable
fun HexagramViewMixedPreview() {
    YiJingTheme {
        HexagramView(
            lines = listOf(
                YaoType.YANG, // 初爻
                YaoType.YIN,  // 二爻
                YaoType.YANG, // 三爻
                YaoType.YIN,  // 四爻
                YaoType.YANG, // 五爻
                YaoType.YIN   // 上爻
            ),
            hexagramName = "未济",
            changingPositions = listOf(1, 4) // 二爻和五爻为变爻
        )
    }
}
