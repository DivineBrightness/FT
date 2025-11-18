package com.yijing.divination.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yijing.divination.data.local.database.entity.HexagramEntity
import kotlinx.coroutines.flow.Flow

/**
 * 卦象数据访问对象
 */
@Dao
interface HexagramDao {

    /**
     * 插入单个卦象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hexagram: HexagramEntity)

    /**
     * 批量插入卦象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hexagrams: List<HexagramEntity>)

    /**
     * 根据 ID 获取卦象
     */
    @Query("SELECT * FROM hexagrams WHERE id = :id")
    suspend fun getById(id: Int): HexagramEntity?

    /**
     * 根据 ID 获取卦象（Flow）
     */
    @Query("SELECT * FROM hexagrams WHERE id = :id")
    fun getByIdFlow(id: Int): Flow<HexagramEntity?>

    /**
     * 根据爻组合获取卦象
     * @param lines 爻组合字符串，如 "111111"
     */
    @Query("SELECT * FROM hexagrams WHERE lines = :lines")
    suspend fun getByLines(lines: String): HexagramEntity?

    /**
     * 根据卦名获取卦象
     */
    @Query("SELECT * FROM hexagrams WHERE name = :name")
    suspend fun getByName(name: String): HexagramEntity?

    /**
     * 获取所有卦象
     */
    @Query("SELECT * FROM hexagrams ORDER BY id")
    fun getAllFlow(): Flow<List<HexagramEntity>>

    /**
     * 获取所有卦象（挂起函数）
     */
    @Query("SELECT * FROM hexagrams ORDER BY id")
    suspend fun getAll(): List<HexagramEntity>

    /**
     * 搜索卦象（按卦名或卦义）
     */
    @Query("""
        SELECT * FROM hexagrams
        WHERE name LIKE '%' || :keyword || '%'
           OR meaning LIKE '%' || :keyword || '%'
        ORDER BY id
    """)
    fun searchFlow(keyword: String): Flow<List<HexagramEntity>>

    /**
     * 根据上下卦查找
     */
    @Query("""
        SELECT * FROM hexagrams
        WHERE upperTrigram = :upperTrigram
          AND lowerTrigram = :lowerTrigram
    """)
    suspend fun getByTrigrams(upperTrigram: String, lowerTrigram: String): HexagramEntity?

    /**
     * 获取数据库中卦象总数
     */
    @Query("SELECT COUNT(*) FROM hexagrams")
    suspend fun getCount(): Int

    /**
     * 删除所有卦象（用于重置数据库）
     */
    @Query("DELETE FROM hexagrams")
    suspend fun deleteAll()
}
