package com.yijing.divination.data.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 爻辞数据实体
 * 每卦有 6 爻，每爻都有独立的爻辞
 */
@Entity(
    tableName = "yaos",
    foreignKeys = [
        ForeignKey(
            entity = HexagramEntity::class,
            parentColumns = ["id"],
            childColumns = ["hexagramId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["hexagramId"])]
)
data class YaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val hexagramId: Int,                // 所属卦象 ID
    val position: Int,                  // 爻位：1-6 (从下到上)
    val name: String,                   // 爻名：如 "初九"、"九二"、"六三" 等
    val text: String,                   // 爻辞原文
    val xiangText: String,              // 象曰（小象传）
    val meaning: String                 // 译文/现代解释
)
