package com.yijing.divination.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yijing.divination.data.local.database.entity.YaoEntity
import kotlinx.coroutines.flow.Flow

/**
 * 爻辞数据访问对象
 */
@Dao
interface YaoDao {

    /**
     * 插入单个爻辞
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(yao: YaoEntity)

    /**
     * 批量插入爻辞
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(yaos: List<YaoEntity>)

    /**
     * 获取某卦的所有爻辞（按位置排序）
     */
    @Query("SELECT * FROM yaos WHERE hexagramId = :hexagramId ORDER BY position")
    suspend fun getByHexagramId(hexagramId: Int): List<YaoEntity>

    /**
     * 获取某卦的所有爻辞（Flow）
     */
    @Query("SELECT * FROM yaos WHERE hexagramId = :hexagramId ORDER BY position")
    fun getByHexagramIdFlow(hexagramId: Int): Flow<List<YaoEntity>>

    /**
     * 获取某卦某爻的爻辞
     */
    @Query("SELECT * FROM yaos WHERE hexagramId = :hexagramId AND position = :position")
    suspend fun getByHexagramIdAndPosition(hexagramId: Int, position: Int): YaoEntity?

    /**
     * 删除某卦的所有爻辞
     */
    @Query("DELETE FROM yaos WHERE hexagramId = :hexagramId")
    suspend fun deleteByHexagramId(hexagramId: Int)

    /**
     * 删除所有爻辞
     */
    @Query("DELETE FROM yaos")
    suspend fun deleteAll()
}
