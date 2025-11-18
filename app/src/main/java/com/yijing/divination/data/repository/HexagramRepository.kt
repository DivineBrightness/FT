package com.yijing.divination.data.repository

import com.yijing.divination.data.local.database.dao.HexagramDao
import com.yijing.divination.data.local.database.dao.YaoDao
import com.yijing.divination.data.local.database.entity.HexagramEntity
import com.yijing.divination.data.local.database.entity.YaoEntity
import com.yijing.divination.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 卦象数据仓库
 *
 * 提供卦象和爻辞数据的访问接口
 */
@Singleton
class HexagramRepository @Inject constructor(
    private val hexagramDao: HexagramDao,
    private val yaoDao: YaoDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * 根据 ID 获取卦象
     */
    suspend fun getHexagramById(id: Int): HexagramEntity? = withContext(ioDispatcher) {
        hexagramDao.getById(id)
    }

    /**
     * 根据 ID 获取卦象（Flow）
     */
    fun getHexagramByIdFlow(id: Int): Flow<HexagramEntity?> {
        return hexagramDao.getByIdFlow(id)
    }

    /**
     * 根据爻组合获取卦象
     */
    suspend fun getHexagramByLines(lines: String): HexagramEntity? = withContext(ioDispatcher) {
        hexagramDao.getByLines(lines)
    }

    /**
     * 根据卦名获取卦象
     */
    suspend fun getHexagramByName(name: String): HexagramEntity? = withContext(ioDispatcher) {
        hexagramDao.getByName(name)
    }

    /**
     * 获取所有卦象
     */
    suspend fun getAllHexagrams(): List<HexagramEntity> = withContext(ioDispatcher) {
        hexagramDao.getAll()
    }

    /**
     * 获取所有卦象（Flow）
     */
    fun getAllHexagramsFlow(): Flow<List<HexagramEntity>> {
        return hexagramDao.getAllFlow()
    }

    /**
     * 搜索卦象
     */
    fun searchHexagrams(keyword: String): Flow<List<HexagramEntity>> {
        return hexagramDao.searchFlow(keyword)
    }

    /**
     * 根据上下卦查找
     */
    suspend fun getHexagramByTrigrams(
        upperTrigram: String,
        lowerTrigram: String
    ): HexagramEntity? = withContext(ioDispatcher) {
        hexagramDao.getByTrigrams(upperTrigram, lowerTrigram)
    }

    /**
     * 获取某卦的所有爻辞
     */
    suspend fun getYaosByHexagramId(hexagramId: Int): List<YaoEntity> = withContext(ioDispatcher) {
        yaoDao.getByHexagramId(hexagramId)
    }

    /**
     * 获取某卦的所有爻辞（Flow）
     */
    fun getYaosByHexagramIdFlow(hexagramId: Int): Flow<List<YaoEntity>> {
        return yaoDao.getByHexagramIdFlow(hexagramId)
    }

    /**
     * 获取某卦某爻的爻辞
     */
    suspend fun getYaoByPosition(
        hexagramId: Int,
        position: Int
    ): YaoEntity? = withContext(ioDispatcher) {
        yaoDao.getByHexagramIdAndPosition(hexagramId, position)
    }

    /**
     * 插入卦象
     */
    suspend fun insertHexagram(hexagram: HexagramEntity) = withContext(ioDispatcher) {
        hexagramDao.insert(hexagram)
    }

    /**
     * 批量插入卦象
     */
    suspend fun insertHexagrams(hexagrams: List<HexagramEntity>) = withContext(ioDispatcher) {
        hexagramDao.insertAll(hexagrams)
    }

    /**
     * 插入爻辞
     */
    suspend fun insertYao(yao: YaoEntity) = withContext(ioDispatcher) {
        yaoDao.insert(yao)
    }

    /**
     * 批量插入爻辞
     */
    suspend fun insertYaos(yaos: List<YaoEntity>) = withContext(ioDispatcher) {
        yaoDao.insertAll(yaos)
    }

    /**
     * 获取数据库中的卦象总数
     */
    suspend fun getHexagramCount(): Int = withContext(ioDispatcher) {
        hexagramDao.getCount()
    }

    /**
     * 清空所有数据
     */
    suspend fun clearAll() = withContext(ioDispatcher) {
        hexagramDao.deleteAll()
        yaoDao.deleteAll()
    }
}
