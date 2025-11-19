package com.yijing.divination.domain.algorithm

import com.yijing.divination.data.local.database.entity.HexagramEntity
import com.yijing.divination.data.local.database.entity.YaoEntity
import com.yijing.divination.data.model.CoinTossResult
import com.yijing.divination.data.model.DivinationResult
import com.yijing.divination.data.model.Hexagram
import com.yijing.divination.data.model.Yao
import com.yijing.divination.data.model.YaoType
import com.yijing.divination.data.repository.HexagramRepository
import javax.inject.Inject

/**
 * 卦象生成器
 *
 * 根据六次铜钱投掷结果生成卦象：
 * 1. 生成本卦（原卦）
 * 2. 识别变爻
 * 3. 生成变卦（如有变爻）
 */
class HexagramGenerator @Inject constructor(
    private val hexagramRepository: HexagramRepository
) {

    /**
     * 根据投掷结果生成完整的占卜结果
     *
     * @param tossResults 六次投掷结果（从下到上：初爻、二爻、三爻、四爻、五爻、上爻）
     * @return DivinationResult 占卜结果，包含本卦和变卦
     */
    suspend fun generateDivinationResult(
        tossResults: List<CoinTossResult>
    ): DivinationResult {
        require(tossResults.size == 6) { "必须有6次投掷结果" }

        // 1. 生成本卦
        val originalLines = tossResults.map { it.yaoType }
        val originalHexagram = findHexagramByLines(originalLines)

        // 2. 找出所有变爻的位置（0-based索引）
        val changingYaoPositions = tossResults
            .mapIndexedNotNull { index, result ->
                if (result.isChanging) index else null
            }

        // 3. 生成变卦（如果有变爻）
        val changedHexagram = if (changingYaoPositions.isNotEmpty()) {
            val changedLines = originalLines.toMutableList()
            // 变爻：阳变阴，阴变阳
            changingYaoPositions.forEach { position ->
                changedLines[position] = when (changedLines[position]) {
                    YaoType.YANG -> YaoType.YIN
                    YaoType.YIN -> YaoType.YANG
                }
            }
            findHexagramByLines(changedLines)
        } else {
            null
        }

        return DivinationResult(
            originalHexagram = originalHexagram,
            changedHexagram = changedHexagram,
            changingYaoPositions = changingYaoPositions,
            tossResults = tossResults
        )
    }

    /**
     * 根据爻组合查找卦象
     *
     * @param lines 六爻列表（从下到上）
     * @return Hexagram 对应的卦象
     */
    private suspend fun findHexagramByLines(lines: List<YaoType>): Hexagram {
        require(lines.size == 6) { "必须有6爻" }

        // 将爻转换为二进制字符串
        // 注意：lines 列表是从下往上的（初爻到上爻）

        // 但数据库中的 lines 字段是"上卦+下卦"的顺序（从上往下）

        // 所以需要反转列表

        val linesString = lines.reversed().joinToString("") {
            if (it == YaoType.YANG) "1" else "0"
        }

        // 从数据库查找对应的卦
        val hexagramEntity = hexagramRepository.getHexagramByLines(linesString)
            ?: throw IllegalStateException("未找到爻组合为 $linesString 的卦象")

        // 获取该卦的所有爻辞
        val yaoEntities = hexagramRepository.getYaosByHexagramId(hexagramEntity.id)

        return hexagramEntity.toDomainModel(yaoEntities, lines)
    }

    /**
     * 根据八卦组合生成卦象
     *
     * @param upperTrigram 上卦（三爻）
     * @param lowerTrigram 下卦（三爻）
     * @return Hexagram 对应的卦象
     */
    suspend fun generateFromTrigrams(
        upperTrigram: List<YaoType>,
        lowerTrigram: List<YaoType>
    ): Hexagram {
        require(upperTrigram.size == 3) { "上卦必须有3爻" }
        require(lowerTrigram.size == 3) { "下卦必须有3爻" }

        val allLines = lowerTrigram + upperTrigram
        return findHexagramByLines(allLines)
    }

    /**
     * 根据卦序号获取卦象
     *
     * @param hexagramId 卦序号（1-64）
     * @return Hexagram 对应的卦象
     */
    suspend fun getHexagramById(hexagramId: Int): Hexagram? {
        val hexagramEntity = hexagramRepository.getHexagramById(hexagramId)
            ?: return null

        val yaoEntities = hexagramRepository.getYaosByHexagramId(hexagramId)

        // 从 lines 字符串解析爻列表
        // 数据库中的 lines 是"上卦+下卦"（从上往下）

        // 需要反转为从下往上的顺序（初爻到上爻）

        val lines = hexagramEntity.lines.map {

            if (it == '1') YaoType.YANG else YaoType.YIN

        }.reversed()

        return hexagramEntity.toDomainModel(yaoEntities, lines)
    }

    /**
     * 获取所有卦象
     *
     * @return List<Hexagram> 所有64卦
     */
    suspend fun getAllHexagrams(): List<Hexagram> {
        val allEntities = hexagramRepository.getAllHexagrams()
        return allEntities.map { entity ->
            val yaoEntities = hexagramRepository.getYaosByHexagramId(entity.id)
            // 数据库中的 lines 是"上卦+下卦"（从上往下）

            // 需要反转为从下往上的顺序（初爻到上爻）

            val lines = entity.lines.map {

                if (it == '1') YaoType.YANG else YaoType.YIN

            }.reversed()
            entity.toDomainModel(yaoEntities, lines)
        }
    }
}

/**
 * 扩展函数：将 HexagramEntity 转换为 Hexagram 领域模型
 */
private fun HexagramEntity.toDomainModel(
    yaoEntities: List<YaoEntity>,
    lines: List<YaoType>
): Hexagram {
    return Hexagram(
        id = this.id,
        name = this.name,
        upperTrigram = this.upperTrigram,
        lowerTrigram = this.lowerTrigram,
        lines = lines,
        unicode = this.unicode,
        guaCi = this.guaCi,
        xiangCi = this.xiangCi,
        tuanCi = this.tuanCi,
        meaning = this.meaning,
        fortune = this.fortune,
        career = this.career,
        love = this.love,
        health = this.health,
        wealth = this.wealth
    )
}

/**
 * 扩展函数：将 YaoEntity 转换为 Yao 领域模型
 */
private fun YaoEntity.toDomainModel(): Yao {
    return Yao(
        id = this.id,
        hexagramId = this.hexagramId,
        position = this.position,
        name = this.name,
        text = this.text,
        xiangText = this.xiangText,
        meaning = this.meaning
    )
}
