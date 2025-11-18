package com.yijing.divination.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.yijing.divination.data.DatabaseInitializer
import com.yijing.divination.ui.navigation.YiJingNavGraph
import com.yijing.divination.ui.theme.YiJingTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 主 Activity
 *
 * 使用 Jetpack Compose 构建 UI
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YiJingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // 初始化数据库
                    LaunchedEffect(Unit) {
                        databaseInitializer.initializeIfNeeded()
                    }

                    YiJingNavGraph(navController = navController)
                }
            }
        }
    }
}
