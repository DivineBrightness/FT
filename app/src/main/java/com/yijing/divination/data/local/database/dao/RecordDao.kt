package com.yijing.divination.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yijing.divination.data.local.database.entity.RecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * 占卜记录数据访问对象
 */
@Dao
interface RecordDao {

    /**
     * 插入占卜记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RecordEntity): Long

    /**
     * 更新占卜记录
     */
    @Update
    suspend fun update(record: RecordEntity)

    /**
     * 删除占卜记录
     */
    @Delete
    suspend fun delete(record: RecordEntity)

    /**
     * 根据 ID 删除
     */
    @Query("DELETE FROM divination_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * 获取所有记录（按时间倒序）
     */
    @Query("SELECT * FROM divination_records ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<RecordEntity>>

    /**
     * 获取所有记录（挂起函数）
     */
    @Query("SELECT * FROM divination_records ORDER BY timestamp DESC")
    suspend fun getAll(): List<RecordEntity>

    /**
     * 根据 ID 获取记录
     */
    @Query("SELECT * FROM divination_records WHERE id = :id")
    suspend fun getById(id: Long): RecordEntity?

    /**
     * 根据 ID 获取记录（Flow）
     */
    @Query("SELECT * FROM divination_records WHERE id = :id")
    fun getByIdFlow(id: Long): Flow<RecordEntity?>

    /**
     * 获取收藏的记录
     */
    @Query("SELECT * FROM divination_records WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoritesFlow(): Flow<List<RecordEntity>>

    /**
     * 搜索记录（按问题或卦名）
     */
    @Query("""
        SELECT * FROM divination_records
        WHERE question LIKE '%' || :keyword || '%'
           OR originalHexagramName LIKE '%' || :keyword || '%'
           OR changedHexagramName LIKE '%' || :keyword || '%'
        ORDER BY timestamp DESC
    """)
    fun searchFlow(keyword: String): Flow<List<RecordEntity>>

    /**
     * 获取某个卦象的所有记录
     */
    @Query("""
        SELECT * FROM divination_records
        WHERE originalHexagramId = :hexagramId
           OR changedHexagramId = :hexagramId
        ORDER BY timestamp DESC
    """)
    fun getByHexagramIdFlow(hexagramId: Int): Flow<List<RecordEntity>>

    /**
     * 更新笔记
     */
    @Query("UPDATE divination_records SET note = :note WHERE id = :id")
    suspend fun updateNote(id: Long, note: String?)

    /**
     * 更新收藏状态
     */
    @Query("UPDATE divination_records SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    /**
     * 获取记录总数
     */
    @Query("SELECT COUNT(*) FROM divination_records")
    suspend fun getCount(): Int

    /**
     * 删除所有记录
     */
    @Query("DELETE FROM divination_records")
    suspend fun deleteAll()
}
