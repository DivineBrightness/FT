package com.yijing.divination

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 易经占卜应用主类
 *
 * 使用 Hilt 进行依赖注入
 */
@HiltAndroidApp
class YiJingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 应用初始化逻辑
    }
}
