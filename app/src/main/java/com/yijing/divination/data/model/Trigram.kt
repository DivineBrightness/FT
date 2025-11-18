package com.yijing.divination.data.model

/**
 * 八卦模型
 */
enum class Trigram(
    val chineseName: String,
    val unicode: String,
    val binary: String,
    val nature: String,
    val symbol: String
) {
    QIAN("乾", "☰", "111", "天", "健"),
    KUN("坤", "☷", "000", "地", "顺"),
    KAN("坎", "☵", "010", "水", "陷"),
    LI("离", "☲", "101", "火", "丽"),
    ZHEN("震", "☳", "001", "雷", "动"),
    XUN("巽", "☴", "110", "风", "入"),
    GEN("艮", "☶", "100", "山", "止"),
    DUI("兑", "☱", "011", "泽", "悦");

    companion object {
        /**
         * 根据二进制字符串获取八卦
         */
        fun fromBinary(binary: String): Trigram? {
            return values().find { it.binary == binary }
        }

        /**
         * 根据中文名称获取八卦
         */
        fun fromChineseName(name: String): Trigram? {
            return values().find { it.chineseName == name }
        }

        /**
         * 根据爻组合获取八卦
         */
        fun fromYaoList(yaos: List<YaoType>): Trigram? {
            require(yaos.size == 3) { "八卦需要3个爻" }
            val binary = yaos.joinToString("") { if (it == YaoType.YANG) "1" else "0" }
            return fromBinary(binary)
        }
    }
}
