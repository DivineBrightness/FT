package com.yijing.divination.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.yijing.divination.ui.navigation.YiJingNavGraph
import com.yijing.divination.ui.theme.YiJingTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主 Activity
 *
 * 使用 Jetpack Compose 构建 UI
 * 集成 Navigation Compose 进行屏幕导航
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YiJingTheme {
                YiJingApp()
            }
        }
    }
}

/**
 * 应用主入口 Composable
 *
 * 设置导航控制器和导航图
 */
@Composable
fun YiJingApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold { paddingValues ->
            YiJingNavGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun YiJingAppPreview() {
    YiJingTheme {
        YiJingApp()
    }
}
