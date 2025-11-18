package com.yijing.divination.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yijing.divination.ui.screen.home.HomeScreen

/**
 * 应用导航图
 */
@Composable
fun YiJingNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 主页
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDivination = {
                    navController.navigate(Screen.Divination.route)
                },
                onNavigateToHexagramList = {
                    navController.navigate(Screen.HexagramList.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToLearning = {
                    navController.navigate(Screen.Learning.route)
                }
            )
        }

        // 占卜
        composable(Screen.Divination.route) {
            com.yijing.divination.ui.screen.divination.DivinationScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { recordId ->
                    navController.navigate(Screen.Result.createRoute(recordId))
                }
            )
        }

        // 结果展示
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("recordId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: 0L
            // TODO: ResultScreen
        }

        // 卦象列表
        composable(Screen.HexagramList.route) {
            // TODO: HexagramListScreen
        }

        // 卦象详情
        composable(
            route = Screen.HexagramDetail.route,
            arguments = listOf(
                navArgument("hexagramId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val hexagramId = backStackEntry.arguments?.getInt("hexagramId") ?: 1
            // TODO: HexagramDetailScreen
        }

        // 历史记录
        composable(Screen.History.route) {
            // TODO: HistoryScreen
        }

        // 学习模块
        composable(Screen.Learning.route) {
            // TODO: LearningScreen
        }

        // 关于
        composable(Screen.About.route) {
            // TODO: AboutScreen
        }
    }
}
