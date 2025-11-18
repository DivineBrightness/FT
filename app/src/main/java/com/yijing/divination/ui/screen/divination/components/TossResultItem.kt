package com.yijing.divination.ui.screen.divination.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yijing.divination.data.model.CoinTossResult
import com.yijing.divination.ui.components.YaoLine

/**
 * 投掷结果展示项
 *
 * 显示单次投掷的结果：爻位、爻类型、数值
 */
@Composable
fun TossResultItem(
    position: Int,
    result: CoinTossResult,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 爻位标签
            Text(
                text = getYaoPositionName(position),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(60.dp)
            )

            // 爻线预览
            YaoLine(
                yaoType = result.yaoType,
                isChanging = result.isChanging,
                lineWidth = 100.dp,
                lineHeight = 6.dp
            )

            // 结果描述
            Column(
                modifier = Modifier.width(100.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = result.getDescription(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (result.isChanging) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = "(${result.value})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 获取爻位名称
 */
private fun getYaoPositionName(position: Int): String {
    return when (position) {
        1 -> "初爻"
        2 -> "二爻"
        3 -> "三爻"
        4 -> "四爻"
        5 -> "五爻"
        6 -> "上爻"
        else -> "第${position}爻"
    }
}
