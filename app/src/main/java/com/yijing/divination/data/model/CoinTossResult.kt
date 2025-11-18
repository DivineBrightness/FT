package com.yijing.divination.data.model

/**
 * 铜钱投掷结果
 *
 * 三枚铜钱投掷的结果，决定一爻的阴阳属性
 */
data class CoinTossResult(
    val value: Int,                     // 6(老阴) 7(少阳) 8(少阴) 9(老阳)
    val yaoType: YaoType,               // 爻类型：阳爻或阴爻
    val isChanging: Boolean             // 是否为变爻（老阴、老阳会变）
) {
    companion object {
        const val OLD_YIN = 6       // 老阴（三背）变爻
        const val YOUNG_YANG = 7    // 少阳（两背一字）
        const val YOUNG_YIN = 8     // 少阴（两字一背）
        const val OLD_YANG = 9      // 老阳（三字）变爻
    }

    /**
     * 获取描述文本
     */
    fun getDescription(): String {
        return when (value) {
            OLD_YIN -> "老阴（变爻）"
            YOUNG_YANG -> "少阳"
            YOUNG_YIN -> "少阴"
            OLD_YANG -> "老阳（变爻）"
            else -> "未知"
        }
    }
}
