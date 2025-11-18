package com.yijing.divination.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yijing.divination.data.local.database.dao.HexagramDao
import com.yijing.divination.data.local.database.dao.RecordDao
import com.yijing.divination.data.local.database.dao.YaoDao
import com.yijing.divination.data.local.database.entity.HexagramEntity
import com.yijing.divination.data.local.database.entity.RecordEntity
import com.yijing.divination.data.local.database.entity.YaoEntity

/**
 * 易经占卜应用数据库
 *
 * 包含三个主要表：
 * 1. hexagrams - 六十四卦数据
 * 2. yaos - 爻辞数据（384条，每卦6爻）
 * 3. divination_records - 用户占卜记录
 */
@Database(
    entities = [
        HexagramEntity::class,
        YaoEntity::class,
        RecordEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class YiJingDatabase : RoomDatabase() {

    abstract fun hexagramDao(): HexagramDao
    abstract fun yaoDao(): YaoDao
    abstract fun recordDao(): RecordDao

    companion object {
        const val DATABASE_NAME = "yijing_database.db"
    }
}
