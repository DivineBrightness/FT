package com.yijing.divination.data.model

/**
 * 卦象领域模型
 */
data class Hexagram(
    val id: Int,
    val name: String,
    val upperTrigram: String,
    val lowerTrigram: String,
    val lines: List<YaoType>,           // 6个爻的类型列表
    val unicode: String,
    val guaCi: String,
    val xiangCi: String,
    val tuanCi: String,
    val meaning: String,
    val fortune: String,
    val career: String,
    val love: String,
    val health: String,
    val wealth: String
) {
    /**
     * 获取爻组合字符串
     */
    fun getLinesString(): String {
        return lines.joinToString("") { if (it == YaoType.YANG) "1" else "0" }
    }

    /**
     * 获取上卦（上三爻）
     */
    fun getUpperLines(): List<YaoType> = lines.takeLast(3)

    /**
     * 获取下卦（下三爻）
     */
    fun getLowerLines(): List<YaoType> = lines.take(3)
}

/**
 * 爻类型
 */
enum class YaoType {
    YANG,       // 阳爻 —
    YIN         // 阴爻 - -
}

/**
 * 爻辞领域模型
 */
data class Yao(
    val id: Int,
    val hexagramId: Int,
    val position: Int,
    val name: String,
    val text: String,
    val xiangText: String,
    val meaning: String
)
