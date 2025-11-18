package com.yijing.divination.di

import com.yijing.divination.data.local.database.dao.HexagramDao
import com.yijing.divination.data.local.database.dao.RecordDao
import com.yijing.divination.data.local.database.dao.YaoDao
import com.yijing.divination.data.repository.HexagramRepository
import com.yijing.divination.data.repository.RecordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * 数据仓库依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * 提供卦象仓库
     */
    @Provides
    @Singleton
    fun provideHexagramRepository(
        hexagramDao: HexagramDao,
        yaoDao: YaoDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): HexagramRepository {
        return HexagramRepository(hexagramDao, yaoDao, ioDispatcher)
    }

    /**
     * 提供占卜记录仓库
     */
    @Provides
    @Singleton
    fun provideRecordRepository(
        recordDao: RecordDao,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): RecordRepository {
        return RecordRepository(recordDao, ioDispatcher)
    }
}
