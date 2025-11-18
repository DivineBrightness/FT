package com.yijing.divination.data.repository

import com.yijing.divination.data.local.database.dao.RecordDao
import com.yijing.divination.data.local.database.entity.RecordEntity
import com.yijing.divination.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 占卜记录数据仓库
 *
 * 管理用户的占卜历史记录
 */
@Singleton
class RecordRepository @Inject constructor(
    private val recordDao: RecordDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * 插入占卜记录
     *
     * @return 插入记录的 ID
     */
    suspend fun insertRecord(record: RecordEntity): Long = withContext(ioDispatcher) {
        recordDao.insert(record)
    }

    /**
     * 更新占卜记录
     */
    suspend fun updateRecord(record: RecordEntity) = withContext(ioDispatcher) {
        recordDao.update(record)
    }

    /**
     * 删除占卜记录
     */
    suspend fun deleteRecord(record: RecordEntity) = withContext(ioDispatcher) {
        recordDao.delete(record)
    }

    /**
     * 根据 ID 删除记录
     */
    suspend fun deleteRecordById(id: Long) = withContext(ioDispatcher) {
        recordDao.deleteById(id)
    }

    /**
     * 获取所有记录（按时间倒序）
     */
    fun getAllRecordsFlow(): Flow<List<RecordEntity>> {
        return recordDao.getAllFlow()
    }

    /**
     * 获取所有记录（挂起函数）
     */
    suspend fun getAllRecords(): List<RecordEntity> = withContext(ioDispatcher) {
        recordDao.getAll()
    }

    /**
     * 根据 ID 获取记录
     */
    suspend fun getRecordById(id: Long): RecordEntity? = withContext(ioDispatcher) {
        recordDao.getById(id)
    }

    /**
     * 根据 ID 获取记录（Flow）
     */
    fun getRecordByIdFlow(id: Long): Flow<RecordEntity?> {
        return recordDao.getByIdFlow(id)
    }

    /**
     * 获取收藏的记录
     */
    fun getFavoritesFlow(): Flow<List<RecordEntity>> {
        return recordDao.getFavoritesFlow()
    }

    /**
     * 搜索记录
     */
    fun searchRecords(keyword: String): Flow<List<RecordEntity>> {
        return recordDao.searchFlow(keyword)
    }

    /**
     * 获取某个卦象的所有记录
     */
    fun getRecordsByHexagramId(hexagramId: Int): Flow<List<RecordEntity>> {
        return recordDao.getByHexagramIdFlow(hexagramId)
    }

    /**
     * 更新笔记
     */
    suspend fun updateNote(id: Long, note: String?) = withContext(ioDispatcher) {
        recordDao.updateNote(id, note)
    }

    /**
     * 更新收藏状态
     */
    suspend fun updateFavorite(id: Long, isFavorite: Boolean) = withContext(ioDispatcher) {
        recordDao.updateFavorite(id, isFavorite)
    }

    /**
     * 获取记录总数
     */
    suspend fun getRecordCount(): Int = withContext(ioDispatcher) {
        recordDao.getCount()
    }

    /**
     * 删除所有记录
     */
    suspend fun deleteAll() = withContext(ioDispatcher) {
        recordDao.deleteAll()
    }
}
