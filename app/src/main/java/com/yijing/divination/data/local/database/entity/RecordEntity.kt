package com.yijing.divination.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 占卜记录实体
 * 保存用户的每次占卜历史
 */
@Entity(tableName = "divination_records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val timestamp: Long,                // 占卜时间戳（毫秒）
    val question: String?,              // 占卜问题（可选）

    // 本卦信息
    val originalHexagramId: Int,        // 本卦 ID
    val originalHexagramName: String,   // 本卦名称
    val yaoResults: String,             // 六次投掷结果 JSON 格式
                                        // 例: "[6,7,8,9,7,8]"

    // 变卦信息（如果有变爻）
    val changedHexagramId: Int?,        // 变卦 ID（null 表示无变爻）
    val changedHexagramName: String?,   // 变卦名称
    val changingYaoPositions: String?,  // 变爻位置 JSON 格式
                                        // 例: "[1,3,5]" 表示第1、3、5爻为变爻

    // 用户笔记
    val note: String? = null,           // 用户添加的笔记/心得
    val isFavorite: Boolean = false     // 是否收藏
)
