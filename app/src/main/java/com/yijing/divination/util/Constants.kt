package com.yijing.divination.util

/**
 * 应用常量
 */
object Constants {

    /**
     * 占卜相关常量
     */
    object Divination {
        const val TOTAL_YAO_COUNT = 6       // 一卦6爻
        const val COIN_HEADS_VALUE = 3      // 铜钱正面（字）
        const val COIN_TAILS_VALUE = 2      // 铜钱背面（背）
    }

    /**
     * 数据库相关常量
     */
    object Database {
        const val TOTAL_HEXAGRAM_COUNT = 64 // 六十四卦
        const val YAO_PER_HEXAGRAM = 6      // 每卦6爻
        const val TOTAL_YAO_COUNT = 384     // 总共384条爻辞
    }

    /**
     * 八卦二进制映射
     */
    object TrigramBinary {
        const val QIAN = "111"  // 乾 ☰
        const val KUN = "000"   // 坤 ☷
        const val KAN = "010"   // 坎 ☵
        const val LI = "101"    // 离 ☲
        const val ZHEN = "001"  // 震 ☳
        const val XUN = "110"   // 巽 ☴
        const val GEN = "100"   // 艮 ☶
        const val DUI = "011"   // 兑 ☱
    }

    /**
     * 共享偏好设置键
     */
    object Preferences {
        const val PREFS_NAME = "yijing_prefs"
        const val KEY_FIRST_LAUNCH = "first_launch"
        const val KEY_DATABASE_INITIALIZED = "database_initialized"
        const val KEY_DARK_MODE = "dark_mode"
        const val KEY_SHOW_DISCLAIMER = "show_disclaimer"
    }

    /**
     * 导航路由
     */
    object Routes {
        const val HOME = "home"
        const val DIVINATION = "divination"
        const val RESULT = "result/{recordId}"
        const val HEXAGRAM_LIST = "hexagram_list"
        const val HEXAGRAM_DETAIL = "hexagram_detail/{hexagramId}"
        const val HISTORY = "history"
        const val LEARNING = "learning"
        const val ABOUT = "about"

        fun resultRoute(recordId: Long) = "result/$recordId"
        fun hexagramDetailRoute(hexagramId: Int) = "hexagram_detail/$hexagramId"
    }

    /**
     * 动画时长（毫秒）
     */
    object Animation {
        const val COIN_FLIP_DURATION = 800L
        const val PAGE_TRANSITION_DURATION = 300L
        const val YAO_REVEAL_DURATION = 400L
    }
}
