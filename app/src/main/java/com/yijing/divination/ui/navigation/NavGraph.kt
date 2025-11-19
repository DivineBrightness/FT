package com.yijing.divination.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yijing.divination.ui.screen.divination.DivinationScreen
import com.yijing.divination.ui.screen.history.HistoryScreen
import com.yijing.divination.ui.screen.home.HomeScreen
import com.yijing.divination.ui.screen.method.CoinMethodScreen
import com.yijing.divination.ui.screen.result.ResultScreen

/**
 * 应用主导航图
 *
 * @param navController 导航控制器
 * @param modifier 修饰符
 * @param startDestination 起始目的地，默认为主页
 */
@Composable
fun YiJingNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 主屏幕 - 太极图
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMethod = {
                    navController.navigate(Screen.DivinationMethod.route)
                }
            )
        }

        // 占卜方法选择屏幕 - 铜钱
        composable(Screen.DivinationMethod.route) {
            CoinMethodScreen(
                onNavigateToCoinToss = {
                    navController.navigate(Screen.Divination.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // 占卜屏幕
        composable(Screen.Divination.route) {
            DivinationScreen(
                onNavigateToResult = { recordId ->
                    navController.navigate(Screen.Result.createRoute(recordId)) {
                        // 跳转到结果页面时，移除占卜页面
                        popUpTo(Screen.Divination.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // 结果屏幕
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("recordId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            ResultScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // 卦象列表屏幕
        composable(Screen.HexagramList.route) {
            HexagramListScreenPlaceholder(
                onNavigateToDetail = { hexagramId ->
                    navController.navigate(Screen.HexagramDetail.createRoute(hexagramId))
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // 卦象详情屏幕
        composable(
            route = Screen.HexagramDetail.route,
            arguments = listOf(
                navArgument("hexagramId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val hexagramId = backStackEntry.arguments?.getInt("hexagramId") ?: 1
            HexagramDetailScreenPlaceholder(
                hexagramId = hexagramId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // 历史记录屏幕
        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToResult = { recordId ->
                    navController.navigate(Screen.Result.createRoute(recordId))
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // 学习模块屏幕
        composable(Screen.Learning.route) {
            LearningScreenPlaceholder(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

// ==================== 占位符屏幕 ====================
// 这些将在后续阶段被实际的屏幕实现替换

@Composable
private fun HexagramListScreenPlaceholder(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    PlaceholderScreen(
        title = "六十四卦",
        subtitle = "卦象列表屏幕"
    )
}

@Composable
private fun HexagramDetailScreenPlaceholder(
    hexagramId: Int,
    onNavigateBack: () -> Unit
) {
    PlaceholderScreen(
        title = "卦象详情",
        subtitle = "卦象 ID: $hexagramId"
    )
}

@Composable
private fun LearningScreenPlaceholder(
    onNavigateBack: () -> Unit
) {
    PlaceholderScreen(
        title = "易经学习",
        subtitle = "学习模块屏幕"
    )
}

/**
 * 通用占位符屏幕组件
 */
@Composable
private fun PlaceholderScreen(
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title\n$subtitle",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
