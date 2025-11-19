package com.yijing.divination.ui.navigation

/**
 * 应用导航屏幕定义
 *
 * 使用 sealed class 确保类型安全的导航路由
 */
sealed class Screen(val route: String) {

    /**
     * 主屏幕 - 欢迎页面和快捷入口
     */
    data object Home : Screen("home")

    /**
     * 占卜方法选择屏幕 - 选择占卜方法
     */
    data object DivinationMethod : Screen("divination_method")

    /**
     * 占卜屏幕 - 进行铜钱占卜
     */
    data object Divination : Screen("divination")

    /**
     * 结果屏幕 - 显示占卜结果
     * @param recordId 占卜记录ID（可选，用于查看历史记录）
     */
    data object Result : Screen("result/{recordId}") {
        fun createRoute(recordId: Long) = "result/$recordId"

        /**
         * 用于新占卜结果（使用特殊标记 -1）
         */
        fun createRouteForNew() = "result/-1"
    }

    /**
     * 卦象列表屏幕 - 浏览六十四卦
     */
    data object HexagramList : Screen("hexagram_list")

    /**
     * 卦象详情屏幕 - 查看单个卦象详细信息
     * @param hexagramId 卦象ID
     */
    data object HexagramDetail : Screen("hexagram_detail/{hexagramId}") {
        fun createRoute(hexagramId: Int) = "hexagram_detail/$hexagramId"
    }

    /**
     * 历史记录屏幕 - 查看过往占卜记录
     */
    data object History : Screen("history")

    /**
     * 学习模块屏幕 - 易经知识学习
     */
    data object Learning : Screen("learning")
}
