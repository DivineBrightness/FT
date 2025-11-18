package com.yijing.divination.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 卦象数据实体
 * 存储六十四卦的完整信息
 */
@Entity(tableName = "hexagrams")
data class HexagramEntity(
    @PrimaryKey
    val id: Int,                        // 1-64，卦序号

    // 基本信息
    val name: String,                   // 卦名：如 "乾为天"
    val upperTrigram: String,           // 上卦：如 "乾"
    val lowerTrigram: String,           // 下卦：如 "乾"
    val lines: String,                  // 爻组合：如 "111111" (1=阳爻, 0=阴爻)
    val unicode: String,                // Unicode 符号：如 "䷀"

    // 经文内容
    val guaCi: String,                  // 卦辞
    val xiangCi: String,                // 象辞
    val tuanCi: String,                 // 彖辞

    // 现代解读
    val meaning: String,                // 卦义解释
    val fortune: String,                // 吉凶判断

    // 应用指导
    val career: String,                 // 事业指导
    val love: String,                   // 爱情指导
    val health: String,                 // 健康指导
    val wealth: String                  // 财富指导
)
