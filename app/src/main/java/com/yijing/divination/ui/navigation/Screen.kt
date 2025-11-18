package com.yijing.divination.ui.navigation

/**
 * 应用导航路由
 */
sealed class Screen(val route: String) {

    // 主页
    object Home : Screen("home")

    // 占卜
    object Divination : Screen("divination")

    // 结果展示
    object Result : Screen("result/{recordId}") {
        fun createRoute(recordId: Long) = "result/$recordId"
    }

    // 卦象列表
    object HexagramList : Screen("hexagram_list")

    // 卦象详情
    object HexagramDetail : Screen("hexagram_detail/{hexagramId}") {
        fun createRoute(hexagramId: Int) = "hexagram_detail/$hexagramId"
    }

    // 历史记录
    object History : Screen("history")

    // 学习模块
    object Learning : Screen("learning")

    // 关于
    object About : Screen("about")
}
