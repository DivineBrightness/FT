package com.yijing.divination.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 占卜记录领域模型
 */
data class DivinationRecord(
    val id: Long,
    val timestamp: Long,
    val question: String?,
    val originalHexagram: Hexagram,
    val changedHexagram: Hexagram?,
    val yaoResults: List<CoinTossResult>,
    val changingYaoPositions: List<Int>,
    val note: String?,
    val isFavorite: Boolean
) {
    /**
     * 格式化时间
     */
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        return sdf.format(Date(timestamp))
    }

    /**
     * 是否有变爻
     */
    fun hasChangingYao(): Boolean = changingYaoPositions.isNotEmpty()

    /**
     * 获取变爻数量
     */
    fun getChangingYaoCount(): Int = changingYaoPositions.size
}

/**
 * 占卜结果
 * 用于占卜过程中临时保存结果
 */
data class DivinationResult(
    val originalHexagram: Hexagram,
    val changedHexagram: Hexagram?,
    val changingYaoPositions: List<Int>,
    val tossResults: List<CoinTossResult>
) {
    /**
     * 是否有变爻
     */
    fun hasChangingYao(): Boolean = changingYaoPositions.isNotEmpty()
}
