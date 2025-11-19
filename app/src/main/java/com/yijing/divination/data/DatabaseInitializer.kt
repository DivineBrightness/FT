package com.yijing.divination.data

import android.content.Context
import com.yijing.divination.R
import com.yijing.divination.data.local.database.entity.HexagramEntity
import com.yijing.divination.data.local.database.entity.YaoEntity
import com.yijing.divination.data.repository.HexagramRepository
import com.yijing.divination.util.HexagramDataRoot
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 数据库初始化器
 *
 * 负责在应用首次启动时从 JSON 文件导入数据到数据库
 */
@Singleton
class DatabaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hexagramRepository: HexagramRepository
) {

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * 检查并初始化数据库
     *
     * 如果数据库为空，则从 hexagram_data.json 导入数据
     */
    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        val count = hexagramRepository.getHexagramCount()

        if (count == 0) {
            // 数据库为空，开始导入数据
            importDataFromJson()
        }
        // 数据库已初始化，跳过导入
    }

    /**
     * 从 JSON 文件导入数据
     */
    private suspend fun importDataFromJson() {
        try {
            // 读取 JSON 文件
            val jsonString = context.resources
                .openRawResource(R.raw.hexagram_data)
                .bufferedReader()
                .use { it.readText() }

            // 解析 JSON
            val data = json.decodeFromString<HexagramDataRoot>(jsonString)

            // 转换并插入数据
            data.hexagrams.forEach { hexagramData ->
                // 插入卦象
                val hexagramEntity = HexagramEntity(
                    id = hexagramData.id,
                    name = hexagramData.name,
                    upperTrigram = hexagramData.upperTrigram,
                    lowerTrigram = hexagramData.lowerTrigram,
                    lines = hexagramData.lines,
                    unicode = hexagramData.unicode,
                    guaCi = hexagramData.guaCi,
                    xiangCi = hexagramData.xiangCi,
                    tuanCi = hexagramData.tuanCi,
                    meaning = hexagramData.meaning,
                    fortune = hexagramData.fortune,
                    career = hexagramData.career,
                    love = hexagramData.love,
                    health = hexagramData.health,
                    wealth = hexagramData.wealth
                )

                hexagramRepository.insertHexagram(hexagramEntity)

                // 插入爻辞
                val yaoEntities = hexagramData.yaos.map { yaoData ->
                    YaoEntity(
                        hexagramId = hexagramData.id,
                        position = yaoData.position,
                        name = yaoData.name,
                        text = yaoData.text,
                        xiangText = yaoData.xiangText,
                        meaning = yaoData.meaning
                    )
                }

                hexagramRepository.insertYaos(yaoEntities)
            }

        } catch (e: Exception) {
            // 导入失败，重新抛出异常供上层处理
            throw e
        }
    }

    /**
     * 重新导入数据（清空并重新导入）
     */
    suspend fun reimportData() = withContext(Dispatchers.IO) {
        hexagramRepository.clearAll()
        importDataFromJson()
    }
}
