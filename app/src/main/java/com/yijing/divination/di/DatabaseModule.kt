package com.yijing.divination.di

import android.content.Context
import androidx.room.Room
import com.yijing.divination.data.local.database.YiJingDatabase
import com.yijing.divination.data.local.database.dao.HexagramDao
import com.yijing.divination.data.local.database.dao.RecordDao
import com.yijing.divination.data.local.database.dao.YaoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供 Room 数据库实例
     */
    @Provides
    @Singleton
    fun provideYiJingDatabase(
        @ApplicationContext context: Context
    ): YiJingDatabase {
        return Room.databaseBuilder(
            context,
            YiJingDatabase::class.java,
            YiJingDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // 简化开发，生产环境需要提供迁移策略
            .build()
    }

    /**
     * 提供卦象 DAO
     */
    @Provides
    @Singleton
    fun provideHexagramDao(database: YiJingDatabase): HexagramDao {
        return database.hexagramDao()
    }

    /**
     * 提供爻辞 DAO
     */
    @Provides
    @Singleton
    fun provideYaoDao(database: YiJingDatabase): YaoDao {
        return database.yaoDao()
    }

    /**
     * 提供占卜记录 DAO
     */
    @Provides
    @Singleton
    fun provideRecordDao(database: YiJingDatabase): RecordDao {
        return database.recordDao()
    }
}
